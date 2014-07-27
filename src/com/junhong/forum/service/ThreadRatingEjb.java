package com.junhong.forum.service;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.junhong.forum.dao.ThreadRatingDAO;
import com.junhong.forum.entity.ThreadRating;

/**
 * Session Bean implementation class ThreadEjb
 */
/**
 * @author zhanjung
 * 
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ThreadRatingEjb extends GenericCRUDService<ThreadRating> {

	@Inject
	private ThreadRatingDAO	ratingDAO;

	/*
	 * @Inject UserTransaction userTransaction;
	 */

	@Inject
	Logger					logger;

	public boolean isExist(String userIdThreadId) {

		return ratingDAO.isExist(userIdThreadId);
	}

}
