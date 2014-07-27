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
@ApplicationException(rollback = true)
public class AuthorizationFailException extends BusinessException {

	private static final long serialVersionUID = 7538989504174805410L;

	public AuthorizationFailException(String errorMessage) {
		super(errorMessage);
	}

	public AuthorizationFailException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
}
