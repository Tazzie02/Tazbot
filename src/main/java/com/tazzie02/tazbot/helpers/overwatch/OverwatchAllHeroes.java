package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;

public class OverwatchAllHeroes extends Overwatch {

	public OverwatchAllHeroes(String battleTag, String platform, String region, OverwatchGameMode mode) throws IOException {
		super(battleTag, platform, region, mode);
	}
	
	@Override
	protected String getURLEnd() {
		return "allHeroes";
	}
	
	public int getMeleeFinalBlows() {
		if (data.has("MeleeFinalBlows")) {
			return Integer.parseInt(data.getString("MeleeFinalBlows").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getSoloKills() {
		if (data.has("SoloKills")) {
			return Integer.parseInt(data.getString("SoloKills").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getObjectiveKills() {
		if (data.has("ObjectiveKills")) {
			return Integer.parseInt(data.getString("ObjectiveKills").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getFinalBlows() {
		if (data.has("FinalBlows")) {
			return Integer.parseInt(data.getString("FinalBlows").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getDamageDone() {
		if (data.has("DamageDone")) {
			return Integer.parseInt(data.getString("DamageDone").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getEliminations() {
		if (data.has("Eliminations")) {
			return Integer.parseInt(data.getString("Eliminations").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getEnvironmentalKills() {
		if (data.has("EnvironmentalKills")) {
			return Integer.parseInt(data.getString("EnvironmentalKills").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getMultikills() {
		if (data.has("Multikills")) {
			return Integer.parseInt(data.getString("Multikills").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getHealingDone() {
		if (data.has("HealingDone")) {
			return Integer.parseInt(data.getString("HealingDone").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getReconAssists() {
		if (data.has("ReconAssists")) {
			return Integer.parseInt(data.getString("ReconAssists").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getTeleporterPadsDestroyed() {
		if (data.has("TeleporterPadsDestroyed")) {
			return Integer.parseInt(data.getString("TeleporterPadsDestroyed").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getEliminationsMostinGame() {
		if (data.has("Eliminations-MostinGame")) {
			return Integer.parseInt(data.getString("Eliminations-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getFinalBlowsMostinGame() {
		if (data.has("FinalBlows-MostinGame")) {
			return Integer.parseInt(data.getString("FinalBlows-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getDamageDoneMostinGame() {
		if (data.has("DamageDone-MostinGame")) {
			return Integer.parseInt(data.getString("DamageDone-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getHealingDoneMostinGame() {
		if (data.has("HealingDone-MostinGame")) {
			return Integer.parseInt(data.getString("HealingDone-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getDefensiveAssistsMostinGame() {
		if (data.has("DefensiveAssists-MostinGame")) {
			return Integer.parseInt(data.getString("DefensiveAssists-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getOffensiveAssistsMostinGame() {
		if (data.has("OffensiveAssists-MostinGame")) {
			return Integer.parseInt(data.getString("OffensiveAssists-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getObjectiveKillsMostinGame() {
		if (data.has("ObjectiveKills-MostinGame")) {
			return Integer.parseInt(data.getString("ObjectiveKills-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public String getObjectiveTimeMostinGame() {
		if (data.has("ObjectiveTime-MostinGame")) {
			return data.getString("ObjectiveTime-MostinGame");
		}
		else {
			return "0";
		}
	}
	
	public int getMultikillBest() {
		if (data.has("Multikill-Best")) {
			return Integer.parseInt(data.getString("Multikill-Best").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getSoloKillsMostinGame() {
		if (data.has("SoloKills-MostinGame")) {
			return Integer.parseInt(data.getString("SoloKills-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public String getTimeSpentonFireMostinGame() {
		if (data.has("TimeSpentonFire-MostinGame")) {
			return data.getString("TimeSpentonFire-MostinGame");
		}
		else {
			return "0";
		}
	}
	
	public double getMeleeFinalBlowsAverage() {
		if (data.has("MeleeFinalBlows-Average")) {
			return Double.parseDouble(data.getString("MeleeFinalBlows-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public double getFinalBlowsAverage() {
		if (data.has("FinalBlows-Average")) {
			return Double.parseDouble(data.getString("FinalBlows-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public String getTimeSpentonFireAverage() {
		if (data.has("TimeSpentonFire-Average")) {
			return data.getString("TimeSpentonFire-Average");
		}
		else {
			return "0";
		}
	}
	
	public double getSoloKillsAverage() {
		if (data.has("SoloKills-Average")) {
			return Double.parseDouble(data.getString("SoloKills-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public String getObjectiveTimeAverage() {
		if (data.has("ObjectiveTime-Average")) {
			return data.getString("ObjectiveTime-Average");
		}
		else {
			return "0";
		}
	}
	
	public double getObjectiveKillsAverage() {
		if (data.has("ObjectiveKills-Average")) {
			return Double.parseDouble(data.getString("ObjectiveKills-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getHealingDoneAverage() {
		if (data.has("HealingDone-Average")) {
			return Integer.parseInt(data.getString("HealingDone-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public double getDeathsAverage() {
		if (data.has("Deaths-Average")) {
			return Double.parseDouble(data.getString("Deaths-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getDamageDoneAverage() {
		if (data.has("DamageDone-Average")) {
			return Integer.parseInt(data.getString("DamageDone-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public double getEliminationsAverage() {
		if (data.has("Eliminations-Average")) {
			return Double.parseDouble(data.getString("Eliminations-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getDeaths() {
		if (data.has("Deaths")) {
			return Integer.parseInt(data.getString("Deaths").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getEnvironmentalDeaths() {
		if (data.has("EnvironmentalDeaths")) {
			return Integer.parseInt(data.getString("EnvironmentalDeaths").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getCards() {
		if (data.has("Cards")) {
			return Integer.parseInt(data.getString("Cards").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getMedals() {
		if (data.has("Medals")) {
			return Integer.parseInt(data.getString("Medals").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getMedalsGold() {
		if (data.has("Medals-Gold")) {
			return Integer.parseInt(data.getString("Medals-Gold").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getMedalsSilver() {
		if (data.has("Medals-Silver")) {
			return Integer.parseInt(data.getString("Medals-Silver").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getMedalsBronze() {
		if (data.has("Medals-Bronze")) {
			return Integer.parseInt(data.getString("Medals-Bronze").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getGamesWon() {
		if (data.has("GamesWon")) {
			return Integer.parseInt(data.getString("GamesWon").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getGamesPlayed() {
		if (data.has("GamesPlayed")) {
			return Integer.parseInt(data.getString("GamesPlayed").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public String getTimeSpentonFire() {
		if (data.has("TimeSpentonFire")) {
			return data.getString("TimeSpentonFire");
		}
		else {
			return "0";
		}
	}
	
	public String getObjectiveTime() {
		if (data.has("ObjectiveTime")) {
			return data.getString("ObjectiveTime");
		}
		else {
			return "0";
		}
	}
	
	public String getTimePlayed() {
		if (data.has("TimePlayed")) {
			return data.getString("TimePlayed");
		}
		else {
			return "0";
		}
	}
	
	public int getMeleeFinalBlowsMostinGame() {
		if (data.has("MeleeFinalBlows-MostinGame")) {
			return Integer.parseInt(data.getString("MeleeFinalBlows-MostinGame").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getDefensiveAssists() {
		if (data.has("DefensiveAssists")) {
			return Integer.parseInt(data.getString("DefensiveAssists").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getDefensiveAssistsAverage() {
		if (data.has("DefensiveAssists-Average")) {
			return Integer.parseInt(data.getString("DefensiveAssists-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getOffensiveAssists() {
		if (data.has("OffensiveAssists")) {
			return Integer.parseInt(data.getString("OffensiveAssists").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getOffensiveAssistsAverage() {
		if (data.has("OffensiveAssists-Average")) {
			return Integer.parseInt(data.getString("OffensiveAssists-Average").replace(",", ""));
		}
		else {
			return 0;
		}
	}

}
