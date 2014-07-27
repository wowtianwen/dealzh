package com.junhong.auth.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.Context;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.LoginType;
import com.junhong.forum.common.RecordStatus;
import com.junhong.forum.common.SendEmailService;
import com.junhong.forum.common.UserCashBackRecordStatus;
import com.junhong.forum.common.annotation.Added;
import com.junhong.forum.common.annotation.Removed;
import com.junhong.forum.common.annotation.Updated;
import com.junhong.forum.dao.UserDAO;
import com.junhong.forum.entity.CashBackWithdrawRequest;
import com.junhong.forum.entity.OrderInquery;
import com.junhong.forum.entity.UserCashBackHistory;
import com.junhong.forum.service.GenericCRUDService;
import com.junhong.forum.service.UserCashBackEjb;
import com.junhong.util.CommonUtil;
import com.junhong.util.EncryptionUtil;
import com.junhong.util.StringUtil;
import com.junhong.util.WebConfigUtil;

/**
 * Session Bean implementation class UserEjb
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserEjb {

	@Inject
	private UserDAO										userDAO;
	@Inject
	private SendEmailService							emailService;
	@EJB
	private GenericCRUDService<CashBackWithdrawRequest>	cashBackWithdrawRequestEjb;
	@EJB
	private UserCashBackEjb								userCashBackHistoryEjb;
	@EJB
	private GenericCRUDService<OrderInquery>			orderInqueryEjb;

	@EJB
	private UserCashBackEjb								userCashBackEjb;

	@Inject
	Logger												logger;

	/**
	 * Default constructor.
	 */
	public UserEjb() {
	}

	/*
	 * @PostConstruct public void init() { ((EntityDAOImpl) userDAO).setEm(em); }
	 */

	/**
	 * @return
	 */
	public List<User> getUsers() {
		return userDAO.findAll(User.class);
	}

	/**
	 * @param userId
	 * @return
	 */
	public User getUserById(int userId) {
		return userDAO.findById(User.class, userId);
	}

	/**
	 * @param user
	 */
	public void createUser(User user) {
		String activationCode = RandomStringUtils.randomAlphanumeric(20);
		user.setActivationCode(activationCode);
		user.setActivationCodeCreTime(new Date());
		userDAO.create(user);
		// send activation email
		// emailService.sendRegActivationLetter(user);

	}

	/**
	 * this method is for user registration, it will generate activation code and send email to user note. this is different from createuser, which is
	 * used to create user based on minimum information
	 * 
	 * @param user
	 */
	public void enrollUser(User user) {
		String activationCode = RandomStringUtils.randomAlphanumeric(20);
		user.setActivationCode(activationCode);
		user.setActivationCodeCreTime(CommonUtil.getESTDate(new Date()));
		userDAO.create(user);
		// create reg bonus user cash back history
		UserCashBackHistory regBonusUCBH = createRegBonusUserCashBackHistory(user);
		user.setRegBonusUCBHId(regBonusUCBH.getId());

		// create reg bonus if refer user
		UserCashBackHistory refererRegBonusUCBH = createRegBonusUserCashBackHistory4Refer(user);
		if (refererRegBonusUCBH != null) {
			user.setRefererRegBonusUCBHId(refererRegBonusUCBH.getId());
		}
		updateUser(user);
		// send activation email
		emailService.sendRegActivationLetter(user);
	}

	/**
	 * create registration bonus when new user is created
	 * 
	 * @param user
	 */
	public UserCashBackHistory createRegBonusUserCashBackHistory(User user) {

		UserCashBackHistory regBonusUserCashBack = new UserCashBackHistory();
		regBonusUserCashBack.setTransactionDate(CommonUtil.getCurrentDate());
		regBonusUserCashBack.setTransactionId(Constants.REG_BONUS);
		// default to $5
		String regBonusAmount = "5";
		if (WebConfigUtil.getProp("REGBONUSAMOUNT") != null) {
			regBonusAmount = WebConfigUtil.getProp("REGBONUSAMOUNT");
		}
		regBonusUserCashBack.setCashBackAmount(new BigDecimal(regBonusAmount));
		regBonusUserCashBack.setTransactionAmount(new BigDecimal(regBonusAmount));
		regBonusUserCashBack.setStatus(UserCashBackRecordStatus.PENDING);
		regBonusUserCashBack.setAffiliateNetWork(Constants.REG_BONUS);
		regBonusUserCashBack.setCommissionAmount(BigDecimal.ZERO);
		regBonusUserCashBack.setProfit(regBonusUserCashBack.getCommissionAmount().subtract(regBonusUserCashBack.getCashBackAmount()));
		regBonusUserCashBack.setUser(user);
		userCashBackHistoryEjb.createWithoutAuthorization(regBonusUserCashBack);
		return regBonusUserCashBack;
	}

	public UserCashBackHistory createRegBonusUserCashBackHistory4Refer(User user) {
		UserCashBackHistory result = null;
		String refererId = user.getRefererId();
		User referer = null;
		if (!StringUtil.isNullOrBlank(refererId)) {
			try {
				int refererIdInt = Integer.parseInt(refererId);
				referer = this.getUserById(refererIdInt);
			} catch (NumberFormatException nfexp) {
				logger.warn("Invalid referer Id {} for current user registration ", user.getRefererId(), user.getId());
				return result;
			}

		}
		if (referer != null) {
			result = createRegBonusUserCashBackHistory(referer);
		}
		return result;

	}

	public void enrollUser4Social(User user) {
		userDAO.create(user);

		// create reg bonus user cash back history
		UserCashBackHistory regBonusUCBH = createRegBonusUserCashBackHistory(user);
		user.setRegBonusUCBHId(regBonusUCBH.getId());
		// create reg bonus if refer user
		UserCashBackHistory refererRegBonusUCBH = createRegBonusUserCashBackHistory4Refer(user);
		if (refererRegBonusUCBH != null) {
			user.setRefererRegBonusUCBHId(refererRegBonusUCBH.getId());
		}
		updateUser(user);
	}

	/**
	 * resend Registration Activation code
	 * 
	 * @param userId
	 * @return
	 */
	public String resendRegActivationCode(String userId) {
		User tempUser = this.getUserByUserId(userId);
		// login.initializeUserLogin(tempUser);
		String activationCode = RandomStringUtils.randomAlphanumeric(20);
		tempUser.setActivationCode(activationCode);
		tempUser.setActivationCodeCreTime(CommonUtil.getESTDate(new Date()));
		userDAO.update(tempUser);
		// send activation email
		emailService.sendRegActivationLetter(tempUser);
		return Constants.NavSuccess;
	}

	/**
	 * check if if given email already exist
	 * 
	 * @param email
	 * @return
	 */
	public boolean checkEmailExistence(String email) {
		boolean result = false;
		String hql = "select user from User user where exists ( select 1 from User u where u=user and u.email=:email)";
		List<User> usersWithGivenEmail = userDAO.getEm().createQuery(hql, User.class).setParameter("email", email).getResultList();
		if (usersWithGivenEmail != null && usersWithGivenEmail.size() > 0) {
			result = true;
		}
		return result;

	}

	/**
	 * @param user
	 */
	public void updateUser(User user) {
		userDAO.update(user);
	}

	/**
	 * @param user
	 */
	public void deleteUser(User user) {
		userDAO.delete(user);
	}

	/**
	 * @param userId
	 * @return
	 */
	public boolean isExist(String userId) {
		return getUserByUserId(userId) != null;
	}

	/**
	 * @param userId
	 * @return
	 */
	public User getUserByUserId(String userId) {
		// return
		// userDAO.findByHQLSingleResult("select user from User user where user.userId='"
		// + userId + "'", User.class);

		String query = "select user from User user where user.userId=?1";
		Object[] parms = { userId };
		return userDAO.findByHQLSingleResult(query, User.class, parms);
	}

	public void refreshUser(User user) {
		userDAO.refresh(user);

	}

	/**
	 * @param user
	 * @return the matching user if found based on userid otherwise, return null;
	 */
	public User getMatchingUser(User user) {
		String md5Pwd = EncryptionUtil.md5(user.getPassword());
		// get the first user
		User userTemp = getUserByUserId(user.getUserId());
		if (null != userTemp) {
			if (null != userTemp.getPassword() && userTemp.getPassword().equals(md5Pwd)) {
				return userTemp;
			}
		}
		return null;
	}

	/**
	 * get all the userIds based on the prefix.
	 * 
	 * @param prefix
	 * @return
	 */
	public List<String> getUserIds(String prefix) {
		return userDAO.getUserIds(prefix);
	}

	public User getUserWithEagerStickyThread(int id) {
		User user = userDAO.findById(User.class, id);
		user.getStickyThreads().size();

		return user;
	}

	/**
	 * update the number of Reply for the given User
	 * 
	 * @param user
	 * @param delta
	 */
	public void updateNumOfReplies(User user, int delta) {
		User currUser = this.getUserById(user.getId());
		currUser.setNumOfReplies(currUser.getNumOfReplies() + delta);
		this.updateUser(currUser);
	}

	public void updateNumOfPosts(User user, int delta) {
		User currUser = this.getUserById(user.getId());
		currUser.setNumOfPosts(currUser.getNumOfPosts() + delta);
		this.updateUser(currUser);
	}

	/**
	 * get the userCashBackHistory counts
	 * 
	 * @param user
	 * @return
	 */
	public int getTotalNumberOfUserCashBacks(User user, UserCashBackRecordStatus status, Map<String, Object> filters) {
		int result = 0;
		StringBuilder hql = new StringBuilder(
				"select count(ucbh) from UserCashBackHistory ucbh where user=:user and status=:status and transactionAmount is not null and transactionAmount!=0 ");
		if (!filters.isEmpty()) {
			hql.append(" and ");
		}
		String filterProperty;
		Object filterValue;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("transactionDate")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("transactionId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("store.name")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("user.userId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		TypedQuery<Long> query = this.userDAO.getEm().createQuery(hql.toString(), Long.class);
		query.setParameter("user", user);
		query.setParameter("status", status);
		result = query.getSingleResult().intValue();
		return result;
	}

	/**
	 * check if user has any transactions
	 * 
	 * @param user
	 * @return
	 */
	public boolean doesUserHasTransactions(User user, UserCashBackHistory userCashBackHistory) {
		StringBuilder hql = new StringBuilder(
				"select ucbh from UserCashBackHistory ucbh where user=:user and transactionId!=:regBonus and  transactionAmount is not null and transactionAmount!=0 and id!=:id");
		TypedQuery<UserCashBackHistory> query = this.userDAO.getEm().createQuery(hql.toString(), UserCashBackHistory.class)
				.setParameter("regBonus", Constants.REG_BONUS).setParameter("id", userCashBackHistory.getId()).setMaxResults(1);
		query.setParameter("user", user);
		return !query.getResultList().isEmpty();
	}

	/**
	 * get cashback history for the given user
	 * 
	 * @param user
	 * @param start
	 * @param size
	 * @return
	 */
	public List<UserCashBackHistory> getUserCashBackHistory(User user, int start, int size, UserCashBackRecordStatus status,
			Map<String, Object> filters) {

		List<UserCashBackHistory> result = null;
		String filterProperty;
		Object filterValue;
		StringBuilder hql = new StringBuilder(
				"select ucbh from UserCashBackHistory ucbh where user=:user and status=:status and  transactionAmount is not  null and transactionAmount!=0 ");
		if (!filters.isEmpty()) {
			hql.append(" and ");
		}
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("transactionDate")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("transactionId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("store.name")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("user.userId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		hql.append(" order by createTime desc");
		TypedQuery<UserCashBackHistory> query = this.userDAO.getEm().createQuery(hql.toString(), UserCashBackHistory.class);
		query.setParameter("user", user);
		query.setParameter("status", status);
		query.setFirstResult(start).setMaxResults(size);
		result = query.getResultList();
		return result;

	}

	public int getTotalNumberOfUserCashBacksForAllUsers(UserCashBackRecordStatus status, Map<String, Object> filters) {
		int result = 0;
		StringBuilder hql = new StringBuilder("select count(ucbh) from UserCashBackHistory ucbh ");
		if (!filters.isEmpty()) {
			hql.append(" where ");
		}
		String filterProperty;
		Object filterValue;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("transactionDate")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("transactionId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("store.name")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("user.userId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("status")) {
				hql.append(filterProperty).append(" = '").append(filterValue).append("'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		TypedQuery<Long> query = this.userDAO.getEm().createQuery(hql.toString(), Long.class);
		result = query.getSingleResult().intValue();
		return result;
	}

	public List<UserCashBackHistory> getUserCashBackHistoryForAllUsers(int start, int size, UserCashBackRecordStatus status,
			Map<String, Object> filters) {

		List<UserCashBackHistory> result = null;
		StringBuilder hql = new StringBuilder("select ucbh from UserCashBackHistory ucbh  ");
		if (!filters.isEmpty()) {
			hql.append(" where ");
		}
		String filterProperty;
		Object filterValue;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("transactionDate")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("transactionId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("store.name")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("user.userId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("status")) {
				hql.append(filterProperty).append(" = '").append(filterValue).append("'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		hql.append(" order by createTime desc");
		TypedQuery<UserCashBackHistory> query = this.userDAO.getEm().createQuery(hql.toString(), UserCashBackHistory.class);
		query.setFirstResult(start).setMaxResults(size);
		result = query.getResultList();
		return result;

	}

	/**
	 * @param thread
	 */
	public User findByIdWithPessimisticWrite(int id) {
		return userDAO.findByIdWithPessimisticWrite(User.class, id);

	}

	public User findByIdWithPessimisticWrite(String userId) {
		User user = this.getUserByUserId(userId);
		return userDAO.findByIdWithPessimisticWrite(User.class, user.getId());

	}

	public String createCashbackWithdrawRequest(CashBackWithdrawRequest cashBackWithdrawRequest) {
		cashBackWithdrawRequestEjb.create(cashBackWithdrawRequest);
		return Constants.NavSuccess;
	}

	public void createCashbackWithdrawRequest(User user, CashBackWithdrawRequest cashBackWithdrawRequest) {
		cashBackWithdrawRequest.setStatus(RecordStatus.PENDING);
		cashBackWithdrawRequest.setUser(user);
		cashBackWithdrawRequestEjb.create(cashBackWithdrawRequest);
		user = this.findByIdWithPessimisticWrite(user.getId());
		user.setAvailableAmount(user.getAvailableAmount().subtract(cashBackWithdrawRequest.getWithdrawAmount()));
		updateUser(user);
	}

	public void updateCashbackWithdrawRequest(BigDecimal totalAmount, CashBackWithdrawRequest cashBackWithdrawRequest, User user) {
		user.setAvailableAmount(totalAmount.subtract(cashBackWithdrawRequest.getWithdrawAmount()));
		if (cashBackWithdrawRequest.getStatus().equals(RecordStatus.PROCESSED)) {
			user.setPaidAmount(user.getPaidAmount().add(cashBackWithdrawRequest.getWithdrawAmount()));
		}
		this.updateUser(user);
		cashBackWithdrawRequestEjb.update(cashBackWithdrawRequest);
	}

	public void deleteCashBackWithdrawRequest(User user, CashBackWithdrawRequest cashBackWithdrawRequest) {
		user = this.findByIdWithPessimisticWrite(user.getId());
		user.setAvailableAmount(user.getAvailableAmount().add(cashBackWithdrawRequest.getWithdrawAmount()));
		updateUser(user);
		cashBackWithdrawRequestEjb.delete(cashBackWithdrawRequest);
	}

	public CashBackWithdrawRequest getCashBackWithdrawRequestById(int id) {
		return cashBackWithdrawRequestEjb.findById(CashBackWithdrawRequest.class, id);
	}

	/**
	 * update user cash back history record and update user's pending amount
	 * 
	 * @param cashBackHistory
	 */
	public void updateCashBackAvailable(UserCashBackHistory cashBackHistory) {

		// update userid
		User newUser = getUserByUserId(cashBackHistory.getUser().getUserId());
		cashBackHistory.setUser(newUser);
		userCashBackHistoryEjb.update(cashBackHistory);

		// status changed to available
		/*
		 * UserCashBackRecordStatus status = cashBackHistory.getStatus(); UserCashBackRecordStatus oldStatus = oldCashBackHistory.getStatus(); User
		 * user = cashBackHistory.getUser(); if (oldStatus.equals(UserCashBackRecordStatus.PENDING) &&
		 * status.equals(UserCashBackRecordStatus.AVAIABLE)) { // update pending amount and avaiable amount // update cashback history record user =
		 * this.findByIdWithPessimisticWrite(user.getId()); user.setPendingAmount (user.getPendingAmount().subtract(cashBackHistory
		 * .getCashBackAmount())); user.setAvailableAmount(user.getAvailableAmount ().add(cashBackHistory.getCashBackAmount())); updateUser(user);
		 * UserCashBackHistoryEjb.update(cashBackHistory);
		 * 
		 * } else if (oldStatus.equals(UserCashBackRecordStatus.AVAIABLE) && status.equals(UserCashBackRecordStatus.PENDING)) { user =
		 * this.findByIdWithPessimisticWrite(user.getId()); user.setPendingAmount (user.getPendingAmount().add(cashBackHistory.getCashBackAmount()));
		 * user .setAvailableAmount(user.getAvailableAmount().subtract(cashBackHistory .getCashBackAmount())); updateUser(user);
		 * UserCashBackHistoryEjb.update(cashBackHistory); }
		 */

	}

	public void createUserCashBackHisotry(UserCashBackHistory userCashBackHistory) {
		// create user cash back entity
		userCashBackEjb.createWithoutAuthorization(userCashBackHistory);

	}

	/**
	 * when user cash back history is created, the its user pending amount will be updated
	 * 
	 * @param userCashBackHistory
	 */
	public void HandleUserCashBackHistoryAddEvent(@Observes @Added UserCashBackHistory userCashBackHistory) {
		// update user's pending amount
		User user = userCashBackHistory.getUser();
		user = findByIdWithPessimisticWrite(user.getId());
		BigDecimal pendingAmt = user.getPendingAmount().add(userCashBackHistory.getCashBackAmount());
		user.setPendingAmount(pendingAmt);
		// make reg bonus and referer reg bonus available
		// userCashBackHistory.getTransactionId() has reg bonus
		if (!userCashBackHistory.getTransactionId().equalsIgnoreCase(Constants.REG_BONUS)) {
			updateUserNRefererRegBonusAvailable(user);
		}
		updateUser(user);
	}

	/**
	 * when user get the first order, then make its reg bonus available
	 * 
	 * @param user
	 */
	private void updateUserNRefererRegBonusAvailable(User user) {
		if (user == null) {
			return;
		}
		// make user's and referers reg bonus available
		int regBonusUCBHId = user.getRegBonusUCBHId();
		GenericCRUDService<UserCashBackHistory> genCRUDService = userCashBackEjb.getGenericCRUDService();
		UserCashBackHistory regBonusUCBH = genCRUDService.findById(UserCashBackHistory.class, regBonusUCBHId);
		if (regBonusUCBH != null && regBonusUCBH.getStatus().equals(UserCashBackRecordStatus.PENDING)) {
			regBonusUCBH.setStatus(UserCashBackRecordStatus.AVAILABLE);
			genCRUDService.update(regBonusUCBH);
			user.setPendingAmount(user.getPendingAmount().subtract(regBonusUCBH.getCashBackAmount()));
			user.setAvailableAmount(user.getAvailableAmount().add(regBonusUCBH.getCashBackAmount()));
			updateUser(user);
		}
		int refererBonusUCBHId = user.getRefererRegBonusUCBHId();
		UserCashBackHistory refererBonusUCBH = userCashBackEjb.getGenericCRUDService().findById(UserCashBackHistory.class, refererBonusUCBHId);
		if (refererBonusUCBH != null && refererBonusUCBH.getStatus().equals(UserCashBackRecordStatus.PENDING)) {
			refererBonusUCBH.setStatus(UserCashBackRecordStatus.AVAILABLE);
			genCRUDService.update(refererBonusUCBH);
			User referer = refererBonusUCBH.getUser();
			referer.setPendingAmount(referer.getPendingAmount().subtract(refererBonusUCBH.getCashBackAmount()));
			referer.setAvailableAmount(referer.getAvailableAmount().add(refererBonusUCBH.getCashBackAmount()));
			updateUser(referer);
		}

	}

	/**
	 * update reg bonus and referer reg bonus pending if user does not have any transations
	 * 
	 * @param user
	 */
	private void updateUserNRefererRegBonusPending(User user, UserCashBackHistory userCashBackHistory) {
		if (user == null) {
			return;
		}
		if (doesUserHasTransactions(user, userCashBackHistory)) {
			return;
		}
		// make user's and referers reg bonus available
		int regBonusUCBHId = user.getRegBonusUCBHId();
		GenericCRUDService<UserCashBackHistory> genCRUDService = userCashBackEjb.getGenericCRUDService();
		UserCashBackHistory regBonusUCBH = genCRUDService.findById(UserCashBackHistory.class, regBonusUCBHId);
		if (regBonusUCBH != null && regBonusUCBH.getStatus().equals(UserCashBackRecordStatus.AVAILABLE)) {
			regBonusUCBH.setStatus(UserCashBackRecordStatus.PENDING);
			genCRUDService.update(regBonusUCBH);
			user.setPendingAmount(user.getPendingAmount().add(regBonusUCBH.getCashBackAmount()));
			user.setAvailableAmount(user.getAvailableAmount().subtract(regBonusUCBH.getCashBackAmount()));
			updateUser(user);
		}
		int refererBonusUCBHId = user.getRefererRegBonusUCBHId();
		UserCashBackHistory refererBonusUCBH = userCashBackEjb.getGenericCRUDService().findById(UserCashBackHistory.class, refererBonusUCBHId);

		if (refererBonusUCBH != null && refererBonusUCBH.getStatus().equals(UserCashBackRecordStatus.AVAILABLE)) {
			refererBonusUCBH.setStatus(UserCashBackRecordStatus.PENDING);
			genCRUDService.update(refererBonusUCBH);
			User referer = refererBonusUCBH.getUser();
			referer.setPendingAmount(referer.getPendingAmount().add(refererBonusUCBH.getCashBackAmount()));
			referer.setAvailableAmount(referer.getAvailableAmount().subtract(refererBonusUCBH.getCashBackAmount()));
			updateUser(referer);

		}

	}

	/**
	 * when user cash back history is created, the its user pending amount will be updated this need to be fired before the usercashbackhistory is
	 * updated to database
	 * 
	 * @param userCashBackHistory
	 */
	public void HandleUserCashBackHistoryUpdatedEvent(@Observes @Updated UserCashBackHistory cashBackHistory) {
		UserCashBackHistory oldCashBackHistory = userDAO.findByIdParmMethod(UserCashBackHistory.class, cashBackHistory.getId());
		// skip if transaction amount is blank

		// remove old cashbackamount from old user whether user id is changed
		User oldUser = oldCashBackHistory.getUser();
		oldUser = this.findByIdWithPessimisticWrite(oldUser.getId());
		if (oldCashBackHistory.getStatus().equals(UserCashBackRecordStatus.PENDING)) {
			BigDecimal pendingAmount = oldUser.getPendingAmount();
			oldUser.setPendingAmount(pendingAmount.subtract(oldCashBackHistory.getCashBackAmount()));

		} else if (oldCashBackHistory.getStatus().equals(UserCashBackRecordStatus.AVAILABLE)) {
			BigDecimal availableAmount = oldUser.getAvailableAmount();
			oldUser.setAvailableAmount(availableAmount.subtract(oldCashBackHistory.getCashBackAmount()));
		}

		// update the new users pending amount and available amount whether user
		// id is changed
		User newUser = cashBackHistory.getUser();
		if (!oldUser.equals(newUser)) {
			newUser = this.findByIdWithPessimisticWrite(newUser.getUserId());
			if (!oldCashBackHistory.getTransactionId().equalsIgnoreCase(Constants.REG_BONUS)) {
				updateUserNRefererRegBonusPending(oldUser, oldCashBackHistory);
			}
			updateUser(oldUser);
		} else {
			newUser = oldUser;
		}
		cashBackHistory.setCashBackAmount(cashBackHistory.getTransactionAmount().multiply(cashBackHistory.getCashBackPercent())
				.setScale(2, RoundingMode.HALF_DOWN));
		cashBackHistory.setProfit(cashBackHistory.getCommissionAmount().subtract(cashBackHistory.getCashBackAmount())
				.setScale(2, RoundingMode.HALF_DOWN));
		if (cashBackHistory.getStatus().equals(UserCashBackRecordStatus.PENDING)) {
			BigDecimal pendingAmount = newUser.getPendingAmount();
			newUser.setPendingAmount(pendingAmount.add(cashBackHistory.getCashBackAmount()).setScale(2, RoundingMode.HALF_DOWN));

		} else if (cashBackHistory.getStatus().equals(UserCashBackRecordStatus.AVAILABLE)) {
			BigDecimal availableAmount = newUser.getAvailableAmount();
			newUser.setAvailableAmount(availableAmount.add(cashBackHistory.getCashBackAmount()).setScale(2, RoundingMode.HALF_DOWN));
		}
		if (!cashBackHistory.getTransactionId().equalsIgnoreCase(Constants.REG_BONUS)) {
			updateUserNRefererRegBonusAvailable(newUser);
		}
		updateUser(newUser);

	}

	/**
	 * when user cash back history is created, the its user pending amount will be updated
	 * 
	 * @param userCashBackHistory
	 */
	public void HandleUserCashBackHistoryRemovedEvent(@Observes @Removed UserCashBackHistory userCashBackHistory) {
		// update user's pending amount
		User user = userCashBackHistory.getUser();
		user = findByIdWithPessimisticWrite(user.getId());
		BigDecimal pendingAmt = user.getPendingAmount().subtract(userCashBackHistory.getCashBackAmount());
		user.setPendingAmount(pendingAmt);
		updateUser(user);
		updateUserNRefererRegBonusPending(user, userCashBackHistory);
	}

	/**
	 * get all Cash Back withdraw request for given user, return all cashback withdraw request for all users if user is null
	 * 
	 * @param user
	 * @return
	 */
	public List<CashBackWithdrawRequest> findAllCashBackWithdrawRequest(User user) {
		StringBuilder hql = new StringBuilder("select cbwr from CashBackWithdrawRequest cbwr ");
		if (user != null) {
			hql.append(" where cbwr.user=?1 ");
		}
		if (user != null) {
			return cashBackWithdrawRequestEjb.findByHQL(hql.toString(), CashBackWithdrawRequest.class, new Object[] { user });
		} else {
			return cashBackWithdrawRequestEjb.findByHQL(hql.toString(), CashBackWithdrawRequest.class, new Object[] {});
		}
	}

	public List<CashBackWithdrawRequest> findCashBackWithdrawRequest(User user, int start, int size) {
		StringBuilder hql = new StringBuilder("select cbwr from CashBackWithdrawRequest cbwr ");
		if (user != null) {
			hql.append(" where cbwr.user=?1 ");
		}
		if (user != null) {
			return cashBackWithdrawRequestEjb.findByHQL(hql.toString(), CashBackWithdrawRequest.class, start, size, new Object[] { user });
		} else {
			return cashBackWithdrawRequestEjb.findByHQL(hql.toString(), CashBackWithdrawRequest.class, start, size, new Object[] {});
		}
	}

	/**
	 * get total number of cashback withdraw request for given user, if user is null, then return total count for all cashback withdraw request
	 * 
	 * @param user
	 * @return
	 */
	public long getTotalCashBackWithdrawRequestCount(User user) {

		StringBuilder hql = new StringBuilder("select count(cbwr) from CashBackWithdrawRequest cbwr ");
		if (user != null) {
			hql.append(" where cbwr.user=?1 ");
		}
		if (user != null) {
			return cashBackWithdrawRequestEjb.findByHQLSingleResultParmMethod(hql.toString(), Long.class, new Object[] { user });
		} else {
			return cashBackWithdrawRequestEjb.findByHQLSingleResultParmMethod(hql.toString(), Long.class, new Object[] {});
		}

	}

	public int getTotalCashBackWithdrawRequestCount(User user, Map<String, Object> filters) {
		StringBuilder hql = new StringBuilder("select count(cbwr) from CashBackWithdrawRequest cbwr  where ");
		if (user != null) {
			hql.append(" cbwr.user=:user");
		}
		String filterProperty = null;
		Object filterValue = null;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("user.userId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		TypedQuery<Long> query = userDAO.getEm().createQuery(hql.toString(), Long.class);
		if (user != null) {
			query.setParameter("user", user);
		}
		return query.getSingleResult().intValue();
	}

	/**
	 * get
	 * 
	 * @param start
	 * @param size
	 * @param filters
	 * @return
	 */
	public List<CashBackWithdrawRequest> getCashBackWithdrawList(User user, int start, int size, Map<String, Object> filters) {
		List<CashBackWithdrawRequest> result = null;
		StringBuilder hql = new StringBuilder("select cbwr from CashBackWithdrawRequest cbwr  where ");
		if (user != null) {
			hql.append(" cbwr.user=:user");
		}
		String filterProperty = null;
		Object filterValue = null;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("user.userId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			}
			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		hql.append(" order by createTime desc");
		TypedQuery<CashBackWithdrawRequest> query = userDAO.getEm().createQuery(hql.toString(), CashBackWithdrawRequest.class);
		if (user != null) {
			query.setParameter("user", user);
		}
		result = query.setFirstResult(start).setMaxResults(size).getResultList();
		return result;
	}

	public void resetPassword(String userId) {
		String tempPwd = RandomStringUtils.randomAlphanumeric(10);
		// update user password to the temppwd
		User user = this.getUserByUserId(userId);
		user.setPassword(EncryptionUtil.md5(tempPwd));
		updateUser(user);
		emailService.sendResetPassword(tempPwd, user.getEmail());
	}

	/**
	 * get total number of Order inqueries for given user
	 * 
	 * @param user
	 * @return
	 */
	public int getTotalOrderInqueryCount(User user, Map<String, Object> filters) {
		StringBuilder hql = new StringBuilder("select count(oi) from OrderInquery oi  ");
		if (user != null || (filters != null && !filters.isEmpty())) {
			hql.append(" where ");
		}
		if (user != null) {
			hql.append(" oi.user=:user ");
		}
		String filterProperty = null;
		Object filterValue = null;
		if (filters != null) {
			if (user != null && !filters.isEmpty()) {
				hql.append(" and ");
			}
			for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
				filterProperty = ite.next();
				filterValue = filters.get(filterProperty);
				if (filterProperty.equals("status")) {
					hql.append(filterProperty).append("='").append(filterValue).append("'");
				} else if (filterProperty.equals("user.userId")) {
					hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
				} else if (filterProperty.equals("orderNumber")) {
					hql.append(filterProperty).append(" ='").append(filterValue).append("'");
				}
				if (ite.hasNext()) {
					hql.append(" and ");
				}

			}
		}
		TypedQuery<Long> query = userDAO.getEm().createQuery(hql.toString(), Long.class);
		if (user != null) {
			query.setParameter("user", user);
		}
		return query.getSingleResult().intValue();
	}

	/**
	 * 
	 * @param start
	 * @param size
	 * @param filters
	 * @return
	 */
	public List<OrderInquery> getOrderInqueryList(User user, int start, int size, Map<String, Object> filters) {
		List<OrderInquery> result = null;
		StringBuilder hql = new StringBuilder("select oi from OrderInquery oi   ");
		if (user != null || (filters != null && !filters.isEmpty())) {
			hql.append(" where ");
		}
		if (user != null) {
			hql.append(" oi.user=:user ");
		}
		String filterProperty = null;
		Object filterValue = null;
		if (filters != null) {
			if (user != null && !filters.isEmpty()) {
				hql.append(" and ");
			}
			for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
				filterProperty = ite.next();
				filterValue = filters.get(filterProperty);
				if (filterProperty.equals("status")) {
					hql.append(filterProperty).append("='").append(filterValue).append("'");
				} else if (filterProperty.equals("user.userId")) {
					hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
				} else if (filterProperty.equals("orderNumber")) {
					hql.append(filterProperty).append(" ='").append(filterValue).append("'");
				}
				if (ite.hasNext()) {
					hql.append(" and ");
				}

			}
		}
		hql.append(" order by createTime desc");
		TypedQuery<OrderInquery> query = userDAO.getEm().createQuery(hql.toString(), OrderInquery.class);
		if (user != null) {
			query.setParameter("user", user);
		}
		result = query.setFirstResult(start).setMaxResults(size).getResultList();
		return result;
	}

	public void createOrderInquery(User user, OrderInquery OrderInquery) {
		OrderInquery.setStatus(RecordStatus.PENDING);
		OrderInquery.setUser(user);
		orderInqueryEjb.create(OrderInquery);
	}

	public void updateOrderInquery(OrderInquery OrderInquery) {
		orderInqueryEjb.update(OrderInquery);
	}

	public void deleteOrderInquery(User user, OrderInquery OrderInquery) {
		orderInqueryEjb.delete(OrderInquery);
	}

	public OrderInquery getOrderInqueryById(int id) {
		return orderInqueryEjb.findById(OrderInquery.class, id);
	}

	public OrderInquery getOrderInquery(String orderNumber) {
		OrderInquery result = null;
		String hql = "select oi from OrderInquery oi where oi.orderNumber=?1";
		List<OrderInquery> orderInqueryList = orderInqueryEjb.findByHQL(hql, OrderInquery.class, orderNumber);

		// dont allow more than 1 order inquery which have the same order number
		if (orderInqueryList != null && !orderInqueryList.isEmpty()) {
			result = orderInqueryList.get(0);
		}
		return result;

	}

	/**
	 * get user based on login type and social token
	 * 
	 * @param loginType
	 * @param socialToken
	 * @return
	 */
	public User getUserIdByToken(LoginType loginType, String socialToken) {
		User result = null;
		if (null != loginType && !StringUtil.isNullOrBlank(loginType.toString()) && !StringUtil.isNullOrBlank(socialToken)) {
			String hql = "select user from User user where user.loginType=:loginType and user.socialToken=:socialToken";
			TypedQuery<User> query = userDAO.getEm().createQuery(hql, User.class).setParameter("loginType", loginType)
					.setParameter("socialToken", socialToken);
			List<User> userList = query.getResultList();
			if (!userList.isEmpty()) {
				result = userList.get(0);
			}
		}
		return result;
	}

	public void removeUserCashBack(UserCashBackHistory userCashBackHistory) {
		userCashBackEjb.remove(userCashBackHistory);
	}

	/**
	 * get all the referred users for given user
	 * 
	 * @param user
	 * @return
	 */
	public List<User> populateReferredUsers(User user) {
		String hql = "select user from User user where user.refererId=?1";
		List<User> result = userDAO.findByHQL(hql, User.class, user.getId() + "");
		return result;
	}

	/*
	 * public List<CashBackWithdrawRequest> findAllCashBackWithdrawRequest() { return
	 * cashBackWithdrawRequestEjb.findAll(CashBackWithdrawRequest.class); }
	 */
	public static void main(String[] args) {
		User user = new User();
		user.setFirstName("andrew");
		user.setLastName("zhang");
		EJBContainer ec = null;
		try {
			ec = EJBContainer.createEJBContainer();
			Context ctx = ec.getContext();
			UserEjb userEjb = (UserEjb) ctx.lookup("java:global/forum/UserEjb");
			userEjb.createUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ec.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
