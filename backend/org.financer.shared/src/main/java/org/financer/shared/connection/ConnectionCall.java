package org.financer.shared.connection;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Raphael Müßeler
 */
public class ConnectionCall implements Serializable {
    private static final long serialVersionUID = -4886612977513896452L;
    private final String methodName;
    private final Map<String, Serializable> parameters;

    /**
     * Instantiates a new ConnectionCall.
     * With the help of a ConnectionCall, as a client, you can call methods of the FinancerService.
     *
     * @param methodName Name of the corresponding FinancerService method. If the method does not exist, an Exception is thrown.
     * @param parameters parameters, which the FinancerService method needs
     */
    public ConnectionCall(String methodName, Map<String, Serializable> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public Map<String, Serializable> getParameters() {
        return parameters;
    }
}
