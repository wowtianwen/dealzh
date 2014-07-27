/**
 * forum zhanjung
 */
package com.junhong.forum.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.junhong.auth.entity.RoleType;
import com.junhong.auth.entity.UserStatus;
import com.junhong.message.domain.MessageStatus;
import com.junhong.util.WebConfigUtil;

/**
 * @author zhanjung
 * 
 */
@Named
@ApplicationScoped
// TODO not sure if concurrencyManagement and lock will work with
// applicationScope
// @ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
// @Lock(LockType.READ)
public class Constants {
	@Inject
	private Logger	logger;

	public Constants() {

	}

	public static final String						BLOCKQUOTETAG								= "<blockquote class='quoteBlockQuote'>";
	public static final String						BLANK										= "";
	public static final String						ZEROES										= "0";

	public static final String						REPLYQUOTEWATERMARK							= "Please type here to reply ";
	public static final String						Yes											= "Yes";
	public static final String						No											= "No";
	public static final String						SUCCESS										= "SUCCESS";
	public static final String						FAIL										= "FAIL";
	public static final String						NOTVALID									= "NOTVALID";
	public static final String						NEWUSERID									= "NEWUSERID";
	public static final String						DOMAIN										= "DOMAIN";

	public static final String						CurrentUser									= "CURRENTUSER";
	public static final String						NavSuccess									= "SUCCESS";
	public static final String						ThreadNeedReview							= "REVIEW";
	public static final String						NavLogin									= "LOGIN";
	public static final String						NAVCREATETHREAD								= "CREATETHREAD";
	public static final String						NAVCREATEREPLY								= "CREATEREPLY";
	public static final String						NavNull										= "";
	public static final String						Action_READ_ALL								= "READ";
	public static final String						Action_CREATE								= "CREATE";
	public static final String						Action_UPDATE								= "UPDATE";
	public static final String						Action_DELETE								= "DELETE";
	public static final String						Action_DASHBOARD							= "DASHBOARD";
	public static final String						Action_UNDASHBOARD							= "UNDASHBOARD";
	public static final String						Action_PROMOTE								= "PROMOTE";
	public static final String						Action_APPROVE								= "APPROVE";
	public static final String						Action_REJECT								= "REJECT";

	public static final String						POSTTHREAD_LOCATION							= "FROMPOSTTHREAD";
	public static final String						POSTREPLY_LOCATION							= "FROMPOSTREPLY";
	public static final String						LOGIN_LOCATION								= "LOGIN";
	public static final String						CURRENT_LOCATION							= "CURRENTLOCATION";
	public static final String						NavLogout									= "LOGOUT";
	public static final String						LOGINTYPE									= "LOGINTYPE";

	// public static final String Belonging_Category = "BELONGINGCATEGORY";

	public static final String						USERGROUP_REGISTERED						= "REGISTERED";

	public static final String						PROFILE_TAB_BASICPROFILE					= "BASICPROFILE";
	public static final String						PROFILE_TAB_MESSAGES						= "MESSAGES";
	public static final String						PROFILE_TAB_CHANGE_PASSWORD					= "CHANGEPASSWORD";
	public static final String						PROFILE_TAB_MYPOST							= "MYPOST";
	public static final String						CURRENT_CATEGORY							= "currentCategory";
	public static final String						CURRENT_NEWSCATEGORY						= "currentNewsCategory";
	public static final String						BELONG_THREAD_ID							= "belongThreadId";
	public static final String						CURRENT_NEWS_INVIEW							= "currentNewsInView";
	public static final String						blockQuoteLine								= "----------";
	public static final String						blockQuoteStyle								= " 'background: #F7F7F7;border: 1px dashed green; clear: left;  margin-left: 0;   padding: 5px 10px;'";

	/*-----------------Message-------------------------*/

	public static String							Message_Inbox								= "Inbox";
	public static String							Message_Sent								= "Sent";
	public static String							Message_Archieve							= "Archieve";
	public static String							Message_Draft								= "Draft";
	public static final String						Message_Status_NEW							= MessageStatus.NEW.toString();
	public static final String						Number_Of_New_Message						= "NumberOfNewMessages";
	public static final String						Message_Bundle								= "resources.application";

