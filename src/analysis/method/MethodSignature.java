package analysis.method;

import analysis.value.Terminal;

import java.util.ArrayList;
import java.util.List;

public class MethodSignature {
    private final String methodName;
    private final List<Terminal> parameters;

    public MethodSignature(String methodName) {
        this.methodName = methodName;
        this.parameters = new ArrayList<>();
    }

    public MethodSignature(String methodName, List<Terminal> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public String getMethodName() {
        return methodName;
    }

    public List<Terminal> getParameters() {
        return parameters;
    }

    // ----------------------------------------------------------------
    // Comparison function
    // ----------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof MethodSignature))
            return false;
        MethodSignature operand = (MethodSignature) obj;
        return this.methodName.equals(operand.methodName) && this.parameters.equals(operand.parameters);
    }
}
