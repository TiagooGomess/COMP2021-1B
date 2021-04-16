package analysis;

import analysis.symbol.Class;
import analysis.symbol.Program;
import analysis.method.Method;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import analysis.value.Value;
import analysis.value.Terminal;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.*;

// public String[] jjtNodeName = { "skipParenthesis", "Program",
// "ImportDeclaration", "void", "Class", "Attributes",
// "Methods", "MethodDeclaration", "Arguments", "Argument", "Body", "Return",
// "VariableDeclaration", "AttributeDeclaration",
//
// "Block", "If", "Then", "Else", "While", "Assignment",
// "Condition",
//
// "Operation", "Operator"
// "Position", "Access", "Call", "Method", "Construction", "Size",
// "Literal", "Variable", "This" };

public class JmmVisitor extends PreorderJmmVisitor<SymbolTable, Value> {
    private final SymbolTable symbolTable = new SymbolTable();
    private final static List<String> statementTypes = Arrays.asList("Body", "Block", "If", "Then", "Else", "While");
    private final static List<String> valueTypes = Arrays.asList("Operation", "Access", "Call", "Construction", "Literal", "Variable", "This");
    private Method currentMethod = null;

    private final Map<Method, JmmNode> methodReturn = new HashMap<>();
    private final Map<Method, List<JmmNode>> methodConditions = new HashMap<>();
    private final Map<Method, List<JmmNode>> methodValues = new HashMap<>();
    private final Map<Method, List<JmmNode>> methodAssignments = new HashMap<>();

    private final Map<JmmNode, Value> valueNode = new HashMap<>();

