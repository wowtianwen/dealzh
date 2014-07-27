/**
 * forum
 * zhanjung
 */
package com.junhong.auth.backingbean;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.auth.entity.UserStatus;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.backingbean.AbstractBacking;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.LoginType;
import com.junhong.forum.common.Messages;
import com.junhong.forum.common.RecordStatus;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.common.UserCashBackRecordStatus;
import com.junhong.forum.datamodel.LazyCashBackWithdrawRequestDataModel;
import com.junhong.forum.datamodel.LazyOrderInqueryDataModel;
import com.junhong.forum.datamodel.LazyReplyDataModel;
import com.junhong.forum.datamodel.LazyThreadDataModel;
import com.junhong.forum.datamodel.LazyTransactionRecordDataModel;
import com.junhong.forum.datamodel.LazyUserCashBackHistoryDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.CashBackWithdrawRequest;
import com.junhong.forum.entity.ForumReply;
import com.junhong.forum.entity.OrderInquery;
import com.junhong.forum.entity.Store;
import com.junhong.forum.entity.UserCashBackHistory;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.StoreEjb;
import com.junhong.util.CommonUtil;
import com.junhong.util.EncryptionUtil;
import com.junhong.util.HttpUtil;
import com.junhong.util.PictureUtil;
import com.junhong.util.StringUtil;
import com.junhong.util.ViewScoped;
import com.junhong.util.WebConfigUtil;

@ViewScoped
@Named
public class UserBackingBean extends AbstractBacking {
	private static final long						serialVersionUID			= 01l;

	/* ------------instance Variable-------------- */

	@EJB
	private UserEjb									userEjb;

	@Inject
	private Login									login;

	@Inject
	private LazyThreadDataModel						myDataModel;

	@Inject
	private LazyReplyDataModel						myReplyDataModel;
	@Inject
	private LazyUserCashBackHistoryDataModel		cashBackHistoryDataModel;

	private User									user						= new User();
	// used in the profile page
	private User									basicUser					= new User();
	private String									profiletab					= Constants.USER_TAB_PROFILE_ACCOUNT_SUMMARY;

	private String									prefix;
	private ForumReply								reply;
	@Inject
	private Logger									logger;

	private boolean									isSystemProfilePic			= false;
	// used for paymentMethod.xhtml
	private String									paymentMethod;
	@Inject
	private CashBackWithdrawRequest					cashBackWithdrawRequest;
	@Inject
	private OrderInquery							orderInquery;
	@Inject
	private LazyCashBackWithdrawRequestDataModel	lazyCashBackWithdrawRequestDataModel;
	@Inject
	private LazyOrderInqueryDataModel				lazyOrderInqueryDataModel;
	@Inject
	private LazyTransactionRecordDataModel			lazyTransactionRecordDataModel;

	private SelectItem[]							recordStatusOptions;
	private SelectItem[]							cashBackStatusOptions;
	private String									filterField;
	@EJB
	private StoreEjb								storeEjb;

	private boolean									submitOrderInquerySuccess	= false;
	private List<ThreadStatus>						allThreadStatus				= new ArrayList<ThreadStatus>(3);
	private int										activeTab;
	@Inject
	private UserCashBackHistory						cashBackHistory;
	private List<User>								referredUserList;

	/* -------------business logic----------------- */

	@PostConstruct
	public void init() {
		restoreUserProfileTab();
		recordStatusOptions = CommonUtil.createFilterOptions();
		cashBackStatusOptions = CommonUtil.createFilterOptionsForCashBackStatus();
		initAllThreadStatus();
	}

	public void initAllThreadStatus() {
		allThreadStatus.add(ThreadStatus.APPROVED);
		allThreadStatus.add(ThreadStatus.PENDINGREVIEW);
		allThreadStatus.add(ThreadStatus.REJECTED);
	}

	public void restoreUserProfileTab() {
		// remember where the user was
		Object ob = this.getSessionMap().get(Constants.USER_PROFILE_TAB);
		if (ob != null) {
			this.profiletab = (String) ob;
		}
	}

