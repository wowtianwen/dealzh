/**
 * all the categories are cached when the system start up.
 * 
 */
package com.junhong.forum.backingbean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;

import com.junhong.auth.common.Login;
import com.junhong.auth.entity.User;
import com.junhong.auth.service.UserEjb;
import com.junhong.forum.batch.PopulateDealBatch;
import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;
import com.junhong.forum.datamodel.LazyStoreDataModel;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.Store;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.AffiliateEjb;
import com.junhong.forum.service.StoreEjb;
import com.junhong.util.CommonUtil;
import com.junhong.util.HttpUtil;
import com.junhong.util.StringUtil;
import com.junhong.util.ViewScoped;
import com.junhong.util.WebConfigUtil;

@Named
@ViewScoped
public class StoreBackingBean extends AbstractBacking {

	@Inject
	private Logger				logger;

	@Inject
	private UserEjb				userEjb;

	@Inject
	private StoreEjb			storeEjb;
	@Inject
	private PopulateDealBatch	storeDealBatch;

	@Inject
	private AffiliateEjb		affiliateEjb;

	@Inject
	private Store				store;

	@Inject
	protected Login				login;

	@Inject
	private LazyStoreDataModel	storeDataModel;

	private List<ForumThread>	LatestDeals;
	private boolean				editMode;

	/* ------------instance Variable-------------- */

	@PostConstruct
	public void initialize() {

	}

	public void loadStore() {
		Object storeIdObj = this.getSessionMap().get("storeId");
		int id = -1;
		if (storeIdObj == null) {
			this.setBizErrorNSkipToResp("InvalidRequest");
		} else {
			try {
				id = Integer.parseInt((String) storeIdObj);
			} catch (NumberFormatException e) {
				this.setBizErrorNSkipToResp("InvalidRequest");
			}
		}
		this.store = storeEjb.getStoreById(id);
		this.LatestDeals = storeEjb.loadLatestDeals(this.store, 0, 25);
	}

	public void deleteStore() {
		processBusinessWithAuthorizationCheck(Constants.Action_DELETE, store);
		this.store = new Store();
	}

