package net.bluewizardhat.dockerwebapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import net.bluewizardhat.dockerwebapp.util.concurrent.ThreadPoolTaskExecutorFactory;

@EnableAsync
@Configuration
public class AsyncConfig /* extends AsyncConfigurerSupport */ {

	@Value("${webapp.defaultCorePoolSize:50}")
	private int corePoolSize;

	@Value("${webapp.defaultMaxPoolSize:50}")
	private int maxPoolSize;

	/**
	 * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
	 * <p>Any positive value will lead to a LinkedBlockingQueue instance;
	 * any other value will lead to a SynchronousQueue instance.
	 * @see java.util.concurrent.LinkedBlockingQueue
	 * @see java.util.concurrent.SynchronousQueue
	 *
	 * Note that it is unwise to set this value higher than corePoolSize as new threads are only spawned if the queue is full.
	 */
	@Value("${webapp.defaultQueueCapacity:50}")
	private int queueCapacity;

	/**
	 * Default ThreadPoolTaskExecutor.
	 */
	@Primary
	@Bean(name = "defaultThreadPoolTaskExecutor")
	public ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
		return ThreadPoolTaskExecutorFactory.threadPoolTaskExecutor(corePoolSize, maxPoolSize, queueCapacity);
	}

	/*
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}
	*/
}
