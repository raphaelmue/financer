package org.financer.server.application.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MigrationConfiguration {

    @Value("${financer.database.driver}")
    private String driverClassName;

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .locations("classpath:db/migration/" + getVendor())
                .load();
        flyway.migrate();
        return flyway;
    }

    private String getVendor() {
        if (this.driverClassName.contains("h2")) {
            return "h2";
        } else {
            return "mysql";
        }
    }
}
