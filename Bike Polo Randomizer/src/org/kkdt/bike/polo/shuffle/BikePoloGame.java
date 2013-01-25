package org.kkdt.bike.polo.shuffle;

import java.util.ArrayList;
import java.util.List;


public class BikePoloGame {
	public static boolean LEFT = true;
	public static boolean RIGHT = false;
	
	private String id;
	private String startTime;
	private String endTime;
	private int duration;
	private int resultL;
	private int sizeL;
	private int playerL1;
	private int playerL2;
	private int playerL3;
	private int playerL1Goals;
	private int playerL2Goals;
	private int playerL3Goals;
	private int resultR;
	private int sizeR;
	private int playerR1;
	private int playerR2;
	private int playerR3;
	private int playerR1Goals;
	private int playerR2Goals;
	private int playerR3Goals;
	private int winner; // left or right

	public BikePoloGame(String newId, String newStartTime, String newEndTime, int newDuration,
			int newResultL, int newSizeL, int newPlayerL1, int newPlayerL2, int newPlayerL3,
			int newPlayerL1Goals, int newPlayerL2Goals, int newPlayerL3Goals,
			int newResultR, int newSizeR, int newPlayerR1, int newPlayerR2, int newPlayerR3,
			int newPlayerR1Goals, int newPlayerR2Goals, int newPlayerR3Goals, int newWinner						
			) {
		id = newId;
		startTime = newStartTime;
		endTime = newEndTime;
		duration = newDuration;
		resultL = newResultL;
		sizeL = newSizeL;
		playerL1 = newPlayerL1;
		playerL2 = newPlayerL2;
		playerL3 = newPlayerL3;
		playerL1Goals = newPlayerL1Goals;
		playerL2Goals = newPlayerL2Goals;
		playerL3Goals = newPlayerL3Goals;
		resultR = newResultR;
		sizeR = newSizeR;
		playerR1 = newPlayerR1;
		playerR2 = newPlayerR2;
		playerR3 = newPlayerR3;
		playerR1Goals = newPlayerR1Goals;
		playerR2Goals = newPlayerR2Goals;
		playerR3Goals = newPlayerR3Goals;
		winner = newWinner; // left or right
	}
	
	public BikePoloGame(List<BikePoloPlayer> playersL, List<BikePoloPlayer> playersR, 
			String newStartTime, int newDuration) {
		id = "";
		startTime = newStartTime;
		endTime = "";
		duration = newDuration;
		resultL = 0;
		sizeL = playersL.size();
		switch (sizeL) {
		case 3:
			playerL3 = playersL.get(2).getIdInt();
		case 2:
			playerL2 = playersL.get(1).getIdInt();
		case 1:
			playerL1 = playersL.get(0).getIdInt();
			break;			
		}
		playerL1Goals = 0;
		playerL2Goals = 0;
		playerL3Goals = 0;
		resultR = 0;
		sizeR = playersR.size();
		switch (sizeR) {
		case 3:
			playerR3 = playersR.get(2).getIdInt();
		case 2:
			playerR2 = playersR.get(1).getIdInt();
		case 1:
			playerR1 = playersR.get(0).getIdInt();
			break;			
		}
		playerR1Goals = 0;
		playerR2Goals = 0;
		playerR3Goals = 0;
		winner = -1; // left or right
	}

	String getId() {
		return this.id;
	}

	int getIdInt() {		
		return Integer.parseInt(this.id);
	}
	
	String getStartTime() {
		return startTime;
	}
	
	String getEndTime() {
		return endTime;
	}
	
	int getDuration() {
		return duration;
	}
	
	int getResult(boolean leftTeam) {
		if (leftTeam) {
			return resultL;
		} else {
			return resultR;
		}
	}
	
	int getWinner() {
		return winner;
	}
	
	int getNumPlayers(boolean leftTeam) {
		if (leftTeam) {
			return sizeL;
		} else { // right team
			return sizeR;
		}
	}
	
	List<Integer> getPlayers(boolean leftTeam) {
		if (leftTeam) {
			List<Integer> result = new ArrayList<Integer>();
			switch (sizeL) {
			case 3:
				result.add(playerL3);
			case 2:
				result.add(playerL2);
			case 1:
				result.add(playerL1);
				break;			
			}
			return result;
		} else { // team right
			List<Integer> result = new ArrayList<Integer>();
			switch (sizeR) {
			case 3:
				result.add(playerR3);
			case 2:
				result.add(playerR2);
			case 1:
				result.add(playerR1);
				break;			
			}
			return result;
		}
	}

	int[] getGoals(boolean leftTeam) {
		if (leftTeam) {
			int[] result = new int[sizeL];
			switch (sizeL) {
			case 3:
				result[2] = playerL3Goals;
			case 2:
				result[1] = playerL2Goals;
			case 1:
				result[0] = playerL1Goals;
				break;			
			}
			return result;
		} else { // team right
			int[] result = new int[sizeR];
			switch (sizeR) {
			case 3:
				result[2] = playerR3Goals;
			case 2:
				result[1] = playerR2Goals;
			case 1:
				result[0] = playerR1Goals;
				break;			
			}
			return result;
		}
	}

	@Override
	public String toString() {
		return "Game " + this.id + " " + this.startTime;
	}

	@Override	
	public boolean equals (Object otherObject) {
		boolean result = false;
		if (otherObject instanceof BikePoloGame) {
			BikePoloGame otherGame = (BikePoloGame) otherObject;
			if (otherGame == this) { // same object
				result = true;
			} else {
				result = otherGame.startTime.equals(startTime) &&
						otherGame.endTime.equals(endTime) &&
						otherGame.playerL1 == playerL1 &&
						otherGame.playerL2 == playerL2 &&
						otherGame.playerL3 == playerL3 &&
						otherGame.playerR1 == playerR1 &&
						otherGame.playerR2 == playerR2 &&
						otherGame.playerR3 == playerR3 &&
						otherGame.id.equals(id);						
			}

		} 
		return result;		
	}

	@Override
	public int hashCode() {
		int hash = id.hashCode() | startTime.hashCode() | endTime.hashCode() | playerL1 | playerL2 | playerL3 | playerR1 | playerR2 | playerR3;
		return hash;
	}

}
