/**
 * all the categories are cached when the system start up.
 * 
 */
package com.junhong.forum.backingbean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;

import com.junhong.auth.service.UserEjb;
import com.junhong.forum.batch.PopulateDealBatch;
import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.AbstractEntity;
import com.junhong.forum.entity.Affiliate;
import com.junhong.forum.exceptions.AuthorizationFailException;
import com.junhong.forum.service.AffiliateEjb;
import com.junhong.util.Timing;
import com.junhong.util.ViewScoped;

@Named
@ViewScoped
@Timing
public class AffiliateBackingBean extends AbstractBacking {

	@Inject
	private Logger logger;

	@Inject
	private UserEjb userEjb;

	@Inject
	private AffiliateEjb affiliateEjb;
	@Inject
	private PopulateDealBatch populateDealBatch;
	@Inject
	private Affiliate affiliate;

	private List<Affiliate> allAffiliateList;

	/* ------------instance Variable-------------- */

	@PostConstruct
	public void initialize() {
		processBusinessWithAuthorizationCheck(Constants.Action_READ_ALL);

	}

	public void deleteAffiliate() {
		int total = affiliateEjb.getNumberOfStores(affiliate);
		if (total > 0) {
			setBizErrorNSkipToResp("CanNotDeleteAffiliate");

		} else {
			processBusinessWithAuthorizationCheck(Constants.Action_DELETE, affiliate);
			affiliate = new Affiliate();
		}
	}

	public void createAffiliate() {
		processBusinessWithAuthorizationCheck(Constants.Action_CREATE, affiliate);
		affiliate = new Affiliate();
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("isValid", true);
	}

	public void updateAffiliate() {
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, affiliate);
		affiliate = new Affiliate();
	}

	public void updateAffiliate(Affiliate affiliate) {
		processBusinessWithAuthorizationCheck(Constants.Action_UPDATE, affiliate);
	}

	/**
	 * handle edit event
	 * 
	 * @param event
	 */
	public void onEdit(RowEditEvent event) {
		Affiliate aff = (Affiliate) event.getObject();
		updateAffiliate(aff);
		aff = affiliateEjb.getaffiliateById(aff.getId());
		initialize();

	}

	@Override
	protected void processBusiness(String action, AbstractEntity entity) throws AuthorizationFailException {
		// TODO Auto-generated method stub
		Affiliate affiliate = (Affiliate) entity;
		if (action.equals(Constants.Action_UPDATE)) {
			affiliateEjb.updateaffiliate(affiliate);
		}
		if (action.equals(Constants.Action_CREATE)) {
			affiliateEjb.createaffiliate(affiliate);
			initialize();
		}

		if (action.equals(Constants.Action_DELETE)) {
			affiliateEjb.removeaffiliate(affiliate);
			initialize();
		}

	}

	@Override
	protected void processBusiness(String action) throws EJBException {
		if (action.equals(Constants.Action_READ_ALL)) {
			allAffiliateList = affiliateEjb.getAllAffiliates();
		}

	}

	public void populateAffiliateDeal(Affiliate affiliate) {
		populateDealBatch.processAffiliateDeals(affiliate);
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);
	}

	public void getDailyTransactionReport(Affiliate affiliate) {
		populateDealBatch.getDailyTransactionReport(affiliate, getWebApplicationContextPath());
		this.setMessageNSkipToResp("SUCCEED", FacesMessage.SEVERITY_INFO);

	}

	/* -------------getter/setter----------------- */
	public List<Affiliate> getAllAffiliateList() {
		return allAffiliateList;
	}

	public void setAllAffiliateList(List<Affiliate> allAffiliateList) {
		this.allAffiliateList = allAffiliateList;
	}

	public Affiliate getAffiliate() {
		return affiliate;
	}

	public void setAffiliate(Affiliate affiliate) {
		this.affiliate = affiliate;
	}

}
