module de.raphaelmuesseler.financer.client {
    exports de.raphaelmuesseler.financer.client.format;
    exports de.raphaelmuesseler.financer.client.connection;
    exports de.raphaelmuesseler.financer.client.local;

    requires transitive de.raphaelmuesseler.financer.shared;
    requires transitive de.raphaelmuesseler.financer.util;
    requires java.logging;
}