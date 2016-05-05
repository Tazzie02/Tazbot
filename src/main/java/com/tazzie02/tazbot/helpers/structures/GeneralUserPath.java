package com.tazzie02.tazbot.helpers.structures;

import com.tazzie02.tazbot.helpers.structures.UserPath;

public class GeneralUserPath extends UserPath {
	
	private String general;

	public String getGeneral() {
		return general;
	}

	public void setGeneral(String general) {
		this.general = general;
	}
	
	@Override
	public String getUser(String userID) {
		String value = super.getUser(userID);
		if (value == null) {
			return getGeneral();
		}
		else {
			return value;
		}
	}
	
}
