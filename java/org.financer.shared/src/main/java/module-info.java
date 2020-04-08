module org.financer.shared {
    exports org.financer.shared.connection;
    exports org.financer.shared.exceptions;
    exports org.financer.shared.domain.model;
    exports org.financer.shared.domain.model.api;
    exports org.financer.shared.domain.model.api.user;
    exports org.financer.shared.domain.model.api.category;
    exports org.financer.shared.domain.model.api.transaction;
    exports org.financer.shared.domain.model.value.objects;

    requires org.financer.util;
    requires org.hibernate.orm.core;
    requires com.google.gson;
    requires java.persistence;
    requires commons.validator;
    requires java.validation;

    uses javax.persistence.spi.PersistenceProvider;

    opens org.financer.shared.domain.model.api to com.google.gson, modelmapper;
    opens org.financer.shared.domain.model.api.user to org.hibernate.validator, java.persistence;
    opens org.financer.shared.domain.model.api.category to org.hibernate.validator;
    opens org.financer.shared.domain.model.api.transaction to org.hibernate.validator;
    opens org.financer.shared.domain.model.value.objects to spring.core, org.hibernate.orm.core, com.fasterxml.jackson.databind;

}