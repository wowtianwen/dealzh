package com.junhong.util;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;

@Named
@RequestScoped
public class SimpleCaptchaValidator {

	public void validateCaptcha(FacesContext context, UIComponent component, Object value) {
		// TODO Auto-generated method stub
		String val = (String) value;
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);

		// Or, for an AudioCaptcha:
		// AudioCaptcha captcha = (AudioCaptcha)
		// session.getAttribute(Captcha.NAME);
		if (!captcha.isCorrect(val)) {
			HtmlInputText inputTextComp = (HtmlInputText) component;
			String vadatorErrorMessage = inputTextComp.getValidatorMessage();
			FacesMessage error = new FacesMessage(vadatorErrorMessage);
			error.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), error);
			context.renderResponse();
		}

	}
}
