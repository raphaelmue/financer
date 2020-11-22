module org.financer.shared {
    exports org.financer.shared.path;
    exports org.financer.shared.connection;
    exports org.financer.shared.exceptions;
    exports org.financer.shared.domain.model;
    exports org.financer.shared.domain.model.api;
    exports org.financer.shared.domain.model.api.user;
    exports org.financer.shared.domain.model.api.category;
    exports org.financer.shared.domain.model.api.transaction;
    exports org.financer.shared.domain.model.api.transaction.variable;
    exports org.financer.shared.domain.model.api.transaction.fixed;
    exports org.financer.shared.domain.model.value.objects;
    exports org.financer.shared.domain.model.api.admin;

    requires static lombok;
    requires org.financer.util;
    requires org.hibernate.orm.core;
    requires java.xml.bind;
    requires java.persistence;
    requires commons.validator;
    requires java.validation;
    requires spring.hateoas;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires io.swagger.v3.oas.annotations;

    uses javax.persistence.spi.PersistenceProvider;

    opens org.financer.shared.domain.model.api to modelmapper;
    opens org.financer.shared.domain.model.api.user to org.hibernate.validator, java.persistence;
    opens org.financer.shared.domain.model.api.category to org.hibernate.validator, modelmapper;
    opens org.financer.shared.domain.model.api.transaction to org.hibernate.validator;
    opens org.financer.shared.domain.model.api.transaction.fixed to org.hibernate.validator;
    opens org.financer.shared.domain.model.api.transaction.variable to org.hibernate.validator;
    opens org.financer.shared.domain.model.value.objects to spring.core, org.hibernate.orm.core, com.fasterxml.jackson.databind;

}