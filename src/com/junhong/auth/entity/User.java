package com.junhong.auth.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.junhong.forum.common.Constants;
import com.junhong.forum.common.LoginType;
import com.junhong.forum.constaint.PasswordMatch;
import com.junhong.forum.constaint.UserIdUniquenessCheck;
import com.junhong.forum.constaint.group.EnrollGroup;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.UserCashBackHistory;
import com.junhong.message.domain.Message;

@Entity
@Table(name = "user")
@NamedQueries({ @NamedQuery(name = "user.getAll", query = "select u from User as u") })
@PasswordMatch(first = "password", second = "passwordagain", message = "{PassWordNOTMatch}")
public class User extends AbstractEntity {
	private static final long			serialVersionUID	= 01l;

	/* ------------instance Variable-------------- */
	@NotNull
	@UserIdUniquenessCheck(groups = { EnrollGroup.class })
	private String						userId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "usergroup_Id")
	private UserGroup					userGroup;

	@NotNull
	@Size(min = 6, message = "{passwordlength}")
	private String						password;
	@Size(min = 6)
	private String						passwordagain;

	@NotNull
	private String						firstName;
	@NotNull
	private String						lastName;
	private String						middleName;
	private String						currentTitle;
	@Pattern(regexp = "[\\w\\.]+@[\\w]+[\\w\\.]*[a-zA-Z]{2,4}$", message = "{InvalidEmail}")
	private String						email;
	@Column(length = 255)
	@Enumerated(EnumType.STRING)
	private UserStatus					status				= UserStatus.PENDING;
	private String						activationCode;
	@Temporal(TemporalType.TIMESTAMP)
	private Date						activationCodeCreTime;
	private String						phoneNumber;
	private String						title;
	private int							reputation;
	private boolean						locked				= false;
	private boolean						isOnline			= false;

	private int							numOfPosts;
	private int							NumOfReplies;
	@Temporal(TemporalType.DATE)
	private Date						lastLogin;

	private boolean						isCategoryAdmin;
	private String						profilePicName;

	@OneToMany(mappedBy = "owner")
	private List<ForumCategory>			owningCategories;

	@OneToMany(mappedBy = "owner")
	private List<ForumThread>			forumThreadList;

	@OneToMany(mappedBy = "owner")
	private List<ForumReply>			forumReplyList;

	@ManyToMany
	@JoinTable(name = "User_Sticky_Thread", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "thread_id"))
	private List<ForumThread>			stickyThreads;

	@OneToMany(mappedBy = "from")
	private List<Message>				outBoundMessages;
	@OneToMany(mappedBy = "user")
	private List<UserCashBackHistory>	cashBackHistory;
	@Column(nullable = false, scale = 2)
	private BigDecimal					paidAmount			= BigDecimal.ZERO;
	@Column(nullable = false, scale = 2)
	private BigDecimal					pendingAmount		= BigDecimal.ZERO;
	@Column(nullable = false, scale = 2)
	private BigDecimal					availableAmount		= BigDecimal.ZERO;
	private Date						nextCashBackDate;
	private String						paymentMethod;
	private String						payPalEmail			= Constants.EDITME;
	private boolean						trustedUser			= true;
	@Enumerated(EnumType.STRING)
	private LoginType					loginType;
	private String						socialToken;
	// referer user's id value
	private String						refererId;

	private int							regBonusUCBHId;
	private int							refererRegBonusUCBHId;

	/*
	 * @ManyToMany
	 * 
	 * @JoinTable(name = "user_to_notification") private List<Notification>
	 * inBoundMessages;
	 */
	/* -------------business logic----------------- */
	public User() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	/* -------------getter/setter----------------- */

	// getter setter method
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getCurrentTitle() {
		return currentTitle;
	}

	public void setCurrentTitle(String currentTitle) {
		this.currentTitle = currentTitle;
	}

	public boolean isCategoryAdmin() {
		return isCategoryAdmin;
	}

	public void setCategoryAdmin(boolean isCategoryAdmin) {
		this.isCategoryAdmin = isCategoryAdmin;
	}

	public List<ForumCategory> getOwningCategories() {
		return owningCategories;
	}

	public void setOwningCategories(List<ForumCategory> owningCategories) {
		this.owningCategories = owningCategories;
	}

	public List<ForumThread> getForumThreadList() {
		return forumThreadList;
	}

	public void setForumThreadList(List<ForumThread> forumThreadList) {
		this.forumThreadList = forumThreadList;
	}

	public List<ForumReply> getForumReplyList() {
		return forumReplyList;
	}

	public void setForumReplyList(List<ForumReply> forumReplyList) {
		this.forumReplyList = forumReplyList;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordagain() {
		return passwordagain;
	}

	public void setPasswordagain(String passwordagain) {
		this.passwordagain = passwordagain;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public String getTitle() {
		return title;
	}

	public int getReputation() {
		return reputation;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public List<ForumThread> getStickyThreads() {
		return stickyThreads;
	}

	public void setStickyThreads(List<ForumThread> stickyThreads) {
		this.stickyThreads = stickyThreads;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public List<Message> getOutBoundMessages() {
		return outBoundMessages;
	}

	public void setOutBoundMessages(List<Message> outBoundMessages) {
		this.outBoundMessages = outBoundMessages;
	}

	/**
	 * @return the profilePicName
	 */
	public String getProfilePicName() {
		return profilePicName;
	}

	/**
	 * @param profilePicName
	 *            the profilePicName to set
	 */
	public void setProfilePicName(String profilePicName) {
		this.profilePicName = profilePicName;
	}

	/**
	 * @return the isOnline
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * @param isOnline
	 *            the isOnline to set
	 */
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	/**
	 * @return the lastLogin
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin
	 *            the lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * @return the numOfPosts
	 */
	public int getNumOfPosts() {
		return numOfPosts;
	}

	/**
	 * @param numOfPosts
	 *            the numOfPosts to set
	 */
	public void setNumOfPosts(int numOfPosts) {
		this.numOfPosts = numOfPosts;
	}

	/**
	 * @return the numOfReplies
	 */
	public int getNumOfReplies() {
		return NumOfReplies;
	}

	/**
	 * @param numOfReplies
	 *            the numOfReplies to set
	 */
	public void setNumOfReplies(int numOfReplies) {
		NumOfReplies = numOfReplies;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public Date getActivationCodeCreTime() {
		return activationCodeCreTime;
	}

	public void setActivationCodeCreTime(Date activationCodeCreTime) {
		this.activationCodeCreTime = activationCodeCreTime;
	}

	public Date getNextCashBackDate() {
		return nextCashBackDate;
	}

	public void setNextCashBackDate(Date nextCashBackDate) {
		this.nextCashBackDate = nextCashBackDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPayPalEmail() {
		return payPalEmail;
	}

	public void setPayPalEmail(String payPalEmail) {
		this.payPalEmail = payPalEmail;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(BigDecimal pendingAmount) {
		this.pendingAmount = pendingAmount;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public boolean isTrustedUser() {
		return trustedUser;
	}

	public void setTrustedUser(boolean trustedUser) {
		this.trustedUser = trustedUser;
	}

	public LoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(LoginType loginType) {
		this.loginType = loginType;
	}

	public String getSocialToken() {
		return socialToken;
	}

	public void setSocialToken(String socialToken) {
		this.socialToken = socialToken;
	}

	public String getRefererId() {
		return refererId;
	}

	public void setRefererId(String refererId) {
		this.refererId = refererId;
	}

	public int getRegBonusUCBHId() {
		return regBonusUCBHId;
	}

	public void setRegBonusUCBHId(int regBonusUCBHId) {
		this.regBonusUCBHId = regBonusUCBHId;
	}

	public int getRefererRegBonusUCBHId() {
		return refererRegBonusUCBHId;
	}

	public void setRefererRegBonusUCBHId(int refererRegBonusUCBHId) {
		this.refererRegBonusUCBHId = refererRegBonusUCBHId;
	}

}
