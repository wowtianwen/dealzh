/**
 * forum
 * zhanjung
 */
package com.junhong.auth.backingbean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.auth.entity.UserGroup;
import com.junhong.auth.service.UserGroupSingleEjb;
import com.junhong.forum.backingbean.AbstractBacking;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.exceptions.AuthorizationFailException;

@ApplicationScoped
@Named
public class UserGroupBackingBean extends AbstractBacking {
	private static final long serialVersionUID = 01l;

	/* ------------instance Variable-------------- */

	@Inject
	private UserGroupSingleEjb userGroupEjb;

	private List<UserGroup> userGroups;

	/* -------------business logic----------------- */
	@PostConstruct
	private void init() {
		userGroups = userGroupEjb.findUserGroups();
	}

	public String save(UserGroup userGroup) {
		UserGroup tempUserGroup = new UserGroup(userGroup);
		userGroupEjb.createUserGroup(tempUserGroup);
		init();
		return null;
	}

	public void update(UserGroup UserGroup) {
		userGroupEjb.updateUserGroup(UserGroup);
	}

	public String delete(UserGroup UserGroup) {
		userGroupEjb.deleteUserGroup(UserGroup);
		init();
		return null;

	}

	public UserGroup getUserGroup(int id) {
		return userGroupEjb.findUserGroupById(id);
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub

	}

	/* -------------getter/setter----------------- */
}
