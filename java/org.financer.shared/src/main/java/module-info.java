module org.financer.shared {
    exports org.financer.shared.connection;
    exports org.financer.shared.exceptions;
    exports org.financer.shared.domain.model.api;
    exports org.financer.shared.domain.model.value.objects;

    requires org.financer.util;
    requires org.hibernate.orm.core;
    requires com.google.gson;
    requires java.persistence;
    requires commons.validator;

    opens org.financer.shared.domain.model.api to com.google.gson;
    opens org.financer.shared.domain.model.value.objects to spring.core, org.hibernate.orm.core;

    exports org.financer.shared.domain.model;
}