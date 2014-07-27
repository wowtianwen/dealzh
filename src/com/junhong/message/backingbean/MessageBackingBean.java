/**
 * forum
 * zhanjung
 */
package com.junhong.message.backingbean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.backingbean.AbstractBacking;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.ContextObjectProducer.CurrentUser;
import com.junhong.forum.common.Messages;
import com.junhong.forum.datamodel.LazyMessageDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.RecipientMessageStatusService;
import com.junhong.message.domain.Message;
import com.junhong.message.domain.MessageStatus;
import com.junhong.message.domain.RecipientMessageStatus;
import com.junhong.message.domain.RecipientMessageStatusId;
import com.junhong.message.service.MessageEjb;
import com.junhong.util.StringUtil;
import com.junhong.util.ViewScoped;

/**
 * @author zhanjung
 * 
 */
@Named
@ViewScoped
public class MessageBackingBean extends AbstractBacking {
	/* ------------instance Variable-------------- */
	private static final long serialVersionUID = 01l;

	@Inject
	private Logger logger;

	private int numberOfNewMessages;
	private List<Message> newIncomingMessages;

	@Inject
	private LazyMessageDataModel inboxSentArchieveDraftMessages;

	private UIData dataTable; // need
								// to
								// reset
								// the
								// first
								// property
								// of
								// dataTable
								// to
								// 0;

	@Inject
	private Message currentMessage; //

	private Message messageDisplay;
	@EJB
	private UserEjb userEjb;
	@EJB
	private MessageEjb messageEjb;
	@Inject
	@CurrentUser
	private User currUser;

	private String messageGroup = Constants.Message_Inbox;

	private List<String> toUserIdList = new ArrayList<String>();

	private TreeNode root;

	private Message selectedMessage;

	private Message[] selectedMessages;

	@Inject
	private RecipientMessageStatusService recipientMessageStatusEjb;

	// Hold the current user's message status in his inbox
	private Map<Integer, RecipientMessageStatus> inboxMessageStatusMap = new ConcurrentHashMap<Integer, RecipientMessageStatus>();

	/* -------------business logic----------------- */

	@PostConstruct
	public void initTreeNode() {
		root = new DefaultTreeNode("Root", null);
		TreeNode inbox = new DefaultTreeNode(Constants.Message_Inbox, Constants.Message_Inbox, root);
		TreeNode draft = new DefaultTreeNode(Constants.Message_Draft, Constants.Message_Draft, root);
		TreeNode sent = new DefaultTreeNode(Constants.Message_Sent, Constants.Message_Sent, root);
		TreeNode archieved = new DefaultTreeNode(Constants.Message_Archieve, Constants.Message_Archieve, root);

		messageGroup = Constants.Message_Inbox;
	}

	public void sendMessage() {

		messageEjb.sendMessage(this.currentMessage, currUser, toUserIdList);

		// update the message group
		this.retrieveMessages(this.messageGroup);

		// reset the message
		this.currentMessage = new Message();

		// populate the success message
		FacesMessage message = Messages.getMessage("", "SUCCEED", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);

	}

	public void saveDraftMessage() {

		messageEjb.saveDraftMessage(this.currentMessage, currUser, toUserIdList);
		// populate the success message
		FacesMessage message = Messages.getMessage("", "message_saved_success", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);

	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// left blank intentially

	}

	public void getAllIncomingNewMessages() {

		if (currUser != null) {
			newIncomingMessages = messageEjb.getAllIncomingNewMessages(currUser);
		} else {
			newIncomingMessages = new ArrayList<Message>();

		}
	}

	/**
	 * fetch all inbox messages
	 */
	public void fetchAllInboxMessages(String messGroup) {

		retrieveMessages(messGroup);

	}

	/**
	 * 
	 * get the all Incoming Message for each person.
	 * 
	 * @return
	 */
	public String fetchAllInboxSentArchieveMessages(NodeSelectEvent event) {
		currUser = (User) this.getSessionMap().get(Constants.CurrentUser);
		String node = event.getTreeNode().toString();
		retrieveMessages(node);
		return null;
	}

