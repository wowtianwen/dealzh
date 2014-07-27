package com.junhong.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * 
	 * convert HTMl <BR>
	 * to OS-based new line code
	 * 
	 * @param ori
	 * @param des
	 */
	public static String br2nl(String ori) {

		String eol = System.getProperty("line.separator");
		return ori.replaceAll("(<br>|<br/>)", eol).replaceAll("</br>", "");

	}

	/**
	 * convert os-based new line code to HTML <BR>
	 * convert HTMl <BR>
	 * to OS-based new line code
	 * 
	 * @param ori
	 * @param des
	 */
	public static String nl2br(String ori) {

		return ori.replaceAll("(\r\n|\r|\n|\n\r)", "<br/>");

	}

	public static boolean isNullOrBlank(String string) {
		return string == null || string.length() == 0;
	}

	/**
	 * extract html link from given content
	 * 
	 * @param content
	 * @return
	 */
	public static List<String> getHtmlLink(String content) {
		List<String> htmlLinkList = new ArrayList<String>();
		final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
		final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*[\"']?(([^\"']*)|[^'\"]*|([^'\">\\s]+))";

		Pattern patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
		Pattern patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);

		Matcher matcherTag = patternTag.matcher(content);

		while (matcherTag.find()) {
			String href = matcherTag.group(1); // href
			// String linkText = matcherTag.group(2); // link text
			Matcher matcherLink = patternLink.matcher(href);
			while (matcherLink.find()) {
				String link = matcherLink.group(1); // link
				htmlLinkList.add(link);
			}
		}

		return htmlLinkList;
	}

	public static String getDomain(String htmlLink) {
		String domain = "";
		// String domainRegxp = "(?i)https?://[a-zA-Z0-9]+\\.([\\w\\.]+)/?.*";
		String domainRegxp = "(?i)https?://(www)?[0-9]*\\.?([\\w-\\.]+)/?.*";
		Pattern domainPattern = Pattern.compile(domainRegxp);
		Matcher matcher = domainPattern.matcher(htmlLink);
		while (matcher.find()) {
			domain = matcher.group(2);
			break;
		}
		return domain;
	}

	public static void testPriceReg() {

		String regex = "(^\\$?(([1-9]\\d{0,2}(,?\\d{3})*)|(([1-9]\\d*)?\\d))(\\.\\d\\d)?)|([0-9]{1,2}%\\s+((?i)(off))$)";
		String input = "12% offs";
		Pattern domainPattern = Pattern.compile(regex);
		Matcher matcher = domainPattern.matcher(input);
		if (matcher.matches()) {
			System.out.println("valid");
		} else {
			System.out.println("Invalid");
		}

	}

	public static void main(String[] args) {
		testPriceReg();

	}
}
