package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;

public class OverwatchAllHeroes extends Overwatch {

	public OverwatchAllHeroes(String battleTag, String platform, String region) throws IOException, QuotaExceededException {
		super(battleTag, platform, region);
	}
	
	@Override
	protected String getURLEnd() {
		return "allHeroes";
	}
	
	public int getMeleeFinalBlows() {
		return Integer.parseInt(data.getString("MeleeFinalBlows").replace(",", ""));
	}
	
	public int getSoloKills() {
		return Integer.parseInt(data.getString("SoloKills").replace(",", ""));
	}
	
	public int getObjectiveKills() {
		return Integer.parseInt(data.getString("ObjectiveKills").replace(",", ""));
	}
	
	public int getFinalBlows() {
		return Integer.parseInt(data.getString("FinalBlows").replace(",", ""));
	}
	
	public int getDamageDone() {
		return Integer.parseInt(data.getString("DamageDone").replace(",", ""));
	}
	
	public int getEliminations() {
		return Integer.parseInt(data.getString("Eliminations").replace(",", ""));
	}
	
	public int getEnvironmentalKills() {
		return Integer.parseInt(data.getString("EnvironmentalKills").replace(",", ""));
	}
	
	public int getMultikills() {
		return Integer.parseInt(data.getString("Multikills").replace(",", ""));
	}
	
	public int getHealingDone() {
		return Integer.parseInt(data.getString("HealingDone").replace(",", ""));
	}
	
	public int getReconAssists() {
		return Integer.parseInt(data.getString("ReconAssists").replace(",", ""));
	}
	
	public int getTeleporterPadsDestroyed() {
		return Integer.parseInt(data.getString("TeleporterPadsDestroyed").replace(",", ""));
	}
	
	public int getEliminationsMostinGame() {
		return Integer.parseInt(data.getString("Eliminations-MostinGame").replace(",", ""));
	}
	
	public int getFinalBlowsMostinGame() {
		return Integer.parseInt(data.getString("FinalBlows-MostinGame").replace(",", ""));
	}
	
	public int getDamageDoneMostinGame() {
		return Integer.parseInt(data.getString("DamageDone-MostinGame").replace(",", ""));
	}
	
	public int getHealingDoneMostinGame() {
		return Integer.parseInt(data.getString("HealingDone-MostinGame").replace(",", ""));
	}
	
	public int getDefensiveAssistsMostinGame() {
		return Integer.parseInt(data.getString("DefensiveAssists-MostinGame").replace(",", ""));
	}
	
	public int getOffensiveAssistsMostinGame() {
		return Integer.parseInt(data.getString("OffensiveAssists-MostinGame").replace(",", ""));
	}
	
	public int getObjectiveKillsMostinGame() {
		return Integer.parseInt(data.getString("ObjectiveKills-MostinGame").replace(",", ""));
	}
	
	public String getObjectiveTimeMostinGame() {
		return data.getString("ObjectiveTime-MostinGame");
	}
	
	public int getMultikillBest() {
		return Integer.parseInt(data.getString("Multikill-Best").replace(",", ""));
	}
	
	public int getSoloKillsMostinGame() {
		return Integer.parseInt(data.getString("SoloKills-MostinGame").replace(",", ""));
	}
	
	public String getTimeSpentonFireMostinGame() {
		return data.getString("TimeSpentonFire-MostinGame");
	}
	
	public double getMeleeFinalBlowsAverage() {
		return Double.parseDouble(data.getString("MeleeFinalBlows-Average").replace(",", ""));
	}
	
	public double getFinalBlowsAverage() {
		return Double.parseDouble(data.getString("FinalBlows-Average").replace(",", ""));
	}
	
	public String getTimeSpentonFireAverage() {
		return data.getString("TimeSpentonFire-Average");
	}
	
	public double getSoloKillsAverage() {
		return Double.parseDouble(data.getString("SoloKills-Average").replace(",", ""));
	}
	
	public String getObjectiveTimeAverage() {
		return data.getString("ObjectiveTime-Average");
	}
	
	public double getObjectiveKillsAverage() {
		return Double.parseDouble(data.getString("ObjectiveKills-Average").replace(",", ""));
	}
	
	public int getHealingDoneAverage() {
		return Integer.parseInt(data.getString("HealingDone-Average").replace(",", ""));
	}
	
	public double getDeathsAverage() {
		return Double.parseDouble(data.getString("Deaths-Average").replace(",", ""));
	}
	
	public int getDamageDoneAverage() {
		return Integer.parseInt(data.getString("DamageDone-Average").replace(",", ""));
	}
	
	public double getEliminationsAverage() {
		return Double.parseDouble(data.getString("Eliminations-Average").replace(",", ""));
	}
	
	public int getDeaths() {
		return Integer.parseInt(data.getString("Deaths").replace(",", ""));
	}
	
	public int getEnvironmentalDeaths() {
		return Integer.parseInt(data.getString("EnvironmentalDeaths").replace(",", ""));
	}
	
	public int getCards() {
		return Integer.parseInt(data.getString("Cards").replace(",", ""));
	}
	
	public int getMedals() {
		return Integer.parseInt(data.getString("Medals").replace(",", ""));
	}
	
	public int getMedalsGold() {
		return Integer.parseInt(data.getString("Medals-Gold").replace(",", ""));
	}
	
	public int getMedalsSilver() {
		return Integer.parseInt(data.getString("Medals-Silver").replace(",", ""));
	}
	
	public int getMedalsBronze() {
		return Integer.parseInt(data.getString("Medals-Bronze").replace(",", ""));
	}
	
	public int getGamesWon() {
		return Integer.parseInt(data.getString("GamesWon").replace(",", ""));
	}
	
	public int getGamesPlayed() {
		return Integer.parseInt(data.getString("GamesPlayed").replace(",", ""));
	}
	
	public String getTimeSpentonFire() {
		return data.getString("TimeSpentonFire");
	}
	
	public String getObjectiveTime() {
		return data.getString("ObjectiveTime");
	}
	
	public int getScore() {
		return Integer.parseInt(data.getString("Score").replace(",", ""));
	}
	
	public String getTimePlayed() {
		return data.getString("TimePlayed");
	}
	
	public int getMeleeFinalBlowsMostinGame() {
		return Integer.parseInt(data.getString("MeleeFinalBlows-MostinGame").replace(",", ""));
	}
	
	public int getDefensiveAssists() {
		return Integer.parseInt(data.getString("DefensiveAssists").replace(",", ""));
	}
	
	public int getDefensiveAssistsAverage() {
		return Integer.parseInt(data.getString("DefensiveAssists-Average").replace(",", ""));
	}
	
	public int getOffensiveAssists() {
		return Integer.parseInt(data.getString("OffensiveAssists").replace(",", ""));
	}
	
	public int getOffensiveAssistsAverage() {
		return Integer.parseInt(data.getString("OffensiveAssists-Average").replace(",", ""));
	}

}
