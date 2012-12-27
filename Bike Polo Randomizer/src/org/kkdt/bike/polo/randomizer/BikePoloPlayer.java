package org.kkdt.bike.polo.randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BikePoloPlayer {
	private String name;
	private int games;
	private int handicap; // value added to games due to missed games
	private boolean plays;
	public static final int MODE_RANDOM = 0;
	public static final int MODE_EVEN = 1;

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
	
	@Override	
	public boolean equals (Object otherObject) {
		if (otherObject instanceof BikePoloPlayer) {
			BikePoloPlayer otherPlayer = (BikePoloPlayer) otherObject;
			if (otherPlayer == this) { // same object
				return true;
			} else {
				return otherPlayer.name.equals(name) &&
						otherPlayer.games == games &&
						otherPlayer.handicap == handicap &&
						otherPlayer.plays == plays;						
			}
				
		} else
		return false;		
	}
	
	@Override
	public int hashCode() {
		int hash = name.hashCode() | games | handicap;
		if (!plays) {
			hash = - hash;
		}
		return hash;
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
	
    
    public static List<BikePoloPlayer> drawRandomPlayers(int playersToDraw, List<BikePoloPlayer> playersInDraw, int mode) {
    	List<BikePoloPlayer> drawnPlayers = new ArrayList<BikePoloPlayer>();		 
    	Random rand = new Random();
    	if (playersToDraw < playersInDraw.size()) {
	    	if (mode == MODE_RANDOM) {
	    		while (drawnPlayers.size() < playersToDraw) {
	    			int playerNumber = rand.nextInt(playersInDraw.size());
	    			BikePoloPlayer drawnPlayer = playersInDraw.get(playerNumber);
	    			boolean alreadyDrawn = false;
	    			for (int j= 0; j < drawnPlayers.size(); j++) {
	    				if (drawnPlayers.get(j) == drawnPlayer) {
	    					alreadyDrawn = true;
	    					break;
	    				}
	    			}
	    			if (!alreadyDrawn) {
	    				drawnPlayers.add(drawnPlayer);				
	    			}
	    		}
	    	} else { // MODE_EVEN - players with least games play first
	    		List<BikePoloPlayer> minPlaying = 
	    				BikePoloPlayer.findMinPlaying(playersInDraw);
	    		if (minPlaying.size() < playersToDraw) { // all least playing ones are in the game, we need more
	    			int playersNextDraw = playersToDraw - minPlaying.size();
	    			for (int i=0; i<minPlaying.size(); i++) {
	    				playersInDraw.remove(minPlaying.get(i));
	    			}	    		
	    			drawnPlayers = drawRandomPlayers(playersNextDraw, playersInDraw, MODE_EVEN);
	    			drawnPlayers.addAll(minPlaying);
	    		} else { // more players at minimum level that we need, draw random out of them
	    			drawnPlayers = drawRandomPlayers(playersToDraw, minPlaying, MODE_RANDOM);
	    		}
	    	}
		}
		else { // playersToDraw >= playersInDraw.size so all can play
			while (playersInDraw.size()>0) {
				int playerNumber = rand.nextInt(playersInDraw.size());
				drawnPlayers.add(playersInDraw.get(playerNumber));
				playersInDraw.remove(playerNumber); 
			}
		}	    		
		return drawnPlayers;
    }
}
