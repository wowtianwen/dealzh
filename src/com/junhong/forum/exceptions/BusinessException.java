/**
 * forum
 * zhanjung
 */
package com.junhong.forum.exceptions;

import javax.ejb.ApplicationException;

/**
 * @author zhanjung
 * 
 */
@ApplicationException
public abstract class BusinessException extends Exception {

	private static final long serialVersionUID = 4457053364140416743L;

	public BusinessException(String errorMessage) {
		super(errorMessage);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

}
