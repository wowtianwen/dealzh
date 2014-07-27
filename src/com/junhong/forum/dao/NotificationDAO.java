/**
 * forum
 * zhanjung
 */
package com.junhong.forum.dao;

import com.junhong.message.domain.Message;

/**
 * @author zhanjung
 * 
 *         this class contain logic of displaying messasges all in one place
 *         instead of group them into Inbox, Sent etc.
 *         can be referenced if decided to implemtned this way later
 */
@Deprecated
public class NotificationDAO extends EntityDAOImpl<Message> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/*
	 * public List<Message> getAllNewNotification(User currUser) {
	 * return this.getEm().createQuery(
	 * "select noti from Notification noti inner join noti.to t where noti.status=:status and  :user = t order by noti.createTime desc",
	 * Message.class).setParameter("status", MessageStatus.New).setParameter("user", currUser).getResultList();
	 * }
	 * public List<Notification> getAllNotifications(User currUser) {
	 * return this .getEm() .createQuery(
	 * "select noti from Notification noti inner join noti.to t where  :user = t order by noti.createTime desc"
	 * , Notification.class).setParameter("user", currUser).getResultList();
	 * }
	 * public List<Message> getAllNotifications(User currUser) {
	 * List<Message> result = new ArrayList<Message>();
	 * Set<User> contactUsers = new HashSet<User>();
	 * List<User> outGoingUsers = this.getEm().createQuery(
	 * "select distinct t from Notification noti inner join noti.to t where noti.isGroupMessage=false and    noti.from=:currUser ",
	 * User.class).setParameter("currUser", currUser).getResultList();
	 * List<User> incomingUsers =
	 * this.getEm().createQuery(" select distinct noti.from from Notification noti inner join noti.to t where  noti.isGroupMessage=false and t=:currUser "
	 * , User.class).setParameter("currUser", currUser).getResultList();
	 * contactUsers.addAll(outGoingUsers);
	 * contactUsers.addAll(incomingUsers);
	 * for each user @, select all notifications where from =current user
	 * and touser=@ union select all notifications where from=@ and
	 * toUser=current user
	 * for one to one message
	 * Query allNotificationQuery = this.getEm().createNativeQuery(
	 * "select n.* from notification n , notification_user u where n.isGroupMessage=false and  n.id=u.notification_id and n.from_id=:currUserId and u.to_id=:toUserId union select n.* from notification n , notification_user u where  n.isGroupMessage=false and n.id=u.notification_id and n.from_id=:toUserId and u.to_id=:currUserId"
	 * , Message.class);
	 * List<Message> temp = null;
	 * Message tempNoti = null;
	 * for (User user : contactUsers) {
	 * temp = allNotificationQuery.setParameter("currUserId", currUser.getId()).setParameter("toUserId", user.getId()).getResultList();
	 * // sort by the creattime shoudl be done
	 * Collections.sort(temp, new MessageComparator());
	 * tempNoti = temp.get(temp.size() - 1);
	 * result.add(tempNoti);
	 * // result.addAll(temp);
	 * }
	 * for Group messages ------sent out
	 * List<Message> groupNotificationResult = new ArrayList<Message>();
	 * String query = "select n from Notification n where n.isGroupMessage=true and n.from=:currUser order by n.createTime desc";
	 * List<Message> groupNotiList = this.getEm().createQuery(query, Message.class).setParameter("currUser", currUser).getResultList();
	 * List<List<User>> toUserLists = new ArrayList<List<User>>();
	 * List<User> toUsers = null;
	 * for (Message not : groupNotiList) {
	 * toUsers = new ArrayList<User>();
	 * for (User user : not.getTo()) {
	 * toUsers.add(user);
	 * }
	 * if (!toUserLists.contains(toUsers)) {
	 * toUserLists.add(toUsers);
	 * groupNotificationResult.add(not);
	 * }
	 * }
	 * for Group messages ------incoming
	 * List<Message> groupNotificationResultIncoming = new ArrayList<Message>();
	 * query = "select n from Notification n where n.isGroupMessage=true and :currUser member of n.to  order by n.createTime desc";
	 * groupNotiList = this.getEm().createQuery(query, Message.class).setParameter("currUser", currUser).getResultList();
	 * Map<User, List<User>> fromToUsers = new HashMap<User, List<User>>();
	 * for (Message not : groupNotiList) {
	 * toUsers = new ArrayList<User>();
	 * for (User user : not.getTo()) {
	 * toUsers.add(user);
	 * }
	 * Collections.sort(toUsers, new UserComparator());
	 * if (fromToUsers.containsKey(not.getFrom()) && fromToUsers.get(not.getFrom()).equals(toUsers)) {
	 * continue;
	 * } else {
	 * fromToUsers.put(not.getFrom(), toUsers);
	 * groupNotificationResultIncoming.add(not);
	 * }
	 * }
	 * groupNotificationResult.addAll(groupNotificationResultIncoming);
	 * // sort it and remove duplicate
	 * Collections.sort(groupNotificationResult, new MessageComparator());
	 * Map<List<User>, Message> map = new HashMap<List<User>, Message>();
	 * List<User> participatedParties = new ArrayList<User>();
	 * Message not = null;
	 * for (int i = groupNotificationResult.size() - 1; i >= 0; i--) {
	 * not = groupNotificationResult.get(i);
	 * participatedParties = new ArrayList<User>();
	 * participatedParties.add(not.getFrom());
	 * participatedParties.addAll(not.getTo());
	 * Collections.sort(participatedParties, new UserComparator());
	 * if (!map.containsKey(participatedParties)) {
	 * map.put(participatedParties, not);
	 * result.add(not);
	 * }
	 * }
	 * return result;
	 * }
	 * public long getNumberOfNewNotifications(User currUser) {
	 * return this.getEm().createQuery("select count(noti) from Notification noti, IN (noti.to) t where noti.status=:status and  :user= t ",
	 * Long.class).setParameter("status", MessageStatus.New).setParameter("user", currUser).getSingleResult();
	 * }
	 * public Notification fetchPredecessor(Notification notification) { return
	 * this
	 * .getEm().createQuery("select noti from Notification noti where noti=:noti"
	 * , Notification.class) .setParameter("noti",
	 * notification).getSingleResult(); }
	 * public List<Message> fetchPredecessor(Message notification, User currUser) {
	 * List<Message> result = new ArrayList<Message>();
	 * if (notification.isGroupMessage()) {
	 * // TODO consolidated message is not right.
	 * // for outgoing group messages
	 * String query = "select n from Notification n where n.isGroupMessage=true and n.from=:currUser order by n.createTime desc";
	 * List<Message> groupNotifications = this.getEm().createQuery(query, Message.class).setParameter("currUser", currUser).getResultList();
	 * List<User> currFromNTousers = new ArrayList<User>();
	 * currFromNTousers.add(notification.getFrom());
	 * for (User user : notification.getTo()) {
	 * currFromNTousers.add(user);
	 * }
	 * Collections.sort(currFromNTousers, new UserComparator());
	 * List<User> tempFromNToUsers = new ArrayList<User>();
	 * for (Message n : groupNotifications) {
	 * tempFromNToUsers = new ArrayList<User>();
	 * tempFromNToUsers.add(n.getFrom());
	 * for (User u : n.getTo()) {
	 * tempFromNToUsers.add(u);
	 * }
	 * Collections.sort(tempFromNToUsers, new UserComparator());
	 * if (currFromNTousers.equals(tempFromNToUsers)) {
	 * result.add(n);
	 * }
	 * }
	 * // for incoming group messages
	 * query = "select n from Notification n where n.isGroupMessage=true and :currUser member of n.to order by n.createTime desc";
	 * groupNotifications = this.getEm().createQuery(query, Message.class).setParameter("currUser", currUser).getResultList();
	 * for (Message n : groupNotifications) {
	 * tempFromNToUsers = new ArrayList<User>();
	 * tempFromNToUsers.add(n.getFrom());
	 * for (User u : n.getTo()) {
	 * tempFromNToUsers.add(u);
	 * }
	 * Collections.sort(tempFromNToUsers, new UserComparator());
	 * if (currFromNTousers.equals(tempFromNToUsers)) {
	 * result.add(n);
	 * }
	 * }
	 * Collections.sort(result, new MessageComparator());
	 * return result;
	 * }
	 * Set<User> contactUsers = new HashSet<User>();
	 * // outgoing message
	 * if (notification.getFrom().equals(currUser)) {
	 * notification = refreshNotification(notification);
	 * contactUsers.addAll(notification.getTo());
	 * } else {
	 * // incoming message
	 * contactUsers.add(notification.getFrom());
	 * }
	 * for each user @, select all notifications where from =current user
	 * and touser=@ union select all notifications where from=@ and
	 * toUser=current user
	 * Query allNotificationQuery = this.getEm().createNativeQuery(
	 * "select n.* from notification n , notification_user u where n.id=u.notification_id and n.from_id=:currUserId and u.to_id=:toUserId  and n.isGroupMessage=false union select n.* from notification n , notification_user u where n.id=u.notification_id and n.from_id=:toUserId and u.to_id=:currUserId and  n.isGroupMessage=false  order by createtime asc"
	 * , Message.class);
	 * List<Message> temp = null;
	 * for (User user : contactUsers) {
	 * temp = allNotificationQuery.setParameter("currUserId", currUser.getId()).setParameter("toUserId", user.getId()).getResultList();
	 * // sort by the creattime shoudl be done
	 * Collections.sort(temp, new MessageComparator());
	 * // result.add(temp.get(temp.size() - 1));
	 * result.addAll(temp);
	 * }
	 * return result;
	 * }
	 *//**
	 * refresh notification with toUsers is loaded eagerly.
	 * 
	 * @param notification
	 * @return
	 */
	/*
	 * public Message refreshNotification(Message notification) {
	 * return this.getEm().createQuery("select noti from Notification noti left join fetch noti.to t where noti=:noti",
	 * Message.class).setParameter("noti", notification).getSingleResult();
	 * }
	 */

	/* -------------getter/setter----------------- */
}
