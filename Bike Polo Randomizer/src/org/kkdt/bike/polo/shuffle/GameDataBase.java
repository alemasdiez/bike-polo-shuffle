package org.kkdt.bike.polo.shuffle;

import android.provider.BaseColumns;

public abstract class GameDataBase implements BaseColumns {
	    public static final String TABLE_NAME = "GameDataBase";
	    public static final String ENTRY_ID = _ID;
	    public static final String START_TIME = "startTime";
	    public static final String END_TIME = "endTime";
	    public static final String DURATION = "duration";
	    public static final String RESULT_L = "resultL";
	    public static final String SIZE_L = "sizeL";
	    public static final String PLAYER_L1 = "playerL1";
	    public static final String PLAYER_L2 = "playerL2";
	    public static final String PLAYER_L3 = "playerL3";
	    public static final String P_L1_GOALS = "playerL1Goals";
	    public static final String P_L2_GOALS = "playerL2Goals";
	    public static final String P_L3_GOALS = "playerL3Goals";
	    public static final String RESULT_R = "resultR";
	    public static final String SIZE_R = "sizeR";
	    public static final String PLAYER_R1 = "playerR1";
	    public static final String PLAYER_R2 = "playerR2";
	    public static final String PLAYER_R3 = "playerR3";
	    public static final String P_R1_GOALS = "playerR1Goals";
	    public static final String P_R2_GOALS = "playerR2Goals";
	    public static final String P_R3_GOALS = "playerR3Goals";
	    public static final String WINNER = "winner"; // left or right
	    private static final String TEXT_TYPE = " TEXT";
	    private static final String LONG_TYPE = " INTEGER";
	    private static final String COMMA_SEP = ",";
	    public static final String SQL_CREATE_ENTRIES =
	        "CREATE TABLE " + TABLE_NAME + " (" +
	        ENTRY_ID + " INTEGER PRIMARY KEY," +
	        START_TIME + TEXT_TYPE + COMMA_SEP +	        
	        END_TIME + TEXT_TYPE + COMMA_SEP +	        
	        DURATION + LONG_TYPE + COMMA_SEP +
	        RESULT_L + LONG_TYPE + COMMA_SEP +
	        SIZE_L + LONG_TYPE + COMMA_SEP +
	        PLAYER_L1 + LONG_TYPE + COMMA_SEP +
	        PLAYER_L2 + LONG_TYPE + COMMA_SEP +
	        PLAYER_L3 + LONG_TYPE + COMMA_SEP +
	        P_L1_GOALS + LONG_TYPE + COMMA_SEP +
	        P_L2_GOALS + LONG_TYPE + COMMA_SEP +
	        P_L3_GOALS + LONG_TYPE + COMMA_SEP +
	        RESULT_R + LONG_TYPE + COMMA_SEP +
	        SIZE_R + LONG_TYPE + COMMA_SEP +
	        PLAYER_R1 + LONG_TYPE + COMMA_SEP +
	        PLAYER_R2 + LONG_TYPE + COMMA_SEP +
	        PLAYER_R3 + LONG_TYPE + COMMA_SEP +
	        P_R1_GOALS + LONG_TYPE + COMMA_SEP +
	        P_R2_GOALS + LONG_TYPE + COMMA_SEP +
	        P_R3_GOALS + LONG_TYPE + COMMA_SEP +
	        WINNER + LONG_TYPE +
	        " )";

	    public static final String SQL_DELETE_ENTRIES =
	        "DROP TABLE IF EXISTS " + TABLE_NAME;
	    public static final String WHERE_ID =
	    		ENTRY_ID + "=?";
	    public static final String DESC = " DESC";
	    	    	    
	 // Prevents the class from being instantiated.
	    private GameDataBase() {}
}
