package social.ionch.core.api.impl;

import social.ionch.api.social.User;

public class SimpleUser implements User {
	private int id;
	private String name;
	private String displayName;
	
	public SimpleUser(int id, String name, String displayName) {
		this.id = id;
		this.name = name;
		this.displayName = displayName;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

}
