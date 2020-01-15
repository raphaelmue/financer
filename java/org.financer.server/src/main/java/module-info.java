module org.financer.server {
    requires org.financer.shared;
    requires org.financer.util;

    requires org.hibernate.orm.core;

    requires java.persistence;
    requires java.xml;
    requires java.logging;
    requires java.naming;
    requires commons.email;
}