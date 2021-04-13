package nodes.expression;

import nodes.Method;
import nodes.Program;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class Terminal extends Symbol implements Expression {
    public Terminal(Type type, String name) {
        super(type, name);
    }

    @Override
    public Type getReturnType() {
        return getType();
    }

    // ----------------------------------------------------------------
    // Creating from node
    // ----------------------------------------------------------------

    public static Terminal fromLiteral(JmmNode node) {
        return new Terminal(Program.stringToType(node.get("type")), node.get("value"));
    }

    public static Terminal fromVariable(Method method, JmmNode node) {
        String name = node.getKind().equals("This") ? "this" : node.get("name");
        Type type = method.getVariableType(name);
        if (type == null)
            return null;
        return new Terminal(type, name);
    }

    public static Terminal fromDeclaration(JmmNode node) {
        return new Terminal(Program.stringToType(node.get("type")), node.get("name"));
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    public String toString(String padding) {
        String result = padding + this.getName() + " : " + this.getReturnType().getName();
        if (this.getReturnType().isArray())
            result += "[]";
        return result;
    }

    @Override
    public String toString() {
        return this.toString("");
    }
}
