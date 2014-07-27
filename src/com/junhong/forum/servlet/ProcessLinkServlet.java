package com.junhong.forum.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;
import com.junhong.forum.entity.ForumThread;
import com.junhong.forum.entity.InternalLink;
import com.junhong.forum.service.GenericCRUDService;
import com.junhong.util.StringUtil;

@WebServlet(urlPatterns = { "/procLink" }, asyncSupported = true, name = "procLink")
public class ProcessLinkServlet extends HttpServlet {
	@Inject
	private GenericCRUDService<InternalLink>	genericInternalLinkEjb;
	@Inject
	private Logger								logger;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String internalId = req.getParameter("pid");
		if (!StringUtil.isNullOrBlank(internalId)) {
			InternalLink internalLink = genericInternalLinkEjb.findById(InternalLink.class, Integer.parseInt(internalId));
			String convertedLink = internalLink.getLinkURL();

			// construct link parm
			StringBuilder linkParm = new StringBuilder("");
			User currUser = (User) req.getSession().getAttribute(Constants.CurrentUser);
			if (currUser != null) {
				linkParm.append(currUser.getUserId());
			} else {
				linkParm.append("deallover");
			}

			ForumThread thread = internalLink.getThread();
			String threadId = null;
			if (thread != null) {
				threadId = thread.getId() + "";
				Object threadReferUserIdObj = req.getSession().getAttribute(threadId);
				if (threadReferUserIdObj != null) {
					String dealReferUId = (String) threadReferUserIdObj;
					linkParm.append(Constants.DEALREFERDELIMIT).append(dealReferUId);
				}
			}

			convertedLink = convertedLink.replace("deallover", linkParm.toString());
			resp.sendRedirect(convertedLink);
		}
	}
}
