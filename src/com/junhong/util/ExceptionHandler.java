package com.junhong.util;

import java.util.Iterator;
import java.util.Map;

import javax.ejb.EJBException;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.junhong.forum.common.Constants;
import com.junhong.forum.common.Messages;

public class ExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger							logger	= LoggerFactory.getLogger(ExceptionHandler.class);

	private final javax.faces.context.ExceptionHandler	wrapped;

	public ExceptionHandler(final javax.faces.context.ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public javax.faces.context.ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = facesContext.getExternalContext();
		final Map<String, Object> requestMap = externalContext.getRequestMap();
		String message;
		String userMessage = "";
		for (final Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator(); it.hasNext();) {
			Throwable t = it.next().getContext().getException();
			while ((t instanceof FacesException || t instanceof EJBException || t instanceof ELException) && t.getCause() != null) {
				t = t.getCause();
			}
			try {
				logger.error("{}: {}", t.getClass().getSimpleName(), t);
				if (t instanceof ViewExpiredException) {
					final String viewId = ((ViewExpiredException) t).getViewId();
					message = "oops! your view is expired. please revisit it";
					userMessage = message;
				} else {
					message = t.getMessage(); // beware, don't leak internal
					userMessage = Messages.getString(Constants.ResourceApplication, null, "Error", null);
				}
				requestMap.put("userMsg", userMessage);

				logger.error("----------------------oops!, unexpected Error, detail is: ", t);
				t.printStackTrace();
				String errorPageLocation = "/error.xhtml";
				facesContext.setViewRoot(facesContext.getApplication().getViewHandler().createView(facesContext, errorPageLocation));
				facesContext.getPartialViewContext().setRenderAll(true);
				facesContext.renderResponse();
			} finally {
				it.remove();
			}

			// handle other exceptions
			getWrapped().handle();
		}
	}
}
