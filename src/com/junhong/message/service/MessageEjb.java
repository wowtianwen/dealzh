package com.junhong.message.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.common.Constants;
import com.junhong.forum.service.RecipientMessageStatusService;
import com.junhong.message.dao.MessageDAO;
import com.junhong.message.domain.Message;
import com.junhong.message.domain.MessageStatus;
import com.junhong.message.domain.RecipientMessageStatus;

/**
 * Session Bean implementation class ThreadEjb
 */
/**
 * @author zhanjung
 * 
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MessageEjb {

	@Inject
	private MessageDAO				dao;

	/*
	 * @Inject UserTransaction userTransaction;
	 */

	@Inject
	Logger							logger;

	@Inject
	UserEjb							userEjb;

	@EJB
	RecipientMessageStatusService	recipientMessageStatusEjb;

	/**
	 * Default constructor.
	 */
	public MessageEjb() {
	}

	/** business method */
	public Message getMessageById(int id) {
		return dao.findById(Message.class, id);
	}

	public void createMessage(Message Message) {
		dao.create(Message);
	}

	public Message updateMessage(Message Message) {
		return dao.update(Message);
	}

	public void deleteMessage(Message notifiation) {
		dao.delete(notifiation);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Message> getAllIncomingNewMessages(User currUser) {
		return dao.getAllIncomingNewMessages(currUser);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Message> getAllIncomingMessages(User currUser) {
		return dao.getAllIncomingMessages(currUser);
	}

	public int getTotalNumberOfIncomingMessages(User currUser) {
		return (int) dao.getTotalNumberOfIncomingMessages(currUser);
	}

	public int getTotalNumberOfOutgoingMessages(User currUser) {
		return (int) dao.getTotalNumberOfOutgoingMessages(currUser);
	}

	public int getTotalNumberOfArchieveMessages(User currUser) {
		return (int) dao.getTotalNumberOfArchieveMessages(currUser);
	}

	public int getTotalNumberOfDraftMessages(User currUser) {
		return (int) dao.getTotalNumberOfDraftMessages(currUser);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Message> getAllOutgoingMessages(User currUser) {
		return dao.getAllOutgoingMessages(currUser);
	}

	public int getNumberOfNewMessages(User curUser) {
		return (int) dao.getNumberOfNewMessages(curUser);
	}

	// may can be deleted
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Message fetchMessageEagerly(Message message) {
		return dao.fetchMessageEagerly(message);

	}

	public List<RecipientMessageStatus> findIncomingMessages(User currUser, int start, int size) {
		return dao.findIncomingMessages(currUser, start, size);
	}

	public List<Message> findOutGoingMessages(User currUser, int start, int size) {
		return dao.findOutgoingMessages(currUser, start, size);
	}

	public List<Message> findArchieveMessages(User currUser, int start, int size) {
		return dao.findArchieveMessages(currUser, start, size);
	}

	public List<Message> findDraftMessages(User currUser, int start, int size) {
		return dao.findDraftMessages(currUser, start, size);
	}

	public void updateRecipientMessageStatus(List<Message> messageList, User user, MessageStatus messStatus) {
		dao.updateRecipientMessageStatus(messageList, user, messStatus);
	}

	public void updateMessageOutBoxMessStatus(List<Message> messages, MessageStatus messStatus) {
		dao.updateMessageOutBoxMessStatus(messages, messStatus);
	}

	public void createRecipientStatus(Message message, User user, MessageStatus messStatus) {

		dao.createRecipientStatus(message, user, messStatus);

	}

	/**
	 * create a new message when people send a new message
	 * 
	 * @param message
	 * @param from
	 * @param toUserIdsList
	 */
	public void sendMessage(Message message, User from, List<String> toUserIdsList) {

		// reset the message id
		if (message.getOutBoxMessstatus().equals(MessageStatus.DRAFT)) {
			sendDraftMessage(message, from, toUserIdsList);
			return;

		}
		message.setId(0);
		// calculate recipientUsers
		User recipientTemp = null;

		RecipientMessageStatus recipientStatusTemp = null;
		List<RecipientMessageStatus> recipientMessages = new ArrayList<RecipientMessageStatus>();

		StringBuilder toUserIdStr = new StringBuilder();
		for (String userId : toUserIdsList) {
			recipientTemp = userEjb.getUserByUserId(userId);
			recipientStatusTemp = new RecipientMessageStatus(message, recipientTemp);
			recipientStatusTemp.setStatus(MessageStatus.NEW);

			recipientMessages.add(recipientStatusTemp);
			// recipientStatusCol.add(recipientStatusTemp);
			toUserIdStr.append(userId).append(";");
		}

		// associate message with recipientmessages
		// message.setRecipientStatus(recipientMessages);
		// Message and from user
		if (toUserIdsList.size() > 1) {
			message.setGroupMessage(true);
		}
		message.setFrom(from);
		message.setToUserids(toUserIdStr.substring(0, toUserIdStr.length() - 1));
		// this.currentMessage.setContent(StringUtil.nl2br(this.currentMessage.getContent()));
		message.setContent(message.getContent());
		this.createMessage(message);

		/*
		 * message = this.getMessageById(message.getId());
		 * message.setRecipientStatus(recipientMessages);
		 * this.updateMessage(message);
		 */

		for (RecipientMessageStatus pms : recipientMessages) {
			recipientMessageStatusEjb.create(pms);

		}
	}

	private void sendDraftMessage(Message message, User from, List<String> toUserIdsList) {

		message.setFrom(from);
		StringBuilder toUserIdStr = new StringBuilder();
		for (String userId : toUserIdsList) {
			// recipientStatusCol.add(recipientStatusTemp);
			toUserIdStr.append(userId).append(";");
		}
		message.setToUserids(toUserIdStr.substring(0, toUserIdStr.length() - 1));
		// this.currentMessage.setContent(StringUtil.nl2br(this.currentMessage.getContent()));
		message.setContent(message.getContent());
		message.setOutBoxMessstatus(MessageStatus.SENT);
		message = this.updateMessage(message);
		User recipientTemp = null;
		RecipientMessageStatus recipientStatusTemp = null;
		for (String userId : toUserIdsList) {
			recipientTemp = userEjb.getUserByUserId(userId);
			recipientStatusTemp = new RecipientMessageStatus(message, recipientTemp);
			recipientStatusTemp.setStatus(MessageStatus.NEW);
			recipientMessageStatusEjb.create(recipientStatusTemp);
		}

	}

	public void saveDraftMessage(Message message, User from, List<String> toUserIdsList) {

		// reset the message id
		message.setId(0);

		StringBuilder toUserIdStr = new StringBuilder();
		for (String userId : toUserIdsList) {
			toUserIdStr.append(userId).append(";");
		}

		// associate message with recipientmessages
		// Message and from user
		message.setFrom(from);
		message.setOutBoxMessstatus(MessageStatus.DRAFT);
		message.setToUserids(toUserIdStr.substring(0, toUserIdStr.length() - 1));
		// this.currentMessage.setContent(StringUtil.nl2br(this.currentMessage.getContent()));
		message.setContent(message.getContent());
		this.createMessage(message);

	}

	/**
	 * mark the list of messages read/unread
	 * 
	 * @param messageList
	 * @param user
	 * @param messStatus
	 */
	public void markInboxMessageStatus(List<Message> messageList, User user, MessageStatus messStatus) {
		dao.markInboxMessageStatus(messageList, user, messStatus);
	}

	public void markSentBoxMessageStatus(List<Message> messageList, User user, MessageStatus messStatus) {

		dao.markSentBoxMessageStatus(messageList, user, messStatus);
	}

	/**
	 * delete message from the given users inbox, sent and archieve box
	 * 
	 * @param message
	 * @param user
	 * @param messageGroup
	 */
	public void deleteMessageFromMessageGroup(List<Message> messageList, User currUser, String messageGroup) {

		if (messageGroup.equalsIgnoreCase(Constants.Message_Inbox)) {
			this.removeMessageStatus(messageList, currUser);
		} else if (messageGroup.equalsIgnoreCase(Constants.Message_Sent)) {
			this.markSentBoxMessageStatus(messageList, currUser, MessageStatus.DELETED);
		} else if (messageGroup.equalsIgnoreCase(Constants.Message_Archieve)) {

			List<Message> inboxMessageList = new ArrayList<Message>();
			List<Message> sentBoxMessageList = new ArrayList<Message>();
			for (Message mess : messageList) {
				if (mess.getFrom().equals(currUser)) {
					sentBoxMessageList.add(mess);
				} else {
					//
					inboxMessageList.add(mess);
				}
			}
			if (inboxMessageList.size() > 0) {
				this.removeMessageStatus(inboxMessageList, currUser);
			}
			if (sentBoxMessageList.size() > 0) {
				this.markSentBoxMessageStatus(sentBoxMessageList, currUser, MessageStatus.DELETED);
			}
		}

		// check if the any message is set deleted both in the sentbox and inbox.
		List<Message> orphanMessageList = new ArrayList<Message>();
		messageList = this.fetchMessages(messageList, true);
		for (Message mess : messageList) {
			if ((mess.getRecipientStatus() == null || mess.getRecipientStatus().size() == 0)
					&& mess.getOutBoxMessstatus().equals(MessageStatus.DELETED)) {
				orphanMessageList.add(mess);

			}

		}

		// delete the orphan messages
		if (orphanMessageList.size() > 0) {
			removeMessages(orphanMessageList);
		}

	}

	public List<Message> fetchMessages(List<Message> messageList, boolean isEarger) {
		return dao.fetchMessages(messageList, isEarger);

	}

	public void removeMessages(List<Message> messageList) {
		dao.removeMessages(messageList);

	}

	public void removeMessageStatus(List<Message> messageList, User user) {
		dao.removeMessageStatus(messageList, user);
	}
}
