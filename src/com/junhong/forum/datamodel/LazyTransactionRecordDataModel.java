/**
 * jsf_demo zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.forum.entity.TransactionReportStaging;
import com.junhong.forum.service.TransactionRecordStagingEjb;

/**
 * @author zhanjung
 * 
 */

public class LazyTransactionRecordDataModel extends LazyDataModel<TransactionReportStaging> {
	@EJB
	private TransactionRecordStagingEjb	transactionRecordStagingEjb;

	public LazyTransactionRecordDataModel() {
	}

	@Override
	public List<TransactionReportStaging> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<TransactionReportStaging> result = new ArrayList<TransactionReportStaging>();

		int dataSize = transactionRecordStagingEjb.getTotalCount(filters);
		this.setRowCount(dataSize);
		// paginatet
		result = transactionRecordStagingEjb.getTransactionRecords(first, pageSize, filters);
		return result;

	}

}