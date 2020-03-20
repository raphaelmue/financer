module org.financer.server {
    requires org.financer.shared;
    requires org.financer.util;

    requires org.hibernate.orm.core;

    requires java.persistence;
    requires java.xml;
    requires java.logging;
    requires java.naming;
    requires commons.email;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.web;
    requires java.validation;
    requires spring.context;
    requires spring.tx;
    requires slf4j.api;
    requires spring.jdbc;
    requires spring.orm;
    requires spring.beans;

    opens org.financer.server.main to spring.core;
    opens org.financer.server.configuration to spring.core;

    exports org.financer.server.api to spring.beans, spring.web;
    exports org.financer.server.main to spring.beans, spring.context;
    exports org.financer.server.configuration to spring.beans, spring.context;
}