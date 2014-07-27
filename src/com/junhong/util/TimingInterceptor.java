package com.junhong.util;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

@Timing
@Interceptor
public class TimingInterceptor {
	@Inject
	private Logger	logger;

	@AroundInvoke
	public Object calculateTiming(InvocationContext ic) throws Exception {
		Object result;
		String methodName = ic.getMethod().getName();
		long currTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("Start---------- {}", methodName);
		}
		result = ic.proceed();

		if (logger.isDebugEnabled()) {
			long timeAfterInvocation = System.currentTimeMillis();

			logger.debug("complete---------- {}", methodName);
			logger.debug("time for method {} is -------{}", methodName, timeAfterInvocation - currTime);
		}
		return result;

	}

}
