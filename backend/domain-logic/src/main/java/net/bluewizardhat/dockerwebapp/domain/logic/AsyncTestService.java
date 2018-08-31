package net.bluewizardhat.dockerwebapp.domain.logic;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.util.logging.LogInvocation;
import net.bluewizardhat.dockerwebapp.util.logging.Sensitive;

@Slf4j
@Component
@LogInvocation
public class AsyncTestService  {

	@Async("domainLogicExecutor")
	public ListenableFuture<String> doSpringAsync(String someStrinh, @Sensitive String sensitiveString) {
		log.info("doSpringAsync: {}", SecurityContextHolder.getContext());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new AsyncResult<>("spring1\n");
	}

	@Async("domainLogicExecutor")
	public void logMdcContentsMeow() {
		Map<String, String> mdcCopy = new TreeMap<>(MDC.getCopyOfContextMap());
		log.info("MDC contains {} entries; security context {}", mdcCopy.size(), SecurityContextHolder.getContext());
		mdcCopy.forEach((key, value) -> log.info("MDC key='{}', value='{}'", key, value));
		//log.info("Trace", new RuntimeException("Trace"));
	}

	public void throwException() {
		//throw new IllegalArgumentException("test");
		throw new RuntimeException(new IOException(new IllegalStateException("test")));
	}
}
