/**
 * forum zhanjung
 */
package com.junhong.forum.backingbean;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.common.ThreadType;
import com.junhong.forum.datamodel.LazyThread4AdminDataModel;
import com.junhong.forum.datamodel.LazyThreadDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.CategoryServiceSingleton;
import com.junhong.forum.service.EventServiceSingleton;
import com.junhong.forum.service.SystemCacheEjb;
import com.junhong.forum.service.ThreadEjb;
import com.junhong.forum.stats.CacheKey;
import com.junhong.forum.stats.CacheType;
import com.junhong.forum.stats.HitCache;
import com.junhong.forum.stats.RatingCache;
import com.junhong.util.CommonUtil;
import com.junhong.util.StringUtil;
import com.junhong.util.ViewScoped;

/**
 * @author zhanjung
 * 
 */

@Named
@ViewScoped
public class ThreadBackingBean extends AbstractBacking {
	private static final long				serialVersionUID		= 01l;

	/* ------------instance Variable-------------- */

	// this will determine if it is in view/edit mode
	private boolean							editMode				= false;

	@Inject
	private LazyThreadDataModel				lazyThreadDataModel;

	@Inject
	private LazyThread4AdminDataModel		lazyThread4AdminDataModel;

	@Inject
	ForumThread								selectedForumThread;
	@Inject
	private Login							login;
	@EJB
	ThreadEjb								threadEjb;
	@EJB
	CategoryServiceSingleton				categoryEjb;
	@EJB
	SystemCacheEjb							systemCacheEjb;
	@EJB
	EventServiceSingleton					eventEjb;
	@EJB
	private HitCache						hitCache;

	@EJB
	private RatingCache						ratingCache;

	private ForumCategory					belongingCategory;
	private com.junhong.forum.entity.Event	parentEvent;

	// user's current position event
	@Inject
	private Event<ForumCategory>			currentCategoryEvent;
	@Inject
	private Event<ForumThread>				currentForumThreadEvent;
	private User							currUser;
	private boolean							ownerForCurrCategory	= false;
	private List<ForumThread>				newestThreads;

	private SelectItem[]					threadStatusFilterOptions;

	@PostConstruct
	public void initialize() {
		int id = -1;
		Object cat_thread_id = this.getSessionMap().get(Constants.CATEGORY__THREAD_ID);
		if (cat_thread_id != null) {
			id = Integer.parseInt((String) cat_thread_id);
			Object belongCategory = this.getSessionMap().get(Constants.BelongingCategory);
			if (null != belongCategory && ((ForumCategory) belongCategory).getId() == id) {
				belongingCategory = (ForumCategory) belongCategory;
			} else {
				belongingCategory = categoryEjb.getForumCategoryById(id);
				this.getSessionMap().put(Constants.BelongingCategory, belongingCategory);
			}
		} else {
			belongingCategory = (ForumCategory) this.getSessionMap().get(Constants.BelongingCategory);
		}

		currUser = getCurrentUser();

		// populate isOwnerForCurrCategory
		ownerForCurrCategory = calculateOwnerForCurrCategory(currUser, belongingCategory);

		threadStatusFilterOptions = CommonUtil.createFilterOptionsForTheadStatus();
	}

	/**
	 * set the -999999 forumcategory to the session, so that lazyThradDataModel will featch all the threads regardless its category
	 */
	public void prepareFeatch4AllThread(ForumCategory category) {
		if (category == null) {
			category = new ForumCategory();
			category.setId(Constants.CategoryId_For_All_Threads);
		}
		this.getSessionMap().put(Constants.BelongingCategory, category);
		lazyThreadDataModel.setSource(Constants.USER_TAB_GENERAL);

	}

	public void prepareFeatch4AllPendingThread(ForumCategory category) {
		if (category == null) {
			category = new ForumCategory();
			category.setId(Constants.CategoryId_For_All_Threads);
		}
		this.getSessionMap().put(Constants.BelongingCategory, category);
		lazyThreadDataModel.setSource(Constants.USER_TAB_GENERAL);

	}

