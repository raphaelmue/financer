package de.raphaelmuesseler.financer.server.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    //XML based configuration
    private static SessionFactory sessionFactory;
    private static boolean isHostLocal = false;
    private static DatabaseName databaseName = DatabaseName.DEV;

    public static void setDatabaseName(DatabaseName databaseName) {
        HibernateUtil.databaseName = databaseName;
    }

    public static void setIsHostLocal(boolean isHostLocal) {
        HibernateUtil.isHostLocal = isHostLocal;
    };

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("/de/raphaelmuesseler/financer/server/db/config/hibernate" + (isHostLocal ? ".local" : "") + ".cfg.xml");
            String url = configuration.getProperty("hibernate.connection.url");
            configuration.setProperty("hibernate.connection.url", url.substring(0, url.indexOf('_') - 8) + databaseName.getName());

            // load mappings
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/category.hbm.xml");
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/fixed_transaction.hbm.xml");
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/fixed_transaction_amount.hbm.xml");
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/token.hbm.xml");
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/transaction.hbm.xml");
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/transaction_attachment.hbm.xml");
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/user.hbm.xml");
            configuration.addResource("/de/raphaelmuesseler/financer/shared/model/db/users_settings.hbm.xml");


            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }
}
