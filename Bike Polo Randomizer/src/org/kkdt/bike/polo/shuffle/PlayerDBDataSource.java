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
	  private String[] allColumns = { PlayerDataBase.ENTRY_ID, PlayerDataBase.PLAYER_NAME, 
	      PlayerDataBase.PLAYER_GAMES, PlayerDataBase.PLAYER_HANDICAP, PlayerDataBase.PLAYER_INPLAY};

	  public PlayerDBDataSource(Context context) {
	    dbHelper = new BikePoloDataBaseHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

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
	        allColumns, PlayerDataBase.ENTRY_ID + " = " + insertId, null,
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
	        allColumns, PlayerDataBase.ENTRY_ID + " = " + updateId, null,
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
	        allColumns, null, null, null, null, orderBy);

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
		        allColumns, PlayerDataBase.WHERE_NAME, paramArray, null, null, null);		    

		    cursor.moveToFirst();
		    boolean result = !cursor.isAfterLast();
		    // Make sure to close the cursor
		    cursor.close();
		    return result;
		  }

	  public void deleteAllPlayers() {
		dbHelper.onUpgrade(database, 0, 0);  
	  }
	  
	  private BikePoloPlayer cursorGetPlayer(Cursor cursor) {
		String name = cursor.getString(1);
		int games = (int)cursor.getLong(2);
		int games_handicap = (int)cursor.getLong(3);		
		boolean inPlay = false;
		if (cursor.getLong(4)>0) { inPlay = true; }
	    BikePoloPlayer player = new BikePoloPlayer(name, games, games_handicap, inPlay);
	    return player;
	  }
}
