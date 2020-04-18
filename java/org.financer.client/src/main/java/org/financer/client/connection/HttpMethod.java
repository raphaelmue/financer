package org.financer.client.connection;

import okhttp3.Request;
import okhttp3.RequestBody;
import org.financer.shared.domain.model.api.DataTransferObject;

public interface HttpMethod {

    static HttpMethod fromString(String httpMethod) {
        switch (httpMethod) {
            case "GET":
                return new Get();
            case "POST":
                return new Post();
            case "PUT":
                return new Put();
            case "DELETE":
                return new Delete();
            case "PATCH":
                return new Patch();
            case "OPTIONS":
                return new Options();
            default:
                throw new IllegalArgumentException("Illegal Http Method");
        }
    }

    default RequestBody requestBody(DataTransferObject body) {
        return RequestBody.create(body.toJson(), ServerRequest.MEDIA_TYPE_JSON);
    }

    Request.Builder build(Request.Builder builder, DataTransferObject body);

    class Get implements HttpMethod {
        @Override
        public Request.Builder build(Request.Builder builder, DataTransferObject body) {
            return builder.get();
        }

        @Override
        public String toString() {
            return "GET";
        }
    }

    class Post implements HttpMethod {
        @Override
        public Request.Builder build(Request.Builder builder, DataTransferObject body) {
            return builder.post(requestBody(body));
        }

        @Override
        public String toString() {
            return "POST";
        }
    }

    class Put implements HttpMethod {
        @Override
        public Request.Builder build(Request.Builder builder, DataTransferObject body) {
            return builder.post(requestBody(body));
        }

        @Override
        public String toString() {
            return "PUT";
        }
    }

    class Delete implements HttpMethod {
        @Override
        public Request.Builder build(Request.Builder builder, DataTransferObject body) {
            return builder.delete();
        }

        @Override
        public String toString() {
            return "DELETE";
        }
    }

    class Patch implements HttpMethod {
        @Override
        public Request.Builder build(Request.Builder builder, DataTransferObject body) {
            return builder.patch(requestBody(body));
        }

        @Override
        public String toString() {
            return "PATCH";
        }
    }

    class Options implements HttpMethod {
        @Override
        public Request.Builder build(Request.Builder builder, DataTransferObject body) {
            return builder.method("OPTIONS", null);
        }

        @Override
        public String toString() {
            return "OPTIONS";
        }
    }
}