	public void createStore() {
		if (store.getAffiliateId() != 0) {
			store.setAffiliate(affiliateEjb.getaffiliateById(store.getAffiliateId()));
		}
		this.store.setId(0);
		this.store.setName(this.store.getName().trim());
		processBusinessWithAuthorizationCheck(Constants.Action_CREATE, store);
		this.store = new Store();
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);

	}

	public void updateStore() {
		if (store.getAffiliateId() != 0) {
			store.setAffiliate(affiliateEjb.getaffiliateById(store.getAffiliateId()));
		}
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, store);
		this.setEditMode(false);
		this.store = new Store();
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);
	}

	public void updateStore(Store store) {
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, store);
	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub
		Store store = (Store) entity;
		if (action.equals(Constants.Action_UPDATE)) {
			storeEjb.updateStore(store);
		}
		if (action.equals(Constants.Action_CREATE)) {
			storeEjb.createStore(store);
		}

		if (action.equals(Constants.Action_DELETE)) {
			storeEjb.removeStore(store);
		}

	}

	public void handleStoreAdScanFileUpload(FileUploadEvent event) {
		boolean fileSavedSuccessful = false;
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		UploadedFile uploadedFile = event.getFile();

		// deprive file name;
		StringBuilder fileName = new StringBuilder(this.getStore().getName());
		String storeAdScanFileName = fileName.append(CommonUtil.getESTDate(new Date())).append(".pdf").toString();

		String destDir = WebConfigUtil.profilePicDir;
		if (destDir == null) {
			String profileRelativePath = File.separator + "resources" + File.separator + "bfAdScan";
			destDir = HttpUtil.getRealApplicationPath(getFacesContext()) + profileRelativePath;
		}

		File file = null;
		BufferedOutputStream buffOS = null;
		InputStream input = null;
		try {
			File adScan_dir = new File(destDir);
			if (!adScan_dir.exists()) {
				adScan_dir.mkdir();
			}

			input = uploadedFile.getInputstream();
			// BufferedImage originalImage = ImageIO.read(input);
			/*
			 * if (originalImage.getColorModel().hasAlpha()) { originalImage =
			 * PictureUtil.dropAlphaChannel(originalImage); }
			 */
			// BufferedImage resizedImg = PictureUtil.resize(originalImage);

			file = new File(destDir, storeAdScanFileName);
			// check if the file exist
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			buffOS = new BufferedOutputStream(os);
			// ImageIO.write(originalImage, "jpg", os);

			byte[] buffer = new byte[1024];
			while (input.read(buffer) >= 0) {
				buffOS.write(buffer);
			}
			fileSavedSuccessful = true;
		} catch (FileNotFoundException e) {
			logger.error("Not able to save the Store Ad Scan. Throw File not found error");
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error("Not able to save the " + "Store Ad Scan. throw IOException");
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			if (buffOS != null) {
				try {
					buffOS.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		FacesMessage message = Messages.getMessage("", "save_fail", null);
		if (fileSavedSuccessful) {
			// update store with the file name
			this.store.setBfAdScan(storeAdScanFileName);
			updateStore(this.store);
			// set success message
			message = Messages.getMessage("", "save_success", null);
		}

		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("SAVE_SUCCESS", message.getSummary());

	}

	public void handleStoreProfilePicFileUpload(FileUploadEvent event) {
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		UploadedFile uploadedFile = event.getFile();

		// deprive file name;
		StringBuilder fileName = new StringBuilder(this.getStore().getName());
		String storeProfilePicFileName = fileName.append(".jpg").toString();

		String destDir = WebConfigUtil.profilePicDir;
		if (destDir == null) {
			String profileRelativePath = File.separator + "resources" + File.separator + "storePic";
			destDir = HttpUtil.getRealApplicationPath(getFacesContext()) + profileRelativePath;
		}

		File file = null;
		BufferedOutputStream buffOS = null;
		InputStream input = null;
		try {
			File pic_dir = new File(destDir);
			if (!pic_dir.exists()) {
				pic_dir.mkdir();
			}

			input = uploadedFile.getInputstream();
			// BufferedImage originalImage = ImageIO.read(input);
			/*
			 * if (originalImage.getColorModel().hasAlpha()) { originalImage =
			 * PictureUtil.dropAlphaChannel(originalImage); }
			 */
			// BufferedImage resizedImg = PictureUtil.resize(originalImage);

			file = new File(destDir, storeProfilePicFileName);
			// check if the file exist
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			buffOS = new BufferedOutputStream(os);
			// ImageIO.write(originalImage, "jpg", os);

			byte[] buffer = new byte[1024];
			while (input.read(buffer) >= 0) {
				buffOS.write(buffer);
			}
		} catch (FileNotFoundException e) {
			logger.error("Not able to save the profile pic. Throw File not found error");
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error("Not able to save the profile pic. throw IOException");
			logger.error(e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			if (buffOS != null) {
				try {
					buffOS.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}

		// update profilePicname
		updateStorePic(storeProfilePicFileName);

		// set success message
		FacesMessage message = Messages.getMessage("", "save_success", null);
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("SAVE_SUCCESS", message.getSummary());

	}

	private void updateStorePic(String storeProfilePicFileName) {
		this.store.setStorePicturePathURL(storeProfilePicFileName);
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, store);
		this.setEditMode(false);
	}

	public void validateUserAccess() {
		if (!login.isUserInSysadmin()) {
			storeDataModel.setAuthorized(false);
		}
	}

	public List<String> findStoreNames(String query) {
		List<String> results = new ArrayList<String>();
		results = storeEjb.getStoreListByNameFilter(query.trim());
		return results;
	}

	public void handleSelect(SelectEvent event) {
		String value = event.getObject().toString();
		if (!StringUtil.isNullOrBlank(value)) {
			storeDataModel.setFilter(value);
		}

	}

	public void valueChange(AjaxBehaviorEvent vce) {
		AutoComplete ac = (AutoComplete) vce.getSource();
		Object value = ac.getValue();
		if (null == value) {
			storeDataModel.setFilter(null);
		}
	}

	public void populateStoreDealBatch(Store store) {
		storeDealBatch.processStoreDeals(store);
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	public void processGenericStoreLinkClick(Store store) {
		User currUser = login.getCurrentUser();
		String convertedLink = store.getGenericStoreLink();
		if (currUser != null) {
			convertedLink = store.getGenericStoreLink().replace("deallover", currUser.getUserId());
		}
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(convertedLink);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/* -------------getter/setter----------------- */

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
		this.store.setAffiliateId(store.getAffiliate().getId());
	}

	public LazyStoreDataModel getStoreDataModel() {
		return storeDataModel;
	}

	public void setStoreDataModel(LazyStoreDataModel storeDataModel) {
		this.storeDataModel = storeDataModel;
	}

	public List<ForumThread> getLatestDeals() {
		return LatestDeals;
	}

	public void setLatestDeals(List<ForumThread> latestDeals) {
		LatestDeals = latestDeals;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.store = storeEjb.getStoreById(this.store.getId());
		this.store.setAffiliateId(this.store.getAffiliate().getId());
		this.editMode = editMode;
	}

}
