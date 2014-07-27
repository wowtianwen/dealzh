/**
 * forum
 * zhanjung
 */
package com.junhong.message.dao;

import java.math.BigInteger;
import java.util.List;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.QueryConstants;
import com.junhong.forum.dao.EntityDAOImpl;
import com.junhong.message.domain.Message;
import com.junhong.message.domain.MessageStatus;
import com.junhong.message.domain.RecipientMessageStatus;

/**
 * @author zhanjung
 * 
 */
public class MessageDAO extends EntityDAOImpl<Message> {
	/* ------------instance Variable-------------- */

	/* -------------business logic----------------- */

	/**
	 * get all the incoming new message
	 * 
	 * @param currUser
	 * @return
	 */
	public List<Message> getAllIncomingNewMessages(User currUser) {
		return this
				.getEm()
				.createQuery(
						"select mess from Message mess inner join mess.recipientStatus rs where rs.status=:status and  :user = rs.user order by mess.id desc",
						Message.class).setParameter("status", MessageStatus.NEW).setParameter("user", currUser).getResultList();

	}

	/**
	 * get all the incoming message
	 * 
	 * @param currUser
	 * @return
	 */
	public List<Message> getAllIncomingMessages(User currUser) {
		return this.getEm()
				.createQuery("select mess from Message mess inner join mess.to t where :userId = rs.userId order by mess.id desc", Message.class)
				.setParameter("userId", currUser.getUserId()).getResultList();

	}

	/**
	 * get all the outgoing message
	 * 
	 * @param currUser
	 * @return
	 */
	public List<Message> getAllOutgoingMessages(User currUser) {

		return this.getEm().createQuery("select mess from Message mess where mess.from=:user order by mess.id desc", Message.class)
				.setParameter("user", currUser).getResultList();
	}

	/**
	 * Get the number of New Messages for the given user
	 * 
	 * @param currUser
	 * @return
	 */
	public long getNumberOfNewMessages(User currUser) {
		return this
				.getEm()
				.createQuery(
						"select count(mess.id) from Message mess inner join mess.recipientStatus rs where rs.status=:status and rs.user.userId=:userId ",
						Long.class).setParameter("userId", currUser.getUserId()).setParameter("status", MessageStatus.NEW).getSingleResult();
	}

	public Message fetchMessageEagerly(Message message) {
		String query = "select mess from Message mess left join fetch mess.recipientStatus rs  where mess.id=:id";
		return this.getEm().createQuery(query, Message.class).setParameter("id", message.getId()).getSingleResult();
	}

	public List<RecipientMessageStatus> findIncomingMessages(User currUser, int start, int size) {
		return this.getEm().createQuery(QueryConstants.Get_Incoming_Messages_With_Start_Size, RecipientMessageStatus.class)
				.setParameter("userId", currUser.getUserId()).setParameter("messStatus", MessageStatus.ARCHIEVED).setFirstResult(start)
				.setMaxResults(size).getResultList();
	}

