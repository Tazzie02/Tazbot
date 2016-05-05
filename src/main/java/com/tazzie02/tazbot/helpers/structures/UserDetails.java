package com.tazzie02.tazbot.helpers.structures;

public class UserDetails {
	
	private String name;
	private String discriminator;
	private String id;
	private boolean debug;
	
	public UserDetails(String name, String discriminator, String id, boolean debug) {
		this.name = name;
		this.discriminator = discriminator;
		this.id = id;
		this.debug = debug;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
}
