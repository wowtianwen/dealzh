/**
 * all the categories are cached when the system start up.
 * 
 */
package com.junhong.forum.backingbean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;

import com.junhong.auth.common.Login;
import com.junhong.forum.common.Constants;
import com.junhong.forum.datamodel.LazyThreadStagingDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.ThreadStaging;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.ThreadStagingEjb;
import com.junhong.util.CommonUtil;
import com.junhong.util.ViewScoped;

@Named
@ViewScoped
public class ThreadStagingBackingBean extends AbstractBacking {

	@Inject
	private Logger						logger;

	@Inject
	private ThreadStagingEjb			threadStagingEjb;

	@Inject
	private ThreadStaging				threadStaging;
	@Inject
	protected Login						login;
	private SelectItem[]				recordStatusOptions;

	@Inject
	private LazyThreadStagingDataModel	threadStagingDataModel;

	private boolean						editMode;

	/* ------------instance Variable-------------- */

	@PostConstruct
	public void initialize() {
		recordStatusOptions = CommonUtil.createFilterOptions();
	}

	public void deleteThreadStaging() {
		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, threadStaging);
	}

	public void updateThreadStaging() {
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, threadStaging);
		this.setEditMode(false);
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub
		ThreadStaging threadStaging = (ThreadStaging) entity;
		if (action.equals(Constants.Action_UPDATE)) {
			threadStagingEjb.updateThreadStaging(threadStaging);
		}
		if (action.equals(Constants.Action_CREATE)) {
			threadStagingEjb.createThreadStaging(threadStaging);
		}

		if (action.equals(Constants.Action_DELETE)) {
			threadStagingEjb.removeThreadStaging(threadStaging);
		}
		if (action.equals(Constants.Action_PROMOTE)) {
			threadStagingEjb.promoteThreadStaging(threadStaging);
		}

	}

	public void promoteThreadStaging(ThreadStaging threadStaging) {
		processBusinessWithAuthorizationCheck(Constants.Action_PROMOTE, threadStaging);
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	public void validateUserAccess() {
		if (!login.isUserInSysadmin() && !login.isUserOwnerOfAnyCategory(login.getCurrentUser())) {
			threadStagingDataModel.setAuthorized(false);
		}
	}

	public ThreadStaging getThreadStaging() {
		return threadStaging;
	}

	public void setThreadStaging(ThreadStaging threadStaging) {
		this.threadStaging = threadStaging;
	}

	public LazyThreadStagingDataModel getThreadStagingDataModel() {
		return threadStagingDataModel;
	}

	public void setThreadStagingDataModel(LazyThreadStagingDataModel threadStagingDataModel) {
		this.threadStagingDataModel = threadStagingDataModel;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public SelectItem[] getRecordStatusOptions() {
		if (null == recordStatusOptions) {
			recordStatusOptions = CommonUtil.createFilterOptions();
		}
		return recordStatusOptions;
	}

	public void setRecordStatusOptions(SelectItem[] recordStatusOptions) {
		this.recordStatusOptions = recordStatusOptions;
	}

	/* -------------getter/setter----------------- */

}
