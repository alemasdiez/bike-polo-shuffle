package org.kkdt.bike.polo.shuffle;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PlayerDBDataSource {
	// Database fields
	private SQLiteDatabase database;
	private BikePoloDataBaseHelper dbHelper;
	private String[] allPlayerColumns = { PlayerDataBase.ENTRY_ID, PlayerDataBase.PLAYER_NAME, 
			PlayerDataBase.PLAYER_GAMES, PlayerDataBase.PLAYER_HANDICAP, PlayerDataBase.PLAYER_INPLAY};
	private String[] allGameColumns = { GameDataBase.ENTRY_ID, GameDataBase.START_TIME, GameDataBase.END_TIME,
			GameDataBase.DURATION, GameDataBase.RESULT_L, GameDataBase.SIZE_L,
			GameDataBase.PLAYER_L1, GameDataBase.PLAYER_L2, GameDataBase.PLAYER_L3,
			GameDataBase.P_L1_GOALS, GameDataBase.P_L2_GOALS, GameDataBase.P_L3_GOALS,
			GameDataBase.RESULT_R, GameDataBase.SIZE_R,
			GameDataBase.PLAYER_R1, GameDataBase.PLAYER_R2, GameDataBase.PLAYER_R3,
			GameDataBase.P_R1_GOALS, GameDataBase.P_R2_GOALS, GameDataBase.P_R3_GOALS,
			GameDataBase.WINNER	};
	private boolean dbOpen;

	// general DB handling
	public PlayerDBDataSource(Context context) {
		dbHelper = new BikePoloDataBaseHelper(context);
	}

	public void open() throws SQLException {
		if (!dbOpen) {
			database = dbHelper.getWritableDatabase();
			dbOpen = true;
		}
	}

	public void close() {
		if (dbOpen) {
			dbHelper.close();
			dbOpen = false;
		}
	}

	public boolean isOpen() {
		return dbOpen;
	}

	// Player handling
	public void insertPlayer(BikePoloPlayer player) {
		String name = player.getName();
		int games = player.getGames();
		int handicap = player.getHandicap();		
		boolean inPlay = player.ifPlays(); 
		ContentValues values = new ContentValues();
		values.put(PlayerDataBase.PLAYER_NAME, name);
		values.put(PlayerDataBase.PLAYER_GAMES, games);
		values.put(PlayerDataBase.PLAYER_HANDICAP, handicap);
		long inPlayLong=0;
		if (inPlay) {inPlayLong=1;}
		values.put(PlayerDataBase.PLAYER_INPLAY, inPlayLong);
		long insertId = database.insert(PlayerDataBase.TABLE_NAME, null,
				values);
		Cursor cursor = database.query(PlayerDataBase.TABLE_NAME,
				allPlayerColumns, PlayerDataBase.ENTRY_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		cursor.close();
	}

	public void updatePlayer(BikePoloPlayer player) {
		String name = player.getName();
		int games = player.getGames();
		int handicap = player.getHandicap();
		boolean inPlay = player.ifPlays(); 
		String[] paramArray = {name};
		ContentValues values = new ContentValues();
		values.put(PlayerDataBase.PLAYER_GAMES, games);
		values.put(PlayerDataBase.PLAYER_HANDICAP, handicap);
		long inPlayLong=0;
		if (inPlay) {inPlayLong=1;}
		values.put(PlayerDataBase.PLAYER_INPLAY, inPlayLong);
		long updateId = database.update(PlayerDataBase.TABLE_NAME, values,
				PlayerDataBase.WHERE_NAME, paramArray);
		Cursor cursor = database.query(PlayerDataBase.TABLE_NAME,
				allPlayerColumns, PlayerDataBase.ENTRY_ID + " = " + updateId, null,
				null, null, null);
		cursor.moveToFirst();
		cursor.close();
	}


	public void deletePlayer(String name) {
		String[] paramArray = {name};
		database.delete(PlayerDataBase.TABLE_NAME, PlayerDataBase.WHERE_NAME, paramArray);
	}

	public List<BikePoloPlayer> getAllPlayers() {
		List<BikePoloPlayer> players = new ArrayList<BikePoloPlayer>();
		String orderBy  = PlayerDataBase.PLAYER_NAME + PlayerDataBase.COLLATE;  	          
		Cursor cursor = database.query(PlayerDataBase.TABLE_NAME,
				allPlayerColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BikePoloPlayer player = cursorGetPlayer(cursor);
			players.add(player);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return players;
	}

	public boolean checkPlayer(String name) {
		String[] paramArray = {name};
		Cursor cursor = database.query(PlayerDataBase.TABLE_NAME,
				allPlayerColumns, PlayerDataBase.WHERE_NAME, paramArray, null, null, null);		    

		cursor.moveToFirst();
		boolean result = !cursor.isAfterLast();
		// Make sure to close the cursor
		cursor.close();
		return result;
	}

	public BikePoloPlayer getPlayer(String id) {
		String[] paramArray = {id};
		BikePoloPlayer result;
		Cursor cursor = database.query(PlayerDataBase.TABLE_NAME,
				allPlayerColumns, PlayerDataBase.WHERE_ID, paramArray, null, null, null);		    

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) { // player found
			result = cursorGetPlayer(cursor);		    	
		} else {
			result = null;
		}
		// Make sure to close the cursor
		cursor.close();		    
		return result;
	}

	public void deleteAllPlayers() {
		database.execSQL(PlayerDataBase.SQL_DELETE_ENTRIES);    	
        database.execSQL(PlayerDataBase.SQL_CREATE_ENTRIES);		
	}

	private BikePoloPlayer cursorGetPlayer(Cursor cursor) {
		String id = cursor.getString(0);		  
		String name = cursor.getString(1);
		int games = (int)cursor.getLong(2);
		int games_handicap = (int)cursor.getLong(3);		
		boolean inPlay = false;
		if (cursor.getLong(4)>0) { inPlay = true; }
		BikePoloPlayer player = new BikePoloPlayer(id, name, games, games_handicap, inPlay);
		return player;
	}

	// Game handling
	public void insertGame(BikePoloGame game) {
		String startTime = game.getStartTime();
		String endTime = game.getEndTime();
		int duration = game.getDuration();
		int resultL = game.getResult(BikePoloGame.LEFT);
		int sizeL = game.getNumPlayers(BikePoloGame.LEFT);
		List<Integer> players = game.getPlayers(BikePoloGame.LEFT);
		int playerL1 = 0;
		int playerL2 = 0;
		int playerL3 = 0;
		switch (sizeL) {
		case 3:
			playerL3 = players.get(2);
		case 2:
			playerL2 = players.get(1);
		case 1:
			playerL1 = players.get(0);
			break;			
		}
		int[] goals = game.getGoals(BikePoloGame.LEFT);
		int playerL1Goals = 0;
		int playerL2Goals = 0;
		int playerL3Goals = 0;
		switch (sizeL) {
		case 3:
			playerL3Goals = goals[2];
		case 2:
			playerL2Goals = goals[1];
		case 1:
			playerL1Goals = goals[0];
			break;			
		}
		int resultR = game.getResult(BikePoloGame.RIGHT);
		int sizeR = game.getNumPlayers(BikePoloGame.RIGHT);
		players = game.getPlayers(BikePoloGame.RIGHT);
		int playerR1 = 0;
		int playerR2 = 0;
		int playerR3 = 0;
		switch (sizeR) {
		case 3:
			playerR3 = players.get(2);
		case 2:
			playerR2 = players.get(1);
		case 1:
			playerR1 = players.get(0);
			break;			
		}
		goals = game.getGoals(BikePoloGame.RIGHT);
		int playerR1Goals = 0;
		int playerR2Goals = 0;
		int playerR3Goals = 0;
		switch (sizeR) {
		case 3:
			playerR3Goals = goals[2];
		case 2:
			playerR2Goals = goals[1];
		case 1:
			playerR1Goals = goals[0];
			break;			
		}		
		int winner = game.getWinner();
		
		ContentValues values = new ContentValues();
		values.put(GameDataBase.START_TIME, startTime);
		values.put(GameDataBase.END_TIME, endTime);
		values.put(GameDataBase.DURATION, duration);
		values.put(GameDataBase.RESULT_L, resultL);
		values.put(GameDataBase.SIZE_L, sizeL);
		values.put(GameDataBase.PLAYER_L1, playerL1);
		values.put(GameDataBase.PLAYER_L2, playerL2);
		values.put(GameDataBase.PLAYER_L3, playerL3);
		values.put(GameDataBase.P_L1_GOALS, playerL1Goals);
		values.put(GameDataBase.P_L2_GOALS, playerL2Goals);
		values.put(GameDataBase.P_L3_GOALS, playerL3Goals);
		values.put(GameDataBase.RESULT_R, resultR);
		values.put(GameDataBase.SIZE_R, sizeR);
		values.put(GameDataBase.PLAYER_R1, playerR1);
		values.put(GameDataBase.PLAYER_R2, playerR2);
		values.put(GameDataBase.PLAYER_R3, playerR3);
		values.put(GameDataBase.P_R1_GOALS, playerR1Goals);
		values.put(GameDataBase.P_R2_GOALS, playerR2Goals);
		values.put(GameDataBase.P_R3_GOALS, playerR3Goals);
		values.put(GameDataBase.WINNER, winner); 		
		long insertId = database.insert(GameDataBase.TABLE_NAME, null,
				values);
		Cursor cursor = database.query(GameDataBase.TABLE_NAME,
				allGameColumns, GameDataBase.ENTRY_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		cursor.close();
	}
	
	public List<BikePoloGame> getAllGames() {
		List<BikePoloGame> games = new ArrayList<BikePoloGame>();
		String orderBy  = GameDataBase.ENTRY_ID + GameDataBase.DESC;  	          
		Cursor cursor = database.query(GameDataBase.TABLE_NAME,
				allGameColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BikePoloGame game = cursorGetGame(cursor);
			games.add(game);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return games;
	}

	public void deleteGame(String id) {
		String[] paramArray = {id};
		database.delete(GameDataBase.TABLE_NAME, GameDataBase.WHERE_ID, paramArray);
	}

	public void deleteAllGames() {
		database.execSQL(GameDataBase.SQL_DELETE_ENTRIES);
        database.execSQL(GameDataBase.SQL_CREATE_ENTRIES);		
	}

	private BikePoloGame cursorGetGame(Cursor cursor) {
		String id = cursor.getString(0);
		String startTime = cursor.getString(1);
		String endTime = cursor.getString(2);
		int duration = (int)cursor.getLong(3);
		int resultL = (int)cursor.getLong(4);
		int sizeL = (int)cursor.getLong(5);
		int playerL1 = (int)cursor.getLong(6);
		int playerL2 = (int)cursor.getLong(7);
		int playerL3 = (int)cursor.getLong(8);
		int playerL1Goals = (int)cursor.getLong(9);
		int playerL2Goals = (int)cursor.getLong(10);
		int playerL3Goals = (int)cursor.getLong(11);
		int resultR = (int)cursor.getLong(12);
		int sizeR = (int)cursor.getLong(13);
		int playerR1 = (int)cursor.getLong(14);
		int playerR2 = (int)cursor.getLong(15);
		int playerR3 = (int)cursor.getLong(16);
		int playerR1Goals = (int)cursor.getLong(17);
		int playerR2Goals = (int)cursor.getLong(18);
		int playerR3Goals = (int)cursor.getLong(19);
		int winner = (int)cursor.getLong(20);
		BikePoloGame game = new BikePoloGame(id, startTime, endTime, duration,
				resultL, sizeL, playerL1, playerL2, playerL3, playerL1Goals, playerL2Goals, playerL3Goals,
				resultR, sizeR, playerR1, playerR2, playerR3, playerR1Goals, playerR2Goals, playerR3Goals,
				winner);
		return game;
	}

}
