/**
 * forum zhanjung
 */
package com.junhong.forum.backingbean;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.batch.PopulateDealBatch;
import com.junhong.forum.batch.manage.SystemManageBatch;
import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.CashBackWithdrawRequest;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.CategoryServiceSingleton;
import com.junhong.forum.service.ThreadStagingEjb;
import com.junhong.forum.service.UserCashBackEjb;
import com.junhong.util.ViewScoped;

/**
 * designed for all the system management including category owner setup and others
 * 
 * @author zhanjung
 * 
 */

@Named
@ViewScoped
public class SystemManageBean extends AbstractBacking {

	private static final long			serialVersionUID	= -85049363231430233L;
	/* ------------instance Variable-------------- */
	@EJB
	private UserEjb						userEjb;
	@EJB
	private CategoryServiceSingleton	categorySingleton;
	@EJB
	private SystemManageBatch			systemManageBatch;
	@Inject
	private PopulateDealBatch			populateDealBatch;
	@Inject
	private ThreadStagingEjb			threadStagingEbj;

	private CashBackWithdrawRequest		cashBackWithdrawRequest;
	@Inject
	private UserCashBackEjb				userCashBackEjb;

	public String assignCategoryOwner(ForumCategory forumCategory) {
		String userId = forumCategory.getOwner().getUserId();
		long catetoryId = forumCategory.getId();
		User user = userEjb.getUserByUserId(userId);
		ForumCategory category = categorySingleton.getForumCategoryById(catetoryId);
		category.setOwner(user);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, category);
		this.getRequestMap().put("categoryowner", userId);
		this.getRequestMap().put("categoryName", category.getCategoryName());

		return Constants.NavSuccess;

	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub
		ForumCategory category = (ForumCategory) entity;
		if (action.equalsIgnoreCase(Constants.Action_UPDATE)) {
			categorySingleton.updateForumCategory(category);
		}

	}

	public void populateCashBackHistory() {
		populateDealBatch.populateCashBackHistory();
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	public void cleanDashBoardThreads() {
		systemManageBatch.cleanDashBoardThreads();
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	public void sendPendingCashBackEmail() {
		userCashBackEjb.sendPendingCashBackEmail();
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	public void sendAvailableCashBackEmail() {
		userCashBackEjb.sendAvailableCashBackEmail();
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	/**
	 * clean all threadstaging older than 5 days
	 * 
	 */
	public void cleanThreadStaging() {
		threadStagingEbj.cleanStagingThreads();
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	/* -------------getter/setter----------------- */
	public CashBackWithdrawRequest getCashBackWithdrawRequest() {
		return cashBackWithdrawRequest;
	}

	public void setCashBackWithdrawRequest(CashBackWithdrawRequest cashBackWithdrawRequest) {
		this.cashBackWithdrawRequest = cashBackWithdrawRequest;
	}

}
