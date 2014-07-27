/**
 * jsf_demo zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.ContextObjectProducer.CurrentUser;
import com.junhong.message.domain.Message;
import com.junhong.message.domain.RecipientMessageStatus;
import com.junhong.message.service.MessageEjb;

/**
 * @author zhanjung
 * 
 */

public class LazyMessageDataModel extends LazyDataModel<Message> {

	private static final long						serialVersionUID		= 3661709517834023380L;
	private String									messageGroup;
	private Map<Integer, RecipientMessageStatus>	inboxMessageStatusMap	= null;
	@EJB
	private MessageEjb								messageEjb;

	@Inject
	@CurrentUser
	private User									currUser;

	public LazyMessageDataModel() {
	}

	@Override
	public List<Message> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<Message> result = new ArrayList<Message>();
		List<RecipientMessageStatus> rsList = null;
		int dataSize = 0;

		if (messageGroup.equalsIgnoreCase(Constants.Message_Inbox)) {
			// rowCount
			dataSize = messageEjb.getTotalNumberOfIncomingMessages(currUser);
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				rsList = messageEjb.findIncomingMessages(currUser, first, pageSize);
			} else {
				rsList = messageEjb.findIncomingMessages(currUser, 0, pageSize);
			}
			for (RecipientMessageStatus rms : rsList) {
				result.add(rms.getMessage());
				inboxMessageStatusMap.put(rms.getMessage().getId(), rms);
			}

		} else if (messageGroup.equalsIgnoreCase(Constants.Message_Sent)) {
			// rowCount
			dataSize = messageEjb.getTotalNumberOfOutgoingMessages(currUser);
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				result = messageEjb.findOutGoingMessages(currUser, first, pageSize);
			} else {
				result = messageEjb.findOutGoingMessages(currUser, first, pageSize);
			}

		} else if (messageGroup.equalsIgnoreCase(Constants.Message_Archieve)) {
			// rowCount
			dataSize = messageEjb.getTotalNumberOfArchieveMessages(currUser);
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				result = messageEjb.findArchieveMessages(currUser, first, pageSize);
			} else {
				result = messageEjb.findArchieveMessages(currUser, 0, pageSize);
			}

		} else if (messageGroup.equalsIgnoreCase(Constants.Message_Draft)) {
			// rowCount
			dataSize = messageEjb.getTotalNumberOfDraftMessages(currUser);
			this.setRowCount(dataSize);

			// paginate
			if (dataSize > pageSize) {
				result = messageEjb.findDraftMessages(currUser, first, pageSize);
			} else {
				result = messageEjb.findDraftMessages(currUser, 0, pageSize);
			}

		}

		return result;

	}

	public String getMessageGroup() {
		return messageGroup;
	}

	public void setMessageGroup(String messageGroup) {
		this.messageGroup = messageGroup;
	}

	/**
	 * @return the inboxMessageStatusMap
	 */
	public Map<Integer, RecipientMessageStatus> getInboxMessageStatusMap() {
		return inboxMessageStatusMap;
	}

	/**
	 * @param inboxMessageStatusMap
	 *            the inboxMessageStatusMap to set
	 */
	public void setInboxMessageStatusMap(Map<Integer, RecipientMessageStatus> inboxMessageStatusMap) {
		this.inboxMessageStatusMap = inboxMessageStatusMap;
	}

}