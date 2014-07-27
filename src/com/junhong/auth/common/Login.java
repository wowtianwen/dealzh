/**
 * forum zhanjung
 */
package com.junhong.auth.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import com.junhong.auth.entity.RoleType;
import com.junhong.auth.entity.User;
import com.junhong.auth.entity.UserStatus;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.backingbean.AbstractBacking;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.LoginType;
import com.junhong.forum.common.UserGroupAndRoleLoaderSingleton;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.Event;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.CategoryServiceSingleton;
import com.junhong.message.backingbean.MessageBackingBean;
import com.junhong.news.entity.NewsCategory;

/**
 * @author zhanjung
 * 
 */
@Named
@RequestScoped
public class Login extends AbstractBacking implements Serializable {
	private static final long				serialVersionUID	= 01l;

	/* ------------instance Variable-------------- */
	@Inject
	private Logger							logger;
	@Inject
	private User							user;

	@EJB
	private UserEjb							userEjb;
	@Inject
	private MessageBackingBean				messageBackingBean;

	@Inject
	private UserGroupAndRoleLoaderSingleton	userGroupRoleEjb;

	private boolean							isUserInGuestRole	= false;
	private boolean							isUserInRegistered	= false;
	private boolean							userInSysadmin		= false;
	@Inject
	private CategoryServiceSingleton		categoryEjb;

	/* -------------business logic----------------- */

	public String login() {
		boolean isSucceed = verifyUserLogin(this.user);
		if (!isSucceed) {
			return null;
		}

		// determine where to forward
		String loginOutcome = (String) this.getRequestMap().get("loginOutcome");
		logger.info("loginOutcome+++++++++++++{}", loginOutcome);
		if (null == loginOutcome || loginOutcome.equals(Constants.Blank_String)) {
			return Constants.NavSuccess;
		}
		return loginOutcome;
	}

	public boolean verifyUserLogin(User user) {
		boolean result = true;
		user = userEjb.getMatchingUser(user);
		if (user != null) {
			if (user.isLocked()) {
				this.setMessageAndRender("loginFailed_account_locked");
				result = false;
				return result;
			}
			if (user.getLoginType() != null && !user.getLoginType().equals(LoginType.SELF)) {
				this.setMessageAndRender("loginWithSocial", new Object[] { user.getLoginType() });
				result = false;
			} else {
				/*
				 * if (user.getStatus().equals(UserStatus.PENDING)) {
				 * this.setMessageAndRender("loginFailed_account_pending");
				 * result = false; return result; }
				 */
				initializeUserLogin(user);
				messageBackingBean.checkNumberOfNewMessages();
				// update lastLogin time
				user.setLastLogin(new Date());
				user.setOnline(true);
				userEjb.updateUser(user);
			}
		} else {
			this.setMessageAndRender("loginfailed");
			result = false;
		}
		return result;
	}

	/**
	 * check if user is active, not pending, then dont allow create any post,
	 * reply etc
	 * 
	 * @param userId
	 */
	public boolean checkIfUserActive(String userId) {
		boolean result = true;
		User user = userEjb.getUserByUserId(userId);
		if (user.getStatus().equals(UserStatus.PENDING)) {
			this.setMessageAndRender("loginFailed_account_pending");
			result = false;
		}
		return result;
	}

	public void initializeUserLogin(User user) {

		this.getSessionMap().put(Constants.CurrentUser, user);
		// set isUserInGuestRole variable
		checkUserInGuestRole();
		checkUserInRegistered();
		checkUserInSysadmin();
	}

	/**
	 * login method if the request is from login dialog this is a method called
	 * through ajax
	 */
	public void loginFromDiag() {
		boolean isSucceed = verifyUserLogin(this.user);
		// set the succeed flag
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", isSucceed);

	}

	/**
	 * check if user exist based on the userid and password
	 * 
	 * @param user
	 * @return
	 */
	public boolean verifyUser(String userId, String pwd) {

		User user = new User();
		user.setUserId(userId);
		user.setPassword(pwd);
		User matchingUser = userEjb.getMatchingUser(user);
		if (matchingUser != null) {
			return true;
		}
		return false;

	}

