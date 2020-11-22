module org.financer.util {
    exports org.financer.util.collections;
    exports org.financer.util.validation;
    exports org.financer.util.mapping;
    exports org.financer.util.security;

    requires commons.beanutils;
    requires java.validation;
    requires modelmapper;
}
