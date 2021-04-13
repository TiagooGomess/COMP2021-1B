package semantics;

import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;
import java.util.ArrayList;
import java.lang.Void;

public class JmmVisitor extends PreorderJmmVisitor<JmmSymbolTable, Void> {
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
        addVisit("Expression", this::dealWithExpression);
    }

    public JmmSymbolTable getSymbolTable() {
        return symbolTable;
    }

    private Type typeFromString(String typeName) {
        int size = typeName.length();
        boolean isArray = false;
        if (typeName.startsWith("[]", size - 2)) {
            isArray = true;
            typeName = typeName.substring(0, size - 2);
        }
        return new Type(typeName, isArray);
    }

    private Void dealWithImport(JmmNode node, JmmSymbolTable parent) {
        symbolTable.addImport(node.get("name"));
        return null;
    }

    private Void dealWithClass(JmmNode node, JmmSymbolTable parent) {
        String className = node.get("name");
        if (node.getAttributes().contains("extends")) {
            symbolTable.setSuper(node.get("extends"));
        }
        symbolTable.setClassName(className);
        return null;
    }

    private Void dealWithAttribute(JmmNode node, JmmSymbolTable parent) {
        symbolTable.addField(new Symbol(typeFromString(node.get("type")), node.get("name")));
        return null;
    }

    private Void dealWithMethod(JmmNode node, JmmSymbolTable parent) {
        String methodName = node.get("name");
        currentMethod = methodName;
        Type returnType = new Type(node.get("type"), false);
        symbolTable.addMethod(methodName, returnType);
        return null;
    }

    private Void dealWithArgument(JmmNode node, JmmSymbolTable parent) {
        Type type = typeFromString(node.get("type"));
        Symbol variableSymbol = new Symbol(type, node.get("name"));
        symbolTable.addParameter(currentMethod, variableSymbol);
        return null;
    }

    private Void dealWithLocalVariable(JmmNode node, JmmSymbolTable parent) {
        Type type = typeFromString(node.get("type"));
        Symbol variableSymbol = new Symbol(type, node.get("name"));
        symbolTable.addLocalVariable(currentMethod, variableSymbol);
        return null;
    }

    private Void dealWithExpression(JmmNode node, JmmSymbolTable parent) {
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
        // Verificar se o número de parêmetros é igual ao número de argumentos
        // Verificar se o tipo dos parâmetros e dos argumentos é o mesmo

        return null;
    }
}