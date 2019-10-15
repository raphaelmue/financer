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
    private enum Table {
        FIXED_TRANSACTIONS("fixed_transactions"),
        FIXED_TRANSACTIONS_AMOUNTS("fixed_transactions_amounts"),
        TRANSACTIONS("transactions"),
        TRANSACTIONS_ATTACHMENTS("transactions_attachments"),
        USERS("users"),
        USERS_CATEGORIES("users_categories"),
        USERS_TOKENS("users_tokens"),
        USERS_SETTINGS("users_settings");

        private String tableName;

        Table(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return tableName;
        }

        @Override
        public String toString() {
            return this.getTableName();
        }
    }

    //XML based configuration
    private static SessionFactory sessionFactory;
    private static boolean isHostLocal = false;
    private static DatabaseName databaseName = DatabaseName.DEV;

    public static void setDatabaseName(DatabaseName databaseName) {
        HibernateUtil.databaseName = databaseName;
    }

    public static void setIsHostLocal(boolean isHostLocal) {
        HibernateUtil.isHostLocal = isHostLocal;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("/de/raphaelmuesseler/financer/server/db/config/hibernate" + (isHostLocal ? ".local" : "") + ".cfg.xml")
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
            for (Table table : Table.values()) {
                String hql = String.format("truncate table %s", table.getTableName());
                Query query = session.createSQLQuery(hql);
                query.executeUpdate();
            }
            transaction.commit();
            session.close();
        } else {
            throw new IllegalArgumentException("It is only allowed to clean the test database");
        }
    }
}