	/**
	 * for basic user info update
	 */
	public void saveUserBasicInfo() {
		userEjb.updateUser(this.basicUser);
		FacesMessage message = Messages.getMessage("", "save_success", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("SAVE_SUCCESS", message.getSummary());

	}

	public void changePassword() {
		String oldPwd = (String) FacesContext.getCurrentInstance().getViewRoot().getViewMap(false).get("oldPassword");
		boolean isvalid = login.verifyUser(basicUser.getUserId(), oldPwd);
		FacesMessage message;
		if (isvalid) {
			this.basicUser.setPassword(EncryptionUtil.md5(this.basicUser.getPassword()));
			this.basicUser.setPasswordagain(EncryptionUtil.md5(this.basicUser.getPassword()));
			update(this.basicUser);
			message = Messages.getMessage("", "basic_profile_change_password_suc", null);
			message.setSeverity(FacesMessage.SEVERITY_INFO);

		} else {
			message = Messages.getMessage("", "basic_profile_change_password_fail", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
		}

		this.getRequestMap().put("CHANGE_PWD_RESULT", message.getSummary());
	}

	public void loadBasicUser(User user) {
		// refresh the user
		user = userEjb.getUserById(user.getId());
		if (null != user) {
			this.basicUser = user;
			this.paymentMethod = this.basicUser.getPaymentMethod();
			if (StringUtil.isNullOrBlank(this.basicUser.getPayPalEmail())) {
				this.basicUser.setPayPalEmail("Edit Me");
			}
			// initialize cashBackHistoryDataModel
			cashBackHistoryDataModel.setUser(user);
		}
		restoreUserProfileTab();
	}

	public String openUserProfile() {
		this.setProfiletab(Constants.USER_TAB_PROFILE_ACCOUNT_SUMMARY);
		resetFirstDataTable();
		return Constants.NavSuccess;
	}

	public String openUserProfile(String profiletab) {
		this.setProfiletab(profiletab);
		resetFirstDataTable();
		return Constants.NavSuccess;
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public String enroll() {
		User tempUser = this.user;
		tempUser.setUserGroup(this.findUserGroup(Constants.USERGROUP_REGISTERED));
		tempUser.setProfilePicName(Constants.PROFILE_PIC_DEFAULT_VALUE);

		// encrypt the password
		tempUser.setPassword(EncryptionUtil.md5(tempUser.getPassword()));
		tempUser.setPasswordagain(EncryptionUtil.md5(tempUser.getPasswordagain()));
		tempUser.setLastLogin(new Date());
		tempUser.setTrustedUser(true);
		tempUser.setLoginType(LoginType.SELF);
		String refererId = Constants.BLANK;
		Object obj = this.getSessionMap().get("referBy");
		if (obj != null) {
			refererId = (String) obj;
		}
		tempUser.setRefererId(refererId);
		userEjb.enrollUser(tempUser);
		login.initializeUserLogin(tempUser);
		// login.initializeUserLogin(tempUser);
		// this.getFlash().put("regEmail", tempUser.getEmail());
		this.getRequestMap().put("regEmail", tempUser.getEmail());

		return Constants.NavSuccess;
	}

	public String resendRegActivationCode() {
		String userId = this.user.getUserId();
		User user = userEjb.getUserByUserId(userId);
		userEjb.resendRegActivationCode(userId);
		FacesMessage mess = Messages.getMessage(null, "SendRegActivationCode", new String[] { user.getEmail() });
		mess.setSeverity(FacesMessage.SEVERITY_INFO);
		this.getFacesContext().addMessage(null, mess);
		return null;
	}

	public String resendRegActivationCode(User user) {
		String userId = user.getUserId();
		user = userEjb.getUserByUserId(userId);
		if (!user.getStatus().equals(UserStatus.PENDING)) {
			setBizErrorNSkipToResp("userActive");
			return null;
		}
		userEjb.resendRegActivationCode(userId);
		FacesMessage mess = Messages.getMessage(null, "SendRegActivationCode", new String[] { user.getEmail() });
		mess.setSeverity(FacesMessage.SEVERITY_INFO);
		this.getFacesContext().addMessage(null, mess);
		return null;
	}

	public void update(User user) {
		userEjb.updateUser(user);
	}

	public void updateUser() {
		basicUser.setPaymentMethod(paymentMethod);
		update(basicUser);
		FacesMessage message = Messages.getMessage("", "SUCCEED", null, false);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		this.getFacesContext().addMessage(null, message);
	}

	public void delete(User user) {
		userEjb.deleteUser(user);
	}

	public User getUser(int id) {
		return userEjb.getUserById(id);
	}

	/**
	 * check if the password entered twice matches with each other
	 * 
	 * @param context
	 * @param component
	 * @param value
	 */
	public void checkPasswordMatch(FacesContext context, UIComponent component, Object value) {

		UIInput passwordInput = (UIInput) component.findComponent("password");
		Object pwdObject = passwordInput.getLocalValue() == null ? passwordInput.getSubmittedValue() : passwordInput.getLocalValue();
		String pwd = pwdObject.toString();
		String pwdagain = value.toString();
		if (!pwd.equals(pwdagain)) {

			FacesMessage message = Messages.getMessage("", "PassWordNOTMatch", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
			/*
			 * this is another way to show the validation error message FacesContext context1 = FacesContext.getCurrentInstance();
			 * context1.addMessage(component.getClientId(), message); context1.renderResponse();
			 */
		}

	}

	public void checkEmailExistence(FacesContext contet, UIComponent component, Object value) {
		String email = value.toString();
		boolean isExitence = userEjb.checkEmailExistence(email);
		if (isExitence) {
			FacesMessage message = Messages.getMessage("", "duplicateEmail", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);

		}

	}

	/**
	 * 
	 * @param contet
	 * @param component
	 * @param value
	 */
	public void checkUserIdExistence(FacesContext contet, UIComponent component, Object value) {
		String userId = value.toString();
		User user1 = null;
		if (!StringUtil.isNullOrBlank(userId)) {
			user1 = userEjb.getUserByUserId(userId);
		}
		if (user1 == null) {
			FacesMessage message = Messages.getMessage("", "userIdNotExist", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		} else if (!user1.getStatus().equals(UserStatus.PENDING)) {
			FacesMessage message = Messages.getMessage("", "userActive", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);

		}

	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// LEAVE EMPTY INDENDEDLY

	}

	public List<String> getUserids(String query) {
		return userEjb.getUserIds(query);
	}

	public void editUser() {
		user = userEjb.getUserByUserId(user.getUserId());

	}

	public void lockUser(boolean lock) {
		user = userEjb.getUserByUserId(user.getUserId());
		user.setLocked(lock);
		userEjb.updateUser(user);
	}

	public void trustUser(boolean trustUser) {
		user = userEjb.getUserByUserId(user.getUserId());
		user.setTrustedUser(trustUser);
		userEjb.updateUser(user);

	}

	public void deleteUser() {
		user = userEjb.getUserByUserId(user.getUserId());
		userEjb.deleteUser(user);
		user = new User();
		FacesMessage message = Messages.getMessage("", "user_delete_suc_message", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void validateOldPassword(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return;
		}
		String oldpwd = (String) value;

		boolean isValid = login.verifyUser(basicUser.getUserId(), oldpwd);
		if (!isValid) {
			FacesMessage message = Messages.getMessage("", "invalid_password", null);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);

			throw new ValidatorException(message);
		}
	}

	/**
	 * get all init messageboxcontent.xhtml
	 * 
	 * @param profileTab
	 */
	public void initMessages(String profileTab) {
		this.setProfiletab(profileTab);
		// messageBackingBean.fetchAllInboxSentArchieveMessages();
	}

	public void loadMyPosts() {
		myDataModel.setSource(Constants.USER_TAB_MYPOST);
	}

	public void loadMyWatchlist() {
		myDataModel.setSource(Constants.USER_TAB_MYWATCHLIST);
	}

	public void viewReply(ForumReply reply) {
		this.reply = reply;

	}

	/**
	 * listener if Systme profile pic is selected
	 */
	public void updateProfilePic() {

		if (this.isSystemProfilePic) {
			String profilePicName = this.basicUser.getProfilePicName();
			updateCurrUserProfilePic(profilePicName);
		}
		// set success message
		FacesMessage message = Messages.getMessage("", "save_success", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("SAVE_SUCCESS", message.getSummary());

	}

	/**
	 * handler for profile picture upload
	 * 
	 * @param event
	 */
	public void handleProfilePicFileUpload(FileUploadEvent event) {
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		UploadedFile uploadedFile = event.getFile();

		// deprive file name;
		StringBuilder fileName = new StringBuilder(getCurrentUser().getUserId());
		String profilePicFileName = fileName.append(".jpg").toString();

		String destDir = WebConfigUtil.profilePicDir;
		if (destDir == null) {
			String profileRelativePath = File.separator + "resources" + File.separator + "profilepic";
			destDir = HttpUtil.getRealApplicationPath(getFacesContext()) + profileRelativePath;
		}
		logger.info("profile pic destination Dir:" + destDir);

		File file = null;
		OutputStream os = null;
		try {
			File pic_dir = new File(destDir);
			if (!pic_dir.exists()) {
				pic_dir.mkdir();
			}

			BufferedImage resizedImg = ImageIO.read(uploadedFile.getInputstream());
			// shrink the pictures
			resizedImg = PictureUtil.resize(resizedImg);

			file = new File(destDir, profilePicFileName);
			// check if the file exist
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			os = new FileOutputStream(file);
			ImageIO.write(resizedImg, "jpg", os);
		} catch (FileNotFoundException e) {
			logger.error("Not able to save the profile pic. Throw File not found error");
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error("Not able to save the profile pic. throw IOException");
			logger.error(e.getMessage());
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}

		// update profilePicname
		updateCurrUserProfilePic(profilePicFileName);

		// set success message
		FacesMessage message = Messages.getMessage("", "save_success", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("SAVE_SUCCESS", message.getSummary());

	}

	/**
	 * listener for system profile picture change
	 * 
	 * @param profilePicFileName
	 */
	private void updateCurrUserProfilePic(String profilePicFileName) {
		User currUser = this.getCurrentUser();
		currUser = userEjb.getUserByUserId(currUser.getUserId());
		if (currUser.getProfilePicName() == null || !currUser.getProfilePicName().equalsIgnoreCase(profilePicFileName)) {
			currUser.setProfilePicName(profilePicFileName);
			userEjb.updateUser(currUser);
			this.setCurrentUser(currUser);
		}
	}

	// ------------------------------reset password

	public void verifyUserIdExist(FacesContext fc, UIComponent component, Object value) {

		if (null != value) {
			String userId = (String) value;
			if (userEjb.getUserByUserId(userId) == null) {
				FacesMessage message = Messages.getMessage(null, "INVALID_USERID", null, true);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				this.getFacesContext().addMessage(component.getClientId(), message);
				throw new ValidatorException(message);

			}
		}

	}

	public void verifyEmailExist(FacesContext fc, UIComponent component, Object value) {

		UIInput userIdComp = (UIInput) component.findComponent("userId");
		Object userIdOb = userIdComp.getLocalValue() == null ? userIdComp.getSubmittedValue() : userIdComp.getLocalValue();
		// or you can use userIdComp.getValue() to get the value

		String userId = (String) userIdOb;
		if (null != userId) {
			User user = userEjb.getUserByUserId(userId);
			if (user == null) {
				FacesMessage message = Messages.getMessage(null, "INVALID_USERID", null, false);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				this.getFacesContext().addMessage(component.getClientId(), message);
				throw new ValidatorException(message);

			} else {
				user = userEjb.getUserByUserId(userId);
				if (null != user.getEmail()) {
					String email = (String) value;
					if (!user.getEmail().equalsIgnoreCase(email)) {
						FacesMessage message = Messages.getMessage(null, "INVALID_EMAIL", null, false);
						message.setSeverity(FacesMessage.SEVERITY_ERROR);
						this.getFacesContext().addMessage(component.getClientId(), message);
						throw new ValidatorException(message);

					}
				}
			}
		}

	}

	/**
	 * listener for reset password
	 */
	public void resetPassword() {
		userEjb.resetPassword(this.user.getUserId());
		FacesMessage message = Messages.getMessage(null, "USER_RESET_EMAIL_SENT_MESSAGE", null, false);
		FacesContext.getCurrentInstance().addMessage(null, message);
		this.getRequestMap().put("RESETPWDEMAILSUC", "TRUE");
	}

	/**
	 * validate the reg activation code
	 */
	public void validateRegActivateCode() {

		String result = Constants.FAIL;
		String userId = (String) this.getRequestMap().get("userId");
		String activateCode = (String) this.getRequestMap().get("activateCode");
		if (StringUtil.isNullOrBlank(userId) || StringUtil.isNullOrBlank(activateCode)) {

		} else {
			User user = userEjb.getUserByUserId(userId);
			if (user != null) {
				if (!user.getStatus().equals(UserStatus.PENDING)) {
					result = Constants.NOTVALID;
				} else {
					String regActivationTimeout = WebConfigUtil.getProp(Constants.USER_REG_ACTIVATION_TIMEOUT);
					if (StringUtil.isNullOrBlank(regActivationTimeout)) {
						// default to 15 min
						regActivationTimeout = "15";
					}
					int activationTimeout = Integer.parseInt(regActivationTimeout);
					Calendar cal = Calendar.getInstance();
					cal.setTime(user.getActivationCodeCreTime());
					cal.add(Calendar.MINUTE, activationTimeout);
					if (cal.getTime().after(CommonUtil.getESTDate(new Date())) && activateCode.equals(user.getActivationCode())) {
						user.setStatus(UserStatus.ACTIVE);
						userEjb.updateUser(user);
						result = Constants.SUCCESS;
					}
				}
			}

		}

		// set the result in the request map
		this.getViewMap().put("activationSucceed", result);
	}

	/**
	 * request amount must be >0 and < user's avaiable amount
	 * 
	 * @param context
	 * @param component
	 * @param value
	 */
	public void validateCashbackWithdrawAmount(FacesContext context, UIComponent component, Object value) {
		BigDecimal withDrawAmount = (BigDecimal) value;
		if (basicUser.getAvailableAmount() == null) {
			basicUser.setAvailableAmount(BigDecimal.ZERO);
		}
		if (!(withDrawAmount.compareTo(basicUser.getAvailableAmount()) <= 0 && withDrawAmount.compareTo(new BigDecimal(25)) >= 0)) {
			FacesMessage message = Messages.getMessage(null, "RequestAmount_Invalid", null, true);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(component.getClientId(), message);
			throw new ValidatorException(message);
		}

	}

	public String createCashbackWithdrawRequest(CashBackWithdrawRequest cashBackWithdrawRequest) {
		cashBackWithdrawRequest.setUser(this.basicUser);
		// if paypal is not there, throw error message
		if (this.basicUser.getPayPalEmail().equalsIgnoreCase(Constants.EDITME)) {
			FacesMessage message = Messages.getMessage(null, "INVALID_PAYPAL", null, false);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(null, message);
			return null;

		} else {
			userEjb.createCashbackWithdrawRequest(cashBackWithdrawRequest.getUser(), cashBackWithdrawRequest);
			loadBasicUser(login.getCurrentUser());
			this.setProfiletab(Constants.USER_TAB_PROFILE_ACCOUNT_SUMMARY);
			return Constants.NavSuccess;
		}
	}

	public void updateCashbackWithdrawRequest(CashBackWithdrawRequest cashBackWithdrawRequest) {
		CashBackWithdrawRequest oldCashBackWithdrawRequest = userEjb.getCashBackWithdrawRequestById(cashBackWithdrawRequest.getId());
		if (oldCashBackWithdrawRequest.getStatus() != RecordStatus.PENDING) {
			FacesMessage message = Messages.getMessage(null, "Can_Not_Delete_Processed_Cashback_Request", null, false);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(null, message);
			return;
		}
		BigDecimal oldWithdrawAmount = oldCashBackWithdrawRequest.getWithdrawAmount();
		User user = userEjb.findByIdWithPessimisticWrite(cashBackWithdrawRequest.getUser().getId());
		BigDecimal totalAmount = user.getAvailableAmount().add(oldWithdrawAmount);

		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (cashBackWithdrawRequest.getWithdrawAmount().compareTo(totalAmount) > 0) {
			FacesMessage message = Messages.getMessage(null, "RequestAmount_Invalid", null, true);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(null, message);
			requestContext.addCallbackParam("isValid", false);
		} else {
			userEjb.updateCashbackWithdrawRequest(totalAmount, cashBackWithdrawRequest, user);
			requestContext.addCallbackParam("isValid", true);
		}
	}

	public void deleteCashBackWithdrawRequest(CashBackWithdrawRequest cashBackWithdrawRequest) {
		cashBackWithdrawRequest = userEjb.getCashBackWithdrawRequestById(cashBackWithdrawRequest.getId());
		if (cashBackWithdrawRequest.getStatus() == RecordStatus.PENDING) {
			userEjb.deleteCashBackWithdrawRequest(cashBackWithdrawRequest.getUser(), cashBackWithdrawRequest);
			loadBasicUser(cashBackWithdrawRequest.getUser());
		} else {
			FacesMessage message = Messages.getMessage(null, "Can_Not_Delete_Processed_Cashback_Request", null, false);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(null, message);
		}
	}

	public void deleteOrderInquery(OrderInquery orderInquery) {
		orderInquery = userEjb.getOrderInqueryById(orderInquery.getId());
		if (orderInquery.getStatus() == RecordStatus.PENDING) {
			userEjb.deleteOrderInquery(orderInquery.getUser(), orderInquery);
		} else {
			FacesMessage message = Messages.getMessage(null, "Can_Not_Delete_Processed_OrderInquery", null, false);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(null, message);
		}
	}

	public void updateOrderInquery(OrderInquery orderInquery) {
		OrderInquery oldOrderInquery = userEjb.getOrderInqueryById(orderInquery.getId());
		if (oldOrderInquery.getStatus() != RecordStatus.PENDING) {
			FacesMessage message = Messages.getMessage(null, "Can_Not_Delete_Processed_OrderInquery", null, false);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(null, message);
			return;
		} else {
			userEjb.updateOrderInquery(orderInquery);
		}

		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);
	}

	/**
	 * populate cashback amount and paypalemail from user profile when user click withdraw payment
	 */
	public void initializeCashBackWithdrawRequest() {
		this.cashBackWithdrawRequest = new CashBackWithdrawRequest();
		this.cashBackWithdrawRequest.setUser(basicUser);
		this.cashBackWithdrawRequest.setPaypalEmail(basicUser.getPayPalEmail());
		if (null == basicUser.getAvailableAmount()) {
			this.cashBackWithdrawRequest.setWithdrawAmount(BigDecimal.ZERO);
		} else {
			this.cashBackWithdrawRequest.setWithdrawAmount(basicUser.getAvailableAmount());
		}
		setProfiletab(Constants.USER_TAB_NEW_CASHBACK_WITHDRAW_REQUEST);
	}

	public void initDataModel() {
		lazyCashBackWithdrawRequestDataModel.setUser(basicUser);
		lazyOrderInqueryDataModel.setUser(basicUser);
	}

	public void initDataModelPendingForUserCashBack() {
		cashBackHistoryDataModel.setUser(basicUser);
		cashBackHistoryDataModel.setCashBackRecordStatus(UserCashBackRecordStatus.PENDING);
	}

	public void initDataModelAvailableForUserCashBack() {
		cashBackHistoryDataModel.setUser(basicUser);
		cashBackHistoryDataModel.setCashBackRecordStatus(UserCashBackRecordStatus.AVAILABLE);
	}

	public void initDataModelForAll() {
		lazyCashBackWithdrawRequestDataModel.setUser(null);
		cashBackHistoryDataModel.setUser(null);
		lazyOrderInqueryDataModel.setUser(null);
	}

	/**
	 * can only update cash back status
	 * 
	 * @param event
	 */
	public void handleUserCashBackUpdate(RowEditEvent event) {
		UserCashBackHistory cashBackHistory = (UserCashBackHistory) event.getObject();
		// throw error if transaction amount is blank or zero.
		if (cashBackHistory.getTransactionAmount() == null) {
			setBizErrorNSkipToResp("TransactionAmount_Blank");
		} else {
			userEjb.updateCashBackAvailable(cashBackHistory);
		}
	}

	/**
	 * load the user for system admin
	 */
	public void loadManageUser() {
		User user = (User) this.getSessionMap().get(Constants.MANAGEUSER);
		this.basicUser = userEjb.getUserById(user.getId());
	}

	public String setManageUser(User user) {
		this.getSessionMap().remove(Constants.MANAGEUSER);
		this.getSessionMap().put(Constants.MANAGEUSER, user);
		return Constants.SUCCESS;
	}

	public List<String> getStoreList(String query) {
		return storeEjb.getStoreList(query);
	}

	public void createOrderInquery(OrderInquery orderInquery) {
		orderInquery.setUser(this.basicUser);
		String storeName = (String) this.getRequestMap().get("storeName");
		Store store = storeEjb.getStoreByName(storeName);
		orderInquery.setStore(store);
		orderInquery.setCreatedBy(this.basicUser);
		userEjb.createOrderInquery(orderInquery.getUser(), orderInquery);
		this.setProfiletab("");
		submitOrderInquerySuccess = true;
		// return Constants.NavSuccess;
	}

	/**
	 * populate cashback amount and paypalemail from user profile when user click withdraw payment
	 */
	public void initializeOrderInquery() {
		this.orderInquery = new OrderInquery();
		this.basicUser = userEjb.getUserByUserId(basicUser.getUserId());
		this.orderInquery.setUser(basicUser);
		setProfiletab(Constants.USER_TAB_NEW_ORDER_INQUERY);

	}

	public void checkOrderNumberExist(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return;
		}
		String orderNumber = (String) value;
		OrderInquery existingOrderInquery = userEjb.getOrderInquery(orderNumber);
		if (existingOrderInquery != null) {
			FacesMessage message = Messages.getMessage("", "duplidateOrderNumber", null, true);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			this.getFacesContext().addMessage(component.getClientId(), message);
			throw new ValidatorException(message);
		}

	}

	/**
	 * used in the my post to display approved,pending for review and reject thread tabs
	 * 
	 * @param status
	 */
	public void prepare2FetchMyPost(ThreadStatus status) {
		this.getSessionMap().put(Constants.USER_MYPOST_THREAD_STATUS, status);
	}

	public String deriveMypostTabTitle(ThreadStatus status) {
		return Constants.USER_THREADSTATUS_TO_MYPOST_TAB_TITLE__MAP.get(status);
	}

	/**
	 * used in manageuser page
	 * 
	 * @param event
	 */
	public void handleSelectUser(SelectEvent event) {
		String userId = (String) event.getObject();
		this.user = userEjb.getUserByUserId(userId);
	}

	public void setProfiletab(String profiletab) {
		this.profiletab = profiletab;
		this.getSessionMap().put(Constants.USER_PROFILE_TAB, this.profiletab);
		User user = this.getCurrentUser();
		if (profiletab.equalsIgnoreCase(Constants.USER_TAB_MYREPLY)) {
			myReplyDataModel.setSource(profiletab);
			myReplyDataModel.setUser(user);

		} else {
			myDataModel.setSource(profiletab);
			myDataModel.setUser(user);
		}
		resetFirstDataTable();
		submitOrderInquerySuccess = false;
	}

	public void populateSourceNUser() {
		Map<String, Object> sessionMap = this.getSessionMap();
		String source = (String) sessionMap.get(Constants.VIEWPROFILE_SOUCE_4LAZYDATAMODEL);
		String userId = (String) sessionMap.get(Constants.CONTEXT_USER_ID);
		User user = userEjb.getUserByUserId(userId);
		if (!StringUtil.isNullOrBlank(source) && source.equalsIgnoreCase(Constants.USER_TAB_MYREPLY)) {
			myReplyDataModel.setSource(source);
			myReplyDataModel.setUser(user);

		} else {
			myDataModel.setSource(source);
			myDataModel.setUser(user);
		}

	}

	public String login4Social() {
		User tempUser = this.user;
		if (tempUser.getLoginType() == null || tempUser.getLoginType().equals(LoginType.SELF) || StringUtil.isNullOrBlank(tempUser.getSocialToken())) {
			// display to user for the error message
			this.setBizErrorNSkipToResp("InvalidOperation");
			// stop form from submitting again.
			setClientBizErrorNSkipToResp("userid4Social", "InvalidOperation");
			setClientBizErrorNSkipToResp("userid4Socialqq", "InvalidOperation");
			setClientBizErrorNSkipToResp("userid4Socialgoogle", "InvalidOperation");
			return null;
		}
		User user = userEjb.getUserIdByToken(tempUser.getLoginType(), tempUser.getSocialToken());
		if (null == user) {
			logger.info("login4social-----userId, email,socialId,logintype{}",
					tempUser.getUserId() + "-" + tempUser.getEmail() + "-" + tempUser.getSocialToken() + "-" + tempUser.getLoginType());
			// create new account
			/*
			 * boolean isEmailAlreadyExitence = userEjb.checkEmailExistence(tempUser.getEmail()); if (isEmailAlreadyExitence) {
			 * setClientBizErrorNSkipToResp("email4Social", "duplicateEmail"); return null; }
			 */

			boolean isUserIdAlreadyExist = userEjb.isExist(tempUser.getUserId());
			if (isUserIdAlreadyExist) {
				this.getSessionMap().put(Constants.LOGINTYPE, tempUser.getLoginType().toString());
				setClientBizErrorNSkipToResp("userid4Social", "DuplicateUserId");
				setClientBizErrorNSkipToResp("userid4Socialqq", "DuplicateUserId");
				setClientBizErrorNSkipToResp("userid4Socialgoogle", "DuplicateUserId");
				return null;
			}

			if (!isUserIdAlreadyExist) {
				tempUser.setUserGroup(this.findUserGroup(Constants.USERGROUP_REGISTERED));
				tempUser.setProfilePicName(Constants.PROFILE_PIC_DEFAULT_VALUE);
				// encrypt the password
				tempUser.setLastLogin(new Date());
				tempUser.setTrustedUser(true);
				tempUser.setStatus(UserStatus.ACTIVE);
				String refererId = Constants.BLANK;
				Object obj = this.getSessionMap().get("referBy");
				if (obj != null) {
					refererId = (String) obj;
				}
				tempUser.setRefererId(refererId);
				userEjb.enrollUser4Social(tempUser);
				login.initializeUserLogin(tempUser);
			}
		} else {
			// user already exist
			tempUser = user;
			login.initializeUserLogin(tempUser);
		}
		// auto login

		return Constants.SUCCESS;
	}

	/**
	 * manually create user cash back
	 * 
	 * @param cashBackHistory
	 */
	public void createUserCashBackHistory(UserCashBackHistory cashBackHistory) {
		this.cashBackHistory.setId(0);
		String userId = (String) this.getRequestMap().get("userId");
		User user = userEjb.getUserByUserId(userId);
		cashBackHistory.setUser(user);

		// String userId = cashBackHistory.getUser().getUserId();
		String storeName = (String) this.getRequestMap().get("storeName");
		Store store = storeEjb.getStoreByName(storeName);
		cashBackHistory.setStore(store);

		// cash back percent will be overriden
		if (cashBackHistory.getTransactionAmount() == null) {
			cashBackHistory.setTransactionAmount(BigDecimal.ZERO);
		}
		if (cashBackHistory.getTransactionAmount().compareTo(BigDecimal.ZERO) == 0) {
			cashBackHistory.setCashBackAmount(BigDecimal.ZERO);
		} else {
			cashBackHistory.setCashBackPercent(cashBackHistory.getCashBackAmount().divide(cashBackHistory.getTransactionAmount(), 3,
					RoundingMode.HALF_UP));
		}

		// profit
		cashBackHistory.setProfit(cashBackHistory.getCommissionAmount().subtract(cashBackHistory.getCashBackAmount())
				.setScale(2, RoundingMode.HALF_DOWN));

		// set affiliate
		cashBackHistory.setAffiliateNetWork(store.getAffiliate().getName());

		cashBackHistory.setStatus(UserCashBackRecordStatus.PENDING);

		userEjb.createUserCashBackHisotry(cashBackHistory);

		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);
		this.cashBackHistory = new UserCashBackHistory();

	}

	public void deleteUserCashBackHistory(UserCashBackHistory userCashBackHistory) {
		userEjb.removeUserCashBack(userCashBackHistory);
	}

	public void populateReferredUsers(User user) {
		referredUserList = userEjb.populateReferredUsers(user);
		setProfiletab(Constants.USER_TAB_TELL_A_FRIEND);
	}

	/* -------------getter/setter----------------- */

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getBasicUser() {
		return basicUser;
	}

	public void setBasicUser(User basicUser) {
		this.basicUser = basicUser;
	}

	public String getProfiletab() {
		return profiletab;
	}

	/**
	 * @return the myDataModel
	 */
	public LazyThreadDataModel getMyDataModel() {
		return myDataModel;
	}

	/**
	 * @param myDataModel
	 *            the myDataModel to set
	 */
	public void setMyDataModel(LazyThreadDataModel myDataModel) {
		this.myDataModel = myDataModel;
	}

	/**
	 * @return the reply
	 */
	public ForumReply getReply() {
		return reply;
	}

	/**
	 * @param reply
	 *            the reply to set
	 */
	public void setReply(ForumReply reply) {
		this.reply = reply;
	}

	/**
	 * @return the myReplyDataModel
	 */
	public LazyReplyDataModel getMyReplyDataModel() {
		return myReplyDataModel;
	}

	/**
	 * @param myReplyDataModel
	 *            the myReplyDataModel to set
	 */
	public void setMyReplyDataModel(LazyReplyDataModel myReplyDataModel) {
		this.myReplyDataModel = myReplyDataModel;
	}

	/**
	 * @return the isSystemProfilePic
	 */
	public boolean isSystemProfilePic() {
		return isSystemProfilePic;
	}

	/**
	 * @param isSystemProfilePic
	 *            the isSystemProfilePic to set
	 */
	public void setSystemProfilePic(boolean isSystemProfilePic) {
		this.isSystemProfilePic = isSystemProfilePic;
	}

	public LazyUserCashBackHistoryDataModel getCashBackHistoryDataModel() {
		return cashBackHistoryDataModel;
	}

	public void setCashBackHistoryDataModel(LazyUserCashBackHistoryDataModel cashBackHistoryDataModel) {
		this.cashBackHistoryDataModel = cashBackHistoryDataModel;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public CashBackWithdrawRequest getCashBackWithdrawRequest() {
		return cashBackWithdrawRequest;
	}

	public void setCashBackWithdrawRequest(CashBackWithdrawRequest cashBackWithdrawRequest) {
		this.cashBackWithdrawRequest = cashBackWithdrawRequest;
	}

	public LazyCashBackWithdrawRequestDataModel getLazyCashBackWithdrawRequestDataModel() {
		return lazyCashBackWithdrawRequestDataModel;
	}

	public void setLazyCashBackWithdrawRequestDataModel(LazyCashBackWithdrawRequestDataModel lazyCashBackWithdrawRequestDataModel) {
		this.lazyCashBackWithdrawRequestDataModel = lazyCashBackWithdrawRequestDataModel;
	}

	public SelectItem[] getRecordStatusOptions() {
		return recordStatusOptions;
	}

	public void setRecordStatusOptions(SelectItem[] recordStatusOptions) {
		this.recordStatusOptions = recordStatusOptions;
	}

	public String getFilterField() {
		return filterField;
	}

	public void setFilterField(String filterField) {
		this.filterField = filterField;
	}

	public LazyTransactionRecordDataModel getLazyTransactionRecordDataModel() {
		return lazyTransactionRecordDataModel;
	}

	public void setLazyTransactionRecordDataModel(LazyTransactionRecordDataModel lazyTransactionRecordDataModel) {
		this.lazyTransactionRecordDataModel = lazyTransactionRecordDataModel;
	}

	public SelectItem[] getCashBackStatusOptions() {
		return cashBackStatusOptions;
	}

	public void setCashBackStatusOptions(SelectItem[] cashBackStatusOptions) {
		this.cashBackStatusOptions = cashBackStatusOptions;
	}

	public LazyOrderInqueryDataModel getLazyOrderInqueryDataModel() {
		return lazyOrderInqueryDataModel;
	}

	public void setLazyOrderInqueryDataModel(LazyOrderInqueryDataModel lazyOrderInqueryDataModel) {
		this.lazyOrderInqueryDataModel = lazyOrderInqueryDataModel;
	}

	public OrderInquery getOrderInquery() {
		return orderInquery;
	}

	public void setOrderInquery(OrderInquery orderInquery) {
		this.orderInquery = orderInquery;
	}

	public boolean isSubmitOrderInquerySuccess() {
		return submitOrderInquerySuccess;
	}

	public void setSubmitOrderInquerySuccess(boolean submitOrderInquerySuccess) {
		this.submitOrderInquerySuccess = submitOrderInquerySuccess;
	}

	public List<ThreadStatus> getAllThreadStatus() {
		return allThreadStatus;
	}

	public void setAllThreadStatus(List<ThreadStatus> allThreadStatus) {
		this.allThreadStatus = allThreadStatus;
	}

	public int getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
	}

	public UserCashBackHistory getCashBackHistory() {
		return cashBackHistory;
	}

	public void setCashBackHistory(UserCashBackHistory cashBackHistory) {
		this.cashBackHistory = cashBackHistory;
	}

	public List<User> getReferredUserList() {
		return referredUserList;
	}

	public void setReferredUserList(List<User> referredUserList) {
		this.referredUserList = referredUserList;
	}

}