	/**
	 * @param messGroup
	 */
	private void retrieveMessages(String messGroup) {
		if (dataTable != null) {
			dataTable.setFirst(0);
		}
		if (messGroup.equalsIgnoreCase(Constants.Message_Inbox)) {
			inboxSentArchieveDraftMessages.setMessageGroup(Constants.Message_Inbox);
			inboxSentArchieveDraftMessages.setInboxMessageStatusMap(inboxMessageStatusMap);
			messageGroup = Constants.Message_Inbox;

		} else if (messGroup.equalsIgnoreCase(Constants.Message_Sent)) {
			inboxSentArchieveDraftMessages.setMessageGroup(Constants.Message_Sent);
			messageGroup = Constants.Message_Sent;

		}

		else if (messGroup.equalsIgnoreCase(Constants.Message_Archieve)) {
			inboxSentArchieveDraftMessages.setMessageGroup(Constants.Message_Archieve);
			messageGroup = Constants.Message_Archieve;

		} else if (messGroup.equalsIgnoreCase(Constants.Message_Draft)) {
			inboxSentArchieveDraftMessages.setMessageGroup(Constants.Message_Draft);
			messageGroup = Constants.Message_Draft;
		}
	}

	public void checkNumberOfNewMessages() {
		currUser = (User) this.getSessionMap().get(Constants.CurrentUser);
		this.numberOfNewMessages = messageEjb.getNumberOfNewMessages(currUser);
		this.getSessionMap().put(Constants.Number_Of_New_Message, this.numberOfNewMessages);
	}

	public void setForView() {
		setForView(this.selectedMessage);
	}

	public void displayDraftMessage(Message message) {
		this.currentMessage = message;
		String toUserIds = message.getToUserids();
		if (StringUtil.isNullOrBlank(toUserIds)) {
			return;
		}
		String[] userIds = toUserIds.split(";");
		this.toUserIdList = Arrays.asList(userIds);
	}

	/**
	 * called in the messagebox page only
	 * 
	 * @param Message
	 */
	public void setForView(Message message) {

		this.messageDisplay = message;
		RecipientMessageStatus currentRecipientMessage = null;

		currentRecipientMessage = new RecipientMessageStatus(message, this.currUser);
		RecipientMessageStatusId id = new RecipientMessageStatusId(message.getId(), this.currUser.getId());
		currentRecipientMessage = recipientMessageStatusEjb.findById(RecipientMessageStatus.class, id);

		if (currentRecipientMessage != null && currentRecipientMessage.getStatus().equals(MessageStatus.NEW)) {
			List<Message> messageList = new ArrayList<Message>();
			messageList.add(message);
			messageEjb.markInboxMessageStatus(messageList, this.currUser, MessageStatus.READ);
			// messageEjb.updateMessage(messageDisplay);
		}

	}

	/**
	 * called from the Message pop up window. redirect to messagebox.xhtml
	 * 
	 * @param Message
	 * @return
	 */
	public String setForViewNRedirect(Message Message) {

		setForView(Message);
		logger.debug("set for view n redirect is called +++++++++++++++++++++++++");
		return "/notification/messagebox.xhtml?faces-redirect=true";

	}

	/**
	 * send the reply to exitsing message
	 * 
	 * @param replyTo
	 * @param newMessage
	 */
	public void prepareReply() {
		// set replyTo Message
		// reset Id so as to create it
		currentMessage.setId(0);
		// reset toUserStr;
		toUserIdList.clear();
		List<User> to = new ArrayList<User>();
		String toUser = null;
		if (messageDisplay.isGroupMessage()) {
			/*
			 * to.add(messageDisplay.getFrom()); for (RecipientMessageStatus rs
			 * : messageDisplay.getRecipientStatus()) { to.add(rs.getUser()); }
			 * to.remove(currUser);
			 */

			// make sure the recipients is not the current user
			if (!messageDisplay.getFrom().getUserId().equalsIgnoreCase(currUser.getUserId())) {
				toUserIdList.add(messageDisplay.getFrom().getUserId());
			}
			String oriMessageRecipient = messageDisplay.getToUserids();
			String[] oriMessRecipients = oriMessageRecipient.split(";");
			for (String temp : oriMessRecipients) {
				if (!temp.equalsIgnoreCase(currUser.getUserId())) {
					toUserIdList.add(temp);
				}
			}

		} else {
			if (!messageDisplay.getFrom().equals(currUser)) {
				toUser = messageDisplay.getFrom().getUserId();
			} else {

				// if send the same message from the same sender instead of
				// repling message
				toUser = messageDisplay.getToUserids();
			}
			toUserIdList.add(toUser);

		}
		// create message for reply
		this.currentMessage.setFrom(this.getCurrentUser());

		StringBuilder replyMessageContent = new StringBuilder();
		replyMessageContent.append("\n\r \n\r----------------------------\n\r").append(messageDisplay.getCreateTime().toString()).append("  ")
				.append(messageDisplay.getFrom().getUserId()).append(" Wrote:").append("\n\r");
		// prepare the reply message content. set the history of the previous
		// message
		replyMessageContent.append(this.messageDisplay.getContent());
		String replySubject = "Re " + this.messageDisplay.getSubject();
		currentMessage.setSubject(replySubject);
		// currentMessage.setContent(StringUtil.br2nl(replyMessageContent.toString()));
		currentMessage.setContent(replyMessageContent.toString());

	}

