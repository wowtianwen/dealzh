package com.junhong.forum.common;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.NumberTool;
import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.forum.entity.UserCashBackHistory;
import com.junhong.util.StringUtil;
import com.junhong.util.WebConfigUtil;

@LocalBean
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SendEmailService {

	@Resource(name = "mail/yourhotdeal")
	private Session	mailSession;

	@Inject
	private Logger	logger;

	public void sendResetPassword(String tempPwd, String emailAddress) {
		String emailSubject = Messages.getString(Constants.ResourceApplication, null, Constants.USER_RESET_PWD_EMAIL_SUBJECT, null);
		String[] parms = { tempPwd };
		String emailContent = Messages.getString(Constants.ResourceApplication, null, Constants.USER_RESET_PWD_EMAIL_CONTENT_TEMPLATE, parms);
		sendEmailMessage(emailAddress, emailSubject, emailContent);

	}

	public void sendRegActivationLetter(User user) {
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "class");
		p.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(p);

		VelocityContext context = new VelocityContext();

		context.put("userId", user.getUserId());
		context.put("activationpage", "accountActivation.xhtml?u=" + user.getUserId() + "&c=" + user.getActivationCode());
		// default to $5
		String regBonusAmount = "5";
		if (WebConfigUtil.getProp("REGBONUSAMOUNT") != null) {
			regBonusAmount = WebConfigUtil.getProp("REGBONUSAMOUNT");
		}
		context.put("regBonus", regBonusAmount);

		Template template = null;

		try {
			template = Velocity.getTemplate("velocityTemplate/regActivateEmailTemplate.vm");
		} catch (ResourceNotFoundException rnfe) {
			rnfe.printStackTrace();
			// couldn't find the template
		} catch (ParseErrorException pee) {
			pee.printStackTrace();
			// syntax error: problem parsing the template
		} catch (MethodInvocationException mie) {
			mie.printStackTrace();
			// something invoked in the template
			// threw an exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		logger.info(sw.toString());
		String emailSubject = "Please follow the instructions to activate your account";
		sendEmailMessage(user.getEmail(), emailSubject, sw.toString());

	}

	/**
	 * send email to the given email address
	 * 
	 * @param toEmailAddress
	 * @param subject
	 * @param content
	 */
	public void sendEmailMessage(String toEmailAddress, String subject, String content) {
		Transport tr = null;
		try {
			MimeMessage message = new MimeMessage(mailSession);
			InternetAddress[] toAddress = new InternetAddress[2];
			toAddress[0] = new InternetAddress(toEmailAddress);
			toAddress[1] = new InternetAddress("zhang.j.andrew@gmail.com");
			message.setRecipient(RecipientType.TO, toAddress[0]);
			message.setRecipient(RecipientType.BCC, toAddress[1]);
			message.setSubject(subject);
			message.setContent(content, "text/html; charset=utf-8");
			message.saveChanges();
			tr = mailSession.getTransport();
			String serverPassword = mailSession.getProperty("mail.password");
			tr.connect(null, serverPassword);
			tr.sendMessage(message, message.getAllRecipients());

		} catch (MessagingException e) {
			logger.error("Not able to send emails ", e);
			throw new RuntimeException("Not able to send emails " + e.getMessage());
		} finally {
			if (tr != null) {
				try {
					tr.close();
				} catch (MessagingException e) {
					logger.error("Not able to close transport while sending emails, potential memory leak ", e);
					throw new RuntimeException("Not able to close transport while sending emails, potential memory leak " + e.getMessage());

				}
			}

		}
	}

	private String generateRandomPassword() {

		return RandomStringUtils.randomAlphanumeric(10);
	}

	public void sendPendingCashBackLetter(User user, List<UserCashBackHistory> cashBackHistoryList) {
		if (cashBackHistoryList.isEmpty()) {
			return;
		}
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "class");
		p.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(p);

		VelocityContext context = new VelocityContext();
		context.put("userId", user.getUserId());
		context.put("userCashBackHistoryList", cashBackHistoryList);
		context.put("userPendingAmount", user.getPendingAmount());
		context.put("number", new NumberTool());

		Template template = null;

		template = Velocity.getTemplate("velocityTemplate/pendingCashBackEmailTemplate.vm");

		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		// System.out.println(sw.toString());
		String emailSubject = "Cash Back Confirmation for Yourhotdeal.com ";
		sendEmailMessage(user.getEmail(), emailSubject, sw.toString());

	}

	/**
	 * send available cash back to user
	 * 
	 * @param user
	 * @param cashBackHistoryList
	 */
	public void sendAvailableCashBackLetter(User user, List<UserCashBackHistory> cashBackHistoryList) {
		if (cashBackHistoryList.isEmpty()) {
			return;
		}
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "class");
		p.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(p);

		VelocityContext context = new VelocityContext();
		context.put("userId", user.getUserId());
		context.put("userCashBackHistoryList", cashBackHistoryList);
		context.put("userAvailableAmount", user.getAvailableAmount());
		context.put("number", new NumberTool());
		String webURL = WebConfigUtil.getProp(Constants.DOMAIN);
		if (StringUtil.isNullOrBlank(webURL)) {
			webURL = "http://www.yourhotdeal.com";
		}
		context.put("domain", webURL);

		Template template = null;

		template = Velocity.getTemplate("velocityTemplate/availableCashBackEmailTemplate.vm");

		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		// System.out.println(sw.toString());
		String emailSubject = "Cash Back Available to withdraw for Yourhotdeal.com ";
		sendEmailMessage(user.getEmail(), emailSubject, sw.toString());

	}
}
