package analysis.method;

import analysis.value.Terminal;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodSignature {
    private boolean isStatic;
    private final String methodName;
    private final List<Terminal> parameters;

    public MethodSignature(String methodName) {
        this.methodName = methodName;
        this.parameters = new ArrayList<>();
        this.isStatic = false;
    }

    public MethodSignature(String methodName, List<Terminal> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.isStatic = true;
    }

    public MethodSignature(String methodName, List<Terminal> parameters, boolean isStatic) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.isStatic = isStatic;
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

    public boolean isStatic() {
        return this.isStatic;
    }

    private List<Type> getParameterTypes() {
        List<Type> parameterTypes = new ArrayList<>();
        for (var parameter : this.parameters)
            parameterTypes.add(parameter.getType());
        return parameterTypes;
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
        return this.methodName.equals(operand.methodName) && this.getParameterTypes().equals(operand.getParameterTypes());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (Type e : this.getParameterTypes()) {
            if (e == null)
                hashCode = 31 * hashCode;
            else
                hashCode = 31 * hashCode + e.hashCode();
        }
        return Objects.hash(isStatic, methodName) + hashCode;
    }
}
