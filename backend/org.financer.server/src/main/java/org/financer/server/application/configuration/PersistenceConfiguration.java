package org.financer.server.application.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"org.financer.server.domain.repository"})
@EntityScan(basePackages = "org.financer.server.domain.model")
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

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("org.financer.server.domain.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @DependsOn("flyway")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("org.financer.server.domain.model");
        sessionFactory.setHibernateProperties(additionalProperties());

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
        JpaTransactionManager transactionManager = new JpaTransactionManager ();
        transactionManager.setDataSource(dataSource());
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/hibernate.properties"));
            properties.setProperty("hibernate.dialect", dialect);
        } catch (IOException e) {
            logger.error("Failed to load hibernate properties for instantiating data source.", e);
        }
        return properties;
    }
}
