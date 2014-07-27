package com.junhong.util;

import java.util.Comparator;

import com.junhong.news.entity.NewsDashBoard;

public class NewsDashBoardComparator implements Comparator<NewsDashBoard> {

	@Override
	public int compare(NewsDashBoard o1, NewsDashBoard o2) {
		return o1.getCreateTime().compareTo(o2.getCreateTime());
	}

}
