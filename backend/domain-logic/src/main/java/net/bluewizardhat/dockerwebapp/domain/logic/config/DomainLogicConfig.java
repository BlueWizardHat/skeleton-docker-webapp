package net.bluewizardhat.dockerwebapp.domain.logic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import net.bluewizardhat.dockerwebapp.util.concurrent.ThreadPoolTaskExecutorFactory;

@EnableAsync
@Configuration
public class DomainLogicConfig {

	@Bean(name = "domainLogicExecutor1")
	public ThreadPoolTaskExecutor domainLogicExecutor1() {
		return ThreadPoolTaskExecutorFactory.threadPoolTaskExecutor(1, 20);
	}

	@Bean(name = "domainLogicExecutor2")
	public ThreadPoolTaskExecutor domainLogicExecutor2() {
		return ThreadPoolTaskExecutorFactory.threadPoolTaskExecutor(1, 20);
	}
}
