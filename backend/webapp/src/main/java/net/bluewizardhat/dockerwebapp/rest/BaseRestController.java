package net.bluewizardhat.dockerwebapp.rest;

import static net.bluewizardhat.dockerwebapp.util.concurrent.RunnableWrappers.wrapSecurityContext;
import static net.bluewizardhat.dockerwebapp.util.concurrent.RunnableWrappers.wrapThreadContext;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.context.request.async.DeferredResult;


public class BaseRestController {

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private static class ListenableFutureAdaptor<T> extends DeferredResult<T> {
		public ListenableFutureAdaptor(ListenableFuture<T> future) {
			future.addCallback(this::setResult, this::setErrorResult);
		}
	}

	protected <T> DeferredResult<T> submit(final Callable<T> callable) {
		final SettableListenableFuture<T> future = new SettableListenableFuture<>();

		executorService.execute(wrapRunnable(() -> {
			try {
				T result = callable.call();
				future.set(result);
			} catch (Exception e) {
				future.setException(e);
			}
		}));

		return new ListenableFutureAdaptor<>(future);
	}

	protected DeferredResult<Void> execute(final Runnable runnable) {
		final SettableListenableFuture<Void> future = new SettableListenableFuture<>();

		executorService.execute(wrapRunnable(() -> {
			try {
				runnable.run();
				future.set(null);
			} catch (Exception e) {
				future.setException(e);
			}
		}));

		return new ListenableFutureAdaptor<>(future);
	}

	private Runnable wrapRunnable(final Runnable runnable) {
		return wrapSecurityContext(wrapThreadContext(runnable));
	}

}
