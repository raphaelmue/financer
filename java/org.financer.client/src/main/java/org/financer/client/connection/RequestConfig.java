package org.financer.client.connection;

import org.financer.shared.domain.model.api.DataTransferObject;

import java.util.Collections;
import java.util.Map;

public class RequestConfig {

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryParameters;
    private final DataTransferObject body;

    public RequestConfig(HttpMethod httpMethod, String path) {
        this(httpMethod, path, Collections.emptyMap());
    }

    public RequestConfig(HttpMethod httpMethod, String path, DataTransferObject body) {
        this(httpMethod, path, Collections.emptyMap(), body);
    }

    public RequestConfig(HttpMethod httpMethod, String path, Map<String, String> queryParameters) {
        this(httpMethod, path, queryParameters, new DataTransferObject() {
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
