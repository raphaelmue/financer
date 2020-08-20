module org.financer.util {
    exports org.financer.util;
    exports org.financer.util.collections;
    exports org.financer.util.concurrency;
    exports org.financer.util.date;
    exports org.financer.util.network;
    exports org.financer.util.validation;
    exports org.financer.util.mapping;

    requires commons.beanutils;
    requires java.validation;
    requires modelmapper;
}