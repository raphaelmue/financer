module org.financer.shared {
    exports org.financer.shared.connection;
    exports org.financer.shared.exceptions;
    exports org.financer.shared.domain.model.api;
    exports org.financer.shared.domain.model.value.objects;

    requires org.financer.util;
    requires java.persistence;
    requires com.google.gson;
    requires hibernate.annotations;
    requires commons.validator;

    opens org.financer.shared.domain.model.api to com.google.gson;
    exports org.financer.shared.domain.model;
}