package net.bluewizardhat.dockerwebapp.domain.logic;

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
public class AsyncTestService  {

	@LogInvocation(logParameterValues = true)
	@Async // ("domainLogicExecutor")
	public ListenableFuture<String> doSpringAsync(String someStrinh, @Sensitive String sensitiveString) {
		log.info("doSpringAsync: {}", SecurityContextHolder.getContext());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new AsyncResult<>("spring1\n");
	}

}
