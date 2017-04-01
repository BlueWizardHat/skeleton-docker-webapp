package net.bluewizardhat.dockerwebapp.util.concurrent;

import static java.lang.Math.max;
import static net.bluewizardhat.dockerwebapp.util.concurrent.RunnableWrappers.wrapRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.exception.ServerBusyException;

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

	/**
	 * Number of acceptable jobs waiting in queue to be processed, relative to the maximum number of threads
	 * allocated to run background tasks. Note: The actual number of max waiting jobs will never be less than
	 * the number of processors in the system.
	 */
	@Value("${webapp.maxWaitingJobsPercent:25}")
	private int maxWaitingJobsPercent;

	private AtomicInteger waitingJobs = new AtomicInteger(0);
	private ExecutorService executorService;
	private int actualMaxThreads;
	private int actualMaxWaitingJobs;

	@PostConstruct
	private void postConstruct() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		if (maxThreads > 0) {
			actualMaxThreads = max(availableProcessors, maxThreads);
			actualMaxWaitingJobs = max(availableProcessors, actualMaxThreads * maxWaitingJobsPercent / 100);
			executorService = newCachedThreadPool(actualMaxThreads);
			log.info("WebExecutorService initialized with maxThreads={}, maxWaitingJobs={}", actualMaxThreads, actualMaxWaitingJobs);
		} else {
			Assert.isTrue(maxThreadsPrProcessor > 0, "maxThreadsPrProcessor must be larger than zero");
			actualMaxThreads = availableProcessors * maxThreadsPrProcessor;
			actualMaxWaitingJobs = max(availableProcessors, actualMaxThreads * maxWaitingJobsPercent / 100);
			executorService = newCachedThreadPool(actualMaxThreads);
			log.info("WebExecutorService initialized with availableProcessors={}, maxThreadsPrProcessor={}, maxThreads={}, maxWaitingJobs={}", availableProcessors, maxThreadsPrProcessor, actualMaxThreads, actualMaxWaitingJobs);
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
		if (waitingJobs.get() >= actualMaxWaitingJobs) {
			throw new ServerBusyException();
		}

		waitingJobs.incrementAndGet();
		executorService.execute(() -> {
			waitingJobs.decrementAndGet();
			runnable.run();
		});
	}

	/**
	 * A wrapper for {@linkplain ExecutorService#execute(Runnable)} but the Runnable is first wrapped with
	 * RunnableWrappers.wrapRunnable.
	 *
	 * @see ExecutorService#execute(Runnable)
	 * @see RunnableWrappers#wrapRunnable(Runnable)
	 */
	public void executeWrapped(Runnable runnable) {
		execute(wrapRunnable(runnable));
	}

	/**
	 * Like <code>Executors.newCachedThreadPool()</code> but with a maximum number of threads.
	 *
	 * @see java.util.concurrent.Executors#newCachedThreadPool()
	 */
	private ExecutorService newCachedThreadPool(int maxThreads) {
		return new ThreadPoolExecutor(0, maxThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}
}
