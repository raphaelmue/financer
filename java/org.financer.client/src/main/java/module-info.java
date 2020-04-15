module org.financer.client {
    exports org.financer.client.connection;
    exports org.financer.client.domain.api;
    exports org.financer.client.domain.model.user;
    exports org.financer.client.domain.model.category;
    exports org.financer.client.domain.model.transaction;

    exports org.financer.client.format;
    exports org.financer.client.local;

    requires transitive org.financer.shared;
    requires transitive org.financer.util;
    requires java.logging;
    requires okhttp3;
    requires kotlin.stdlib;
    requires annotations;
    requires com.fasterxml.jackson.databind;
}