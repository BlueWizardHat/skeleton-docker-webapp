package net.bluewizardhat.dockerwebapp.database.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "net.bluewizardhat.dockerwebapp.database")
public class PersistenceConfig {
}
