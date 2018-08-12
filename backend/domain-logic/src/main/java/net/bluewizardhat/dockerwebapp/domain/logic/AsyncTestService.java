package net.bluewizardhat.dockerwebapp.domain.logic;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AsyncTestService  {

	public String doManuallyAsync() {
		log.info("doManuallyAsync: {}", SecurityContextHolder.getContext());
		return "manual\n";
	}

	@Async("domainLogicExecutor1")
	public ListenableFuture<String> doSpringAsync1() {
		log.info("doSpringAsync1: {}", SecurityContextHolder.getContext());
		return new AsyncResult<>("spring1\n");
	}

	@Async("domainLogicExecutor2")
	public ListenableFuture<String> doSpringAsync2() {
		log.info("doSpringAsync1: {}", SecurityContextHolder.getContext());
		return new AsyncResult<>("spring2\n");
	}
}
