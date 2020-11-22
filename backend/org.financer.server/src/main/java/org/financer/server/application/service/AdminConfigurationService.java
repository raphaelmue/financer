package org.financer.server.application.service;

import org.financer.server.domain.model.user.User;
import org.financer.shared.domain.model.value.objects.SettingPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Currency;
import java.util.Properties;

@Service
@PropertySource("classpath:/configuration.properties")
@PropertySource(value = "file:${user.home}/.financer/config.properties", ignoreResourceNotFound = true)
public class AdminConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(AdminConfigurationService.class);

    @Value("${defaultLanguage}")
    private String defaultLanguage;

    @Value("${defaultCurrency}")
    private String defaultCurrency;

    @Value("${clientHost}")
    private String clientHost;

    public void setNewUsersDefaultSettings(User user) {
        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, this.defaultLanguage);
        user.putOrUpdateSettingProperty(SettingPair.Property.CURRENCY, this.defaultCurrency);
    }

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
        File propertiesFile = new File(System.getProperty("user.home") + "/.financer/config.properties");
        propertiesFile.getParentFile().mkdirs();
        try {
            propertiesFile.createNewFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        try (OutputStream out = new FileOutputStream(propertiesFile)) {
            DefaultPropertiesPersister p = new DefaultPropertiesPersister();
            p.store(getProperties(), out, "Financer: Custom Properties");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
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