	/*-----------------News-------------------------*/

	public static final String						Display_Type_Tab							= DisplayType.TAB.toString();
	public static final String						Display_Type_Regular						= DisplayType.REGULAR.toString();
	public static final String						NewsPoster_Role_Type						= RoleType.NEWSPOSTER.toString();
	/*-----------------Thread-------------------------*/

	public static final String						Thread_Type_Regular							= ThreadType.REGULAR.toString();
	public static final String						Thread_Type_Announce						= ThreadType.ANNOUNCE.toString();

	/*-----------------threadStaging-------------------------*/

	public static final String						STATUS_PENDING								= RecordStatus.PENDING.toString();

	/*-----------------webiste Index page Display Type-------------------------*/
	public static final String						Display_Type_Regular_Main_Index				= DisplayTypeMainPage.REGULAR.toString();
	public static final String						Display_Type_Mixed_Main_Index				= DisplayTypeMainPage.MIXED.toString();

	/*-----------------User-------------------------*/
	public static final String						USER_TAB_PAYMENT_METHOD						= "PAYMENTMETHOD";
	public static final String						USER_TAB_PROFILE_ACCOUNT_SUMMARY			= "PROFILEACCOUNTSUMMARY";
	public static final String						USER_TAB_CASH_BACK_HISTORY					= "CASHBACKHISTORY";
	public static final String						USER_TAB_ORDER_INQUERY						= "ORDERINQUERYList";
	public static final String						USER_TAB_PROFILE_PIC						= "PROFILEPIC";
	public static final String						USER_TAB_BASICPROFILE						= "BASICPROFILE";
	public static final String						USER_TAB_CHANGEPWD							= "CHANGEPASSWORD";
	public static final String						USER_TAB_MYPOST								= "MYPOST";
	public static final String						USER_TAB_MYREPLY							= "MYREPLY";
	public static final String						USER_TAB_MYWATCHLIST						= "MYWATCHLIST";
	public static final String						USER_TAB_NEW_CASHBACK_WITHDRAW_REQUEST		= "NEWCASHBACKWITHDRAWREQUEST";
	public static final String						USER_TAB_TELL_A_FRIEND						= "TELLAFRIEND";
	public static final String						MANAGEUSER									= "MANAGEUSER";
	public static final String						USER_TAB_NEW_ORDER_INQUERY					= "NEWORDERINQUERY";
	public static final String						USER_MYPOST_THREAD_STATUS					= "MYPOSTTHREADSTATUS";
	public static final String						PENDING_THREAD_STATUS						= "PENDINGTHREADSTATUS";
	public static final String						EDITME										= "Edit Me";
	@NotSupportNameToMap
	public static final Map<ThreadStatus, String>	USER_THREADSTATUS_TO_MYPOST_TAB_TITLE__MAP	= new HashMap<ThreadStatus, String>(3);

	// this is for indexthreadsforAllCategory and listthreads for each category
	public static final String						USER_TAB_GENERAL							= "GENERAL";
	public static final String						USER_PROFILE_PIC_DIR						= WebConfigUtil.getProfilePicDestFoler();
	public static final String						PROFILE_PIC_WIDTH							= "PROFILE_PIC_WIDTH";
	public static final String						PROFILE_PIC_HEIGHT							= "PROFILE_PIC_HEIGHT";
	public static final String						PROFILE_PIC_FILE_EXT						= ".jpg";
	public static final String						PROFILE_PIC_DEFAULT_VALUE					= "dog.jpg";
	public static final String						USER_RESET_PWD_EMAIL_SUBJECT				= "USER_RESET_PASSWORD_SUBJECT";
	public static final String						USER_RESET_PWD_EMAIL_CONTENT_TEMPLATE		= "USER_RESET_PASSWORD_CONTENT_TEMPLATE";
	public static final String						VIEWPROFILE_SOUCE_4LAZYDATAMODEL			= "VIEWPROFILESOURCE";
	public static final String						VIEWPROFILE_USER_4LAZYDATAMODEL				= "VIEWPROFILEUSER";
	public static final String						USER_REG_ACTIVATION_TIMEOUT					= "USER_REG_ACTIVATION_TIMEOUT";
	public static final String						CONTEXT_USER_ID								= "CONTEXTUSERID";

