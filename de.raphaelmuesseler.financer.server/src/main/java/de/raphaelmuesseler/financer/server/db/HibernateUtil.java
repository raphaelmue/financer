package de.raphaelmuesseler.financer.server.db;

import de.raphaelmuesseler.financer.shared.model.db.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static DatabaseName databaseName = DatabaseName.DEV;

    private HibernateUtil() {

    }

    public static void setDatabaseName(DatabaseName databaseName) {
        HibernateUtil.databaseName = databaseName;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("/de/raphaelmuesseler/financer/server/db/config/hibernate" + (databaseName == DatabaseName.TEST ? ".test" : "" ) + ".cfg.xml")
                    .addAnnotatedClass(AttachmentEntity.class)
                    .addAnnotatedClass(CategoryEntity.class)
                    .addAnnotatedClass(ContentAttachmentEntity.class)
                    .addAnnotatedClass(FixedTransactionAmountEntity.class)
                    .addAnnotatedClass(FixedTransactionEntity.class)
                    .addAnnotatedClass(SettingsEntity.class)
                    .addAnnotatedClass(TokenEntity.class)
                    .addAnnotatedClass(UserEntity.class)
                    .addAnnotatedClass(VariableTransactionEntity.class);
            String url = configuration.getProperty("hibernate.connection.url");
            configuration.setProperty("hibernate.connection.url", url.replace(DatabaseName.DEV.getName(), databaseName.getName()));

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void cleanDatabase() {
        if (databaseName == DatabaseName.TEST) {
            Session session = getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            Query query = session.createSQLQuery("truncate schema PUBLIC and commit");
            query.executeUpdate();
            transaction.commit();
            session.close();
        } else {
            throw new IllegalArgumentException("It is only allowed to clean the test database.");
        }
    }
}
