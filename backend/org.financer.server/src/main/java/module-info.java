module org.financer.server {
    requires transitive org.financer.shared;
    requires org.financer.util;

    // hibernate
    requires org.hibernate.orm.core;

    // java
    requires java.persistence;
    requires java.xml;
    requires java.logging;
    requires java.naming;
    requires java.sql;
    requires java.activation;
    requires java.validation;
    requires commons.email;

    //spring
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.web;
    requires spring.context;
    requires spring.tx;
    requires spring.jdbc;
    requires spring.orm;
    requires spring.beans;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.jcl;
    requires spring.security.web;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.core;
    requires spring.hateoas;
    requires slf4j.api;
    requires net.bytebuddy;
    requires com.fasterxml.classmate;
    requires com.fasterxml.jackson.databind;
    requires org.apache.tomcat.embed.core;
    requires modelmapper;
    requires jdk.unsupported;

    // springdoc
    requires springdoc.openapi.common;
    requires springdoc.openapi.ui;
    requires springdoc.openapi.webmvc.core;
    requires springdoc.openapi.hateoas;
    requires io.swagger.v3.core;
    requires io.swagger.v3.oas.models;
    requires io.swagger.v3.oas.annotations;
    requires io.github.classgraph;
    requires org.flywaydb.core;

    requires static lombok;

    uses javax.persistence.spi.PersistenceProvider;

    opens db.migration.h2;
    opens db.migration.mysql;

    opens org.financer.server.domain.service to spring.core, spring.aop;
    opens org.financer.server.domain.repository to spring.core;

    opens org.financer.server.application to spring.core;
    opens org.financer.server.application.api to spring.core;
    opens org.financer.server.application.service to spring.core;
    opens org.financer.server.application.configuration to spring.core, org.flywaydb.core;
    opens org.financer.server.application.configuration.security to spring.core;

    opens org.financer.server.domain.model.user to spring.core, org.hibernate.orm.core, modelmapper;
    opens org.financer.server.domain.model.category to spring.core, org.hibernate.orm.core, modelmapper;
    opens org.financer.server.domain.model.transaction to spring.core, org.hibernate.orm.core, modelmapper;

    opens org.financer.server.application.model to spring.core;
    opens org.financer.server.application.model.user to spring.core;
    opens org.financer.server.application.model.transaction.variable to spring.core;

    exports org.financer.server.domain.service to spring.core, spring.beans;
    exports org.financer.server.domain.repository to spring.core, spring.beans, spring.data.commons, spring.aop;

    exports org.financer.server.domain.model.user to spring.beans, modelmapper;
    exports org.financer.server.domain.model.category to spring.beans, modelmapper;
    exports org.financer.server.domain.model.transaction to spring.beans, modelmapper;

    exports org.financer.server.application to spring.beans, spring.context;
    exports org.financer.server.application.api to spring.beans, spring.web;
    exports org.financer.server.application.service to spring.beans, spring.aop, modelmapper;
    exports org.financer.server.application.api.error to com.fasterxml.jackson.databind;
    exports org.financer.server.application.configuration to spring.beans, spring.context, org.flywaydb.core;
    exports org.financer.server.application.configuration.security to spring.beans, spring.context;

    exports org.financer.server.application.model to spring.beans;
    exports org.financer.server.application.model.user to spring.beans;
    exports org.financer.server.application.model.transaction.variable to spring.beans;
    exports org.financer.server.application.model.transaction.fixed to spring.beans;
}