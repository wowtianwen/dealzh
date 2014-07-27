/**
 * 
 */
package com.junhong.forum.common;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhanjung
 * 
 */
@SuppressWarnings("serial")
public class LoggerUtil implements Serializable {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */

	@Produces
	Logger createLogger(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember()
				.getDeclaringClass());

	}
}
