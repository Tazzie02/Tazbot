package com.tazzie02.tazbot.commands.fun;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.helpers.Roll;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class RollCommand extends Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		try {
			int min = 1;
			int max = 100;
			int number = 0;
			
			if (args.length == 1) {
				number = Roll.random();
			}
			else if (args.length == 2) {
				if (NumberUtils.isDigits(args[1])) {
					max = Integer.parseInt(args[1]);
					number = Roll.random(max);
				}
				else {
					throw new NumberFormatException("Arguments must be whole numbers.");
				}
			}
			else if (args.length >= 3) {
				if (NumberUtils.isDigits(args[1]) && NumberUtils.isDigits(args[2])) {
					min = Integer.parseInt(args[1]);
					max = Integer.parseInt(args[2]);
					number = Roll.random(min, max);
				}
				else {
					throw new NumberFormatException("Arguments must be whole numbers.");
				}
				
			}
			SendMessage.sendMessage(e, String.format("%s rolled %d (%d-%d)",
					e.getAuthor().getUsername(), number, min, max));
		}
		catch (NumberFormatException ex) {
			SendMessage.sendMessage(e, "Error: " + ex.getMessage());
		}
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("roll", "random");
	}
	
	@Override
	public String getDescription() {
		return "Roll a random number.";
	}
	
	@Override
	public String getName() {
		return "Roll Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "roll - Return a random number from 1 to 100.\n"
				+ "roll <max> - Return a random number from 1 to <max>.\n"
				+ "roll <min> <max> - Return a random number from <min> to <max>.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
}
