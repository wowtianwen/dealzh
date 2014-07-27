package com.junhong.util;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;

public abstract class WebConfigUtil {

	private static Properties	environIniProp		= null;
	private static String		EnvironIniFileName	= "config/Environ.ini";
	@Inject
	private static Logger		logger;

	public static final String	profilePicDir;

	/**
	 * initialize
	 */
	static {

		loadEnvironIniFile(EnvironIniFileName);
		profilePicDir = getProp("PROFILE.PIC.DIR");
	}

	/**
	 * get the value of key in the environ.ini file
	 * 
	 * @param key
	 * @return
	 */
	public static String getProp(String key) {
		if (environIniProp == null) {
			loadEnvironIniFile(EnvironIniFileName);
		}
		return environIniProp.getProperty(key);
	}

	public static String getProfilePicDestFoler() {
		return profilePicDir;
	}

	/**
	 * load prop files
	 * 
	 * @param fileName
	 */
	public static void loadEnvironIniFile(String fileName) {
		if (environIniProp == null) {
			environIniProp = new Properties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			try {
				InputStream is = classLoader.getResourceAsStream(fileName);
				if (is != null) {
					environIniProp.load(is);
				} else {
					logger.info("Not able to load Environ.ini file");
				}
			} catch (Exception e) {
				logger.info("Not able to load Environ.ini file");
				logger.error(e.getMessage());

			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String profile_pic = WebConfigUtil.getProp("PROFILE.PIC.DIR");
		System.out.println(profile_pic);

	}
}
