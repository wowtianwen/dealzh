package com.junhong.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.faces.model.SelectItem;

import com.junhong.forum.common.RecordStatus;
import com.junhong.forum.common.ThreadStatus;
import com.junhong.forum.common.UserCashBackRecordStatus;

public class CommonUtil {

	/**
	 * get classloader
	 * 
	 * @return
	 */
	public static final ClassLoader getThreadLoader() {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		return cl;
	}

	/**
	 * URLConnection can not be used to write a file
	 * 
	 * @param fileName
	 * @param content
	 */
	public static final void writeToFile(String fileName, String content) {
		OutputStream ops = null;
		try {
			URL fileURL = ClassLoader.getSystemResource("config/" + fileName);
			URLConnection conn = fileURL.openConnection();
			conn.setDoOutput(true);
			ops = conn.getOutputStream();
			ops.write(content.getBytes());
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} finally {
			try {
				if (ops != null) {
					ops.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static Date getESTDate(Date date) {
		Date estDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");
			dateFormat.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
			String dt = dateFormat.format(date);
			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");
			dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
			estDate = dateFormat2.parse(dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return estDate;
	}

	public static Date getCurrentDate() {
		Date currDate = new Date();
		return getESTDate(currDate);
	}

	public static String getDateString(Date date) {
		String result = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
			// dateFormat.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
			result = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public static String getDateStringyyyymmddDash(Date date) {
		String result = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			// dateFormat.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
			result = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public static String getDateStringmmddyyyySlash(Date date) {
		String result = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			// dateFormat.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
			result = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public static String getDateStringyyyymmdd(Date date) {
		String result = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			// dateFormat.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
			result = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public static Date getESTDateWithoutTime(Date date) {
		Date result = getESTDate(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(result);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();

	}

	public static Date addDay(Date currentDate, int value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DAY_OF_MONTH, value);
		return cal.getTime();
	}

	public static Date parseDateYYYYDashmmDashdd(String dateStr) {
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			result = df.parse(dateStr);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return result;

	}

	public static Date parseDatemmddyyyyDash(String dateStr) {
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		try {
			result = df.parse(dateStr);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return result;

	}

	public static String getISO8601Date(Date currDate) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(currDate);
		return nowAsISO;
	}

	public static String getWebApplicationRootPath() {
		String result = "";
		try {
			String path = CommonUtil.class.getClassLoader().getResource("").getPath();
			String fullPath = URLDecoder.decode(path, "UTF-8");
			String pathArr[] = fullPath.split("/WEB-INF/classes/");
			result = pathArr[0];
			// to read a file from webcontent
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return result;

	}

	public static SelectItem[] createFilterOptions() {
		SelectItem[] options = new SelectItem[4];
		options[0] = new SelectItem("", "Select");
		options[1] = new SelectItem(RecordStatus.PENDING.toString(), RecordStatus.PENDING.toString());
		options[2] = new SelectItem(RecordStatus.PROCESSED.toString(), RecordStatus.PROCESSED.toString());
		options[3] = new SelectItem(RecordStatus.ERROR.toString(), RecordStatus.ERROR.toString());
		return options;
	}

	public static SelectItem[] createFilterOptionsForCashBackStatus() {
		SelectItem[] options = new SelectItem[3];
		options[0] = new SelectItem("", "Select");
		options[1] = new SelectItem(UserCashBackRecordStatus.PENDING.toString(), UserCashBackRecordStatus.PENDING.toString());
		options[2] = new SelectItem(UserCashBackRecordStatus.AVAILABLE, UserCashBackRecordStatus.AVAILABLE.toString());
		return options;
	}

	public static SelectItem[] createFilterOptionsForTheadStatus() {
		SelectItem[] options = new SelectItem[2];
		options[0] = new SelectItem(ThreadStatus.PENDINGREVIEW.toString(), ThreadStatus.PENDINGREVIEW.toString());
		options[1] = new SelectItem(ThreadStatus.REJECTED.toString(), ThreadStatus.REJECTED.toString());
		return options;
	}

	public static void main(String[] args) {

		// writeToFile("clientIPAddress.log", "test");
		getESTDate(new Date());
		System.out.println(getESTDate(new Date()));

		Date currDateTime = CommonUtil.getESTDate(new Date());
		Date currDate = new Date(currDateTime.getYear(), currDateTime.getMonth(), currDateTime.getDate());
		Calendar cal = Calendar.getInstance();

		cal.setTime(currDate);
		cal.add(Calendar.DATE, -1);
		Date yesterday = cal.getTime();
		getWebApplicationRootPath();

		Date dateWithoutTime = getESTDateWithoutTime(new Date());
		System.out.println(dateWithoutTime);

	}

}
