package com.junhong.util;

import javax.faces.context.FacesContext;

public class HttpUtil {

	/**
	 * return the context path
	 * It will return "/forum" for http://localhost:8080/forum/user/leftside.xhtml
	 * 
	 * @param fc
	 * @return
	 */
	public static String getRequestContextPath(FacesContext fc) {
		return fc.getExternalContext().getRequestContextPath();
	}

	/**
	 * It will return "/user/leftside.xhtml" for http://localhost:8080/forum/user/leftside.xhtml
	 * 
	 * @param fc
	 * @return
	 */
	public static String getRequestServletPath(FacesContext fc) {
		return fc.getExternalContext().getRequestServletPath();
	}

	/**
	 * return the phisical file path of the request context path
	 * It will return "/forum" for http://localhost:8080/forum/user/leftside.xhtml
	 * 
	 * @param fc
	 * @return
	 * */
	public static String getRealApplicationPath(FacesContext fc) {
		return fc.getExternalContext().getRealPath("/");
	}

}
