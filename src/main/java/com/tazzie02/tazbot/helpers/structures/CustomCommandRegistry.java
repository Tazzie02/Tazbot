package com.tazzie02.tazbot.helpers.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomCommandRegistry {
	
	private List<CustomCommand> customCommands = new ArrayList<CustomCommand>();
	
	public List<CustomCommand> getCustomCommands() {
		return (List<CustomCommand>) Collections.unmodifiableCollection(customCommands);
	}
	
	public boolean addCustomCommand(CustomCommand customCommand) {
		return customCommands.add(customCommand);
	}
	
	public boolean removeCustomCommand(CustomCommand customCommand) {
		return customCommands.remove(customCommand);
	}

}
