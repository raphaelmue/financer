module de.raphaelmuesseler.financer.shared {
    exports de.raphaelmuesseler.financer.shared.connection;
    exports de.raphaelmuesseler.financer.shared.model.user;
    exports de.raphaelmuesseler.financer.shared.model.categories;
    exports de.raphaelmuesseler.financer.shared.exceptions;
    exports de.raphaelmuesseler.financer.shared.model.transactions;
    exports de.raphaelmuesseler.financer.shared.model.db;

    requires de.raphaelmuesseler.financer.util;
    requires java.persistence;

    opens de.raphaelmuesseler.financer.shared.model.db to org.hibernate.orm.core;
}