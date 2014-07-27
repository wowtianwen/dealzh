/**
 * forum
 * zhanjung
 */
package com.junhong.auth.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.junhong.auth.dao.UserGroupDAO;
import com.junhong.auth.entity.UserGroup;

/**
 * @author zhanjung
 * 
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Startup
public class UserGroupSingleEjb {
	/* ------------instance Variable-------------- */
	@Inject
	private UserGroupDAO userGroupDAO;
	@Inject
	private Logger logger;

	private List<UserGroup> userGroups = new ArrayList<UserGroup>();

	/*--------------load the usergroups----------------*/
	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		logger.info("start loading usergroups.");
		userGroups = findUserGroups();
		if (logger.isDebugEnabled()) {
			for (UserGroup ug : userGroups) {
				logger.debug("-----------{}", ug.getName());
			}

		}
		logger.info("end loading usergroups.");
	}

	/**
	 * find the usergroups based on the given usergroup name
	 * 
	 * @param userGroupName
	 * @return
	 */
	public UserGroup findUserGroup(String userGroupName) {
		for (UserGroup userGroup : userGroups) {
			if (userGroup.getName().equalsIgnoreCase(userGroupName)) {
				return userGroup;
			}
		}
		return null;
	}

	/* -------------business logic----------------- */
	/**
	 * @return
	 */
	public List<UserGroup> findUserGroups() {
		return userGroupDAO.findAll(UserGroup.class);
	}

	/**
	 * @param userGroupId
	 * @return
	 */
	public UserGroup findUserGroupById(int userGroupId) {
		return userGroupDAO.findById(UserGroup.class, userGroupId);
	}

	/**
	 * @param userGroup
	 */
	public void createUserGroup(UserGroup userGroup) {
		userGroupDAO.create(userGroup);
	}

	/**
	 * @param userGroup
	 */
	public void updateUserGroup(UserGroup userGroup) {
		userGroupDAO.update(userGroup);
	}

	/**
	 * @param userGroup
	 */
	public void deleteUserGroup(UserGroup userGroup) {
		userGroupDAO.delete(userGroup);
	}

	/* -------------getter/setter----------------- */
}
