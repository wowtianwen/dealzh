package com.junhong.forum.entity;

import java.util.Comparator;

public class ThreadDashBoardComparator implements Comparator<ThreadDashBoard> {

	@Override
	public int compare(ThreadDashBoard o1, ThreadDashBoard o2) {
		if (o1 == null && o2 != null) {
			return -1;

		} else if (o1 != null && o2 == null) {
			return 1;

		} else if (o1 == null && o2 == null) {
			return 0;
		}

		return o1.getCreateTime().compareTo(o2.getCreateTime());
	}

}
