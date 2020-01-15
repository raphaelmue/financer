package de.raphaelmuesseler.financer.server.db;

import de.raphaelmuesseler.financer.shared.model.db.*;
import de.raphaelmuesseler.financer.shared.model.user.VerificationToken;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {

    private static final String DB_NAME_PLACEHOLDER = "db_name_placeholder";
    private static final String DB_HOST_PLACEHOLDER = "db_host_placeholder";
    private static final String DB_ENGINE_PLACEHOLDER = "db_engine_placeholder";

    private static final Logger logger = Logger.getLogger("Financer Server");

    private static SessionFactory sessionFactory;
    private static Properties databaseProperties;

    private HibernateUtil() {

    }

    public static void setDatabaseProperties(Properties databaseProperties) {
        HibernateUtil.databaseProperties = databaseProperties;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure(HibernateUtil.class.getResource("config/hibernate.cfg.xml"))
                    .addAnnotatedClass(AttachmentEntity.class)
                    .addAnnotatedClass(CategoryEntity.class)
                    .addAnnotatedClass(ContentAttachmentEntity.class)
                    .addAnnotatedClass(FixedTransactionAmountEntity.class)
                    .addAnnotatedClass(FixedTransactionEntity.class)
                    .addAnnotatedClass(SettingsEntity.class)
                    .addAnnotatedClass(TokenEntity.class)
                    .addAnnotatedClass(UserEntity.class)
                    .addAnnotatedClass(VariableTransactionEntity.class)
                    .addAnnotatedClass(VerificationTokenEntity.class);

            String url = configuration.getProperty("hibernate.connection.url");
            configuration.setProperty("hibernate.connection.url", url
                    .replace(DB_ENGINE_PLACEHOLDER, databaseProperties.getProperty("financer.database.engine"))
                    .replace(DB_HOST_PLACEHOLDER, databaseProperties.getProperty("financer.database.host"))
                    .replace(DB_NAME_PLACEHOLDER, databaseProperties.getProperty("financer.database.name")));
            configuration.setProperty("hibernate.connection.username", databaseProperties.getProperty("financer.database.user"));
            configuration.setProperty("hibernate.connection.password", databaseProperties.getProperty("financer.database.password"));

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
        logger.log(Level.INFO, "Cleaning test database.");
        if (databaseProperties.getProperty("financer.database.name").equals(DatabaseName.TEST.getName())) {
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
