package com.tazzie02.tazbot.commands.secrethitler;

import net.dv8tion.jda.entities.User;

public class Player {
	
	private final User user;
	private boolean alive;
	private boolean hitler;
	private boolean fascist;
	private boolean president;
	private boolean chancellor;
	private boolean previousPresident;
	private boolean previousChancellor;
	
	public Player(User user) {
		this.user = user;
		this.alive = true;
	}
	
	public User getUser() {
		return user;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public Player setAlive(boolean alive) {
		this.alive = alive;
		return this;
	}

	public boolean isHitler() {
		return hitler;
	}
	
	public Player setHitler(boolean hitler) {
		this.hitler = hitler;
		return this;
	}

	public boolean isFascist() {
		return fascist;
	}
	
	public Player setFascist(boolean fascist) {
		this.fascist = fascist;
		return this;
	}

	public boolean isPresident() {
		return president;
	}
	
	public Player setPresident(boolean president) {
		this.president = president;
		return this;
	}
	
	public boolean isChancellor() {
		return chancellor;
	}
	
	public Player setChancellor(boolean chancellor) {
		this.chancellor = chancellor;
		return this;
	}

	public boolean isPreviousPresident() {
		return previousPresident;
	}
	
	public Player setPreviousPresident(boolean previousPresident) {
		this.previousPresident = previousPresident;
		return this;
	}

	public boolean isPreviousChancellor() {
		return previousChancellor;
	}
	
	public Player setPreviousChancellor(boolean previousChancellor) {
		this.previousChancellor = previousChancellor;
		return this;
	}

}