	public void markDelete() {

		Message[] selMessages = this.getSelectedMessages();
		if (selMessages == null || selMessages.length == 0) {
			return;
		}

		if (selMessages.length > 0) {
			messageEjb.deleteMessageFromMessageGroup(Arrays.asList(selMessages), this.currUser, this.messageGroup);
		}
	}

	public void archieve() {

		Message[] selMessages = this.getSelectedMessages();
		if (selMessages == null || selMessages.length == 0) {
			return;
		}

		List<Message> messageList = new ArrayList<Message>();
		for (Message mess : selMessages) {
			messageList.add(mess);
		}
		if (this.messageGroup.equalsIgnoreCase(Constants.Message_Inbox)) {

			messageEjb.updateRecipientMessageStatus(messageList, this.currUser, MessageStatus.ARCHIEVED);
		} else if (this.messageGroup.equalsIgnoreCase(Constants.Message_Sent)) {
			messageEjb.updateMessageOutBoxMessStatus(messageList, MessageStatus.ARCHIEVED);

		}

		// unselect all the messages
		this.selectedMessages = null;
	}

	public void markRead(boolean read) {

		Message[] selMessages = this.getSelectedMessages();

		if (selMessages == null || selMessages.length == 0) {
			return;
		}
		List<Message> messageList = Arrays.asList(selMessages);
		if (read) {
			messageEjb.markInboxMessageStatus(messageList, this.currUser, MessageStatus.READ);
		} else {
			messageEjb.markInboxMessageStatus(messageList, this.currUser, MessageStatus.NEW);
		}
		this.selectedMessages = null;
	}

	public MessageStatus getMessageStatus(int messageId) {
		return inboxMessageStatusMap.get(messageId).getStatus();
	}

	/**
	 * set message recipent
	 * 
	 * @param userId
	 */
	public void setMessageRecipent(String userId) {
		if (userId != null && !userId.isEmpty()) {
			this.toUserIdList.clear();
			this.toUserIdList.add(userId);

		}

	}

	/*-------getter/setter------------*/
	public int getNumberOfNewMessages() {
		return numberOfNewMessages;
	}

	public void setNumberOfNewMessages(int numberOfNewMessages) {
		this.numberOfNewMessages = numberOfNewMessages;
	}

	public List<Message> getNewIncomingMessages() {
		return newIncomingMessages;
	}

	public void setNewIncomingMessages(List<Message> newIncomingMessages) {
		this.newIncomingMessages = newIncomingMessages;
	}

	public Message getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(Message currentMessage) {
		this.currentMessage = currentMessage;
	}

	/* -------------getter/setter----------------- */

	public Message getMessageDisplay() {
		return messageDisplay;
	}

	public void setMessageDisplay(Message messageDisplay) {
		this.messageDisplay = messageDisplay;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public String getMessageGroup() {
		return messageGroup;
	}

	public void setMessageGroup(String messageGroup) {
		this.messageGroup = messageGroup;
	}

	public Message getSelectedMessage() {
		return selectedMessage;
	}

	public void setSelectedMessage(Message selectedMessage) {
		this.selectedMessage = selectedMessage;
	}

	public UIData getDataTable() {
		return dataTable;
	}

	public void setDataTable(UIData dataTable) {
		this.dataTable = dataTable;
	}

	/**
	 * @return the selectedMessages
	 */
	public Message[] getSelectedMessages() {
		return selectedMessages;
	}

	/**
	 * @param selectedMessages
	 *            the selectedMessages to set
	 */
	public void setSelectedMessages(Message[] selectedMessages) {
		this.selectedMessages = selectedMessages;
	}

	/**
	 * @return the inboxSentArchieveDraftMessages
	 */
	public LazyMessageDataModel getInboxSentArchieveDraftMessages() {
		return inboxSentArchieveDraftMessages;
	}

	/**
	 * @param inboxSentArchieveDraftMessages
	 *            the inboxSentArchieveDraftMessages to set
	 */
	public void setInboxSentArchieveDraftMessages(LazyMessageDataModel inboxSentArchieveDraftMessages) {
		this.inboxSentArchieveDraftMessages = inboxSentArchieveDraftMessages;
	}

	/**
	 * @return the toUserIdList
	 */
	public List<String> getToUserIdList() {
		return toUserIdList;
	}

	/**
	 * @param toUserIdList
	 *            the toUserIdList to set
	 */
	public void setToUserIdList(List<String> toUserIdList) {
		this.toUserIdList = toUserIdList;
	}
}
