package de.raphaelmuesseler.financer.shared.connection;

import java.util.Map;

public class ConnectionCall {
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
