/**
 * forum
 * zhanjung
 */
package com.junhong.forum.common;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import com.junhong.forum.exceptions.AuthorizationFailException;

/**
 * @author zhanjung
 * 
 */
public class LoggingInterceptor {
	/* ------------instance Variable-------------- */
	@Inject
	private Logger	logger;

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
	@AroundInvoke
	public Object info(InvocationContext ic) throws Exception {
		String className = ic.getTarget().getClass().getName();
		String methodName = ic.getMethod().getName();
		long start = System.currentTimeMillis();
		logger.debug("start method at {}:{}-------------- to be called.", start, className + "." + methodName);
		try {
			return ic.proceed();
		} catch (AuthorizationFailException e) {
			throw e;
		} finally {
			long end = System.currentTimeMillis();
			logger.debug("end method at {} {}:{}-------------- completed.", end, className + "." + methodName);
			logger.debug("this method{} take {} millisecond ", className + "." + methodName, end - start);
		}

	}
}
