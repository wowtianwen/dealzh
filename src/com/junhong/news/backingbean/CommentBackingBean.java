/**
 * forum zhanjung
 */
package com.junhong.news.backingbean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.forum.backingbean.AbstractBacking;
import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.stats.HitCache;
import com.junhong.news.datamodel.LazyCommentDataModel;
import com.junhong.news.ejb.CommentEjb;
import com.junhong.news.ejb.NewsEjb;
import com.junhong.news.entity.Comment;
import com.junhong.news.entity.News;

/**
 * @author zhanjung
 * 
 */
@Named
@com.junhong.util.ViewScoped
public class CommentBackingBean extends AbstractBacking {
	/* ------------instance Variable-------------- */

	@Inject
	private Comment			commentInEdit;
	// the reply the reply replies to

	@Inject
	private Comment			comment;

	// this will determine if it is in view/edit mode
	@Inject
	NewsEjb					newsEjb;
	@EJB
	CommentEjb				commentEjb;

	@Inject
	NewsBackingBean			newsBackingBean;

	@Inject
	Login					login;

	@Inject
	LazyCommentDataModel	lazyCommentDataModel;

	// belonging to thread
	private News			news;

	private User			currUser;
	@Inject
	private HitCache		hitCache;

	private String			replyQuote							= Constants.BLANK;
	private String			replyQuoteWaterMark					= Constants.BLANK;
	private String			replyQuoteForUpdateCompositeComment	= Constants.BLANK;

	/**
	 * 
	 */
	@PostConstruct
	public void initialize() {

		currUser = this.getCurrentUser();

	}

	/**
	 * populate news when viewnews is loaded and newsBackingBean.loadNews is
	 * called.
	 * 
	 * @param news
	 */
	public void populateNews(@Observes News news) {
		this.news = news;
		lazyCommentDataModel.setNews(this.news);
	}

	/* -------------business logic----------------- */

	/**
	 * create reply
	 * 
	 * @return
	 */
	public void createComment() {

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		if (login.checkIfUserActive(currUser.getUserId())) {
			this.comment.setNews(news);
			this.comment.setOwner(currUser);
			if (!this.replyQuote.isEmpty()) {
				this.comment.setContent(this.comment.getContent() + this.replyQuote);
			}

			processBusinessWithAuthorizationCheck(Constants.Action_CREATE, this.comment);
			// TODO concurrent is not working here
			// reset replyto
			comment = new Comment();
			this.replyQuote = Constants.BLANK;
			this.replyQuoteWaterMark = Constants.BLANK;
		}

	}

	/**
	 * load all the replies for the given thread. called when loading the
	 * viewthread.xhtml
	 */
	public void loadComments() {
		// conversation.begin();

		// set it to session so that lazyreplydatamodel will use it
		this.getSessionMap().put(Constants.CURRENT_NEWS_INVIEW, news);

		if (news != null) {
			// prepopulate the subject of the reply
			this.comment.setSubject("Re: " + news.getSubject());
		}

	}

	/**
	 * Update Reply
	 * 
	 * @param reply
	 * @return
	 */
	public String updateComment() {

		commentInEdit.setContent(commentInEdit.getContent() + this.replyQuoteForUpdateCompositeComment);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, this.commentInEdit);

		// set editReplyt to null
		this.commentInEdit = null;
		return Constants.NavNull;
	}

	public void reset() {
		this.comment.setContent(Constants.BLANK);
		this.replyQuote = Constants.BLANK;
		this.replyQuoteWaterMark = Constants.BLANK;
	}

	/**
	 * Update Reply
	 * 
	 * @param reply
	 * @return
	 */
	public String cancelUpdate() {

		// set editReplyt to null
		commentInEdit = null;
		return Constants.NavNull;
	}

	/**
	 * @param thread
	 * @return
	 */
	public void deleteComment(Comment comment) {
		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, comment);

	}

	/**
	 * called when user click "Edit" button to edit.
	 * 
	 * @param comment
	 * @return
	 */
	public String setEditMode(Comment comment) {
		comment.setEditable(true);
		commentInEdit = comment;
		int blockQuotePosition = commentInEdit.getContent().indexOf(Constants.BLOCKQUOTETAG);
		if (blockQuotePosition != -1) {
			this.replyQuoteForUpdateCompositeComment = commentInEdit.getContent().substring(blockQuotePosition);
			commentInEdit.setContent(commentInEdit.getContent().substring(0, blockQuotePosition));

		}
		// editReplyList.add(reply);
		return null;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		Comment comment = (Comment) entity;

		if (action.equals(Constants.Action_UPDATE)) {
			commentEjb.updateComment(comment);

		}
		if (action.equals(Constants.Action_CREATE)) {
			commentEjb.createComment(comment);
		}
		if (action.equals(Constants.Action_DELETE)) {
			commentEjb.deleteComment(comment);
		}

	}

	/**
	 * For quote a reply.
	 * 
	 * @param comment
	 */
	public void replyTo(Comment comment) {
		this.comment.setSubject("Re " + comment.getSubject());
		replyQuote = Constants.BLOCKQUOTETAG + Constants.blockQuoteLine + " Quote FROM  " + comment.getOwner().getUserId() + Constants.blockQuoteLine
				+ "<br/>" + comment.getContent() + "</blockquote>";
		replyQuoteWaterMark = Constants.REPLYQUOTEWATERMARK + comment.getOwner().getUserId();

	}

	/* -------------getter/setter----------------- */
	public Comment getEditComment() {
		return comment;
	}

	public void setEditComment(Comment editComment) {
		this.comment = editComment;
	}

	public Comment getReplyTo() {
		return comment;
	}

	public void setReplyTo(Comment replyTo) {
		this.comment = replyTo;
	}

	public NewsEjb getNewsEjb() {
		return newsEjb;
	}

	public void setNewsEjb(NewsEjb newsEjb) {
		this.newsEjb = newsEjb;
	}

	public CommentEjb getCommentEjb() {
		return commentEjb;
	}

	public void setCommentEjb(CommentEjb commentEjb) {
		this.commentEjb = commentEjb;
	}

	public NewsBackingBean getThreadBackingBean() {
		return newsBackingBean;
	}

	public void setThreadBackingBean(NewsBackingBean threadBackingBean) {
		this.newsBackingBean = threadBackingBean;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public LazyCommentDataModel getLazyCommentDataModel() {
		return lazyCommentDataModel;
	}

	public void setLazyCommentDataModel(LazyCommentDataModel lazyCommentDataModel) {
		this.lazyCommentDataModel = lazyCommentDataModel;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public User getCurrUser() {
		return currUser;
	}

	public void setCurrUser(User currUser) {
		this.currUser = currUser;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Comment getCommentInEdit() {
		return commentInEdit;
	}

	public void setCommentInEdit(Comment commentInEdit) {
		this.commentInEdit = commentInEdit;
	}

	public String getReplyQuote() {
		return replyQuote;
	}

	public void setReplyQuote(String replyQuote) {
		this.replyQuote = replyQuote;
	}

	public String getReplyQuoteWaterMark() {
		return replyQuoteWaterMark;
	}

	public void setReplyQuoteWaterMark(String replyQuoteWaterMark) {
		this.replyQuoteWaterMark = replyQuoteWaterMark;
	}

}