	/*-----------------------Help-----------------------------------*/
	public static final String						HELP_TERMS									= "TERMS";
	public static final String						HELP_FAQ									= "FAQ";
	public static final String						HELP_GENERALUSERULE							= "GENERALUSERULE";

	/** ------------Exception------------------------ */
	public static final String						NOTAUTHORIZEDACTION							= "NOTAUTHORIZEDACTION";

	/** ------------LinkShare webservice------------------------ */
	public static final String						GENDEEPLINKPREFIX							= "http://getdeeplink.linksynergy.com/createcustomlink.shtml?token=";

	/** ------------------global variable------------------------- */
	/*
	 * it is the category in which all its children categories are displaying in the listcategory page.
	 */
	public static final String						NAVIGATION_CATEGORY_ID						= "navCatId";

	public static final String						SUBMIT_SUCCEED								= "SUMMITSUCCEED";

	/*
	 * it is the category in which all the thread are displaying in the viewthread page.
	 */
	public static final String						CATEGORY__THREAD_ID							= "ctgtThdId";
	public static final String						CATEGORY__NEWS_ID							= "ctgtNewsId";
	public static final String						PARENT_THREAD_ID							= "parentThdId";

	public static final String						Login_Page									= "/login.xhtml";

	/*
	 * this is the category matching to CATEGORY__THREAD_ID
	 */
	public static final String						BelongingCategory							= "belongingCategory";
	public static final String						BelonginNewsgCategory						= "belongingNewsCategory";
	public static final String						BelongingEvent								= "belongingEvent";

	public static final String						Blank_String								= "";
	@NotSupportNameToMap
	public static final int							CategoryId_For_All_Threads					= -999999;

	// payment method
	public static final String						PAYMENT_METHOD_PAYPAL						= "Paypal";

	// Event
	public static final String						THREAD_SOURCE_EVENT							= "Event";

	public static final String						CLEAN_DB_LOOKBACK_PERIOD					= "clearDBLookBackPeriod";
	public static final String						DATATABLE_FIRST								= "FIRST";
	public static final String						SOURCE_LAZY_THREAD_DATA_MODEL				= "SOURCE";
	public static final String						USER_LAZY_THREAD_DATA_MODEL					= "USER";
	public static final String						USER_PROFILE_TAB							= "USER_PROFILE_TAB";

	// affilaite network

	public static final String						LINKSHARE									= "WWW.LINKSHARE.COM";
	// for DLG
	public static final String						CJ											= "WWW.CJ.COM";
	// For No DLG(Deep Link Generator)
	public static final String						CJ_NODLG									= "WWW.CJ.COM-NODLG";
	public static final String						PEPPERJAM									= "WWW.PEPPERJAMNETWORK.COM";
	public static final String						EBAYPARTNERNETWORK							= "WWW.EBAYPARTNERNETWORK.COM";
	public static final String						SHAREASALE									= "WWW.SHAREASALE.COM";
	public static final String						SKIMLINKS									= "WWW.SKIMLINKS.COM";
	public static final String						VIGLINK										= "WWW.VIGLINK.COM";

	// report destination folder
	public static final String						TRANSACTION_REPORT_DEST_FOLDER				= "/WEB-INF/transactionReport";
	@NotSupportNameToMap
	public static final int							FILE_DOWNLOAD_CONNECTION_TIMEOUT			= 1000 * 60 * 3;
	@NotSupportNameToMap
	public static final int							FILE_READ_TIMEOUT							= 1000 * 60 * 5;

	// store name
	public static final String						EBAY_DOMAIN									= "EBAY.COM";

	// cj authorization property
	public static final String						AuthorizationValue							= "00981ba677f40beca32e932607400dd00e05ee7d2eaa9b586eead11a4fe524cd2573a1998ce2b641d94e8b465ad536e3a716407022b7f2a7ca063e630e8a66e51f/4ab581fcf6bc089d5999e29abb064dc3257caeb5860c48d229b94c80235f00f6fabb016024bafce963d71cfc428d5e023ac121a339a870f7c61cf4d21c9211c9";
	public static final String						Authorization								= "Authorization";
	// skimlinks public key and private key
	public static final String						SKIMLINKS_PUBKEY							= "bc2ab34e6e6ed6b2f98d025a423b8de1";
	public static final String						SKIMLINKS_PRIKEY							= "7f262a4903e20f9a1b48fabb668d5f90";

