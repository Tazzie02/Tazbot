package com.tazzie02.tazbot.commands.secrethitler;

import net.dv8tion.jda.core.entities.User;

public class Player {
	
	private final User user;
	private final boolean host;
	private boolean alive;
	private boolean hitler;
	private boolean fascist;
	private boolean president;
	private boolean chancellor;
	private boolean previousPresident;
	private boolean previousChancellor;
	
	public Player(User user, boolean host) {
		this.user = user;
		this.host = host;
		this.alive = true;
	}
	
	public User getUser() {
		return user;
	}
	
	public boolean isHost() {
		return host;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public boolean isHitler() {
		return hitler;
	}

	public boolean isFascist() {
		return fascist;
	}

	public boolean isPresident() {
		return president;
	}

	public boolean isChancellor() {
		return chancellor;
	}

	public boolean isPreviousPresident() {
		return previousPresident;
	}

	public boolean isPreviousChancellor() {
		return previousChancellor;
	}
	
	public Player setAlive(boolean alive) {
		this.alive = alive;
		return this;
	}
	
	public Player setHitler(boolean hitler) {
		this.hitler = hitler;
		return this;
	}
	
	public Player setFascist(boolean fascist) {
		this.fascist = fascist;
		return this;
	}
	
	public Player setPresident(boolean president) {
		this.president = president;
		return this;
	}

	public Player setChancellor(boolean chancellor) {
		this.chancellor = chancellor;
		return this;
	}

	public Player setPreviousPresident(boolean previousPresident) {
		this.previousPresident = previousPresident;
		return this;
	}

	public Player setPreviousChancellor(boolean previousChancellor) {
		this.previousChancellor = previousChancellor;
		return this;
	}

}
