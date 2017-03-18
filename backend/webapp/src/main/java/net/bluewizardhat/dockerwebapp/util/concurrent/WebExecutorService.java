package net.bluewizardhat.dockerwebapp.util.concurrent;

import static net.bluewizardhat.dockerwebapp.util.concurrent.RunnableWrappers.wrapRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * A wrapper around an {@linkplain ExecutorService} for web methods.
 */
@Slf4j
@Component
public class WebExecutorService {

	/**
	 * If larger than zero this is the maximum number of threads that will be allocated
	 * to run background tasks. Otherwise use <code>maxThreadsPrProcessor</code> instead.
	 */
	@Value("${webapp.maxThreads:-1}")
	private int maxThreads;

	/**
	 * Maximum number of threads per processor that will be allocated to run background tasks.
	 * If <code>maxThreads</code> is set this will be ignored.
	 */
	@Value("${webapp.maxThreadsPrProcessor:4}")
	private int maxThreadsPrProcessor;

	private ExecutorService executorService;

	@PostConstruct
	private void postConstruct() {
		if (maxThreads > 0) {
			executorService = newCachedThreadPool(maxThreads);
			log.info("WebExecutorService initialized with maxThreads={}", maxThreads);
		} else {
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			int calculatedMaxThreads = availableProcessors * maxThreadsPrProcessor;
			executorService = newCachedThreadPool(calculatedMaxThreads);
			log.info("WebExecutorService initialized with availableProcessors={}, maxThreadsPrProcessor={}, maxThreads={}", availableProcessors, maxThreadsPrProcessor, calculatedMaxThreads);
		}
	}

	@PreDestroy
	private void preDestroy() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	/**
	 * A wrapper for {@linkplain ExecutorService#execute(Runnable)}
	 * @see ExecutorService#execute(Runnable)
	 */
	public void execute(Runnable runnable) {
		executorService.execute(runnable);
	}

	/**
	 * A wrapper for {@linkplain ExecutorService#execute(Runnable)} but the Runnable is first wrapped with
	 * RunnableWrappers.wrapRunnable.
	 *
	 * @see ExecutorService#execute(Runnable)
	 * @see RunnableWrappers#wrapRunnable(Runnable)
	 */
	public void executeWrapped(Runnable runnable) {
		executorService.execute(wrapRunnable(runnable));
	}

	/**
	 * Like <code>Executors.newCachedThreadPool()</code> but with a maximal number of threads.
	 *
	 * @see java.util.concurrent.Executors#newCachedThreadPool()
	 */
	private ExecutorService newCachedThreadPool(int maxThreads) {
		return new ThreadPoolExecutor(0, maxThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}
}
