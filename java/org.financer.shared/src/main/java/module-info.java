module org.financer.shared {
    exports org.financer.shared.connection;
    exports org.financer.shared.model.user;
    exports org.financer.shared.model.categories;
    exports org.financer.shared.exceptions;
    exports org.financer.shared.model.transactions;
    exports org.financer.shared.model.db;

    requires org.financer.util;
    requires java.persistence;

    opens org.financer.shared.model.db to org.hibernate.orm.core;
}