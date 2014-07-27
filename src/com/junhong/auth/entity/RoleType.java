/**
 * forum
 * zhanjung
 */
package com.junhong.auth.entity;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * @author zhanjung
 * 
 */
@Named
@ApplicationScoped
public enum RoleType {

	GUEST("GUEST"), REGISTERED("REGISTERED"), CATEGORY_OWNER("CATEGORY_OWNNER"), SYSADMIN("SYSADMIN"), NEWSPOSTER("NEWSPOSTER");

	private RoleType(String roleName) {
		this.name = roleName;
	}

	private String			name;
	private List<UserGroup>	userGroups;

	public void addUserGroups(UserGroup userGroup) {
		userGroups.add(userGroup);
	}

	public void removeUserGroups(UserGroup userGroup) {
		userGroups.remove(userGroup);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
