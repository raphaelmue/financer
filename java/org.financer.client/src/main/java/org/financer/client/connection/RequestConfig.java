package org.financer.client.connection;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.path.Path;

import java.util.Collections;
import java.util.Map;

public class RequestConfig {

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryParameters;
    private final DataTransferObject body;

    public RequestConfig(Path path) {
        this(path, Collections.emptyMap());
    }

    public RequestConfig(Path path, DataTransferObject body) {
        this(HttpMethod.fromString(path.getMethod()), path.getPath(), Collections.emptyMap(), body);
    }

    public RequestConfig(Path path, Map<String, String> parameters) {
        this(HttpMethod.fromString(path.getMethod()), path.getPath(), parameters, new DataTransferObject() {
            @Override
            public String toJson() {
                return "";
            }
        });
    }

    public RequestConfig(HttpMethod httpMethod, String path, Map<String, String> queryParameters, DataTransferObject body) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParameters = queryParameters;
        this.body = body;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public DataTransferObject getBody() {
        return body;
    }
}