	/**
	 * log out.
	 * 
	 * @return
	 */
	public String logOut() {
		User user = getCurrentUser();
		if (null != user) {
			this.getSessionMap().remove(Constants.CurrentUser);
			user = userEjb.getUserById(user.getId());
			user.setOnline(false);
			userEjb.updateUser(user);
		}

		return Constants.NavLogout;
	}

	/**
	 * calculate next page based on where the login page from
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String calculateNextpage() {
		String currentLocation = (String) this.getSessionMap().get(Constants.CURRENT_LOCATION);
		if (currentLocation != null) {

			if (currentLocation.equalsIgnoreCase(Constants.POSTTHREAD_LOCATION)) {
				return "/thread/createthread.xhtml";
			} else if (currentLocation.equalsIgnoreCase(Constants.POSTREPLY_LOCATION)) {
				return "/reply/createreply.xhtml";
			}
		}

		return Constants.NavSuccess;
	}

	/**
	 * 
	 * @param user
	 * @param roleType
	 * @return
	 */
	public boolean isUserinRole(User user, RoleType roleType) {
		if (null == user || user.getUserGroup() == null) {
			return false;
		}

		List<String> userGroups = userGroupRoleEjb.getUserGroup(roleType.toString().toUpperCase());

		String userGroupName = user.getUserGroup().getName();
		return userGroups.contains(userGroupName);

	}

	/**
	 * check if the current user is in the given role
	 * 
	 * @param roleType
	 * @return
	 */
	public boolean isCurrentUserInRole(String roleType) {
		if (roleType == null || roleType.length() == 0) {
			return false;
		}
		RoleType roleT = RoleType.GUEST;
		if (RoleType.NEWSPOSTER.toString().equalsIgnoreCase(roleType)) {
			roleT = RoleType.NEWSPOSTER;
		} else if (RoleType.REGISTERED.toString().equalsIgnoreCase(roleType)) {
			roleT = RoleType.REGISTERED;
		} else if (RoleType.SYSADMIN.toString().equalsIgnoreCase(roleType)) {
			roleT = RoleType.SYSADMIN;
		} else if (RoleType.CATEGORY_OWNER.toString().equalsIgnoreCase(roleType)) {
			roleT = RoleType.CATEGORY_OWNER;
		}
		User currUser = this.getCurrentUser();
		if (currUser != null) {
			return isUserinRole(currUser, roleT);
		}
		return false;

	}

	/**
	 * 
	 * @param fromLocation
	 * @return outpage could be (createthread,createreply etc)
	 */
	public void verifyLoggedIn(String outcomePage) {
		boolean isLoggedIn = isUserLoggedIn();
		if (!isLoggedIn) {
			this.getRequestMap().put("loginOutcome", outcomePage);
			/**
			 * NavigationHandler navHandler =
			 * this.getApplication().getNavigationHandler();
			 * navHandler.handleNavigation(this.getFacesContext(), null,
			 * Constants.Login_Page); this.getFacesContext().renderResponse();
			 */
			try {
				this.getFacesContext().getExternalContext().dispatch("/login.xhtml");
			} catch (IOException e) {
				logger.error("Can not forward to login.xhtml due to ", e);
			}
		}

	}

	public String forwardToLogin(String outcomePage) {
		this.getRequestMap().put("loginOutcome", outcomePage);
		return Constants.SUCCESS;
	}

	public String initEnroll() {
		this.getSessionMap().put(Constants.LOGINTYPE, Constants.LOGIN_TYPE_SELF);
		return Constants.SUCCESS;
	}

	public String initLogin() {
		this.getSessionMap().put(Constants.LOGINTYPE, Constants.LOGIN_TYPE_SELF);
		return Constants.SUCCESS;
	}

	/**
	 * @return the current user
	 */
	public User getCurrentUser() {
		return (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.CurrentUser);
	}

	/**
	 * check if the current user is in Guest User Group
	 */
	private void checkUserInGuestRole() {
		isUserInGuestRole = isUserinRole(getCurrentUser(), RoleType.GUEST);
	}

	/**
	 * check if the current user is in Guest Register
	 */
	private void checkUserInRegistered() {
		isUserInRegistered = isUserinRole(getCurrentUser(), RoleType.REGISTERED);
	}