	// system
	public static final String						ResourceApplication							= "resources.application";
	public static final String						THREADSTATUS_APPROVED						= ThreadStatus.APPROVED.toString();
	public static final String						THREADSTATUS_PENDINGFORREVIEW				= ThreadStatus.PENDINGREVIEW.toString();
	public static final String						THREADSTATUS_REJECTED						= ThreadStatus.REJECTED.toString();

	// user
	public static final String						USER_STATUS_PENDING							= UserStatus.PENDING.toString();
	public static final String						LOGIN_TYPE_SELF								= LoginType.SELF.toString();
	public static final String						LOGIN_TYPE_FACEBOOK							= LoginType.FACEBOOK.toString();
	public static final String						LOGIN_TYPE_QQ								= LoginType.QQ.toString();
	public static final String						LOGIN_TYPE_GOOGLE							= LoginType.GOOGLE.toString();
	public static final String						REG_BONUS									= "Registration Bonus";
	public static final String						DEAL_REFER_BONUS							= "Deal Refer Bonus";
	public static final String						DEALREFERDELIMIT							= "{D_D}";

	/**
	 * -------------------loading the fields into cache-------------------------
	 * 
	 * 
	 * following logi is used to load all the fields in the map, which is used in EL
	 * 
	 * -------------------------------------------------------------------------
	 */
	// "cache" holding all public fields by it's field name:
	private Map<String, String>						nameToValueMap								= new HashMap<String, String>();

	/**
	 * Puts all public static fields into the resulting Map. Uses the name of the field as key to reference it's in the Map.
	 * 
	 * @return a Map of field names to field values of all public static fields of this class
	 */
	@PostConstruct
	private void createNameToValueMap() {
		// init message inbox, draft etc depending on its locale
		this.Message_Inbox = Messages.getString(null, "Message_Inbox", null);
		this.Message_Draft = Messages.getString(null, "Message_Draft", null);
		this.Message_Sent = Messages.getString(null, "Message_Sent", null);
		this.Message_Archieve = Messages.getString(null, "Message_Archieve", null);
		logger.info("loading constants fields");
		Field[] publicFields = Constants.class.getFields();
		for (int i = 0; i < publicFields.length; i++) {
			Field field = publicFields[i];
			NotSupportNameToMap annotation = field.getAnnotation(NotSupportNameToMap.class);
			if (annotation != null) {
				continue;
			}
			String name = field.getName();
			try {
				nameToValueMap.put(name, (String) field.get(null));
			} catch (Exception e) {
				logger.error("Initialization of Constants class failed!{}", e);
			}
		}

		// populate USER_THREADSTATUS_TO_MYPOST_TAB_TITLE__MAP map
		String approved = Messages.getString(null, "MyPost_Approved", null);
		String pendingReview = Messages.getString(null, "MyPost_PendingReview", null);
		String rejected = Messages.getString(null, "MyPost_Reject", null);
		USER_THREADSTATUS_TO_MYPOST_TAB_TITLE__MAP.put(ThreadStatus.APPROVED, approved);
		USER_THREADSTATUS_TO_MYPOST_TAB_TITLE__MAP.put(ThreadStatus.PENDINGREVIEW, pendingReview);
		USER_THREADSTATUS_TO_MYPOST_TAB_TITLE__MAP.put(ThreadStatus.REJECTED, rejected);

	}

	public Map<String, String> getNameToValueMap() {
		if (nameToValueMap.size() == 0) {
			createNameToValueMap();
		}
		return nameToValueMap;
	}

	public void setNameToValueMap(Map<String, String> nameToValueMap) {
		this.nameToValueMap = nameToValueMap;
	}

	public static Map<ThreadStatus, String> getUserThreadstatusToMypostTabTitleMap() {
		return USER_THREADSTATUS_TO_MYPOST_TAB_TITLE__MAP;
	}

}
