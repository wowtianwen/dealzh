package com.junhong.forum.backingbean;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.backingbean.MenuBean;
import com.junhong.forum.service.CacheManagerEjb;

@RequestScoped
@Named
public class CacheManagerBackingBean {
	@EJB
	private CacheManagerEjb	cacheManagerEjb;
	@Inject
	private MenuBean		menuBean;

	public CacheManagerBackingBean() {

	}

	public void flushAll() {
		cacheManagerEjb.flushAll();
		menuBean.init();
	}

}
