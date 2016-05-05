package com.tazzie02.tazbot.helpers.structures;

import java.util.ArrayList;
import java.util.List;

public class UserPath {
	
	private List<IdPath> users = new ArrayList<IdPath>();

	public String getUser(String userID) {
		for (int i = 0; i < users.size(); i++) {
			IdPath obj = users.get(i);
			if (obj.getId().equals(userID) && !obj.getPath().isEmpty()) {
				return obj.getPath();
			}
		}
		return null;
	}
	
	public void setUser(String userID, String path) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getId().equals(userID)) {
				users.remove(i);
			}
		}
		// Non null, non empty, non '-'
		if (path != null && !path.isEmpty() && !path.equals("-")) {
			users.add(new IdPath(userID, path));
		}
	}
	
	public List<IdPath> getUsers() {
		return users;
	}
	
	public class IdPath {
		private String id;
		private String path;
		
		public IdPath(String id, String path) {
			this.id = id;
			this.path = path;
		}
		
		public String getId() {
			return this.id;
		}
		
		public String getPath() {
			return this.path;
		}
	}
	
}
