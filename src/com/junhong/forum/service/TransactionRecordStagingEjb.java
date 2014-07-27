package com.junhong.forum.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.common.AuthorizationInterceptor;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.RecordStatus;
import com.junhong.forum.common.UserCashBackRecordStatus;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.forum.entity.Store;
import com.junhong.forum.entity.TransactionReportStaging;
import com.junhong.forum.entity.UserCashBackHistory;
import com.junhong.util.StringUtil;
import com.junhong.util.WebConfigUtil;

/**
 * Session Bean implementation class ThreadEjb
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TransactionRecordStagingEjb {

	/*
	 * @Inject UserTransaction userTransaction;
	 */
	@Inject
	private GenericCRUDService<TransactionReportStaging>	genericCRUDService;

	@Inject
	Logger													logger;
	@Inject
	@GeneralDAOImplQualifier
	private EntityDAOImpl<TransactionReportStaging>			entityDAO;
	@Inject
	private UserEjb											userEjb;
	@Inject
	private StoreEjb										storeEjb;
	@Inject
	private UserCashBackEjb									userCashBackEjb;

	/**
	 * Default constructor.
	 */
	public TransactionRecordStagingEjb() {
	}

	/** business method */

	@Role.List({ @Role(RoleType.SYSADMIN) })
	@Interceptors(AuthorizationInterceptor.class)
	public void createTransactionRecordStaging(TransactionReportStaging transactionRecportStaging) {
		genericCRUDService.create(transactionRecportStaging);
	}

	public void update(TransactionReportStaging transactionReportStaging) {
		genericCRUDService.update(transactionReportStaging);
	}

	/**
	 * get transaction Staging record based on transaction Id
	 * 
	 * @param transactionId
	 * @return
	 */
	public TransactionReportStaging getTransactionReportStaging(String transactionId) {
		String hql = "select tranStaging from TransactionReportStaging tranStaging where tranStaging.transactionId=:tranId";
		TypedQuery<TransactionReportStaging> query = this.entityDAO.getEm().createQuery(hql, TransactionReportStaging.class);
		query.setParameter("tranId", transactionId).setMaxResults(1);
		List<TransactionReportStaging> list = query.getResultList();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public TransactionReportStaging getTransactionReportStaging(String transactionId, BigDecimal salesAmount) {
		String hql = "select tranStaging from TransactionReportStaging tranStaging where tranStaging.transactionId=:tranId and tranStaging.transactionAmount=:tranAmount";
		TypedQuery<TransactionReportStaging> query = this.entityDAO.getEm().createQuery(hql, TransactionReportStaging.class);
		query.setParameter("tranId", transactionId).setParameter("tranAmount", salesAmount).setMaxResults(1);
		List<TransactionReportStaging> list = query.getResultList();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public void create(TransactionReportStaging tranReportStaging) {
		genericCRUDService.create(tranReportStaging);
	}

	/**
	 * populate transaction for each store into user cash back table from transaction report staging table
	 */
	public void populateCashBackHistory() {
		// fetch every 100 data
		long totalCount = getTotalPendingRecord();
		int numOfCalls = (int) (totalCount % 100 > 0 ? totalCount / 100 + 1 : totalCount / 100);
		String getPendingTranReportStagingRecord = "select trs from TransactionReportStaging trs where trs.recordStatus=:recordStatus";
		TypedQuery<TransactionReportStaging> query = this.entityDAO.getEm().createQuery(getPendingTranReportStagingRecord,
				TransactionReportStaging.class);
		query.setParameter("recordStatus", RecordStatus.PENDING);
		List<TransactionReportStaging> list = new ArrayList<TransactionReportStaging>();
		for (int i = 0; i <= numOfCalls; i++) {
			query.setFirstResult(i * 100);
			query.setMaxResults(100);
			list = query.getResultList();
			for (TransactionReportStaging tranReportStaging : list) {
				populateUserCashBack(tranReportStaging);
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void populateUserCashBack(TransactionReportStaging transactionReportStaging) {
		String tranUserId = transactionReportStaging.getTransactionUserId();
		int index = tranUserId.indexOf(Constants.DEALREFERDELIMIT);
		User clickUser = null;
		User referUser = null;
		if (index != -1) {
			String clickUserId = tranUserId.substring(0, index);
			clickUser = userEjb.getUserByUserId(clickUserId);

			String referUId = tranUserId.substring(index + Constants.DEALREFERDELIMIT.length());
			if (!StringUtil.isNullOrBlank(referUId)) {
				try {
					int referUIdInt = Integer.parseInt(referUId);
					referUser = userEjb.getUserById(referUIdInt);
				} catch (NumberFormatException nfe) {
					logger.warn("referUser's Id format is wrong {}", referUId);
					referUser = null;
				}

			}
		} else {
			clickUser = userEjb.getUserByUserId(tranUserId);
		}

		if (clickUser == null) {

			clickUser = userEjb.getUserByUserId("deallover");
		}
		UserCashBackHistory userCashBackHistory = new UserCashBackHistory();
		userCashBackHistory.setGetPaid(false);
		userCashBackHistory.setTransactionId(transactionReportStaging.getTransactionId());
		userCashBackHistory.setTransactionAmount(transactionReportStaging.getTransactionAmount());
		userCashBackHistory.setStatus(UserCashBackRecordStatus.PENDING);
		userCashBackHistory.setPendingCBConf(false);
		userCashBackHistory.setAvailCBConf(false);
		userCashBackHistory.setUser(clickUser);

		String advertiserId = transactionReportStaging.getAdvertiserId();
		userCashBackHistory.setTransactionDate(transactionReportStaging.getTransactionDate());
		Store store = storeEjb.getStoreByAdvertiserId(advertiserId);
		if (store != null) {
			userCashBackHistory.setStore(store);
			userCashBackHistory.setCashBackPercent(store.getCashBackPercent());
			if (transactionReportStaging.getTransactionAmount() == null) {
				userCashBackHistory.setCashBackAmount(BigDecimal.ZERO);
				userCashBackHistory.setProfit(transactionReportStaging.getCommissionAmount().subtract(userCashBackHistory.getCashBackAmount()));
			} else {
				if (transactionReportStaging.getCommissionAmount().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal pctAmount = store.getCashBackPercent().multiply(transactionReportStaging.getTransactionAmount())
							.setScale(2, RoundingMode.HALF_DOWN);
					userCashBackHistory.setCashBackAmount(pctAmount);
					userCashBackHistory.setProfit(transactionReportStaging.getCommissionAmount().subtract(pctAmount)
							.setScale(2, RoundingMode.HALF_DOWN));
				} else if (transactionReportStaging.getCommissionAmount().compareTo(BigDecimal.ZERO) == 0) {
					userCashBackHistory.setCashBackAmount(BigDecimal.ZERO);
					userCashBackHistory.setProfit(transactionReportStaging.getCommissionAmount().subtract(userCashBackHistory.getCashBackAmount()));
				} else {
					// comission amount is negative, check the transaction
					// amount, if it is also <0, consider an order cancellation
					// applied to linkshare
					// might need to change logic for other affiliate
					if (transactionReportStaging.getTransactionAmount().compareTo(BigDecimal.ZERO) < 0) {
						BigDecimal pctAmount = store.getCashBackPercent().multiply(transactionReportStaging.getTransactionAmount())
								.setScale(2, RoundingMode.HALF_DOWN);
						userCashBackHistory.setCashBackAmount(pctAmount);
						userCashBackHistory.setProfit(transactionReportStaging.getCommissionAmount().subtract(pctAmount)
								.setScale(2, RoundingMode.HALF_DOWN));
					}
				}

			}
			userCashBackHistory.setCommissionAmount(transactionReportStaging.getCommissionAmount());

			userCashBackHistory.setAffiliateNetWork(transactionReportStaging.getAffiliateNetwork());
			userCashBackEjb.create(userCashBackHistory);
			// process refer transaction
			if (referUser != null && !referUser.equals(clickUser)) {
				createDealReferBonus(referUser, userCashBackHistory);
			}

			// update the user's pending cash back amount
			// user.setPendingAmount(user.getPendingAmount().add(userCashBackHistory.getCashBackAmount()));
			transactionReportStaging.setRecordStatus(RecordStatus.PROCESSED);
		} else {
			transactionReportStaging.setRecordStatus(RecordStatus.ERROR);
		}
		update(transactionReportStaging);

	}

	/**
	 * create deal refer bonus for the refer user
	 * 
	 * @param referUser
	 * @param originalUserCashBackHistory
	 */
	public void createDealReferBonus(User referUser, UserCashBackHistory originalUserCashBackHistory) {
		UserCashBackHistory userCashBackHistory = new UserCashBackHistory();
		userCashBackHistory.setGetPaid(false);
		userCashBackHistory.setTransactionId(Constants.DEAL_REFER_BONUS);
		userCashBackHistory.setStatus(UserCashBackRecordStatus.PENDING);
		userCashBackHistory.setPendingCBConf(false);
		userCashBackHistory.setAvailCBConf(false);
		userCashBackHistory.setUser(referUser);
		// save original transactin in affiliate network, since reg bonus is
		// using transactionid field too.
		userCashBackHistory.setAffiliateNetWork(originalUserCashBackHistory.getTransactionId());
		// default to 5%
		String dealReferBonusPct = "0.05";
		if (WebConfigUtil.getProp("DEALREFERBONUSPCT") != null) {
			dealReferBonusPct = WebConfigUtil.getProp("DEALREFERBONUSPCT");
		}

		userCashBackHistory.setCashBackPercent(new BigDecimal(dealReferBonusPct));
		userCashBackHistory.setCashBackAmount(originalUserCashBackHistory.getCashBackAmount().multiply(new BigDecimal(dealReferBonusPct))
				.setScale(2, RoundingMode.HALF_UP));
		userCashBackHistory.setCommissionAmount(BigDecimal.ZERO);
		userCashBackHistory.setProfit(userCashBackHistory.getCommissionAmount().subtract(userCashBackHistory.getCashBackAmount())
				.setScale(2, RoundingMode.HALF_DOWN));
		userCashBackHistory.setTransactionDate(originalUserCashBackHistory.getTransactionDate());
		userCashBackHistory.setTransactionAmount(originalUserCashBackHistory.getCashBackAmount());

		userCashBackEjb.create(userCashBackHistory);

	}

	public long getTotalPendingRecord() {
		String getPendingTranReportStagingRecord = "select count(trs) from TransactionReportStaging trs where trs.recordStatus=:recordStatus";
		TypedQuery<Long> query = this.entityDAO.getEm().createQuery(getPendingTranReportStagingRecord, Long.class);
		query.setParameter("recordStatus", RecordStatus.PENDING);
		Long totalNumberOfPendingRecord = query.getSingleResult();
		return totalNumberOfPendingRecord;

	}

	/**
	 * check if the transaction staging record exist in the transaction reporst staging table which has the same transaction id and commission amount
	 * 
	 * @param transactionId
	 * @param commissionAmount
	 * @return
	 */
	public boolean checkExist(String transactionId, BigDecimal commissionAmount, String sku) {
		commissionAmount = commissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
		boolean result = false;
		String hql = "select tranStaging from TransactionReportStaging tranStaging where tranStaging.transactionId=:tranId and tranStaging.commissionAmount=:commissionAmount and tranStaging.itemId=:itemId";
		List<TransactionReportStaging> list = this.entityDAO.getEm().createQuery(hql, TransactionReportStaging.class)
				.setParameter("tranId", transactionId).setParameter("commissionAmount", commissionAmount).setParameter("itemId", sku)
				.setMaxResults(1).getResultList();
		if (list != null && !list.isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * check if the transaction staging record exist in the transaction reporst staging table which has the same transaction id and commission amount
	 * 
	 * @param transactionId
	 * @param commissionAmount
	 * @return
	 */
	public boolean checkExist(String transactionId, BigDecimal commissionAmount) {
		commissionAmount = commissionAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
		boolean result = false;
		String hql = "select tranStaging from TransactionReportStaging tranStaging where tranStaging.transactionId=:tranId and transtaging.commissionAmount=:commissionAmount";
		List<TransactionReportStaging> list = this.entityDAO.getEm().createQuery(hql, TransactionReportStaging.class)
				.setParameter("tranId", transactionId).setParameter("commissionAmount", commissionAmount).setMaxResults(1).getResultList();
		if (list != null && !list.isEmpty()) {
			result = true;
		}
		return result;
	}

	public int getTotalCount(Map<String, Object> filters) {
		int result = 0;
		StringBuilder hql = new StringBuilder("select count(tran) from TransactionReportStaging tran ");
		if (!filters.isEmpty()) {
			hql.append(" where ");
		}
		String filterProperty;
		Object filterValue;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("transactionId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("affiliateNetwork")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("transactionDate")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("transactionUserId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("advertiserId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("advertiserName")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			}

			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		TypedQuery<Long> query = this.entityDAO.getEm().createQuery(hql.toString(), Long.class);
		result = query.getSingleResult().intValue();
		return result;

	}

	public List<TransactionReportStaging> getTransactionRecords(int start, int size, Map<String, Object> filters) {
		List<TransactionReportStaging> result = new ArrayList<TransactionReportStaging>();
		StringBuilder hql = new StringBuilder("select tran from TransactionReportStaging tran ");
		if (!filters.isEmpty()) {
			hql.append(" where ");
		}
		String filterProperty;
		Object filterValue;
		for (Iterator<String> ite = filters.keySet().iterator(); ite.hasNext();) {
			filterProperty = ite.next();
			filterValue = filters.get(filterProperty);
			if (filterProperty.equals("transactionId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("affiliateNetwork")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("transactionDate")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("transactionUserId")) {
				hql.append(filterProperty).append(" like '%").append(filterValue).append("%'");
			} else if (filterProperty.equals("status")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("advertiserId")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			} else if (filterProperty.equals("advertiserName")) {
				hql.append(filterProperty).append("='").append(filterValue).append("'");
			}

			if (ite.hasNext()) {
				hql.append(" and ");
			}

		}
		hql.append(" order by createTime desc");
		TypedQuery<TransactionReportStaging> query = this.entityDAO.getEm().createQuery(hql.toString(), TransactionReportStaging.class);
		query.setFirstResult(start).setMaxResults(size);
		result = query.getResultList();
		return result;
	}
}
