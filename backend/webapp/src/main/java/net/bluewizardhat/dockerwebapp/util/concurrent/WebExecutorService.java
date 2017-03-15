package net.bluewizardhat.dockerwebapp.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebExecutorService {

	@Value("${maxThreadsPrProcessor:4}")
	private int maxThreadsPrProcessor;

	private ExecutorService executorService;

	@PostConstruct
	private void postConstruct() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		int maxThreads = availableProcessors * maxThreadsPrProcessor;
		executorService = new ThreadPoolExecutor(0, maxThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		log.info("WebExecutorService initialized with availableProcessors={}, maxThreadsPrProcessor={}, maxThreads={}", availableProcessors, maxThreadsPrProcessor, maxThreads);
	}

	public void execute(Runnable runnable) {
		executorService.execute(runnable);
	}
}
