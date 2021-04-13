package nodes;

import pt.up.fe.comp.jmm.analysis.table.Symbol;

import java.util.List;

public class Method {
    private String name;
    private boolean isStatic;

    private List<Symbol> arguments;
    private List<Symbol> localVariables;

    public Method(String name, boolean isStatic, List<Symbol> arguments, List<Symbol> localVariables) {
        this.name = name;
        this.isStatic = isStatic;
        this.arguments = arguments;
        this.localVariables = localVariables;
    }
}
