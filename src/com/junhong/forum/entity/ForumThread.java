package com.junhong.forum.entity;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.common.ThreadType;

@Dependent
@Entity
@Table(name = "thread")
@NamedQueries({ @NamedQuery(name = "forumthread.all", query = "select t from ForumThread as t") })
public class ForumThread extends AbstractEntity {
	private static final long	serialVersionUID	= 01l;

	/* ------------instance Variable-------------- */
	@NotNull()
	@Size(min = 1, message = "{com.junhong.news.entity.news.subject}")
	@Column(length = 1000)
	private String				subject;

	@Column(length = 6000)
	private String				content;
	private String				shortContent;

	private boolean				locked;
	// =likes
	private int					votes;
	private int					rating;
	private int					numberOfReplies		= 0;
	private int					numberOfView		= 0;
	private boolean				isTopped			= false;

	private Date				toppedTime;
	@Column(length = 1000)
	private String				thumbPicURL;
	/* relationship to other Entities */
	@NotNull()
	@ManyToOne
	@JoinColumn(name = "owner_Id")
	private User				owner;
	@ManyToOne
	@JoinColumn(name = "category_Id")
	private ForumCategory		category;
	@OneToMany(mappedBy = "thread", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<ForumReply>	forumReplyList;

	@OneToOne
	@JoinColumn(name = "lastReply_Id")
	private ForumReply			lastReply;
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastReplyTime;
	private boolean				onDashBoard;
	@ManyToOne
	@JoinColumn(name = "store_Id")
	private Store				store;
	@Pattern(regexp = "(^\\$?(([1-9]\\d{0,2}(,?\\d{3})*)|(([1-9]\\d*)?\\d))(\\.\\d\\d)?)|(^\\$?[0-9]+(\\.\\d\\d)?%?\\s?+((?i)(off))$)|(\\s)*", message = "{InvalidPrice}")
	private String				price;
	@Temporal(TemporalType.DATE)
	private Date				startDate;
	@Temporal(TemporalType.DATE)
	private Date				endDate;
	private boolean				markedHot			= false;
	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event				event;
	@Enumerated(EnumType.STRING)
	private ThreadStatus		status				= ThreadStatus.APPROVED;
	@ManyToOne
	@JoinColumn(name = "approvedBy_user_id", nullable = true)
	private User				reviewedBy;
	@Enumerated(EnumType.STRING)
	private ThreadType			type				= ThreadType.REGULAR;
	private String				buyLink;

	/* -------------Constructor----------------- */
	public ForumThread() {

	}

	/* -------------business logic----------------- */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForumThread other = (ForumThread) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	public void addReply(ForumReply reply) {
		forumReplyList.add(reply);
	}

	public void setNumOfReplies(int numOfReplies) {
		this.numberOfReplies = numOfReplies;
	}

	/* -------------getter/setter----------------- */

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<ForumReply> getForumReplyList() {
		return forumReplyList;
	}

	public void setForumReplyList(List<ForumReply> forumReplyList) {
		this.forumReplyList = forumReplyList;
	}

	public ForumCategory getCategory() {
		return category;
	}

	public void setCategory(ForumCategory category) {
		this.category = category;
	}

	public int getNumberOfReplies() {
		return numberOfReplies;
	}

	public int getNumberOfView() {
		return numberOfView;
	}

	public void setNumberOfReplies(int numberOfReplies) {
		this.numberOfReplies = numberOfReplies;
	}

	public void setNumberOfView(int numberOfView) {
		this.numberOfView = numberOfView;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getRating() {
		return rating;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the isTopped
	 */
	public boolean isTopped() {
		return isTopped;
	}

	/**
	 * @param isTopped
	 *            the isTopped to set
	 */
	public void setTopped(boolean isTopped) {
		this.isTopped = isTopped;
	}

	/**
	 * @return the toppedTime
	 */
	public Date getToppedTime() {
		return toppedTime;
	}

	/**
	 * @param toppedTime
	 *            the toppedTime to set
	 */
	public void setToppedTime(Date toppedTime) {
		this.toppedTime = toppedTime;
	}

	/**
	 * @return the lastReply
	 */
	public ForumReply getLastReply() {
		return lastReply;
	}

	/**
	 * @param lastReply
	 *            the lastReply to set
	 */
	public void setLastReply(ForumReply lastReply) {
		this.lastReply = lastReply;
	}

	/**
	 * @return the onDashBoard
	 */
	public boolean isOnDashBoard() {
		return onDashBoard;
	}

	/**
	 * @param onDashBoard
	 *            the onDashBoard to set
	 */
	public void setOnDashBoard(boolean onDashBoard) {
		this.onDashBoard = onDashBoard;
	}

	public String getThumbPicURL() {
		return thumbPicURL;
	}

	public void setThumbPicURL(String thumbPicURL) {
		this.thumbPicURL = thumbPicURL;
	}

	public Date getLastReplyTime() {
		return lastReplyTime;
	}

	public void setLastReplyTime(Date lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public boolean isMarkedHot() {
		return markedHot;
	}

	public void setMarkedHot(boolean markedHot) {
		this.markedHot = markedHot;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public ThreadStatus getStatus() {
		return status;
	}

	public void setStatus(ThreadStatus status) {
		this.status = status;
	}

	public User getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(User reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public ThreadType getType() {
		return type;
	}

	public void setType(ThreadType type) {
		this.type = type;
	}

	public String getShortContent() {
		return shortContent;
	}

	public void setShortContent(String shortContent) {
		this.shortContent = shortContent;
	}

	public String getBuyLink() {
		return buyLink;
	}

	public void setBuyLink(String buyLink) {
		this.buyLink = buyLink;
	}

}
