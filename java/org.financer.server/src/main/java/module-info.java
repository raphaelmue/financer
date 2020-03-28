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
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.jcl;
    requires slf4j.api;
    requires net.bytebuddy;
    requires com.fasterxml.classmate;
    requires com.fasterxml.jackson.databind;
    requires org.apache.tomcat.embed.core;
    requires modelmapper;
    requires jdk.unsupported;
    requires spring.security.web;
    requires spring.security.core;
    requires spring.security.config;
    requires java.jwt;


    opens org.financer.server.domain.service to spring.core, spring.aop;
    opens org.financer.server.domain.repository to spring.core;

    opens org.financer.server.application to spring.core;
    opens org.financer.server.application.api to spring.core;
    opens org.financer.server.application.service to spring.core;
    opens org.financer.server.application.configuration to spring.core;

    opens org.financer.server.domain.model.user to spring.core, org.hibernate.orm.core;
    opens org.financer.server.domain.model.category to spring.core, org.hibernate.orm.core;
    opens org.financer.server.domain.model.transaction to spring.core,  org.hibernate.orm.core;

    exports org.financer.server.domain.service to spring.core, spring.beans;
    exports org.financer.server.domain.repository to spring.core, spring.beans, spring.data.commons, spring.aop;

    exports org.financer.server.domain.model.user to spring.beans, modelmapper;
    exports org.financer.server.domain.model.category to spring.beans, modelmapper;
    exports org.financer.server.domain.model.transaction to spring.beans, modelmapper;

    exports org.financer.server.application to spring.beans, spring.context;
    exports org.financer.server.application.api to spring.beans, spring.web;
    exports org.financer.server.application.service to spring.aop;
    exports org.financer.server.application.configuration to spring.beans, spring.context;
}