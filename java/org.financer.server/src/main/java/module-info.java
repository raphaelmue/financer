module org.financer.server {
    requires org.financer.shared;
    requires org.financer.util;

    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.xml;
    requires java.logging;
    requires java.naming;
    requires java.sql;
    requires java.validation;
    requires commons.email;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.web;
    requires spring.context;
    requires spring.tx;
    requires spring.jdbc;
    requires spring.orm;
    requires spring.beans;
    requires slf4j.api;
    requires net.bytebuddy;
    requires com.fasterxml.classmate;
    requires com.fasterxml.jackson.databind;
    requires spring.data.commons;
    requires spring.data.jpa;

    opens org.financer.server.application.api to spring.core;
    opens org.financer.server.application.main to spring.core;
    opens org.financer.server.application.service to spring.core;
    opens org.financer.server.configuration to spring.core;

    exports org.financer.server.application.api to spring.beans, spring.web;
    exports org.financer.server.application.main to spring.beans, spring.context;
    exports org.financer.server.application.service to spring.aop;
    exports org.financer.server.configuration to spring.beans, spring.context;
}