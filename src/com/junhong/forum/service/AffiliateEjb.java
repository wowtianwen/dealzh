package com.junhong.forum.service;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.forum.entity.Affiliate;
import com.junhong.forum.entity.Store;

/**
 * Session Bean implementation class ThreadEjb
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AffiliateEjb {

	/*
	 * @Inject UserTransaction userTransaction;
	 */
	@Inject
	private GenericCRUDService<Affiliate>	genericCRUDService;

	@Inject
	Logger									logger;
	@Inject
	@GeneralDAOImplQualifier
	private EntityDAOImpl<Store>			entityDAO;

	/**
	 * Default constructor.
	 */
	public AffiliateEjb() {
	}

	/** business method */

	public Affiliate getaffiliateById(int id) {
		return genericCRUDService.findById(Affiliate.class, id);

	}

	/**
	 * get the number of stores for given affiliate
	 * 
	 * @param affiliate
	 * @return
	 */
	public int getNumberOfStores(Affiliate affiliate) {
		String hql = "select count(store) from Store store where store.affiliate=:affiliate";
		TypedQuery<Long> query = this.entityDAO.getEm().createQuery(hql, Long.class);
		query.setParameter("affiliate", affiliate);
		int totalCount = query.getSingleResult().intValue();
		return totalCount;
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void createaffiliate(Affiliate affiliate) {
		affiliate.setName(affiliate.getName().toUpperCase());
		genericCRUDService.create(affiliate);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void removeaffiliate(Affiliate affiliate) {
		genericCRUDService.delete(affiliate);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public Affiliate updateaffiliate(Affiliate affiliate) {
		affiliate.setName(affiliate.getName().toUpperCase());
		return genericCRUDService.update(affiliate);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public List<Affiliate> getAllAffiliates() {
		return genericCRUDService.findAll(Affiliate.class);
	}

	public List<Affiliate> getAllAffiliatesWithoutAuthorization() {
		return genericCRUDService.findAll(Affiliate.class);
	}

}
