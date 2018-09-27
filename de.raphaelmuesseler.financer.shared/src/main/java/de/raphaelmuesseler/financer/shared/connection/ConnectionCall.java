package de.raphaelmuesseler.financer.shared.connection;

import java.io.Serializable;
import java.util.Map;

public class ConnectionCall implements Serializable {
    private static final long serialVersionUID = -4886612977513896452L;
    private final String methodName;
    private final Map<String, Object> parameters;

    public ConnectionCall(String methodName, Map<String, Object> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
