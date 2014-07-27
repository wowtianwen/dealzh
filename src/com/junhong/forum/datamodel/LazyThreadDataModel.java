/**
 * jsf_demo
 * zhanjung
 */
package com.junhong.forum.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.entity.Event;
import com.junhong.forum.entity.ForumCategory;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.ThreadSorter;
import com.junhong.forum.service.CategoryServiceSingleton;
import com.junhong.forum.service.SystemCacheEjb;
import com.junhong.forum.service.ThreadEjb;

/**
 * @author zhanjung
 * 
 */
public class LazyThreadDataModel extends LazyDataModel<ForumThread> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8730272180146226564L;

	private ForumCategory		parentCategory;
	private Event				parentEvent;

	public Event getParentEvent() {
		return parentEvent;
	}

	public void setParentEvent(Event parentEvent) {
		this.parentEvent = parentEvent;
	}

	private User						user;
	// check where it is from: MAIN, or MYPOST or Event in the user profile page
	private String						source	= "-1";

	@EJB
	private ThreadEjb					threadEjb;

	@Inject
	private Login						login;
	@Inject
	private SystemCacheEjb				systemCacheEjb;
	@Inject
	private CategoryServiceSingleton	categorySingletonEjb;

	public LazyThreadDataModel() {
	}

	public LazyThreadDataModel(ForumCategory category) {
		this.parentCategory = category;
	}

	@Override
	public List<ForumThread> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<ForumThread> result = new ArrayList<ForumThread>();
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put(Constants.DATATABLE_FIRST, first);
		Object source = (String) sessionMap.get(Constants.SOURCE_LAZY_THREAD_DATA_MODEL);
		User user = (User) sessionMap.get(Constants.USER_LAZY_THREAD_DATA_MODEL);
		// For MyPost page.
		if (source != null && source.equals(Constants.USER_TAB_MYPOST)) {
			// load the threads for the given user
			ThreadStatus status = (ThreadStatus) sessionMap.get(Constants.USER_MYPOST_THREAD_STATUS);
			if (null == status) {
				status = ThreadStatus.APPROVED;
			}
			int dataSize = threadEjb.getForumThreadCountByOwner(user, status, false);
			this.setRowCount(dataSize);

			// paginate
			result = threadEjb.getThreadList(user, status, first, pageSize);

		} else if (source != null && source.equals(Constants.USER_TAB_MYWATCHLIST)) {
			// for my watch list
			User currUser = login.getCurrentUser();
			result = login.getUserAllStickyThreads(currUser);
			this.setRowCount(result.size());
		} else if (source != null && source.equals(Constants.THREAD_SOURCE_EVENT)) {
			// for event thread list
			parentEvent = (Event) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.BelongingEvent);

			parentCategory = (ForumCategory) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.BelongingCategory);
			if (parentCategory == null) {
				return result;
			}
			// fetch all threads regardless its category

			if (parentCategory.getId() == Constants.CategoryId_For_All_Threads) {
				boolean isUserAccessableToLockedThreads = login.isUserEvent_Owner(parentEvent) || login.isUserInSysadmin();
				int dataSize = threadEjb.getForumThreadCount4Event(isUserAccessableToLockedThreads, parentEvent, null);
				this.setRowCount(dataSize);

				// paginate
				int toppedThreadsSize = 0;
				List<ForumThread> toppedThreadList = new ArrayList<ForumThread>();
				List<Integer> toppedThreadIdList = new ArrayList<Integer>();

				// announce thread
				ForumCategory globalAnnounceCategory = categorySingletonEjb.getGlobalAnnouncementCategory();
				if (globalAnnounceCategory != null) {
					List<ForumThread> announceThreads = systemCacheEjb.getAnnounceThreadsPerCategory().get(globalAnnounceCategory);
					if (announceThreads != null) {
						toppedThreadsSize += announceThreads.size();
						for (ForumThread ft : announceThreads) {
							toppedThreadIdList.add(ft.getId());
							toppedThreadList.add(ft);
						}
					}
				}

				List<ForumThread> toppedThreadList2 = threadEjb.getToppedThreadList(parentEvent);
				toppedThreadsSize += toppedThreadList2.size();
				for (ForumThread ft : toppedThreadList2) {
					toppedThreadIdList.add(ft.getId());
					toppedThreadList.add(ft);

				}

				if (dataSize > pageSize) {

					/*
					 * this part is pretty confusing. need to consider carefully
					 * before changing the logic. you could draw a diagram for
					 * detail analysis.
					 */
					if (toppedThreadsSize < pageSize) {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadListByEvent(parentEvent, null, first - toppedThreadsSize, pageSize, toppedThreadIdList,
									isUserAccessableToLockedThreads);
						} else {
							result.addAll(toppedThreadList);
							result.addAll(threadEjb.getThreadListByEvent(parentEvent, null, first, pageSize - toppedThreadsSize, toppedThreadIdList,
									isUserAccessableToLockedThreads));
						}

					} else {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadListByEvent(parentEvent, null, first - toppedThreadsSize, pageSize, toppedThreadIdList,
									isUserAccessableToLockedThreads);
						} else if (first + pageSize < toppedThreadsSize) {
							result = toppedThreadList.subList(first, first + pageSize);
						} else {
							/*
							 * when first<stickthreadsize and first+pagesize
							 * >stickthreadsize
							 */
							result.addAll(toppedThreadList.subList(first, toppedThreadsSize));
							result.addAll(threadEjb.getThreadListByEvent(parentEvent, null, 0, pageSize - (toppedThreadsSize - first),
									toppedThreadIdList, isUserAccessableToLockedThreads));
						}

					}
				} else {
					result.addAll(toppedThreadList);
					result.addAll(threadEjb.getThreadListByEvent(parentEvent, null, 0, dataSize - toppedThreadsSize, toppedThreadIdList,
							isUserAccessableToLockedThreads));
				}

			} else {
				// for each category
				boolean isUserAccessableToLockedThreads = login.isUserCategory_Owner(parentCategory) || login.isUserEvent_Owner(parentEvent)
						|| login.isUserInSysadmin();
				int dataSize = threadEjb.getForumThreadCount4Event(isUserAccessableToLockedThreads, parentEvent, parentCategory);
				this.setRowCount(dataSize);

				// paginate
				int toppedThreadsSize = 0;
				List<ForumThread> toppedThreadList = new ArrayList<ForumThread>();
				List<Integer> toppedThreadIdList = new ArrayList<Integer>();

				// announce thread
				List<ForumThread> announceThreads = systemCacheEjb.getAnnounceThreadsPerCategory().get(parentCategory);
				if (announceThreads != null) {
					toppedThreadsSize += announceThreads.size();
					for (ForumThread ft : announceThreads) {
						toppedThreadIdList.add(ft.getId());
						toppedThreadList.add(ft);
					}
				}

				List<ForumThread> toppedThreadList2 = threadEjb.getToppedThreadList(parentEvent);
				toppedThreadsSize += toppedThreadList2.size();
				for (ForumThread ft : toppedThreadList2) {
					toppedThreadIdList.add(ft.getId());
					toppedThreadList.add(ft);

				}

				if (dataSize > pageSize) {

					/*
					 * this part is pretty confusing. need to consider carefully
					 * before changing the logic. you could draw a diagram for
					 * detail analysis.
					 */
					if (toppedThreadsSize < pageSize) {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadListByEvent(parentEvent, parentCategory, first - toppedThreadsSize, pageSize,
									toppedThreadIdList, isUserAccessableToLockedThreads);
						} else {
							result.addAll(toppedThreadList);
							result.addAll(threadEjb.getThreadListByEvent(parentEvent, parentCategory, first, pageSize - toppedThreadsSize,
									toppedThreadIdList, isUserAccessableToLockedThreads));
						}

					} else {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadListByEvent(parentEvent, parentCategory, first - toppedThreadsSize, pageSize,
									toppedThreadIdList, isUserAccessableToLockedThreads);
						} else if (first + pageSize < toppedThreadsSize) {
							result = toppedThreadList.subList(first, first + pageSize);
						} else {
							/*
							 * when first<stickthreadsize and first+pagesize
							 * >stickthreadsize
							 */
							result.addAll(toppedThreadList.subList(first, toppedThreadsSize));
							result.addAll(threadEjb.getThreadListByEvent(parentEvent, parentCategory, 0, pageSize - (toppedThreadsSize - first),
									toppedThreadIdList, isUserAccessableToLockedThreads));
						}

					}
				} else {
					result.addAll(toppedThreadList);
					result.addAll(threadEjb.getThreadListByEvent(parentEvent, parentCategory, 0, dataSize - toppedThreadsSize, toppedThreadIdList,
							isUserAccessableToLockedThreads));
				}
			}
		}
		// for general
		else {

			parentCategory = (ForumCategory) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.BelongingCategory);
			if (parentCategory == null) {
				return result;
			}
			// fetch all threads regardless its category
			if (parentCategory.getId() == Constants.CategoryId_For_All_Threads) {
				boolean isUserAccessableToLockedThreads = login.isUserInSysadmin();
				int dataSize = threadEjb.getForumThreadCount4AllCategory(isUserAccessableToLockedThreads);
				this.setRowCount(dataSize);

				// paginate
				int toppedThreadsSize = 0;
				List<ForumThread> toppedThreadList = new ArrayList<ForumThread>();
				List<Integer> toppedThreadIdList = new ArrayList<Integer>();

				// announce thread
				ForumCategory globalAnnounceCategory = categorySingletonEjb.getGlobalAnnouncementCategory();
				if (globalAnnounceCategory != null) {
					List<ForumThread> announceThreads = systemCacheEjb.getAnnounceThreadsPerCategory().get(globalAnnounceCategory);
					if (announceThreads != null) {
						toppedThreadsSize += announceThreads.size();
						for (ForumThread ft : announceThreads) {
							toppedThreadIdList.add(ft.getId());
							toppedThreadList.add(ft);
						}
					}
				}
				// topped thread

				List<ForumThread> toppedThreadList2 = threadEjb.getToppedThreadList4AllCategory();
				toppedThreadsSize += toppedThreadList2.size();
				for (ForumThread ft : toppedThreadList2) {
					toppedThreadIdList.add(ft.getId());
					toppedThreadList.add(ft);
				}

				if (dataSize > pageSize) {

					/*
					 * this part is pretty confusing. need to consider carefully
					 * before changing the logic. you could draw a diagram for
					 * detail analysis.
					 */
					if (toppedThreadsSize < pageSize) {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadList4AllCategory(first - toppedThreadsSize, pageSize, toppedThreadIdList,
									isUserAccessableToLockedThreads);
						} else {
							result.addAll(toppedThreadList);
							result.addAll(threadEjb.getThreadList4AllCategory(first, pageSize - toppedThreadsSize, toppedThreadIdList,
									isUserAccessableToLockedThreads));
						}

					} else {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadList4AllCategory(first - toppedThreadsSize, pageSize, toppedThreadIdList,
									isUserAccessableToLockedThreads);
						} else if (first + pageSize < toppedThreadsSize) {
							result = toppedThreadList.subList(first, first + pageSize);
						} else {
							/*
							 * when first<stickthreadsize and first+pagesize
							 * >stickthreadsize
							 */
							result.addAll(toppedThreadList.subList(first, toppedThreadsSize));
							result.addAll(threadEjb.getThreadList4AllCategory(0, pageSize - (toppedThreadsSize - first), toppedThreadIdList,
									isUserAccessableToLockedThreads));
						}

					}
				} else {
					result.addAll(toppedThreadList);
					result.addAll(threadEjb.getThreadList4AllCategory(0, dataSize - toppedThreadsSize, toppedThreadIdList,
							isUserAccessableToLockedThreads));
				}

			} else {

				// for the regular thread everyone will navigate through
				// category and
				// thread
				boolean isUserAccessableToLockedThreads = login.isUserCategory_Owner(parentCategory) || login.isUserInSysadmin();
				// rowCount
				int dataSize = threadEjb.getForumThreadCountByCategory(parentCategory, isUserAccessableToLockedThreads);
				this.setRowCount(dataSize);

				// paginate
				int toppedThreadsSize = 0;
				List<ForumThread> toppedThreadList = new ArrayList<ForumThread>();
				List<Integer> toppedThreadIdList = new ArrayList<Integer>();

				// announce thread
				List<ForumThread> announceThreads = systemCacheEjb.getAnnounceThreadsPerCategory().get(parentCategory);
				if (announceThreads != null) {
					toppedThreadsSize += announceThreads.size();
					for (ForumThread ft : announceThreads) {
						toppedThreadIdList.add(ft.getId());
						toppedThreadList.add(ft);
					}
				}

				List<ForumThread> toppedThreadList2 = threadEjb.getToppedThreadList(parentCategory);
				toppedThreadsSize += toppedThreadList2.size();
				for (ForumThread ft : toppedThreadList2) {
					toppedThreadIdList.add(ft.getId());
					toppedThreadList.add(ft);
				}

				if (dataSize > pageSize) {

					/*
					 * this part is pretty confusing. need to consider carefully
					 * before changing the logic. you could draw a diagram for
					 * detail analysis.
					 */
					if (toppedThreadsSize < pageSize) {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadListByCategory(parentCategory, first - toppedThreadsSize, pageSize, toppedThreadIdList,
									isUserAccessableToLockedThreads);
						} else {
							result.addAll(toppedThreadList);
							result.addAll(threadEjb.getThreadListByCategory(parentCategory, first, pageSize - toppedThreadsSize, toppedThreadIdList,
									isUserAccessableToLockedThreads));
						}

					} else {
						if (first >= toppedThreadsSize) {
							result = threadEjb.getThreadListByCategory(parentCategory, first - toppedThreadsSize, pageSize, toppedThreadIdList,
									isUserAccessableToLockedThreads);
						} else if (first + pageSize < toppedThreadsSize) {
							result = toppedThreadList.subList(first, first + pageSize);
						} else {
							/*
							 * when first<stickthreadsize and first+pagesize
							 * >stickthreadsize
							 */
							result.addAll(toppedThreadList.subList(first, toppedThreadsSize));
							result.addAll(threadEjb.getThreadListByCategory(parentCategory, 0, pageSize - (toppedThreadsSize - first),
									toppedThreadIdList, isUserAccessableToLockedThreads));
						}

					}
				} else {
					result.addAll(toppedThreadList);
					result.addAll(threadEjb.getThreadListByCategory(parentCategory, 0, dataSize - toppedThreadsSize, toppedThreadIdList,
							isUserAccessableToLockedThreads));
				}
			}

			// sort
			if (sortField != null) {
				Collections.sort(result, new ThreadSorter(sortField, sortOrder));
			}
		}
		return result;
	}

	public ForumCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(ForumCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(Constants.SOURCE_LAZY_THREAD_DATA_MODEL, source);
	}

	public void setUser(User user) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(Constants.USER_LAZY_THREAD_DATA_MODEL, user);
	}

}