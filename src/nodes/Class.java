package nodes;

import pt.up.fe.comp.jmm.analysis.table.Symbol;

import java.util.List;

public class Class {
    String className;
    String superClassName;
    List<Symbol> attributes;
    List<Method> methods;

    public Class(String name, String superClassName, List<Symbol> attributes, List<Method> methods) {
        this.className = name;
        this.superClassName = superClassName;
        this.attributes = attributes;
        this.methods = methods;
    }
}
