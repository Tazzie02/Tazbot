package com.tazzie02.tazbot.commands.general;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RollCommand implements Command {
	
	private final int MIN_DEFAULT = 1;
	private final int MAX_DEFAULT = 100;
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		int min = MIN_DEFAULT;
		int max = MAX_DEFAULT;
		
		try {
			if (args.length == 2) {
				if (NumberUtils.isDigits(args[0])) {
					max = Integer.parseInt(args[0]);
				}
				else {
					SendMessage.sendMessage(e, "Error: Not a positive number.");
					return;
				}
			}
			else if (args.length == 3) {
				if (NumberUtils.isDigits(args[0]) && NumberUtils.isDigits(args[1])) {
					min = Integer.parseInt(args[0]);
					max = Integer.parseInt(args[1]);
				}
				else {
					SendMessage.sendMessage(e, "Error: Not a positive number.");
					return;
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: Too many arguments.");
				return;
			}
		}
		catch (NumberFormatException ex) {
			SendMessage.sendMessage(e, "Error: Could not parse number as integer.");
			return;
		}
		
		try {
			int result = roll(min, max);
			SendMessage.sendMessage(e, String.format("%s rolled %d (%d-%d)",
					e.getMember().getEffectiveName(), result, min, max));
		}
		catch (IllegalArgumentException ex) {
			SendMessage.sendMessage(e, "Error: " + ex.getMessage());
		}
	}
	
	// Could be useful somewhere else?
	private int roll(int min, int max) {
		if (min < 0 || max < 0) {
			throw new IllegalArgumentException("Arguments must be positive numbers.");
		}
		else if (max <= min) {
			throw new IllegalArgumentException("Max must be larger than min.");
		}
		Random r = new Random();
		int bound = max - min + 1;
		
		return r.nextInt(bound) + min;
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
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
	public String getDetails() {
		return "roll - Return a random number from 1 to 100.\n"
		+ "roll <max> - Return a random number from 1 to <max>.\n"
		+ "roll <min> <max> - Return a random number from <min> to <max>.";
	}

	@Override
	public String getName() {
		return "Roll Command";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
