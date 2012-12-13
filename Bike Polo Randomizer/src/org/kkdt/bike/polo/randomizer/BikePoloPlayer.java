package org.kkdt.bike.polo.randomizer;

import java.util.ArrayList;
import java.util.List;

public class BikePoloPlayer {
	private String name;
	private int games;
	private int handicap; // value added to games due to missed games
	private boolean plays;

	//		player(String newname, int newgames) {
	//			this.name = newname;
	//			this.games = newgames;
	//		}

	BikePoloPlayer(String newName) {
		this.name = newName;
		this.games = 0;
		this.handicap = 0;
		this.plays = true;
	}

	BikePoloPlayer(String newName, int newHandicap) {
		this.name = newName;
		this.games = 0;
		this.handicap = newHandicap;
		this.plays = true;
	}
	
	
	public BikePoloPlayer(String newName, int newGames, int newHandicap, boolean inPlay) {
		this.name = newName;
		this.games = newGames;
		this.plays = inPlay;
		this.handicap = newHandicap;
	}

	String getName() {
		return this.name;
	}

	int getGames() {
		return this.games;
	}
	
	int getGamesRank() {
		return this.games + this.handicap;
	}
	
	int getHandicap() {
		return this.handicap;
	}
	
	void setGames(int newGames) {
		this.games = newGames;
	}
	
	void setHandicap(int newHandicap) {
		this.handicap = newHandicap;
	}
	
	void resetGames() {
		setGames(0);
		setHandicap(0);
	}

	void playGame() {
		this.games++;
	}

	boolean ifPlays() {
		return this.plays;
	}

	void setPlays(boolean ifPlays) {
		this.plays = ifPlays;
	}


	@Override
	public String toString() {
		return this.name;
	}

    public static List<BikePoloPlayer> findMinPlaying(List<BikePoloPlayer> allPlayers) {
    	List<BikePoloPlayer> minPlayers = new ArrayList<BikePoloPlayer>();
    	if (allPlayers.size()>0) {
    		int minGames = allPlayers.get(0).getGamesRank();
    		for (int i=0; i<allPlayers.size(); i++) {
    			BikePoloPlayer tPlayer = allPlayers.get(i);
    			if (tPlayer.getGamesRank() < minGames) {
    				minGames = tPlayer.getGamesRank();
    				minPlayers.removeAll(minPlayers);
    				minPlayers.add(tPlayer);
    			} else if (tPlayer.getGamesRank() == minGames) {
    				minPlayers.add(tPlayer);
    			}
    				
    		}
    	}    	
    	return minPlayers;
    }
    
    public static int findMinGamesRank(List<BikePoloPlayer> allPlayers) {
    	int minGamesRank = 0;
    	if (allPlayers.size()>0) {
    		minGamesRank = allPlayers.get(0).getGamesRank();
    		for (int i=0; i<allPlayers.size(); i++) {
    			BikePoloPlayer tPlayer = allPlayers.get(i);
    			if (tPlayer.ifPlays()) {
    				if (tPlayer.getGamesRank() < minGamesRank) {
    					minGamesRank = tPlayer.getGamesRank();
    				}
    			}
    		}
    	}    	
    	return minGamesRank;
    }
	
}
