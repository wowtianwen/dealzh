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
public class CategoryDuplicationException extends BusinessException {
	private static final long serialVersionUID = 01l;

	public CategoryDuplicationException(String errorMessage) {
		super(errorMessage);
	}

	public CategoryDuplicationException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

}
