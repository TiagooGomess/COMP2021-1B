package semantics;

import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;
import java.util.ArrayList;

public class JmmVisitor extends PreorderJmmVisitor<JmmSymbolTable, Type> {
    private final JmmSymbolTable symbolTable = new JmmSymbolTable();
    private static String currentMethod = null;

    // public String[] jjtNodeName = { "skipParenthesis", "Program",
    // "ImportDeclaration", "void", "Class", "Attributes",
    // "Methods", "MethodDeclaration", "Arguments", "Argument", "Body", "Return",
    // "VariableDeclaration",
    // "AttributeDeclaration", "Block", "If", "Then", "Else", "While", "Assignment",
    // "Condition", "Operation",
    // "Position", "Access", "Call", "Method", "Construction", "Size", "Literal",
    // "Variable", "This",
    // "Operator", };

    public JmmVisitor() {
        super();

        addVisit("ImportDeclaration", this::dealWithImport);
        addVisit("Class", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("AttributeDeclaration", this::dealWithAttribute);
        addVisit("Argument", this::dealWithArgument);
        addVisit("MainArgument", this::dealWithArgument);
        addVisit("VariableDeclaration", this::dealWithLocalVariable);

        addVisit("Body", this::dealWithBody);

        addVisit("Operation", this::dealWithOperation);
    }

    public JmmSymbolTable getSymbolTable() {
        return symbolTable;
    }

    private static Type typeFromString(String typeName) {
        int size = typeName.length();
        boolean isArray = false;
        if (typeName.startsWith("[]", size - 2)) {
            isArray = true;
            typeName = typeName.substring(0, size - 2);
        }
        return new Type(typeName, isArray);
    }

    private Type dealWithImport(JmmNode node, JmmSymbolTable parent) {
        symbolTable.addImport(node.get("name"));
        return null;
    }

    private Type dealWithClass(JmmNode node, JmmSymbolTable parent) {
        String className = node.get("name");
        if (node.getAttributes().contains("extends")) {
            symbolTable.setSuper(node.get("extends"));
        }
        symbolTable.setClassName(className);
        return null;
    }

    private Type dealWithAttribute(JmmNode node, JmmSymbolTable parent) {
        symbolTable.addField(new Symbol(typeFromString(node.get("type")), node.get("name")));
        return null;
    }

    private Type dealWithMethod(JmmNode node, JmmSymbolTable parent) {
        String methodName = node.get("name");
        currentMethod = methodName;
        Type returnType = new Type(node.get("type"), false);
        symbolTable.addMethod(methodName, returnType);
        return null;
    }

    private Type dealWithArgument(JmmNode node, JmmSymbolTable parent) {
        Type type = typeFromString(node.get("type"));
        Symbol variableSymbol = new Symbol(type, node.get("name"));
        symbolTable.addParameter(currentMethod, variableSymbol);
        return null;
    }

    private Type dealWithLocalVariable(JmmNode node, JmmSymbolTable parent) {
        Type type = typeFromString(node.get("type"));
        Symbol variableSymbol = new Symbol(type, node.get("name"));
        symbolTable.addLocalVariable(currentMethod, variableSymbol);
        return null;
    }

    private Type dealWithOperation(JmmNode node, JmmSymbolTable parent) {
        List<JmmNode> operands = new ArrayList<>();
        String operation = "";
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Operator"))
                operation = child.get("name");
            else
                operands.add(child);
        }

        Type expectedType;
        if (operation.equals("Conjunction"))
            expectedType = new Type("bool", false);
        else
            expectedType = new Type("int", false);


        for (JmmNode operand : operands) {
            Type realType = getType(operand);
            if (!expectedType.equals(realType))
                this.symbolTable.addError(new Exception("Incompatible type for " + operation + " operand, expected " + expectedType.getName() + ", found " + realType));
        }

        // Access, Call, NotExpression, Construction, ParenthesisExpression, Literal, Size, Variable, This, Method, Position

        return null;
    }


