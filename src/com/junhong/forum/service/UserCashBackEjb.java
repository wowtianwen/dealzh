package com.junhong.forum.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.junhong.auth.annotation.Role;
import com.junhong.auth.entity.RoleType;
import com.junhong.auth.entity.User;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.common.SendEmailService;
import com.junhong.forum.common.UserCashBackRecordStatus;
import com.junhong.forum.common.annotation.Added;
import com.junhong.forum.common.annotation.Removed;
import com.junhong.forum.common.annotation.Updated;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.forum.entity.UserCashBackHistory;

/**
 * Session Bean implementation class ThreadEjb
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserCashBackEjb {

	/*
	 * @Inject UserTransaction userTransaction;
	 */
	@Inject
	private GenericCRUDService<UserCashBackHistory>	genericCRUDService;

	@Inject
	Logger											logger;
	@Inject
	@GeneralDAOImplQualifier
	private EntityDAOImpl<UserCashBackHistory>		entityDAO;
	@Inject
	private SendEmailService						emailService;
	@Inject
	@Added
	private Event<UserCashBackHistory>				userCashBackAddedEvent;
	@Inject
	@Updated
	private Event<UserCashBackHistory>				userCashBackUpdatedEvent;
	@Inject
	@Removed
	private Event<UserCashBackHistory>				userCashBackRemovedEvent;

	/**
	 * Default constructor.
	 */
	public UserCashBackEjb() {
	}

	/** business method */

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void create(UserCashBackHistory userCashBackingHistory) {
		genericCRUDService.create(userCashBackingHistory);
		userCashBackAddedEvent.fire(userCashBackingHistory);
	}

	public void createWithoutAuthorization(UserCashBackHistory userCashBackingHistory) {
		genericCRUDService.create(userCashBackingHistory);
		userCashBackAddedEvent.fire(userCashBackingHistory);
	}

	public List<UserCashBackHistory> getPendingCashBack() {
		String hql = "select ucb from UserCashBackHistory ucb where ucb.isPendingCBConf=false and ucb.cashBackAmount>0 and ucb.transactionAmount>0  and ucb.status=?1 order by ucb.user";
		List<UserCashBackHistory> result = genericCRUDService.findByHQL(hql, UserCashBackHistory.class,
				new Object[] { UserCashBackRecordStatus.PENDING });
		return result;
	}

	/**
	 * get available cash back which are not sent email to user and cashbackamount/transactionamount>0
	 * 
	 * @return
	 */
	public List<UserCashBackHistory> getAvailableCashBack() {
		String hql = "select ucb from UserCashBackHistory ucb where ucb.isAvailCBConf=false and ucb.cashBackAmount!=0 and ucb.transactionAmount!=0  and ucb.status=?1 order by ucb.user";
		List<UserCashBackHistory> result = genericCRUDService.findByHQL(hql, UserCashBackHistory.class,
				new Object[] { UserCashBackRecordStatus.AVAILABLE });
		return result;
	}

	/**
	 * 
	 * @param userCashBackHistoryList
	 */
	public void updateIsPendingCBConf(List<UserCashBackHistory> userCashBackHistoryList) {
		String hql = "update UserCashBackHistory ucb set ucb.isPendingCBConf=true where ucb in(:ucbList))";
		this.entityDAO.getEm().createQuery(hql).setParameter("ucbList", userCashBackHistoryList).executeUpdate();
	}

	public void updateIsAvailCBConf(List<UserCashBackHistory> userCashBackHistoryList) {
		String hql = "update UserCashBackHistory ucb set ucb.isAvailCBConf=true where ucb in (:ucbList))";
		this.entityDAO.getEm().createQuery(hql).setParameter("ucbList", userCashBackHistoryList).executeUpdate();
	}

	/**
	 * send new pending cash back to users every day or run on demand
	 */
	@Schedule(dayOfWeek = "*", month = "*", year = "*", second = "0", minute = "*", hour = "23", persistent = false)
	public void sendPendingCashBackEmail() {
		List<UserCashBackHistory> newUserCashBackHisotryList = getPendingCashBack();
		List<UserCashBackHistory> ucbList = new ArrayList<UserCashBackHistory>();
		User currUser = null;
		User tempUser = null;
		// get the ucb for each user and sent them out
		for (UserCashBackHistory ucb : newUserCashBackHisotryList) {
			tempUser = ucb.getUser();
			if (null == currUser) {
				currUser = tempUser;
				ucbList.add(ucb);
			} else if (currUser.equals(tempUser)) {
				ucbList.add(ucb);
			} else {
				emailService.sendPendingCashBackLetter(currUser, ucbList);
				updateIsPendingCBConf(ucbList);
				currUser = tempUser;
				ucbList.clear();
				ucbList.add(ucb);
			}
		}

		if (!ucbList.isEmpty()) {
			emailService.sendPendingCashBackLetter(currUser, ucbList);
			updateIsPendingCBConf(ucbList);
		}

	}

	/**
	 * send new pending cash back to users every day or run on demand
	 */
	@Schedule(dayOfWeek = "*", month = "*", year = "*", second = "0", minute = "30", hour = "23", persistent = false)
	public void sendAvailableCashBackEmail() {
		List<UserCashBackHistory> availUserCashBackHisotryList = getAvailableCashBack();
		List<UserCashBackHistory> ucbList = new ArrayList<UserCashBackHistory>();
		User currUser = null;
		User tempUser = null;
		// get the ucb for each user and sent them out
		for (UserCashBackHistory ucb : availUserCashBackHisotryList) {
			tempUser = ucb.getUser();
			if (null == currUser) {
				currUser = tempUser;
				ucbList.add(ucb);
			} else if (currUser.equals(tempUser)) {
				ucbList.add(ucb);
			} else {
				emailService.sendAvailableCashBackLetter(currUser, ucbList);
				updateIsAvailCBConf(ucbList);
				currUser = tempUser;
				ucbList.clear();
				ucbList.add(ucb);
			}
		}

		if (!ucbList.isEmpty()) {
			emailService.sendAvailableCashBackLetter(currUser, ucbList);
			updateIsAvailCBConf(ucbList);
		}

	}

	/** business method */

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void update(UserCashBackHistory userCashBackingHistory) {
		userCashBackUpdatedEvent.fire(userCashBackingHistory);
		genericCRUDService.update(userCashBackingHistory);

	}

	/** business method */

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void remove(UserCashBackHistory userCashBackingHistory) {
		userCashBackRemovedEvent.fire(userCashBackingHistory);
		genericCRUDService.delete(userCashBackingHistory);

	}

	public GenericCRUDService<UserCashBackHistory> getGenericCRUDService() {
		return genericCRUDService;
	}

	public void setGenericCRUDService(GenericCRUDService<UserCashBackHistory> genericCRUDService) {
		this.genericCRUDService = genericCRUDService;
	}
}
