package com.tazzie02.tazbot.helpers.structures;

import java.util.Collections;
import java.util.List;

public class CustomCommand {
	
	private String name;
	private String ownerId;
	private String aliases;
	private CustomCommandType type;
	private String content;
	
	public CustomCommand(String name, String ownerId, List<String> aliases, CustomCommandType type, String content) {
		this.name = name;
		this.ownerId = ownerId;
		this.aliases = String.format(" ", aliases);
		this.type = type;
		this.content = content;
	}
	
	public CustomCommand(String name, String ownerId, CustomCommandType type, String content) {
		this(name, ownerId, Collections.emptyList(), type, content);
	}
	
	public String getName() {
		return name;
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public 
	
	public enum CustomCommandType {
		TEXT("text"),
		COUNTDOWN("countdown")
		;
		
		private final String s;
		
		private CustomCommandType(final String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return s;
		}
	}
	
}
