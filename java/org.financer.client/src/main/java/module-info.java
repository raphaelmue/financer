module org.financer.client {
    exports org.financer.client.format;
    exports org.financer.client.connection;
    exports org.financer.client.local;

    requires transitive org.financer.shared;
    requires transitive org.financer.util;
    requires java.logging;
}