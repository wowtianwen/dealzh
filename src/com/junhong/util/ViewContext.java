package com.junhong.util;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class ViewContext implements Context {

	public Class<? extends Annotation> getScope() {
		return ViewScoped.class;
	}

	public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {

		Bean<T> bean = (Bean<T>) contextual;
		Map<String, T> viewMap = (Map<String, T>) getViewMap();
		if (viewMap.containsKey(bean.getName())) {
			return (T) viewMap.get(bean.getName());
		} else {
			T t = (T) bean.create(creationalContext);
			viewMap.put(bean.getName(), t);
			return t;
		}
	}

	private Map<String, Object> getViewMap() {
		FacesContext fctx = FacesContext.getCurrentInstance();
		if (fctx == null) {
			return null;
		}
		UIViewRoot viewRoot = fctx.getViewRoot();
		return viewRoot.getViewMap(true);
	}

	public <T> T get(Contextual<T> contextual) {
		Bean<T> bean = (Bean<T>) contextual;
		Map<String, T> viewMap = (Map<String, T>) getViewMap();
		if (null != viewMap && viewMap.containsKey(bean.getName())) {
			return (T) viewMap.get(bean.getName());
		}
		return null;
	}

	public boolean isActive() {
		return true;
	}

}