    private Type dealWithExpression(JmmNode node, JmmSymbolTable parent) {
        // Verificar se os dois lados da operação são do mesmo tipo e são válidos para a operação (! só aceita booleanos, etc)
        // Não é possível fazer operações com arrays, por exemplo: array1 + array2
        // Verificar se o acesso de um array não é feito em uma variável que não é um array, por exemplo: 1[2] or notArray[2]
        // Verificar se o índice do acesso do array é um inteiro, por exemplo: 1, 3 + 4 * 5, x + y
        // Verificar se os valores dos dois lados do assignment são do mesmo tipo
        // Verificar se a expressão na condição do if e while retorna um boolean

        // METHODS
        // Verificar se o "target" do método existe, e se este contém o metodo (e.g. a.foo, ver se "a" existe
        // e se tem um metodo "foo")
        // caso seja do tipo da classe da classe declarada (e.g. a user o this), se nao existir
        // declaraçao na propria classe: se nao tiver extends retorna erro, se tiver extends assumir
        // que e da classe super.
        // Inferência de métodos não declarados na própria classe,
        // por exemplo: inteiro = Foo.b()
        // assumimos que  Foo.b() não tem argumentos e retorna um int
        // Verificar se o número de parâmetros é igual ao número de argumentos
        // Verificar se o tipo dos parâmetros e dos argumentos é o mesmo

        // "Block", "If", "Then", "Else", "While", "Assignment", "Condition"
        return null;
    }

    Type getType(JmmNode expressionNode) {
        Type type = null;
        switch (expressionNode.getKind()) {
            case "Literal":
                type = typeFromString(expressionNode.get("type"));
                break;
            case "Access":
                type = typeFromString("int");
                break;
            case "Variable":
                type = symbolTable.getVariableTypeFromScope(currentMethod, expressionNode.get("name"));
                break;
            case "Call": // length
                for (JmmNode child : expressionNode.getChildren())
                    if (child.getKind().equals("Method")) {
                        String method = child.get("name");
                        if (method.equals("length")) {
                            type = typeFromString("int");
                        } else {

                        }
                        System.out.println("--------------");
                        System.out.println(child.toJson());
                        System.out.println("--------------");
                        type = symbolTable.getReturnType(child.get("name"));
                        //break;
                    }
                break;
            default:
                break;
        }
        if (type == null) {
            System.out.println(expressionNode.getKind());
        }
        return type;
    }

    private Type dealWithBody(JmmNode node, JmmSymbolTable parent) {


        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Call")) {
                String externalMethodName = null, classNameTypeStr = null;
                List<Symbol> externalMethodParameters = new ArrayList<>();
                for (JmmNode child2 : child.getChildren()) {
                    if (child2.getKind().equals("Variable")) {
                        String variableName = child2.get("name");
                        Type variableType = this.symbolTable.getVariableTypeFromScope(currentMethod, variableName);

                        if (variableType != null)
                            classNameTypeStr = variableType.getName();
                        else
                            this.symbolTable.addError(new Exception("Variable \"" + variableName + "\" is not defined in this scope"));

                    } else if (child2.getKind().equals("Method")) {
                        externalMethodName = child2.get("name");
                        for (JmmNode child3 : child2.getChildren()) {
                            if (child3.getKind().equals("Arguments")) {
                                int i = 0;
                                for (JmmNode child4 : child3.getChildren()) {
                                    if (child4.getKind().equals("Variable")) {
                                        String variableName = child4.get("name");
                                        Type variableType = this.symbolTable.getVariableTypeFromScope(currentMethod, variableName);
                                        externalMethodParameters.add(new Symbol(variableType, "argument " + ++i));
                                    }
                                }
                            }
                        }
                    }
                }
                if (externalMethodName == null || classNameTypeStr == null)
                    break;
                this.symbolTable.addExternalMethod(externalMethodName, classNameTypeStr, null, false, externalMethodParameters);
            }
        }

        return null;
    }
}

