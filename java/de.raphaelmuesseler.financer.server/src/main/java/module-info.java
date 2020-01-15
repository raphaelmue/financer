module de.raphaelmuesseler.financer.server {
    requires de.raphaelmuesseler.financer.shared;
    requires de.raphaelmuesseler.financer.util;

    requires org.hibernate.orm.core;

    requires java.persistence;
    requires java.xml;
    requires java.logging;
    requires java.naming;
    requires commons.email;
}