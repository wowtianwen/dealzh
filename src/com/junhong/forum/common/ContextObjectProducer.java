package com.junhong.forum.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Qualifier;

import com.junhong.auth.entity.User;

public class ContextObjectProducer {

	@Produces
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	@Produces
	public Application getApplication() {
		return getFacesContext().getApplication();
	}

	@Produces
	public Map<String, Object> getApplicationMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
	}

	@Produces
	public Map<String, Object> getSessionMap() {
		return this.getFacesContext().getExternalContext().getSessionMap();
	}

	@Produces
	public Map<String, Object> getViewMap() {
		return FacesContext.getCurrentInstance().getViewRoot().getViewMap();
	}

	@Produces
	public Map<String, Object> getRequestMap() {
		return this.getFacesContext().getExternalContext().getRequestMap();
	}

	@Produces
	public Flash getFlash() {
		return getFacesContext().getExternalContext().getFlash();
	}

	public Object getRequestParameter(String key) {
		return this.getFacesContext().getExternalContext().getRequestParameterMap().get(key);
	}

	@Produces
	@CurrentUser
	public User getCurrentUser() {

		Object currUser = this.getSessionMap().get(Constants.CurrentUser);
		if (currUser == null) {
			return null;
		}
		return (User) currUser;

	}

	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
	public @interface CurrentUser {

	}

}
