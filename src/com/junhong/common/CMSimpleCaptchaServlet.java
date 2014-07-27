package com.junhong.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;

/**
 * Servlet implementation class CMSimpleCaptchaServlet
 */
@WebServlet(urlPatterns = { "/simpleImg" }, initParams = { @WebInitParam(name = "captcha-height", value = "75"), @WebInitParam(name = "captcha-width", value = "250") })
public class CMSimpleCaptchaServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;
	private static int			_width				= 200;
	private static int			_height				= 50;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CMSimpleCaptchaServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Captcha captcha = new Captcha.Builder(_width, _height).addText().gimp().addBackground().addNoise().build();
		CaptchaServletUtil.writeImage(response, captcha.getImage());
		request.getSession().setAttribute("simpleCaptcha", captcha);
	}

	@Override
	public void init() throws ServletException {
		super.init();

		if (getInitParameter("captcha-height") != null) {
			_height = Integer.valueOf(getInitParameter("captcha-height")).intValue();
		}

		if (getInitParameter("captcha-width") != null)
			_width = Integer.valueOf(getInitParameter("captcha-width")).intValue();
	}

}
