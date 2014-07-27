package com.junhong.forum.stats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * Session Bean implementation class LoggingClientIPAddress
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class StatUtilService {

	@Inject
	private Logger				logger;
	private Map<String, String>	ipAddressMap	= new ConcurrentHashMap<String, String>();
	private SimpleDateFormat	df				= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * Default constructor.
	 */
	public StatUtilService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * log the client IP address
	 * 
	 * @param iPAddress
	 */
	@Asynchronous
	public void logClientIPAddress(String iPAddress) {
		ipAddressMap.put(iPAddress, df.format(new Date()));
	}

	@Timeout
	@Schedule(dayOfMonth = "*", month = "*", year = "*", second = "0", minute = "*/15", hour = "*", persistent = false)
	public void writeToFile() {
		// CommonUtil.writeToFile("clientIPAddress.log",
		// ipAddressMap.toString());
		logger.info("============================Client IP Address=START====================");
		logger.info("visitied {} for past 15 mins", ipAddressMap.size());
		// logger.info(ipAddressMap.toString());
		ipAddressMap.clear();
		logger.info("============================Client IP Address=END====================");
	}
}