	/**
	 * check if the current user is in sysadmin User Group
	 */
	private void checkUserInSysadmin() {
		this.userInSysadmin = isUserinRole(getCurrentUser(), RoleType.SYSADMIN);
	}

	/* -------------getter/setter----------------- */
	public boolean isUserInGuestRole() {
		return isUserInGuestRole;
	}

	public boolean isUserInRegistered() {
		return isUserInRegistered;
	}

	public boolean isUserCategoryOwner() {
		return isUserinRole(getCurrentUser(), RoleType.CATEGORY_OWNER);

	}

	public boolean isUserInSysadmin() {
		return isUserinRole(getCurrentUser(), RoleType.SYSADMIN);
	}

	public void setUserInGuestRole(boolean isUserInGuestRole) {
		this.isUserInGuestRole = isUserInGuestRole;
	}

	public void setUserInRegistered(boolean isUserInRegistered) {
		this.isUserInRegistered = isUserInRegistered;
	}

	public void setUserInSysadmin(boolean isUserInSysadmin) {
		this.userInSysadmin = isUserInSysadmin;
	}

	public boolean isUserLoggedIn() {
		return this.getCurrentUser() != null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * check if the current user is the owner of the category the user is in
	 * right now. the category will vary, depends on where the user is
	 * 
	 * @param category
	 * @return
	 */
	public boolean isUserCategory_Owner(ForumCategory category) {
		if (!isUserLoggedIn()) {
			return false;
		}
		return this.getCurrentUser().equals(category.getOwner());
	}

	public boolean isUserThread_Owner(ForumThread thread) {
		if (!isUserLoggedIn()) {
			return false;
		}
		return this.getCurrentUser().equals(thread.getOwner());
	}

	public boolean isUserEvent_Owner(Event event) {
		if (!isUserLoggedIn()) {
			return false;
		}
		return this.getCurrentUser().equals(event.getOwner());
	}

	public boolean isUserInNewsCategory_Owner(NewsCategory category) {
		if (!isUserLoggedIn()) {
			return false;
		}
		if (null != category) {
			return this.getCurrentUser().equals(category.getCategoryOwner());
		} else {
			return false;
		}
	}

	public boolean IsUserStickThread(ForumThread thread) {
		return userEjb.getUserWithEagerStickyThread(this.getCurrentUser().getId()).getStickyThreads().contains(thread);

	}

	/**
	 * get the current user's stikcy threads based on the given category
	 * 
	 * @param category
	 * @return
	 */

	public List<ForumThread> getStickyThreadsByCategory(ForumCategory category) {
		List<ForumThread> stickyThreads = new ArrayList<ForumThread>();
		List<ForumThread> allStikcyThreads = userEjb.getUserWithEagerStickyThread(this.getCurrentUser().getId()).getStickyThreads();
		for (ForumThread thread : allStikcyThreads) {
			if (thread.getCategory().equals(category)) {
				stickyThreads.add(thread);

			}
		}

		return stickyThreads;
	}

	public List<ForumThread> getUserAllStickyThreads(User user) {
		List<ForumThread> allStikcyThreads = userEjb.getUserWithEagerStickyThread(this.getCurrentUser().getId()).getStickyThreads();

		return allStikcyThreads;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub

	}

	/**
	 * check if the given user is the current logged in user;
	 * 
	 * @param user
	 * @return
	 */
	public boolean isCurrLogInUser(User user) {
		User currUser = (User) this.getSessionMap().get(Constants.CurrentUser);
		if (user != null && currUser != null && currUser.equals(user)) {
			return true;
		}
		return false;

	}

	/**
	 * check the given user is the owner of any catgories
	 * 
	 * @param user
	 * @return
	 */
	public boolean isUserOwnerOfAnyCategory(User user) {
		boolean result = false;
		if (user != null) {
			Collection<ForumCategory> categories = categoryEjb.getCategoryOfThreadMap().values();
			for (ForumCategory category : categories) {
				if (category.getOwner() != null) {
					if (user.equals(category.getOwner())) {
						result = true;
						break;
					}
				}

			}
		}
		return result;
	}
}