	/**
	 * set category when user click a category on indexthreadsAllCategories page
	 * 
	 * @param categoryId
	 */
	public void setCategoryFilter(long categoryId) {
		ForumCategory category = new ForumCategory();
		if (Constants.CategoryId_For_All_Threads == categoryId) {
			category.setId(Constants.CategoryId_For_All_Threads);

		} else {
			category = categoryEjb.getForumCategoryById(categoryId);
		}
		this.getSessionMap().put(Constants.BelongingCategory, category);

	}

	public boolean calculateOwnerForCurrCategory(User user, ForumCategory category) {

		boolean result = false;
		if (user != null && category != null && user.equals(category.getOwner())) {
			result = true;
		}
		return result;

	}

	/* -------------business logic----------------- */
	/**
	 * @param forumThread
	 * @return
	 */
	public String createThread() {

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		if (!login.checkIfUserActive(currUser.getUserId())) {
			return null;
		}
		this.selectedForumThread.setContent("<p>" + this.selectedForumThread.getContent() + "</p>");
		this.selectedForumThread.setCategory(belongingCategory);
		this.selectedForumThread.setOwner(currUser);
		this.selectedForumThread.setLastReplyTime(this.selectedForumThread.getCreateTime());
		if (!StringUtil.isNullOrBlank(this.selectedForumThread.getPrice())) {
			String price = this.selectedForumThread.getPrice();
			if (!price.toUpperCase().contains("OFF")) {
				this.selectedForumThread.setPrice("$" + price.replaceFirst("\\$", ""));
			}
		}
		if (currUser.isTrustedUser()) {
			this.selectedForumThread.setStatus(ThreadStatus.APPROVED);
		} else {
			this.selectedForumThread.setStatus(ThreadStatus.PENDINGREVIEW);
		}
		boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, this.selectedForumThread);
		if (hasError) {
			return null;
		} else {
			return Constants.NavSuccess;
		}
	}

	/**
	 * @param forumThread
	 * @return
	 */
	public String createAnnounceThread4AllCategory() {

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		String categoryId = (String) this.getRequestMap().get("categoryId");
		if (categoryId != null) {
			belongingCategory = categoryEjb.getForumCategoryById(Long.parseLong(categoryId));
		}
		this.selectedForumThread.setId(0);

		this.selectedForumThread.setContent("<p>" + this.selectedForumThread.getContent() + "</p>");
		this.selectedForumThread.setCategory(belongingCategory);
		this.selectedForumThread.setOwner(currUser);
		this.selectedForumThread.setLastReplyTime(this.selectedForumThread.getCreateTime());
		this.selectedForumThread.setType(ThreadType.ANNOUNCE);
		if (!StringUtil.isNullOrBlank(this.selectedForumThread.getPrice())) {
			String price = this.selectedForumThread.getPrice();
			if (!price.toUpperCase().contains("OFF")) {
				this.selectedForumThread.setPrice("$" + price.replaceFirst("\\$", ""));
			}
		}
		// set thread status based on user's trusteduser
		if (currUser.isTrustedUser()) {
			this.selectedForumThread.setStatus(ThreadStatus.APPROVED);
		} else {
			this.selectedForumThread.setStatus(ThreadStatus.PENDINGREVIEW);
		}

		boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, this.selectedForumThread);
		if (hasError) {
			return null;
		} else {
			if (this.selectedForumThread.getOwner().isTrustedUser()) {
				return Constants.NavSuccess;
			} else {

				return Constants.ThreadNeedReview;
			}
		}

	}

	/**
	 * @param forumThread
	 * @return
	 */
	public String createThread4AllCategory() {

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		if (!login.checkIfUserActive(currUser.getUserId())) {
			return null;
		}
		String categoryId = (String) this.getRequestMap().get("categoryId");
		if (categoryId != null) {
			belongingCategory = categoryEjb.getForumCategoryById(Long.parseLong(categoryId));
		}
		this.selectedForumThread.setId(0);

		this.selectedForumThread.setContent("<p>" + this.selectedForumThread.getContent() + "</p>");
		this.selectedForumThread.setCategory(belongingCategory);
		this.selectedForumThread.setOwner(currUser);
		this.selectedForumThread.setLastReplyTime(this.selectedForumThread.getCreateTime());
		if (!StringUtil.isNullOrBlank(this.selectedForumThread.getPrice())) {
			String price = this.selectedForumThread.getPrice();
			if (!price.toUpperCase().contains("OFF")) {
				this.selectedForumThread.setPrice("$" + price.replaceFirst("\\$", ""));
			}
		}
		// set thread status based on user's trusteduser
		if (currUser.isTrustedUser()) {
			this.selectedForumThread.setStatus(ThreadStatus.APPROVED);
		} else {
			this.selectedForumThread.setStatus(ThreadStatus.PENDINGREVIEW);
		}

		boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, this.selectedForumThread);
		if (hasError) {
			return null;
		} else {
			if (this.selectedForumThread.getOwner().isTrustedUser()) {
				return Constants.NavSuccess;
			} else {

				return Constants.ThreadNeedReview;
			}
		}

	}

	public String createThread4Event() {

		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		if (!login.checkIfUserActive(currUser.getUserId())) {
			return null;
		}
		String categoryId = (String) this.getRequestMap().get("categoryId");
		if (categoryId != null) {
			belongingCategory = categoryEjb.getForumCategoryById(Long.parseLong(categoryId));
		}
		this.selectedForumThread.setId(0);

		this.selectedForumThread.setContent("<p>" + this.selectedForumThread.getContent() + "</p>");
		this.selectedForumThread.setCategory(belongingCategory);
		this.selectedForumThread.setOwner(currUser);
		this.selectedForumThread.setLastReplyTime(this.selectedForumThread.getCreateTime());
		if (!StringUtil.isNullOrBlank(this.selectedForumThread.getPrice())) {
			String price = this.selectedForumThread.getPrice();
			if (!price.toUpperCase().contains("OFF")) {
				this.selectedForumThread.setPrice("$" + price.replaceFirst("\\$", ""));
			}
		}
		com.junhong.forum.entity.Event parentEvent = (com.junhong.forum.entity.Event) this.getSessionMap().get(Constants.BelongingEvent);
		this.selectedForumThread.setEvent(parentEvent);

		// set thread status based on user's trusteduser
		if (currUser.isTrustedUser()) {
			this.selectedForumThread.setStatus(ThreadStatus.APPROVED);
		} else {
			this.selectedForumThread.setStatus(ThreadStatus.PENDINGREVIEW);
		}
		boolean hasError = processBusinessWithAuthorizationCheck(Constants.Action_CREATE, this.selectedForumThread);
		if (hasError) {
			return null;
		} else {
			if (this.selectedForumThread.getOwner().isTrustedUser()) {
				return Constants.NavSuccess;
			} else {
				return Constants.ThreadNeedReview;
			}
		}

	}

	/**
	 * prerender view
	 */
	public void calculateNavMap() {
		if (belongingCategory != null) {
			// update the navigation map
			currentCategoryEvent.fire(belongingCategory);
		}
	}

	/**
	 * pre renderview for viewing thread
	 */
	public void loadThread() {

		int threadId = 0;
		int referUserId = 0;
		try {
			Object threadIdObj = this.getSessionMap().get("threadId");
			threadId = Integer.parseInt((String) threadIdObj);

			Object referUserIdOb = this.getSessionMap().get("dealRefer");
			if (referUserIdOb != null) {
				referUserId = Integer.parseInt((String) referUserIdOb);
			}
		} catch (NumberFormatException e) {
			setBizErrorNSkipToResp("INVALIDTHREADID");
			selectedForumThread = null;
			return;
		}
		selectedForumThread = threadEjb.getForumThreadById(threadId);
		if (selectedForumThread == null) {
			setBizErrorNSkipToResp("INVALIDTHREADID");
			selectedForumThread = null;
			return;

		} else if (!selectedForumThread.getStatus().equals(ThreadStatus.APPROVED)
				&& (!login.isUserInSysadmin() && !login.isUserCategory_Owner(selectedForumThread.getCategory()) && !login
						.isUserThread_Owner(selectedForumThread))) {
			selectedForumThread = null;
			setMessageNSkipToResp("ThreadNotApproved", FacesMessage.SEVERITY_INFO);

		} else {
			belongingCategory = selectedForumThread.getCategory();
			hitCache.incrementAndGet(new CacheKey(selectedForumThread.getId(), CacheType.THREAD));
			currentForumThreadEvent.fire(selectedForumThread);
			// populate isOwnerForCurrCategory
			ownerForCurrCategory = calculateOwnerForCurrCategory(currUser, belongingCategory);
		}
		// put referUserId in session
		if (referUserId != 0) {
			this.getSessionMap().put(selectedForumThread.getId() + "", Integer.toString(referUserId));

		}

	}

	public ForumThread refreshThread(ForumThread forumThread) {
		return threadEjb.getForumThreadById(forumThread.getId());
	}

	public String updateThread() {
		if (!StringUtil.isNullOrBlank(this.selectedForumThread.getPrice())) {
			String price = this.selectedForumThread.getPrice();
			if (!price.toUpperCase().contains("OFF")) {
				this.selectedForumThread.setPrice("$" + price.replaceFirst("\\$", ""));
			}
		}
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, selectedForumThread);
		selectedForumThread = refreshThread(selectedForumThread);
		this.enableEditMode(false);
		currentForumThreadEvent.fire(selectedForumThread);
		return Constants.NavNull;
	}

	public String updateThreadForReview() {
		if (!StringUtil.isNullOrBlank(this.selectedForumThread.getPrice())) {
			String price = this.selectedForumThread.getPrice();
			if (!price.toUpperCase().contains("OFF")) {
				this.selectedForumThread.setPrice("$" + price.replaceFirst("\\$", ""));
			}
		}
		selectedForumThread.setStatus(ThreadStatus.PENDINGREVIEW);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, selectedForumThread);
		selectedForumThread = refreshThread(selectedForumThread);
		this.enableEditMode(false);
		currentForumThreadEvent.fire(selectedForumThread);
		addToGlobalMessages("ThreadForReview", FacesMessage.SEVERITY_INFO);
		return Constants.NavNull;
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub
		if (currUser == null) {
			currUser = this.getCurrentUser();
		}
		ForumThread thread = (ForumThread) entity;

		if (action.equals(Constants.Action_UPDATE)) {
			threadEjb.updateForumThread(thread);

		}
		if (action.equals(Constants.Action_CREATE)) {
			threadEjb.createForumThread(thread);

			categoryEjb.updateNumOfThread(belongingCategory, +1);
			userEjb.updateNumOfPosts(currUser, +1);
		}
		if (action.equals(Constants.Action_DELETE)) {
			threadEjb.unDashBoardThread(thread);
			threadEjb.deleteForumThread(thread);

			categoryEjb.updateNumOfThread(belongingCategory, -1);
			userEjb.updateNumOfPosts(thread.getOwner(), -1);
		}
		if (action.equals(Constants.Action_DASHBOARD)) {

			threadEjb.dashBoardThread(thread);
		}
		if (action.equals(Constants.Action_UNDASHBOARD)) {

			threadEjb.unDashBoardThread(thread);
		}
		if (action.equals(Constants.Action_APPROVE)) {

			threadEjb.updateStatus(thread, ThreadStatus.APPROVED);
		}
		if (action.equals(Constants.Action_REJECT)) {

			threadEjb.updateStatus(thread, ThreadStatus.REJECTED);
		}

	}

	/**
	 * @param thread
	 * @return
	 */
	public void deleteThread(ForumThread thread) {

		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, thread);
	}

	public void topThread(ForumThread thread, boolean top) {

		thread.setTopped(top);
		thread.setToppedTime(new Date());
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, thread);
	}

	/**
	 * get the loaded thread
	 * 
	 * @return
	 */
	public ForumThread getLoadedThread() {
		ForumThread loadedThread = threadEjb.getLoadedThreadById(selectedForumThread.getId());
		return loadedThread;
	}

	/**
	 * 
	 * @param rating
	 *            values [1,-1,0] Score: number of votes
	 */
	public void handleVotes(int rating) {
		User currUser = login.getCurrentUser();
		if (currUser == null) {
			setBizErrorNSkipToResp("notLoginYet");
			return;

		} else {
			boolean syadminOrCategoryOwner = this.ownerForCurrCategory || login.isUserInSysadmin();
			boolean isValidVotes = threadEjb.handleVotes(currUser, this.selectedForumThread, rating, syadminOrCategoryOwner);
			if (!isValidVotes) {
				setBizErrorNSkipToResp("alreadyVoted");
			}

		}
	}

	public AtomicInteger getRating(int id) {
		return ratingCache.getRating(new CacheKey(id, CacheType.THREAD));
	}

	public AtomicInteger getVotes(int id) {
		return ratingCache.getVote(new CacheKey(id, CacheType.THREAD));
	}

	/**
	 * concurrent access is to be considered later. should be fine since it cause insert of the recordd on the db
	 */
	public void stickThread() {
		User user = login.getCurrentUser();
		user = userEjb.getUserWithEagerStickyThread(user.getId());

		user.getStickyThreads().add(this.selectedForumThread);
		// selectedForumThread =
		// threadEjb.getThreadWithLoadedStickiedByUserById(this.selectedForumThread.getId());
		// this.selectedForumThread.getStickiedByUsers().add(user);
		// threadEjb.updateForumThreadWithAuthorization(this.selectedForumThread);
		userEjb.updateUser(user);

	}

	/**
	 * concurrent access is to be considered later.
	 */
	public void unStickThread() {
		User user = login.getCurrentUser();
		user = userEjb.getUserWithEagerStickyThread(user.getId());
		user.getStickyThreads().remove(this.selectedForumThread);
		userEjb.updateUser(user);

	}

	public void lockThread(boolean status) {
		this.selectedForumThread = threadEjb.getForumThreadById(this.selectedForumThread.getId());
		this.selectedForumThread.setLocked(status);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, selectedForumThread);
	}

	public void enableEditMode(boolean editMode) {
		if (editMode) {
			selectedForumThread = this.refreshThread(selectedForumThread);
		}
		this.setEditMode(editMode);
	}

	/**
	 * put thread on the main dashboard
	 * 
	 * @param thread
	 */
	public void dashBoardThread(ForumThread thread) {
		processBusinessWithAuthorizationCheck(Constants.Action_DASHBOARD, thread);
		this.selectedForumThread = threadEjb.getForumThreadById(thread.getId());
		FacesMessage message = Messages.getMessage("", "SUCCEED", null, false);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void unDashBoardThread(ForumThread thread) {
		processBusinessWithAuthorizationCheck(Constants.Action_UNDASHBOARD, thread);
		this.selectedForumThread = threadEjb.getForumThreadById(thread.getId());
		FacesMessage message = Messages.getMessage("", "SUCCEED", null, false);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void resetDataTableFirst() {
		this.getSessionMap().put(Constants.DATATABLE_FIRST, 0);
	}

	public String openListThreads(String outcome) {
		this.getSessionMap().put(Constants.DATATABLE_FIRST, 0);
		lazyThreadDataModel.setSource(Constants.USER_TAB_GENERAL);
		return "/thread/listthreads.xhtml?ctgtThdId=" + outcome + "&faces-redirect=true";
	}

	/**
	 * initialize parentEvent and thread source for event from event/index.xhtml
	 */
	public void prepareFeatchAllThread4Event(ForumCategory category) {
		int id = -1;
		Object parent_thread_id = this.getSessionMap().get(Constants.PARENT_THREAD_ID);
		if (parent_thread_id != null) {
			id = Integer.parseInt((String) parent_thread_id);
			Object belongEvent = this.getSessionMap().get(Constants.BelongingEvent);
			if (null != belongEvent && ((com.junhong.forum.entity.Event) belongEvent).getId() == id) {
				parentEvent = (com.junhong.forum.entity.Event) belongEvent;
			} else {
				parentEvent = eventEjb.getEventById(id);
				this.getSessionMap().put(Constants.BelongingEvent, parentEvent);
			}
		} else {
			parentEvent = (com.junhong.forum.entity.Event) this.getSessionMap().get(Constants.BelongingEvent);
		}

		this.getSessionMap().put(Constants.BelongingEvent, parentEvent);
		if (category == null) {
			category = new ForumCategory();
			category.setId(Constants.CategoryId_For_All_Threads);
		}
		this.getSessionMap().put(Constants.BelongingCategory, category);
		lazyThreadDataModel.setSource(Constants.THREAD_SOURCE_EVENT);

	}

	public void approve(ForumThread thread) {
		thread.setReviewedBy(login.getCurrentUser());
		processBusinessWithAuthorizationCheck(Constants.Action_APPROVE, thread);
		this.selectedForumThread = threadEjb.getForumThreadById(thread.getId());
	}

	public void reject(ForumThread thread) {
		thread.setReviewedBy(login.getCurrentUser());
		processBusinessWithAuthorizationCheck(Constants.Action_REJECT, thread);
		this.selectedForumThread = threadEjb.getForumThreadById(thread.getId());
	}

	/**
	 * get the newest thread for the given category
	 * 
	 * @param category
	 */
	public void retrieveNewestThread() {
		ForumCategory category = (ForumCategory) this.getSessionMap().get(Constants.BelongingCategory);
		if (category != null) {
			this.newestThreads = threadEjb.getThreadListByCategory(category, 0, 10, null, false);
		}
	}

	/* -------------getter/setter----------------- */

	public ForumThread getSelectedForumThread() {
		return selectedForumThread;
	}

	public void setSelectedForumThread(ForumThread selectedForumThread) {
		this.selectedForumThread = selectedForumThread;
	}

	public ForumCategory getBelongingCategory() {
		return belongingCategory;
	}

	public void setBelongingCategory(ForumCategory belongingCategory) {
		this.belongingCategory = belongingCategory;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public LazyThreadDataModel getLazyThreadDataModel() {
		return lazyThreadDataModel;
	}

	public void setLazyThreadDataModel(LazyThreadDataModel lazyThreadDataModel) {
		this.lazyThreadDataModel = lazyThreadDataModel;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the ownerForCurrCategory
	 */
	public boolean isOwnerForCurrCategory() {
		return ownerForCurrCategory;
	}

	/**
	 * @param ownerForCurrCategory
	 *            the ownerForCurrCategory to set
	 */
	public void setOwnerForCurrCategory(boolean ownerForCurrCategory) {
		this.ownerForCurrCategory = ownerForCurrCategory;
	}

	public List<ForumThread> getNewestThreads() {
		return newestThreads;
	}

	public void setNewestThreads(List<ForumThread> newestThreads) {
		this.newestThreads = newestThreads;
	}

	public LazyThread4AdminDataModel getLazyThread4AdminDataModel() {
		return lazyThread4AdminDataModel;
	}

	public void setLazyThread4AdminDataModel(LazyThread4AdminDataModel lazyThread4AdminDataModel) {
		this.lazyThread4AdminDataModel = lazyThread4AdminDataModel;
	}

	public SelectItem[] getThreadStatusFilterOptions() {
		return threadStatusFilterOptions;
	}

	public void setThreadStatusFilterOptions(SelectItem[] threadStatusFilterOptions) {
		this.threadStatusFilterOptions = threadStatusFilterOptions;
	}

}
