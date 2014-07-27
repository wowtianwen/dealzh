/**
 * forum
 * zhanjung
 */
package com.junhong.auth.entity;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.junhong.forum.entity.AbstractEntity;

/**
 * @author zhanjung
 * 
 */
@Named
@RequestScoped
@Entity
@Table(name = "usergroup")
@NamedQueries({ @NamedQuery(name = "usergroup.all", query = "select c from UserGroup as c") })
public class UserGroup extends AbstractEntity {
	private static final long	serialVersionUID	= 01l;

	/* ------------instance Variable-------------- */

	@Column(nullable = false)
	private String				name;
	private String				description;

	@OneToMany(mappedBy = "userGroup")
	private List<User>			users;

	// Role-UserGroup mapping will be loaded from xml file
	@Transient
	private List<RoleType>		roleTypes;

	/*--------------constructor------------------------------*/

	public UserGroup() {

	}

	public UserGroup(UserGroup tempUserGroup) {
		this.setName(tempUserGroup.getName());
		this.setDescription(tempUserGroup.getDescription());
	}

	/* -------------business logic----------------- */

	/* -------------getter/setter----------------- */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<RoleType> getRoles() {
		return roleTypes;
	}

	public void setRoles(List<RoleType> roles) {
		this.roleTypes = roles;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserGroup other = (UserGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