    public JmmVisitor() {
        super();

        addVisit("ImportDeclaration", this::dealWithImport);
        addVisit("Class", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("AttributeDeclaration", this::dealWithAttribute);
        addVisit("Argument", this::dealWithArgument);
        addVisit("MainArgument", this::dealWithArgument);
        addVisit("VariableDeclaration", this::dealWithLocalVariable);

        addVisit("Condition", this::dealWithCondition);
        addVisit("Assignment", this::dealWithAssignment);
        addVisit("Return", this::dealWithReturn);

        for (String valueParent : statementTypes)
            addVisit(valueParent, this::dealWithValueParent);

        //setDefaultVisit(this::defaultVisit);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    // ----------------------------------------------------------------
    // Class
    // ----------------------------------------------------------------

    private Value dealWithImport(JmmNode node, SymbolTable jmmSymbolTable) {
        symbolTable.addImport(Class.fromNode(node));
        return null;
    }

    private Value dealWithClass(JmmNode node, SymbolTable jmmSymbolTable) {
        symbolTable.setMainClass(Class.fromNode(node));
        return null;
    }

    private Value dealWithAttribute(JmmNode node, SymbolTable jmmSymbolTable) {
        symbolTable.addField(Terminal.fromDeclaration(node));
        return null;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------

    private Value dealWithMethod(JmmNode node, SymbolTable jmmSymbolTable) {
        currentMethod = Method.fromDeclaration(node);
        symbolTable.addMethod(currentMethod);
        methodAssignments.put(currentMethod, new ArrayList<>());
        return null;
    }

    private Value dealWithArgument(JmmNode node, SymbolTable jmmSymbolTable) {
        currentMethod.addParameter(Terminal.fromDeclaration(node));
        return null;
    }

    private Value dealWithLocalVariable(JmmNode node, SymbolTable jmmSymbolTable) {
        currentMethod.addLocalVariable(Terminal.fromDeclaration(node));
        return null;
    }

    // ----------------------------------------------------------------
    // Expressions
    // ----------------------------------------------------------------

    private Value dealWithAssignment(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        methodAssignments.get(currentMethod).add(jmmNode);
        return null;
    }

    private Value dealWithCondition(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        JmmNode expressionNode = jmmNode.getChildren().get(0);

        if (!methodConditions.containsKey(currentMethod))
            methodConditions.put(currentMethod, new ArrayList<>());
        methodConditions.get(currentMethod).add(expressionNode);

        return null;
    }

    private Value dealWithReturn(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        JmmNode expressionNode = jmmNode.getChildren().get(0);
        methodReturn.put(currentMethod, expressionNode);
        return null;
    }

    private Value dealWithValueParent(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        List<JmmNode> valueNodes = new ArrayList<>();
        for (JmmNode child : jmmNode.getChildren())
            if (JmmVisitor.valueTypes.contains(child.getKind()))
                valueNodes.add(child);

        if (!this.methodValues.containsKey(currentMethod))
            this.methodValues.put(currentMethod, new ArrayList<>());
        this.methodValues.get(currentMethod).addAll(valueNodes);

        return null;
    }

    // ----------------------------------------------------------------
    // Analyse values
    // ----------------------------------------------------------------

    public void analyseMethodValues() {
        // Analyse expressions
        for (Map.Entry<Method, List<JmmNode>> entry : this.methodValues.entrySet()) {
            Method method = entry.getKey();
            for (JmmNode valueNode : entry.getValue()) {
                try {
                    Value value = Value.fromNode(this.symbolTable, method, valueNode, null);
                    this.valueNode.put(valueNode, value);
                } catch (JmmException e) {
                    this.symbolTable.addReport(e);
                }
            }
        }

        // Analyse conditions
        for (Map.Entry<Method, List<JmmNode>> entry : this.methodConditions.entrySet()) {
            Method method = entry.getKey();
            for (JmmNode conditionNode : entry.getValue()) {
                Value value;
                try {
                    value = Value.fromNode(this.symbolTable, method, conditionNode, Program.BOOL_TYPE);
                    this.valueNode.put(conditionNode, value);
                } catch (JmmException e) {
                    this.symbolTable.addReport(e);
                    continue;
                }

                if (!value.getReturnType().equals(Program.BOOL_TYPE)) {
                    JmmException e = JmmException.invalidCondition(value.getReturnType());
                    this.symbolTable.addReport(e);
                }
            }
        }

        // Analyse method assignments
        for (Map.Entry<Method, List<JmmNode>> entry : this.methodAssignments.entrySet()) {
            Method method = entry.getKey();
            for (JmmNode child : entry.getValue()) {
                List<JmmNode> operators = child.getChildren();

                Value variable;
                try {
                    JmmNode variableNode = operators.get(0);
                    if (variableNode.getKind().equals("Access"))
                        variable = Value.fromNode(this.symbolTable, method, variableNode, null);
                    else
                        variable = Terminal.fromVariable(this.symbolTable, method, variableNode);
                    this.valueNode.put(variableNode, variable);
                } catch (JmmException e) {
                    this.symbolTable.addReport(e);
                    continue;
                } catch (java.lang.NullPointerException exception) {
                    JmmException e = JmmException.invalidAssignmentVariable();
                    this.symbolTable.addReport(e);
                    continue;
                }

                Value value;
                try {
                    value = Value.fromNode(this.symbolTable, method, operators.get(1), variable.getReturnType());
                    this.valueNode.put(operators.get(1), value);
                } catch (JmmException e) {
                    this.symbolTable.addReport(e);
                    continue;
                }

                if (!variable.getReturnType().equals(value.getReturnType())) {
                    JmmException e = JmmException.invalidAssignment(variable, value.getReturnType());
                    this.symbolTable.addReport(e);
                }
            }
        }

        // Analyse return
        for (Map.Entry<Method, JmmNode> entry : this.methodReturn.entrySet()) {
            Method method = entry.getKey();
            JmmNode returnNode = entry.getValue();
            Value value;
            try {
                value = Value.fromNode(this.symbolTable, method, returnNode, method.getReturnType());
                this.valueNode.put(returnNode, value);
            } catch (JmmException e) {
                this.symbolTable.addReport(e);
                continue;
            }

            if (!value.getReturnType().equals(method.getReturnType())) {
                JmmException e = JmmException.invalidReturn(method.getName(), method.getReturnType(), value.getReturnType());
                this.symbolTable.addReport(e);
            }
        }
    }

    public void printOllir() {
        StringBuilder builder = new StringBuilder();

        for (Method method : this.symbolTable.getClass(null).getMethods()) {
            StringBuilder declaration = new StringBuilder(method.getOllir() + " {");
            boolean specialCase = !methodValues.containsKey(method);

            if (specialCase) {
                declaration.append("\n  invokespecial(this, \"<init>\").V;");
            } else {
                for (JmmNode node : this.methodValues.get(method))
                    if (valueNode.containsKey(node)) {
                        String ollir = valueNode.get(node).getOllir();
                        if (ollir != null)
                            declaration.append("\n  ").append(ollir.replace("\n", "\n  "));
                    }
            }
            declaration.append("\n}\n");

            if (specialCase)
                builder.insert(0, declaration);
            else
                builder.append(declaration);
        }

        System.out.println(builder.toString());
    }
}

