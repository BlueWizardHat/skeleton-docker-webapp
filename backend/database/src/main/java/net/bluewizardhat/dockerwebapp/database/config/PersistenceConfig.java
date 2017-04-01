package net.bluewizardhat.dockerwebapp.database.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "net.bluewizardhat.dockerwebapp.database")
@EnableJpaRepositories(basePackages = "net.bluewizardhat.dockerwebapp.database")
public class PersistenceConfig {
}
