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
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.Store;
import com.junhong.util.StringUtil;

/**
 * Session Bean implementation class ThreadEjb
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class StoreEjb {

	/*
	 * @Inject UserTransaction userTransaction;
	 */
	@Inject
	private GenericCRUDService<Store>	genericCRUDService;
	@Inject
	@GeneralDAOImplQualifier
	private EntityDAOImpl<Store>		entityDAO;

	@Inject
	Logger								logger;

	/**
	 * Default constructor.
	 */
	public StoreEjb() {
	}

	/** business method */

	public Store getStoreByDomain(String domain) {
		String hql = "select store from Store store where store.domain=?1";
		Store store = genericCRUDService.findByHQLSingleResult(hql, Store.class, domain);
		return store;
	}

	public Store getStoreById(int id) {
		return genericCRUDService.findById(Store.class, id);

	}

	/**
	 * @param thread
	 */
	public Store findByIdWithPessimisticWrite(int id) {
		return entityDAO.findByIdWithPessimisticWrite(Store.class, id);

	}

	public Store getStoreByName(String name) {
		String hql = "select store from Store store where store.name=?1";
		Store store = genericCRUDService.findByHQLSingleResult(hql, Store.class, name);
		return store;

	}

	public List<String> getStoreListByNameFilter(String query) {
		List<String> results = null;
		String hql = "select store.name from Store store where store.name like '%" + query + "%'";
		results = entityDAO.getEm().createQuery(hql, String.class).getResultList();
		return results;

	}

	/**
	 * get store by given advertiser id
	 * 
	 * @param advertiserId
	 * @return
	 */
	public Store getStoreByAdvertiserId(String advertiserId) {
		String hql = "select store from Store store where store.advertiserId=:advertiserId";
		List<Store> stores = entityDAO.getEm().createQuery(hql, Store.class).setParameter("advertiserId", advertiserId).getResultList();
		if (!stores.isEmpty()) {
			return stores.get(0);
		} else {
			return null;
		}

	}

	/**
	 * get given number of deals for given store
	 * 
	 * @param store
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ForumThread> loadLatestDeals(Store store, int start, int size) {
		List<ForumThread> results = null;
		String hql = "select deal from ForumThread deal where deal.store=:store order by createTime desc";
		TypedQuery<ForumThread> query = entityDAO.getEm().createQuery(hql, ForumThread.class);
		query.setParameter("store", store);
		results = query.setFirstResult(start).setMaxResults(size).getResultList();
		return results;

	}

	/**
	 * 
	 * @return
	 */
	public int getTotalStoreCount() {
		String hql = "select count(store) from Store store";
		TypedQuery<Long> query = entityDAO.getEm().createQuery(hql, Long.class);
		int totalCount = query.getSingleResult().intValue();
		return totalCount;
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void createStore(Store store) {
		store.setDomain(store.getDomain().toUpperCase());
		if (!StringUtil.isNullOrBlank(store.getGenericStoreLink())) {

		}
		genericCRUDService.create(store);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void removeStore(Store store) {
		genericCRUDService.delete(store);
	}

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void updateStore(Store store) {
		store.setDomain(store.getDomain().toUpperCase());
		genericCRUDService.update(store);
	}

	public void updateStoreWithAuthorization(Store store) {
		store.setDomain(store.getDomain().toUpperCase());
		genericCRUDService.update(store);
	}

	public List<Store> getStoreList(int start, int size) {
		List<Store> storeList = null;
		String hql = "select store from Store store order by store.name asc";
		storeList = entityDAO.getEm().createQuery(hql, Store.class).setFirstResult(start).setMaxResults(size).getResultList();
		return storeList;
	}

	public List<Store> getAllPopularStores() {
		String hql = "select store from Store store where store.popularOrder>0 order by store.popularOrder asc";
		return genericCRUDService.findByHQL(hql, Store.class, null);

	}

	public List<String> getStoreList(String query) {
		String hql = "select store.name from Store store where store.name like :prefix";
		return entityDAO.getEm().createQuery(hql, String.class).setParameter("prefix", "%" + query + "%").getResultList();
	}

}
