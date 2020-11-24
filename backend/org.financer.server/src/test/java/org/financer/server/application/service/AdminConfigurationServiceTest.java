package org.financer.server.application.service;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.MigrationConfiguration;
import org.financer.server.application.configuration.PersistenceConfiguration;
import org.financer.server.domain.service.UserDomainService;
import org.financer.server.utils.SpringTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FinancerServer.class, AdminConfigurationService.class, AuthenticationService.class, PersistenceConfiguration.class, MigrationConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminConfigurationServiceTest extends SpringTest {

    @MockBean
    private UserDomainService userDomainService;

    @Autowired
    private AdminConfigurationService adminConfigurationService;

    @Test
    public void testUpdateProperties() throws IOException {
        adminConfigurationService.resetProperties();

        assertThat(adminConfigurationService.getDefaultLanguage()).isEqualTo("en");
        assertThat(adminConfigurationService.getDefaultCurrency()).isEqualTo("USD");

        adminConfigurationService.updateProperties("de", "EUR");

        assertThat(adminConfigurationService.getDefaultLanguage()).isEqualTo("de");
        assertThat(adminConfigurationService.getDefaultCurrency()).isEqualTo("EUR");

        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getProperty("user.home") + "/.financer/config.properties"));
        assertThat(properties.get("defaultLanguage")).isEqualTo("de");
        assertThat(properties.get("defaultCurrency")).isEqualTo("EUR");
    }


    @Test
    public void testResetProperties() throws IOException {
        adminConfigurationService.updateProperties("de", "EUR");

        assertThat(adminConfigurationService.getDefaultLanguage()).isEqualTo("de");
        assertThat(adminConfigurationService.getDefaultCurrency()).isEqualTo("EUR");

        adminConfigurationService.resetProperties();

        assertThat(adminConfigurationService.getDefaultLanguage()).isEqualTo("en");
        assertThat(adminConfigurationService.getDefaultCurrency()).isEqualTo("USD");

        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getProperty("user.home") + "/.financer/config.properties"));
        assertThat(properties.get("defaultLanguage")).isEqualTo("en");
        assertThat(properties.get("defaultCurrency")).isEqualTo("USD");
    }
}
