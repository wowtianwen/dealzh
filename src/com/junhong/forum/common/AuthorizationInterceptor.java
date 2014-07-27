/**
 * forum
 * zhanjung
 */
package com.junhong.forum.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.junhong.auth.annotation.OwnerCheck;
import com.junhong.auth.annotation.Role;
import com.junhong.auth.common.Login;
import com.junhong.auth.entity.RoleType;
import com.junhong.auth.entity.User;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.news.entity.Comment;
import com.junhong.news.entity.News;

/**
 * @author zhanjung
 * 
 */
public class AuthorizationInterceptor {
	/* ------------instance Variable-------------- */
	@Inject
	private Login login;

	/* -------------business logic----------------- */

	@AroundInvoke
	public Object authorize(InvocationContext ic) throws Exception {
		assert ic != null : "Invocation Context was not specified";
		Object target = ic.getTarget();
		boolean authorized = false;
		// Method[] methods = target.getClass().getDeclaredMethods();
		// Role role = target.getClass().getAnnotation(Role.class);

		Method method = ic.getMethod();
		List<Role> roles = new ArrayList<Role>();

		// Entity owner check
		OwnerCheck ownerCheck = method.getAnnotation(OwnerCheck.class);
		if (ownerCheck != null) {
			Object param = ic.getParameters()[0];
			User owner = null;

			if (param instanceof ForumThread) {
				owner = ((ForumThread) param).getOwner();
			} else if (param instanceof ForumReply) {
				owner = ((ForumReply) param).getOwner();
			} else if (param instanceof News) {
				owner = ((News) param).getOwner();
			} else if (param instanceof Comment) {
				owner = ((Comment) param).getOwner();
			}
			if (owner != null && owner.equals(login.getCurrentUser())) {
				authorized = true;
			}
		}

		// user in Role check
		if (!authorized) {

			Role.List roleList = method.getAnnotation(Role.List.class);
			if (roleList != null) {
				for (Role r : roleList.value()) {
					roles.add(r);
				}
			} else {
				Role role = method.getAnnotation(Role.class);
				if (role != null) {
					roles.add(role);
				}

			}
			if (roles.size() > 0) {
				// call isUserInRole
				for (Role r : roles) {
					RoleType roleType = r.value();
					/*
					 * check if current user is the owner of the current
					 * category
					 */
					User catOwner = null;
					if (roleType.equals(RoleType.CATEGORY_OWNER)) {
						Object param = ic.getParameters()[0];
						if (param instanceof News) {
							catOwner = ((News) param).getNewsCategory().getCategoryOwner();

						} else if (param instanceof Comment) {
							catOwner = ((Comment) param).getNews().getNewsCategory().getCategoryOwner();
							// catOwner =
							// threadBackingBean.getBelongingCategory().getOwner();

						} else if (param instanceof ForumThread) {
							catOwner = ((ForumThread) param).getCategory().getOwner();
							// catOwner =
							// threadBackingBean.getBelongingCategory().getOwner();

						} else if (param instanceof ForumReply) {
							catOwner = ((ForumReply) param).getThread().getCategory().getOwner();
							// catOwner =
							// threadBackingBean.getBelongingCategory().getOwner();

						}
						if (login.getCurrentUser().equals(catOwner)) {
							authorized = true;
							break;

						}

					}
					// check other roles except category_owner role
					else if (login.isUserinRole(
							((User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.CurrentUser)),
							roleType)) {
						authorized = true;
						break;

					}

				}
			}

			if (!authorized) {
				throw new AuthorizationFailException(Constants.NOTAUTHORIZEDACTION);
			}

		}

		return ic.proceed();

	}
	/* -------------getter/setter----------------- */
}
