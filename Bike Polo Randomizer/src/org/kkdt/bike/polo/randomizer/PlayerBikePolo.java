package org.kkdt.bike.polo.randomizer;

public class PlayerBikePolo {
	private String name;
	private int games;
	private boolean plays;

	//		player(String newname, int newgames) {
	//			this.name = newname;
	//			this.games = newgames;
	//		}

	PlayerBikePolo(String newname) {
		this.name = newname;
		this.games = 0;
		this.plays = true;
	}

	public PlayerBikePolo(String newName, int newGames, boolean inPlay) {
		this.name = newName;
		this.games = newGames;
		this.plays = inPlay;
	}

	String getName() {
		return this.name;
	}

	int getGames() {
		return this.games;
	}
	
	void setGames(int newGames) {
		this.games = newGames;
	}
	
	void resetGames() {
		setGames(0);
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

}
