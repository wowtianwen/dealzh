package com.junhong.forum.service;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.junhong.forum.dao.EntityDAOImpl;

@Stateless
@LocalBean
public class GenericCRUDService<T> {
	@Inject
	@GeneralDAOImplQualifier
	private EntityDAOImpl<T>	entityDAO;

	public void create(T t) {
		entityDAO.create(t);
	}

	public T update(T t) {
		return entityDAO.update(t);
	}

	public T findById(Class<T> entityType, int id) {
		return entityDAO.findById(entityType, id);
	}

	public T findById(Class<T> entityType, Object id) {
		return entityDAO.findById(entityType, id);
	}

	public void delete(T t) {
		entityDAO.delete(t);
	}

	public void refresh(T t) {
		entityDAO.refresh(t);
	}

	public List<T> findAll(Class<T> entityType) {
		return entityDAO.findAll(entityType);

	}

	public T findByHQLSingleResult(String hql, Class<T> entityType) {
		return entityDAO.findByHQLSingleResult(hql, entityType);
	}

	public T findByHQLSingleResult(String hql, Class<T> entityType, Object... parms) {

		return entityDAO.findByHQLSingleResult(hql, entityType, parms);
	}

	public List<T> findByHQL(String hql, Class<T> entityType, Object... parms) {
		return entityDAO.findByHQL(hql, entityType, parms);
	}

	public List<T> findByHQL(String hql, Class<T> entityType, int start, int size, Object... parms) {
		return entityDAO.findByHQL(hql, entityType, start, size, parms);
	}

	public List<T> findByHQL(String hql, Class<T> entityType, int start, int size) {
		return entityDAO.findByHQL(hql, entityType, start, size);
	}

	public <k> k findByHQLSingleResultParmMethod(String hql, Class<k> entityType) {
		return entityDAO.findByHQLSingleResultParmMethod(hql, entityType);
	}

	public <k> k findByHQLSingleResultParmMethod(String hql, Class<k> entityType, Object... parms) {
		return entityDAO.findByHQLSingleResultParmMethod(hql, entityType,parms);
	}

}
