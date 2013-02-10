package org.kkdt.bike.polo.shuffle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class BikePoloPlayer {
	private String id;
	private String name;
	private int games;
	private int handicap; // value added to games due to missed games
	private boolean plays;
	private boolean modView; // player modification view visible
	private String teamName;
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
		this.modView = false;
		this.teamName = null;
	}

	BikePoloPlayer(String newName, int newHandicap) {
		this.name = newName;
		this.games = 0;
		this.handicap = newHandicap;
		this.plays = true;
		this.modView = false;
		this.teamName = null;
	}
	
	
	public BikePoloPlayer(String newId, String newName, int newGames, 
			int newHandicap, boolean inPlay, boolean newModView, String newTeamName) {
		this.id = newId;
		this.name = newName;
		this.games = newGames;
		this.plays = inPlay;
		this.handicap = newHandicap;
		this.modView = newModView;
		if (newTeamName == "") {
			newTeamName = null;
		}		
		this.teamName = newTeamName;
	}

	String getName() {
		return this.name;
	}

	String getId() {
		return this.id;
	}
	
	int getIdInt() {		
		return Integer.parseInt(this.id);
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
	
	boolean getModView() {
		return this.modView;
	}

	void switchView(ViewFlipper vf, boolean left) {
		if (vf != null) {
			if (left) {
				Context context = vf.getContext();
				Animation animFlipInLeft = AnimationUtils.loadAnimation(context, R.anim.flip_in_left);
				Animation animFlipOutLeft = AnimationUtils.loadAnimation(context, R.anim.flip_out_left);
				vf.setInAnimation(animFlipInLeft);
		        vf.setOutAnimation(animFlipOutLeft);				
				vf.showNext();
				vf.clearAnimation();
			} else {
				Context context = vf.getContext();
				Animation animFlipInRight = AnimationUtils.loadAnimation(context, R.anim.flip_in_right);
				Animation animFlipOutRight = AnimationUtils.loadAnimation(context, R.anim.flip_out_right);
				vf.setInAnimation(animFlipInRight);
		        vf.setOutAnimation(animFlipOutRight);				
				vf.showPrevious();
				vf.clearAnimation();
			}
		}
		modView = !modView; 
	}
	
	String getTeamName() {
		if (this.teamName == null) {
			return "";
		}
		return this.teamName;
	}
	
	void setTeamName(String newTeamName) {
		if (newTeamName == "") {
			newTeamName = null;
		}
		this.teamName = newTeamName;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	@Override	
	public boolean equals (Object otherObject) {
		boolean result = false;
		if (otherObject instanceof BikePoloPlayer) {
			BikePoloPlayer otherPlayer = (BikePoloPlayer) otherObject;
			if (otherPlayer == this) { // same object
				result = true;
			} else {
				result = otherPlayer.name.equals(name) &&
						otherPlayer.games == games &&
						otherPlayer.handicap == handicap &&
						otherPlayer.plays == plays &&
						otherPlayer.id.equals(id);						
			}
				
		} 
		return result;		
	}
	
	@Override
	public int hashCode() {
		int hash = name.hashCode() | games | handicap | id.hashCode();
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
