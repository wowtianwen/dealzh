/**
 * forum
 * zhanjung
 */
package com.junhong.forum.common;

/**
 * @author zhanjung
 * 
 */
public interface QueryConstants {
	String	forumcategory_top_all								= "select c from ForumCategory c where c.parentCategory is NULL";
	String	forumcategory_total_count							= "select count(c) from ForumCategory c where c.parentCategory is NULL";
	String	newscategory_total_count							= "select count(c) from NewsCategory c where c.parentCategory is NULL";
	String	sub_forumcategory_total_count						= "select count(c) from ForumCategory c where c.parentCategory=";
	String	sub_newscategory_total_count						= "select count(c) from NewsCategory c where c.parentCategory=";
	String	find_SubCategory_By_parentCategoryId				= "select c from ForumCategory c where c.parentCategory.id=";
	// TODO the nuber of thread for each category.
	String	get_Total_Number_Of_Thread							= "select count() from ForumCategory c where c";
	/* Thread */
	String	GET_THREAD_COUNT_BY_CATEGORY						= "select count(ft) from ForumThread ft where ft.category=:category";
	String	GET_THREAD_COUNT_4_ALL_CATEGORY						= "select count(ft) from ForumThread ft  ";
	String	GET_THREAD_COUNT_4_EVENT							= "select count(ft) from ForumThread ft where ft.event=:event ";
	String	GET_THREAD_LIST_BY_CATEGORY							= "select ft from ForumThread ft where ft.category=:category order by ft.createTime desc";
	String	GET_THREAD_LIST_BY_CATEGORY_LIST_FILTER				= "select ft from ForumThread ft where ft.category=:category and ft.id NOT IN (:stickyThreadsId) order by ft.createTime desc";
	String	UPDATE_THREAD_NUMOFVIEW								= "update ForumThread  ft set ft.numberOfView=:numOfHit where ft.id=:id ";
	String	UPDATE_THREAD_VOTESNRATING							= "update ForumThread  ft set ft.votes=:votes, ft.rating=:rating where ft.id=:id ";
	String	GET_THREAD_COUNT_BY_OWNER							= "select count(ft) from ForumThread ft where ft.owner=:user and ft.status=:status ";
	String	GET_TOPPED_THREAD									= "select ft from ForumThread ft where ft.category=:category and  ft.isTopped=:isTopped  and ft.status=:status and ft.type=:type order by ft.toppedTime desc";
	String	GET_TOPPED_THREAD_4_ALL_CATEGORY					= "select ft from ForumThread ft where  ft.isTopped=:isTopped and ft.status=:status and ft.type=:type order by ft.toppedTime desc";
	String	GET_TOPPED_THREAD_EVENT								= "select ft from ForumThread ft where ft.event=:event and  ft.isTopped=:isTopped and ft.status=:status order by ft.toppedTime desc";

	/* reply */
	String	GET_REPLY_LIST_BY_THREAD							= "select fr from ForumReply fr where fr.thread=:parentThread";
	String	GET_REPLY_COUNT_BY_THREAD							= "select count(fr) from ForumReply fr where fr.thread=:parentThread";
	String	GET_REPLY_LIST_BY_OWNER								= "select fr from ForumReply fr where fr.owner=:user";
	String	GET_REPLY_COUNT_BY_OWNER							= "select count(fr) from ForumReply fr where fr.owner=:user";
	/* User */
	String	GET_USER_IDS_BY_PREFIX								= "select ur.userId from User ur where ur.userId like :prefix";
	/* Message */
	String	Get_Incoming_Messages_With_Start_Size				= "select rs from RecipientMessageStatus rs where :userId=rs.user.userId and rs.status!=:messStatus order by rs.message.id desc";
	String	Get_OutGoing_Messages_With_Start_Size				= "select mess from Message mess  where mess.from=:user  and mess.outBoxMessstatus=:messStatus order by mess.id desc";
	String	Get_Archieve_Messages_With_Start_Size				= "select mess.* from message mess  where mess.from_id=?1 and mess.outBoxMessstatus=?2   union select mess.* from message mess,message_user  rs where mess.id=rs.mess_id and  rs.user_id=?3 and rs.status=?4";

	/** News */
	String	GET_NEWS_COUNT_BY_CATEGORY							= "select count(news) from News news where news.newsCategory=:category";
	String	GET_NEWS_COUNT_BY_OWNER								= "select count(ft) from News news where news.owner=:user";
	String	UPDATE_NEWS_NUMOFVIEW								= "update News  news set news.numOfViews=:numOfHit where news.id=:id ";
	String	UPDATE_NEWS_VOTES									= "update News  news set news.votes=:votes  where news.id=:id ";
	String	GET_NEWS_LIST_BY_CATEGORY							= "select news from News news where news.category=:category order by news.createTime desc";
	String	GET_TOPPED_NEWS										= "select news from News news where news.newsCategory=:category and  news.isTopped=:isTopped order by news.toppedTime desc";

	/** Comments */
	/* reply */
	String	GET_COMMENT_LIST_BY_NEWS							= "select cmt from Comment cmt where cmt.news=:news order by cmt.createTime desc";
	String	GET_COMMENT_COUNT_BY_NEWS							= "select count(cmt) from Comment cmt where cmt.news=:news";

	/* native SQL query */
	String	Get_TotalNumberOfArchieve_Messages_With_Start_Size	= "select count(1) as totalNum from message mess where mess.from_id=?1 and mess.outBoxMessstatus=?2 union all select count(1) as totalNum from message mess,message_user  rs where mess.id=rs.mess_id and  rs.user_id=?3 and rs.status=?4";
}
