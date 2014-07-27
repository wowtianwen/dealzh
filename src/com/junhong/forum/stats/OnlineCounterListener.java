package com.junhong.forum.stats;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;

/**
 * Application Lifecycle Listener implementation class OnlineCounterListener
 * this class is not thread safe, so the online user is not 100% acurate.
 */
@WebListener
public class OnlineCounterListener implements HttpSessionListener {

	/**
	 * Default constructor.
	 */
	@Inject
	private OnlineCounter	onlineCounter;
	@Inject
	Logger					logger;

	public OnlineCounterListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
		onlineCounter.raise();
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {
		onlineCounter.reduce();
	}

}