	public List<Message> findOutgoingMessages(User currUser, int start, int size) {
		return this.getEm().createQuery(QueryConstants.Get_OutGoing_Messages_With_Start_Size, Message.class).setParameter("user", currUser)
				.setParameter("messStatus", MessageStatus.SENT).setFirstResult(start).setMaxResults(size).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Message> findArchieveMessages(User currUser, int start, int size) {
		return this.getEm().createNativeQuery(QueryConstants.Get_Archieve_Messages_With_Start_Size, Message.class).setParameter(1, currUser.getId())
				.setParameter(2, MessageStatus.ARCHIEVED.name()).setParameter(3, currUser.getId()).setParameter(4, MessageStatus.ARCHIEVED.name())
				.setFirstResult(start).setMaxResults(size).getResultList();
	}

	public List<Message> findDraftMessages(User currUser, int start, int size) {
		return this.getEm().createQuery(QueryConstants.Get_OutGoing_Messages_With_Start_Size, Message.class).setParameter("user", currUser)
				.setParameter("messStatus", MessageStatus.DRAFT).setFirstResult(start).setMaxResults(size).getResultList();
	}

	/**
	 * get total number of incoming messages including new and old
	 * 
	 * @param currUser
	 * @return
	 */
	public long getTotalNumberOfIncomingMessages(User currUser) {
		return this.getEm()
				.createQuery("select count(mess) from Message mess inner join mess.recipientStatus rs where :userId = rs.user.userId ", Long.class)
				.setParameter("userId", currUser.getUserId()).getSingleResult();
	}

	public long getTotalNumberOfOutgoingMessages(User currUser) {
		return this.getEm()
				.createQuery("select count(mess) from Message mess where mess.from=:user and mess.outBoxMessstatus=:messStatus", Long.class)
				.setParameter("user", currUser).setParameter("messStatus", MessageStatus.SENT).getSingleResult();

	}

	/**
	 * get total number of archieved messages from inbox or sent box
	 * 
	 * @param currUser
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public long getTotalNumberOfArchieveMessages(User currUser) {
		BigInteger totalNumber = new BigInteger("0");

		List<BigInteger> result = this.getEm().createNativeQuery(QueryConstants.Get_TotalNumberOfArchieve_Messages_With_Start_Size, "LongResut")
				.setParameter(1, currUser.getId()).setParameter(2, MessageStatus.ARCHIEVED.name()).setParameter(3, currUser.getId())
				.setParameter(4, MessageStatus.ARCHIEVED.name()).getResultList();
		for (BigInteger temp : result) {
			totalNumber = totalNumber.add(temp);
		}
		return totalNumber.longValue();
	}

	/**
	 * get the number of saved messages
	 * 
	 * @param currUser
	 * @return
	 */
	public long getTotalNumberOfDraftMessages(User currUser) {
		return this.getEm()
				.createQuery("select count(mess) from Message mess where mess.from=:user and mess.outBoxMessstatus=:messStatus", Long.class)
				.setParameter("user", currUser).setParameter("messStatus", MessageStatus.DRAFT).getSingleResult();
	}

	public void updateRecipientMessageStatus(List<Message> messageList, User user, MessageStatus messStatus) {

		StringBuilder updateQuery = new StringBuilder(
				"update RecipientMessageStatus rms  set rms.status=:messStatus where rms.message  IN(:messageList)  and rms.user=:user");
		this.getEm().createQuery(updateQuery.toString()).setParameter("messStatus", messStatus).setParameter("user", user)
				.setParameter("messageList", messageList).executeUpdate();
	}

	public void updateMessageOutBoxMessStatus(List<Message> messageList, MessageStatus messStatus) {
		StringBuilder updateQuery = new StringBuilder();
		updateQuery.append("update Message mess set mess.outBoxMessstatus=:messStatus where mess in (:messages)");
		this.getEm().createQuery(updateQuery.toString()).setParameter("messStatus", messStatus).setParameter("messages", messageList).executeUpdate();
	}

	/**
	 * 
	 * @param message
	 * @param user
	 * @param messStatus
	 */
	@Deprecated
	public void createRecipientStatus(Message message, User user, MessageStatus messStatus) {
		String insertQuery = "insert into message_user values(?1, ?2, ?3)";
		this.getEm().createNativeQuery(insertQuery).setParameter(1, message.getId()).setParameter(2, user.getId()).setParameter(3, messStatus.name())
				.executeUpdate();
	}

	/**
	 * mark the message to given status for the given user
	 * 
	 * @param messageList
	 * @param user
	 * @param messStatus
	 */
	public void markInboxMessageStatus(List<Message> messageList, User user, MessageStatus messStatus) {

		String updateQuery = "update RecipientMessageStatus rms set rms.status=:messStatus where rms.message in (:messageList) and rms.user=:user";
		this.getEm().createQuery(updateQuery).setParameter("messStatus", messStatus).setParameter("messageList", messageList)
				.setParameter("user", user).executeUpdate();

	}

	public void markSentBoxMessageStatus(List<Message> messageList, User user, MessageStatus messStatus) {

		String updateQuery = "update Message mess  set mess.outBoxMessstatus=:messStatus where mess in (:messageList) and mess.from=:user";
		this.getEm().createQuery(updateQuery).setParameter("messStatus", messStatus).setParameter("messageList", messageList)
				.setParameter("user", user).executeUpdate();

	}

	/**
	 * refresh the list of messages depends on eagerly
	 * 
	 * @param messageList
	 * @param isEarger
	 * @return
	 */
	public List<Message> fetchMessages(List<Message> messageList, boolean isEarger) {
		StringBuilder query = new StringBuilder("select mess from Message mess left join ");
		if (isEarger) {
			query.append("fetch ");
		}
		query.append("mess.recipientStatus rs ").append(" where mess in (:messList)");

		return this.getEm().createQuery(query.toString(), Message.class).setParameter("messList", messageList).getResultList();

	}

	public void removeMessages(List<Message> messageList) {

		String deleteQuery = "delete from Message mess where mess in(:messList)";
		this.getEm().createQuery(deleteQuery.toString()).setParameter("messList", messageList).executeUpdate();

	}

	public void removeMessageStatus(List<Message> messageList, User user) {
		String deleteQuery = "delete from RecipientMessageStatus rms where rms.message in (:messageList) and rms.user=:user ";
		this.getEm().createQuery(deleteQuery).setParameter("messageList", messageList).setParameter("user", user).executeUpdate();
	}
	/* -------------getter/setter----------------- */
}
