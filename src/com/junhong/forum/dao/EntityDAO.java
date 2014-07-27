/**
 * 
 */
package com.junhong.forum.dao;

import java.util.List;

/**
 * @author zhanjung
 * 
 * @param <T>
 */
public interface EntityDAO<T> {

	/**
	 * @param t
	 */
	public void create(T t);

	/**
	 * @param t
	 */
	public T update(T t);

	/**
	 * @param entityType
	 * @param id
	 * @return
	 */
	public T findById(Class<T> entityType, int id);

	public T findByIdWithPessimisticWrite(Class<T> entityType, int id);

	/**
	 * 
	 * @param entityType
	 * @param id
	 * @return
	 */
	public T findById(Class<T> entityType, Object id);

	/**
	 * @param t
	 */
	public void delete(T t);

	/**
	 * @param t
	 */
	public void refresh(T t);

	/**
	 * @param entityType
	 * @return
	 */
	public List<T> findAll(Class<T> entityType);

	/**
	 * @param updateStatement
	 */
	public void executeUpdate(String updateStatement);

	/**
	 * @param hql
	 * @param entityType
	 * @return
	 */
	public List<T> findByHQL(String hql, Class<T> entityType);

	public T findByHQLSingleResult(String hql, Class<T> entityType);

	public T findByHQLSingleResult(String hql, Class<T> entityType, Object... parms);

	public List<T> findByHQL(String hql, Class<T> entityType, Object... parms);

	public List<T> findByHQL(String hql, Class<T> entityType, int start, int size, Object... parms);

	/**
	 * @param hql
	 * @param entityType
	 * @param start
	 * @param size
	 * @return
	 */
	public List<T> findByHQL(String hql, Class<T> entityType, int start, int size);

	/**
	 * @param <U>
	 * @param U
	 * @param entityType
	 * @return
	 */
	public <U> U findByCriteriaQuery(Class<U> U, Class<T> entityType);

	public <k> k findByHQLSingleResultParmMethod(String hql, Class<k> entityType);

	public <k> k findByHQLSingleResultParmMethod(String hql, Class<k> entityType, Object... parms);

	public <k> k findByIdParmMethod(Class<k> entityType, int id);

}
