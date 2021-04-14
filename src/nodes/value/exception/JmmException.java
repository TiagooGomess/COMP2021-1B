package nodes.value.exception;

public class JmmException extends Exception {
    private JmmException(String message) {
        super(message);
    }

    public static JmmException undeclaredVariable(String variableName) {
        return new JmmException("Variable \"" + variableName + "\" was not declared in the scope");
    }
}
