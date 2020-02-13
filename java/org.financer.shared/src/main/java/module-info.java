module org.financer.shared {
    exports org.financer.shared.connection;
    exports org.financer.shared.model.user;
    exports org.financer.shared.model.categories;
    exports org.financer.shared.exceptions;
    exports org.financer.shared.model.transactions;
    exports org.financer.shared.model.db;

    requires org.financer.util;
    requires java.persistence;
    requires spring.context;
    requires com.google.gson;
    requires com.fasterxml.jackson.annotation;
    requires swagger.annotations;

    opens org.financer.shared.model.db to org.hibernate.orm.core;
}