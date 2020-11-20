package org.financer.server.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

@Service
@PropertySource("classpath:/configuration.properties")
@PropertySource(value = "file:${user.home}/.financer/config.properties", ignoreResourceNotFound = true)
public class AdminConfigurationService {

    @Value("${defaultLanguage}")
    private String defaultLanguage;

    @Value("${defaultCurrency}")
    private String defaultCurrency;

    @Value("${clientHost}")
    private String clientHost;

    public void resetProperties() {
        this.setDefaultLanguage("en");
        this.setDefaultCurrency("USD");

        this.updatePropertiesFile();

    }

    public AdminConfigurationService updateProperties(String defaultLanguage, String defaultCurrency) {
        this.setDefaultLanguage(defaultLanguage);
        this.setDefaultCurrency(defaultCurrency);

        this.updatePropertiesFile();

        return this;
    }

    private void updatePropertiesFile() {
        try (OutputStream out = new FileOutputStream(new File(System.getProperty("user.home") + "/.financer/config.properties"))) {
            DefaultPropertiesPersister p = new DefaultPropertiesPersister();
            p.store(getProperties(), out, "Financer: Custom Properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("defaultLanguage", this.defaultLanguage);
        properties.setProperty("defaultCurrency", this.defaultCurrency);
        properties.setProperty("clientHost", this.clientHost);
        return properties;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }
    
    public AdminConfigurationService setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return this;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public AdminConfigurationService setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
        return this;
    }

    public String getClientHost() {
        return clientHost;
    }

    public AdminConfigurationService setClientHost(String clientHost) {
        this.clientHost = clientHost;
        return this;
    }
}

