package net.bluewizardhat.dockerwebapp.util.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
public class MethodNameMdcEnricherAspect {
	private static final String methodNameIdKey = "publicMethodName";

	@Pointcut("execution(public * *.*(..))")
	void anyPublicMethod() {}

	@Pointcut("execution(* net.bluewizardhat..*(..))")
	void withinLocalCode() {}

	@Around("withinLocalCode() && anyPublicMethod()")
	public Object methodNameInMdc(ProceedingJoinPoint jp) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) jp.getSignature();
		String methodName = methodSignature.getMethod().getName();
		String previousMethodName = MDC.get(methodNameIdKey);
		try {
			MDC.put(methodNameIdKey, methodName);
			return jp.proceed();
		} finally {
			restoreMdcValue(methodNameIdKey, previousMethodName);
		}
	}

	private void restoreMdcValue(String key, String previousValue) {
		if (previousValue == null) {
			MDC.remove(key);
		} else {
			MDC.put(key, previousValue);
		}
	}
}
