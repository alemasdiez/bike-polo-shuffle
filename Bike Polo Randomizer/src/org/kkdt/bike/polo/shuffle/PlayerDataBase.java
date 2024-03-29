package org.kkdt.bike.polo.shuffle;

import android.provider.BaseColumns;

public abstract class PlayerDataBase implements BaseColumns {
	    public static final String TABLE_NAME = "PlayerDataBase";
	    public static final String ENTRY_ID = _ID;
	    public static final String UNUSED_ENTRY_ID = "entryid";
	    public static final String PLAYER_NAME = "playerName";
	    public static final String PLAYER_GAMES = "playerGames";
	    public static final String PLAYER_HANDICAP = "playerGamesRank"; // Rank = Games + Handicap
	    public static final String PLAYER_INPLAY = "playerInPlay";
	    public static final String PLAYER_MODVIEW = "playerModView";
	    public static final String PLAYER_TEAM = "playerTeamName";
	    private static final String TEXT_TYPE = " TEXT";
	    private static final String LONG_TYPE = " INTEGER";
	    private static final String COMMA_SEP = ",";
	    public static final String SQL_CREATE_ENTRIES =
	        "CREATE TABLE " + TABLE_NAME + " (" +
	        ENTRY_ID + " INTEGER PRIMARY KEY," +
	        UNUSED_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
	        PLAYER_NAME + TEXT_TYPE + COMMA_SEP +	        
	        PLAYER_GAMES + LONG_TYPE + COMMA_SEP +
	        PLAYER_HANDICAP + LONG_TYPE + COMMA_SEP +
	        PLAYER_INPLAY + LONG_TYPE + COMMA_SEP +
	        PLAYER_MODVIEW + LONG_TYPE + COMMA_SEP +
	        PLAYER_TEAM + TEXT_TYPE +
	        " )";

	    public static final String SQL_DELETE_ENTRIES =
	        "DROP TABLE IF EXISTS " + TABLE_NAME;
	    public static final String WHERE_NAME =
	    		PLAYER_NAME + "=?";
	    public static final String WHERE_ID =
	    		ENTRY_ID + "=?";
	    public static final String COLLATE = " COLLATE NOCASE";
	    
	    public static final String ADD_VIEW_COLUMN = "ALTER TABLE " + TABLE_NAME + 
	    		" ADD " + PLAYER_MODVIEW + LONG_TYPE;
	    public static final String ADD_TEAM_COLUMN = "ALTER TABLE " + TABLE_NAME + 
	    		" ADD " + PLAYER_TEAM + TEXT_TYPE;
	   
	 // Prevents the class from being instantiated.
	    private PlayerDataBase() {}
}
