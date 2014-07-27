package com.junhong.forum.service;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.junhong.forum.entity.ThreadDashBoard;

/**
 * Session Bean implementation class DashBoardService
 */

@Stateless
@LocalBean
public class DashBoardService extends GenericCRUDService<ThreadDashBoard> {

	/**
	 * Default constructor.
	 */
	public DashBoardService() {
		// TODO Auto-generated constructor stub
	}

	public List<ThreadDashBoard> getDashBoard(int threadId) {
		String query = "select db from ThreadDashBoard db where db.threadId=?1";
		List<ThreadDashBoard> result = this.findByHQL(query, ThreadDashBoard.class, new Object[] { threadId });
		return result;

	}
}
