/**
 * forum zhanjung
 */
package com.junhong.forum.backingbean;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.datamodel.LazyReplyDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.ReplyEjb;
import com.junhong.forum.service.ThreadEjb;

/**
 * @author zhanjung
 * 
 */
@Named
@com.junhong.util.ViewScoped
public class ReplyBackingBean extends AbstractBacking {
	/* ------------instance Variable-------------- */

	private static final long	serialVersionUID	= -4007474603933426598L;

	@Inject
	private ForumReply			editReply;
	// the reply the reply replies to

	@Inject
	private ForumReply			replyTo;

	// this will determine if it is in view/edit mode
	@Inject
	ThreadEjb					threadEjb;
	@EJB
	ReplyEjb					replyEjb;

	@Inject
	ThreadBackingBean			threadBackingBean;

	@Inject
	Login						login;

	@Inject
	LazyReplyDataModel			lazyReplyDataModel;

	// belonging to thread
	private ForumThread			thread;

	private User				currUser;

	/**
	 * 
	 */
	@PostConstruct
	public void initialize() {

		currUser = this.getCurrentUser();

	}

	/* -------------business logic----------------- */

	/**
	 * create reply
	 * 
	 * @return
	 */
	public void createReply() {

		thread = threadBackingBean.getLoadedThread();
		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		if (login.checkIfUserActive(currUser.getUserId())) {
			this.replyTo.setThread(thread);
			this.replyTo.setOwner(currUser);
			processBusinessWithAuthorizationCheck(Constants.Action_CREATE, this.replyTo);
			loadLastPage();
			// TODO concurrent is not working here
			// reset replyto
			replyTo = new ForumReply();
		}

	}

	/**
	 * load all the replies for the given thread. called when loading the
	 * viewthread.xhtml
	 */
	public void loadReplies() {
		// conversation.begin();
		thread = threadBackingBean.getSelectedForumThread();

		// set it to session so that lazyreplydatamodel will use it
		this.getSessionMap().put(Constants.BELONG_THREAD_ID, thread);
		Map<String, Object> sessionMap = this.getSessionMap();

		if (thread != null) {
			// prepopulate the subject of the reply
			this.replyTo.setSubject("Re: " + thread.getSubject());
		}

	}

	/**
	 * load the last page of the viewthread page after creating a new reply so
	 * that the newly created thread will be displayed
	 */
	public void loadLastPage() {

		lazyReplyDataModel.setLoadLastPage(true);

	}

	/**
	 * Update Reply
	 * 
	 * @param reply
	 * @return
	 */
	public String updateReply() {

		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, this.editReply);

		// set editReplyt to null
		this.editReply = null;
		return Constants.NavNull;
	}

	/**
	 * Update Reply
	 * 
	 * @param reply
	 * @return
	 */
	public String cancelUpdate() {

		// set editReplyt to null
		editReply = null;
		return Constants.NavNull;
	}

	/**
	 * @param thread
	 * @return
	 */
	public void deleteReply(ForumReply reply) {
		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, reply);

	}

	/**
	 * called when user click "Edit" button to edit.
	 * 
	 * @param reply
	 * @return
	 */
	public String setEditMode(ForumReply reply) {
		reply.setEditable(true);
		editReply = reply;
		// editReplyList.add(reply);
		return null;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		ForumReply reply = (ForumReply) entity;

		if (action.equals(Constants.Action_UPDATE)) {
			replyEjb.updateForumReply(reply);

		}
		if (action.equals(Constants.Action_CREATE)) {
			replyEjb.createForumReply(reply);
		}
		if (action.equals(Constants.Action_DELETE)) {
			replyEjb.deleteForumReply(reply);
		}

	}

	/**
	 * For quote a reply.
	 * 
	 * @param reply
	 */
	public void replyTo(ForumReply reply) {
		this.replyTo.setSubject("Re " + reply.getSubject());
		this.replyTo.setContent("<br/><br/><blockquote style=" + Constants.blockQuoteStyle + ">" + Constants.blockQuoteLine + " Quote FROM  "
				+ reply.getOwner().getUserId() + Constants.blockQuoteLine + "<br/>" + reply.getContent() + "</blockquote>");

	}

	/* -------------getter/setter----------------- */
	public ForumReply getEditReply() {
		return editReply;
	}

	public void setEditReply(ForumReply editReply) {
		this.editReply = editReply;
	}

	public LazyReplyDataModel getLazyReplyDataModel() {
		return lazyReplyDataModel;
	}

	public void setLazyReplyDataModel(LazyReplyDataModel lazyReplyDataModel) {
		this.lazyReplyDataModel = lazyReplyDataModel;
	}

	public ForumReply getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(ForumReply replyTo) {
		this.replyTo = replyTo;
	}

}
