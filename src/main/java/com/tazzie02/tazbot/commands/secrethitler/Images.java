package com.tazzie02.tazbot.commands.secrethitler;

public enum Images {
	
	COMBINED_HITLER("http://i.imgur.com/no8TZWs.jpg"),
	COMBINED_FASCIST("http://i.imgur.com/xV8iOED.jpg"),
	COMBINED_LIBERAL("http://i.imgur.com/RkuOVQ5.jpg"),
	
	PARTY_FASCIST("http://i.imgur.com/lFp3hXy.jpg"),
	PARTY_LIBERAL("http://i.imgur.com/8V7dPzZ.jpg"),
	
	ROLE_FASCIST("http://i.imgur.com/krA7JSU.jpg"),
	ROLE_HITLER("http://i.imgur.com/e7j8mY8.jpg"),
	ROLE_LIBERAL("http://i.imgur.com/SbxxXEp.jpg"),
	
	POLICY_FASCIST("http://i.imgur.com/LAnMLLy.jpg"),
	POLICY_LIBERAL("http://i.imgur.com/ER4sXHs.jpg"),
	
	VOTE_JA("http://i.imgur.com/OroPVFM.jpg"),
	VOTE_NEIN("http://i.imgur.com/Y8C3LgC.jpg"),
	VOTE_COMBINED("http://i.imgur.com/YG79QPs.jpg"),
	
	POLICY_2_2FASCIST_0LIBERAL("http://i.imgur.com/EnUmQ5R.jpg"),
	POLICY_2_1FASCIST_1LIBERAL("http://i.imgur.com/MX39hdo.jpg"),
	POLICY_2_0FASCIST_2LIBERAL("http://i.imgur.com/6w27FQE.jpg"),
	
	POLICY_3_3FASCIST_0LIBERAL("http://i.imgur.com/hf0NCBU.jpg"),
	POLICY_3_2FASCIST_1LIBERAL("http://i.imgur.com/IilKnkQ.jpg"),
	POLICY_3_1FASCIST_2LIBERAL("http://i.imgur.com/bLp1dpN.jpg"),
	POLICY_3_0FASCIST_3LIBERAL("http://i.imgur.com/KkJzfvb.jpg"),
	
	BOARD_LIBERAL_0("http://i.imgur.com/KBDVigZ.jpg"),
	BOARD_LIBERAL_1("http://i.imgur.com/2bwoiBu.jpg"),
	BOARD_LIBERAL_2("http://i.imgur.com/O6uwyka.jpg"),
	BOARD_LIBERAL_3("http://i.imgur.com/tXWFT6L.jpg"),
	BOARD_LIBERAL_4("http://i.imgur.com/KDhOaMj.jpg"),
	BOARD_LIBERAL_5("http://i.imgur.com/UTnu2WD.jpg"),
	
	BOARD_56_FASCIST_0("http://i.imgur.com/0D4rlGB.jpg"),
	BOARD_56_FASCIST_1("http://i.imgur.com/K8jj2kY.jpg"),
	BOARD_56_FASCIST_2("http://i.imgur.com/PODrHNM.jpg"),
	BOARD_56_FASCIST_3("http://i.imgur.com/2jxruGy.jpg"),
	BOARD_56_FASCIST_4("http://i.imgur.com/GqHiPn4.jpg"),
	BOARD_56_FASCIST_5("http://i.imgur.com/BObk6Ir.jpg"),
	BOARD_56_FASCIST_6("http://i.imgur.com/swJNkZ1.jpg"),
	
	BOARD_78_FASCIST_0("http://i.imgur.com/KiRCa00.jpg"),
	BOARD_78_FASCIST_1("http://i.imgur.com/qKbOpMj.jpg"),
	BOARD_78_FASCIST_2("http://i.imgur.com/vAKexXC.jpg"),
	BOARD_78_FASCIST_3("http://i.imgur.com/ShCeYBD.jpg"),
	BOARD_78_FASCIST_4("http://i.imgur.com/3TssRzu.jpg"),
	BOARD_78_FASCIST_5("http://i.imgur.com/phSzxCG.jpg"),
	BOARD_78_FASCIST_6("http://i.imgur.com/d2PXWZj.jpg"),
	
	BOARD_910_FASCIST_0("http://i.imgur.com/nxNH31A.jpg"),
	BOARD_910_FASCIST_1("http://i.imgur.com/wcufLzf.jpg"),
	BOARD_910_FASCIST_2("http://i.imgur.com/xMcYvkV.jpg"),
	BOARD_910_FASCIST_3("http://i.imgur.com/y9tXkz4.jpg"),
	BOARD_910_FASCIST_4("http://i.imgur.com/TSs8FQO.jpg"),
	BOARD_910_FASCIST_5("http://i.imgur.com/mPQepmh.jpg"),
	BOARD_910_FASCIST_6("http://i.imgur.com/x4dP6ZM.jpg")
	;
	
	private final String s;
	
	private Images(final String s) {
		this.s = s;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
}
