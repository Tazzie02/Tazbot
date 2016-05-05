package com.tazzie02.tazbot.util;

import java.util.ArrayList;
import java.util.List;

import com.tazzie02.tazbot.helpers.structures.Config;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.managers.SettingsManager;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

public class UserUtil {
	
	/**
	 * Check whether {@link net.dv8tion.jda.entities.User User} with userId is listed as a developer in the
	 * configuration file.
	 * @param userId Id of {@link net.dv8tion.jda.entities.User User}.
	 * @return True if {@link net.dv8tion.jda.entities.User User} with userId is a developer.
	 */
	public static boolean isDev(String userId) {
		return ConfigManager.getInstance().getConfig().getDevs().stream().anyMatch(d -> d.getId().equals(userId));
	}
	
	/**
	 * Check whether {@link net.dv8tion.jda.entities.User User} is listed as a developer in the
	 * configuration file.
	 * @param user {@link net.dv8tion.jda.entities.User User}.
	 * @return True if {@link net.dv8tion.jda.entities.User User} is a developer.
	 */
	public static boolean isDev(User user) {
		return isDev(user.getId());
	}
	
	/**
	 * Check whether {@link net.dv8tion.jda.entities.User User} with userId is listed as a moderator in 
	 * {@link net.dv8tion.jda.entities.Guild Guild} with guildId in the guild specific settings file.
	 * @param userId Id of the {@link net.dv8tion.jda.entities.User User}.
	 * @param guildId Id of the {@link net.dv8tion.jda.entities.Guild Guild}.
	 * @return True if {@link net.dv8tion.jda.entities.User User} with userId is a moderator in
	 * {@link net.dv8tion.jda.entities.Guild Guild} with guildId.
	 */
	public static boolean isMod(String userId, String guildId) {
		return SettingsManager.getInstance(guildId).getSettings().getModerators().contains(userId);
	}
	
	/**
	 * Check whether {@link net.dv8tion.jda.entities.User User} is listed as a moderator in 
	 * {@link net.dv8tion.jda.entities.Guild Guild} in the guild specific settings file.
	 * @param user {@link net.dv8tion.jda.entities.User User}.
	 * @param guild {@link net.dv8tion.jda.entities.Guild Guild}.
	 * @return True if {@link net.dv8tion.jda.entities.User User} is a moderator in {@link net.dv8tion.jda.entities.Guild Guild}.
	 */
	public static boolean isMod(User user, Guild guild) {
		return isMod(user.getId(), guild.getId());
	}
	
	/**
	 * Check whether {@link net.dv8tion.jda.entities.User User} is listed as a moderator in the 
	 * {@link net.dv8tion.jda.entities.Guild Guild} with guildId in the guild specific settings file.
	 * @param {@link net.dv8tion.jda.entities.User User}
	 * @param guildId Id of the {@link net.dv8tion.jda.entities.Guild Guild}
	 * @return True if {@link net.dv8tion.jda.entities.User User} is a moderator in 
	 * {@link net.dv8tion.jda.entities.Guild Guild} with guildId.
	 */
	public static boolean isMod(User user, String guildId) {
		return isMod(user.getId(), guildId);
	}
	
	/**
	 * Check whether {@link net.dv8tion.jda.entities.User User} with userId is listed as a moderator in 
	 * {@link net.dv8tion.jda.entities.Guild Guild} in the guild specific settings file.
	 * @param userId Id of the {@link net.dv8tion.jda.entities.User User}.
	 * @param guild {@link net.dv8tion.jda.entities.Guild Guild}.
	 * @return True if {@link net.dv8tion.jda.entities.User User} with userId is a moderator in {@link net.dv8tion.jda.entities.Guild Guild}
	 */
	public static boolean isMod(String userId, Guild guild) {
		return isMod(userId, guild.getId());
	}
	
	/**
	 * Get a list of all developer names in the configuration file.
	 * @return List of Strings of developer names.
	 */
	public static List<String> getDevNames() {
		List<String> devNames = new ArrayList<String>();
		ConfigManager.getInstance().getConfig().getDevs().stream().forEach(d -> devNames.add(d.getName()));
		
		return devNames;
	}
	
	/**
	 * Get a list of all developer names combined with discriminators in the configuration file.
	 * Name and discriminator are separated by #.
	 * <pre>eg. UserName#0000</pre>
	 * @return List of Strings of developer names combined with discriminators.
	 */
	public static List<String> getDevNamesDiscrims() {
		Config config = ConfigManager.getInstance().getConfig();
		List<String> devNames = new ArrayList<String>();
		List<String> devDiscrims = new ArrayList<String>();
		
		config.getDevs().stream().forEach(d -> {
			devNames.add(d.getName());
			devDiscrims.add(d.getDiscriminator());
		});
		
		List<String> devNamesDiscrims = new ArrayList<String>();
		for (int i = 0; i < devNames.size(); i++) {
			devNamesDiscrims.add(devNames.get(i) + "#" + devDiscrims.get(i));
		}
		
		return devNamesDiscrims;
	}
	
}
