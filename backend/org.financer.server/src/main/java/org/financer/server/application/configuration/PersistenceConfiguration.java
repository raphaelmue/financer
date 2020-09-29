package org.financer.server.application.configuration;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceConfiguration.class);

    @Value("${financer.database.dialect}")
    private String dialect;

    @Value("${financer.database.driver}")
    private String driverClassName;

    @Value("${financer.database.url}")
    private String url;

    @Value("${financer.database.user}")
    private String user;

    @Value("${financer.database.password}")
    private String password;

    @Bean(name = "entityManagerFactory")
    @DependsOn("flyway")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("org.financer.server.domain.model");
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/hibernate.properties"));
            properties.setProperty("hibernate.dialect", dialect);
            sessionFactory.setHibernateProperties(properties);
        } catch (IOException e) {
            logger.error("Failed to load hibernate properties for instantiating data source.", e);
        }
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl("jdbc:" + url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        logger.info("Connecting to database '{}' with user '{}'.", url, user);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setDataSource(dataSource());
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
}
