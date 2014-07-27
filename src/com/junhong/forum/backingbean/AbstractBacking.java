package com.junhong.forum.backingbean;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJBException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.auth.entity.UserGroup;
import com.junhong.auth.service.UserEjb;
import com.junhong.auth.service.UserGroupSingleEjb;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.util.StringUtil;

public abstract class AbstractBacking implements Serializable {

	private static final long			serialVersionUID	= -7273618348902968989L;

	@Inject
	private UserGroupSingleEjb			userGroupSingleEjb;

	@Inject
	private NavigationMapBackingBean	navigationMapBackingBean;
	@Inject
	private Logger						logger;

	@Inject
	protected UserEjb					userEjb;

	/*----------------business method-----------------*/
	public User getCurrentUser() {
		return (User) this.getSessionMap().get(Constants.CurrentUser);
	}

	public void setCurrentUser(User currentUser) {
		this.getSessionMap().remove(Constants.CurrentUser);
		if (null != currentUser) {
			this.getSessionMap().put(Constants.CurrentUser, currentUser);
		}
	}

	public boolean isUserLoggedIn() {
		return this.getSessionMap().containsKey("currentUser");
	}

	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public Application getApplication() {
		return getFacesContext().getApplication();
	}

	public Map<String, Object> getSessionMap() {
		return this.getFacesContext().getExternalContext().getSessionMap();
	}

	public Map<String, Object> getRequestMap() {
		return this.getFacesContext().getExternalContext().getRequestMap();
	}

	public Flash getFlash() {
		return getFacesContext().getExternalContext().getFlash();
	}

	public Object getRequestParameter(String key) {
		return this.getFacesContext().getExternalContext().getRequestParameterMap().get(key);
	}

	public Map<String, Object> getViewMap() {
		return FacesContext.getCurrentInstance().getViewRoot().getViewMap();
	}

	public Map<String, Object> getApplicationMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
	}

	/**
	 * when you get application exception during any phase, this will set the
	 * error message in the global messages and skip to render Response
	 * 
	 * @param errorMessKey
	 *            : the error message key
	 * @return TODO
	 */
	public String setBizErrorNSkipToResp(String errorMessKey) {
		FacesMessage message = Messages.getMessage("", errorMessKey, null, false);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, message);
		fc.renderResponse();

		return null;

	}

	public String setMessageNSkipToResp(String errorMessKey, Severity severity) {
		FacesMessage message = Messages.getMessage("", errorMessKey, null, false);
		if (null != severity) {
			message.setSeverity(severity);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, message);
		fc.renderResponse();

		return null;

	}

	public String setClientBizErrorNSkipToResp(String clientId, String errorMessKey) {
		FacesMessage message = Messages.getMessage("", errorMessKey, null, true);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(clientId, message);
		fc.renderResponse();

		return null;

	}

	public void addToGlobalMessages(String errorMessKey, Severity severity) {
		FacesMessage message = Messages.getMessage("", errorMessKey, null, false);
		if (null != severity) {
			message.setSeverity(severity);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, message);

	}

	public boolean processBusinessWithAuthorizationCheck(String action, AbstractEntity entity) {
		logger.debug("processBusinessWithAuthorizationCheck Action:---{} ------Entity type is: {}", action, entity.getClass().getName());
		boolean encountError = false;
		try {
			processBusiness(action, entity);
		} catch (AuthorizationFailException ex) {
			if (ex.getMessage().equalsIgnoreCase(Constants.NOTAUTHORIZEDACTION)) {
				this.setBizErrorNSkipToResp("notauthorizedaccess");
				encountError = true;
			}
		} catch (EJBException ex) {
			if (ex.getCause().getMessage().equalsIgnoreCase(Constants.NOTAUTHORIZEDACTION)) {
				this.setBizErrorNSkipToResp("notauthorizedaccess");
				encountError = true;
			}
		}
		return encountError;
	}

	public void processBusinessWithAuthorizationCheck(String action) {
		try {
			processBusiness(action);
		} catch (EJBException ex) {
			if (ex.getCause().getMessage().equalsIgnoreCase(Constants.NOTAUTHORIZEDACTION)) {
				this.setBizErrorNSkipToResp("notauthorizedaccess");
			}
		}
	}

	// it mainly contains calling EJB update/create/delete action
	abstract protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException;

	protected void processBusiness(String action) throws EJBException {

		// Empty on purpose
	}

	public boolean isCurrUserOwner(User user) {
		return getCurrentUser().equals(user);
	}

	/**
	 * find the usergroups based on given usergroup name
	 * 
	 * @param userGroupName
	 * @return
	 */
	public UserGroup findUserGroup(String userGroupName) {
		return userGroupSingleEjb.findUserGroup(userGroupName);
	}

	/*--------------------get/sett method-------------------------------*/

	public NavigationMapBackingBean getNavigationMapBackingBean() {
		return navigationMapBackingBean;
	}

	public void setNavigationMapBackingBean(NavigationMapBackingBean navigationMapBackingBean) {
		this.navigationMapBackingBean = navigationMapBackingBean;
	}

	/**
	 * set the message in the facesContext and skip to render phase.
	 * 
	 * @param resourceId
	 */
	public void setMessageAndRender(String resourceId) {
		FacesMessage message = Messages.getMessage("", resourceId, null);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext context1 = FacesContext.getCurrentInstance();
		context1.addMessage(null, message);
		context1.renderResponse();
	}

	public void setMessageAndRender(String resourceId, Object[] parms) {
		FacesMessage message = Messages.getMessage("", resourceId, null);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext context1 = FacesContext.getCurrentInstance();
		context1.addMessage(null, message);
		context1.renderResponse();
	}

	/**
	 * set the message in the facesContext and skip to render phase.
	 * 
	 * @param resourceId
	 */
	public void setMessageAndRender(String clientId, String resourceId) {
		FacesMessage message = Messages.getMessage("", resourceId, null);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		if (StringUtil.isNullOrBlank(clientId)) {
			clientId = null;
		}
		FacesContext context1 = FacesContext.getCurrentInstance();
		context1.addMessage(clientId, message);
		context1.renderResponse();
	}

	public void resetFirstDataTable() {
		this.getSessionMap().put(Constants.DATATABLE_FIRST, 0);
	}

	public String getWebApplicationContextPath() {
		String result = null;
		result = this.getFacesContext().getExternalContext().getRealPath("/");
		return result;
	}

}
