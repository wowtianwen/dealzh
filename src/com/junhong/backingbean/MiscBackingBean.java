package com.junhong.backingbean;

import javax.inject.Named;

import com.junhong.forum.common.Constants;
import com.junhong.util.ViewScoped;

@Named
@ViewScoped
public class MiscBackingBean {

	private String	helpTab	= Constants.HELP_FAQ;

	public String getHelpTab() {
		return helpTab;
	}

	public void setHelpTab(String helpTab) {
		this.helpTab = helpTab;
	}

}
