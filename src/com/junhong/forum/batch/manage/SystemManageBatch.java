package com.junhong.forum.batch.manage;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.ExcludeDefaultInterceptors;

import com.junhong.forum.common.Constants;
import com.junhong.forum.service.ThreadEjb;
import com.junhong.util.StringUtil;
import com.junhong.util.WebConfigUtil;

/**
 * Session Bean implementation class PopulateStoreDealBatch
 */
@ExcludeDefaultInterceptors
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SystemManageBatch {

	@Inject
	private ThreadEjb	threadEjb;

	/**
	 * Default constructor.
	 */
	public SystemManageBatch() {
	}

	@Schedule(dayOfWeek = "Sun", month = "*", year = "*", second = "0", minute = "0", hour = "3", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void cleanDashBoardThreads() {
		int lookBackPeriod = 30;
		String clearnDBLookBackPeriod = WebConfigUtil.getProp(Constants.CLEAN_DB_LOOKBACK_PERIOD);
		if (!StringUtil.isNullOrBlank(clearnDBLookBackPeriod)) {
			lookBackPeriod = Integer.parseInt(clearnDBLookBackPeriod);
		}
		threadEjb.cleanDashBoardThreads(lookBackPeriod);
	}

	public static void main(String[] args) {

	}

}
