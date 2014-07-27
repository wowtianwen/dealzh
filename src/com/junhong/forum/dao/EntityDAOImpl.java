/**
 * 
 */
package com.junhong.forum.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.service.GeneralDAOImplQualifier;

/**
 * @author zhanjung
 * 
 */

@GeneralDAOImplQualifier
public class EntityDAOImpl<T> implements EntityDAO<T> {

	@PersistenceContext(unitName = "forumPU", type = PersistenceContextType.TRANSACTION)
	private EntityManager	em;

	/*-----------CURD  Operation---------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.junhong.forum.dao.EntityDAO#create(java.lang.Object)
	 */
	@Override
	public void create(T t) {
		em.persist(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.junhong.forum.dao.EntityDAO#update(java.lang.Object)
	 */
	@Override
	public T update(T t) {
		return em.merge(t);
	}

	@Override
	public T findById(Class<T> entityType, int id) {
		return (T) em.find(entityType, id);
	}

	@Override
	public <k> k findByIdParmMethod(Class<k> entityType, int id) {
		return (k) em.find(entityType, id);
	}

	@Override
	public T findById(Class<T> entityType, Object id) {
		return (T) em.find(entityType, id);
	}

	@Override
	public T findByIdWithPessimisticWrite(Class<T> entityType, int id) {

		Map<String, Object> props = new HashMap<String, Object>();
		props.put("javax.persistence.lock.timeout", 10000);
		return em.find(entityType, id, LockModeType.PESSIMISTIC_WRITE, props);
	}

	@Override
	public void delete(T t) {
		t = em.merge(t);
		em.remove(t);
	}

	@Override
	public void refresh(T t) {
		em.refresh(t);

	}

	@Override
	public List<T> findAll(Class<T> entityType) {
		return em.createNamedQuery(entityType.getSimpleName().toLowerCase() + ".all", entityType).getResultList();
	}

	@Override
	public void executeUpdate(String updateStatement) {
		em.createQuery(updateStatement).executeUpdate();
	}

	@Override
	public List<T> findByHQL(String hql, Class<T> entityType, int start, int size) {
		return em.createQuery(hql, entityType).setFirstResult(start).setMaxResults(size).getResultList();
	}

	@Override
	public List<T> findByHQL(String hql, Class<T> entityType, int start, int size, Object... parms) {
		TypedQuery<T> tq = em.createQuery(hql, entityType);

		int position = 1;
		for (Object parm : parms) {
			tq.setParameter(position, parm);
			position++;
		}
		List<T> result = tq.setFirstResult(start).setMaxResults(size).getResultList();
		return result;
	}

	@Override
	public List<T> findByHQL(String hql, Class<T> entityType) {
		return em.createQuery(hql, entityType).getResultList();
	}

	@Override
	public List<T> findByHQL(String hql, Class<T> entityType, Object... parms) {
		try {
			TypedQuery<T> tq = em.createQuery(hql, entityType);

			int position = 1;
			if (parms != null) {
				for (Object parm : parms) {
					tq.setParameter(position, parm);
					position++;
				}
			}
			return tq.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public T findByHQLSingleResult(String hql, Class<T> entityType) {
		try {
			return em.createQuery(hql, entityType).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public <k> k findByHQLSingleResultParmMethod(String hql, Class<k> entityType, Object... parms) {
		try {
			TypedQuery<k> tq = em.createQuery(hql, entityType);

			int position = 1;
			for (Object parm : parms) {
				tq.setParameter(position, parm);
				position++;
			}
			return tq.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public <k> k findByHQLSingleResultParmMethod(String hql, Class<k> entityType) {
		try {
			TypedQuery<k> tq = em.createQuery(hql, entityType);
			return tq.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public T findByHQLSingleResult(String hql, Class<T> entityType, Object... parms) {
		try {
			TypedQuery<T> tq = em.createQuery(hql, entityType);

			int position = 1;
			for (Object parm : parms) {
				tq.setParameter(position, parm);
				position++;
			}
			return tq.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public <U> U findByCriteriaQuery(Class<U> U, Class<T> entityType) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<U> query = builder.createQuery(U);
		return em.createQuery(query).getSingleResult();
	}

	/*-----------getter setter---------------*/

	public static void main(String[] args) {

		EntityDAOImpl<ForumCategory> dao = new EntityDAOImpl<ForumCategory>();
		List<ForumCategory> cLit = dao.findAll(ForumCategory.class);
		System.out.println(cLit);

	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

}
