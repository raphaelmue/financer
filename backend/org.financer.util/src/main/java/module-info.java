module org.financer.util {
    exports org.financer.util.collections;
    exports org.financer.util.validation;
    exports org.financer.util.mapping;
    exports org.financer.util.security;

    requires jakarta.validation;
    requires commons.beanutils;
    requires modelmapper;
}
