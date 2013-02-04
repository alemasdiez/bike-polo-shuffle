package org.kkdt.bike.polo.shuffle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BikePoloDataBaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "players.db";
	private static final int DATABASE_VERSION = 4;
	
	BikePoloDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlayerDataBase.SQL_CREATE_ENTRIES);
        db.execSQL(GameDataBase.SQL_CREATE_ENTRIES);
    }
	
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	boolean upgradeSupported = false;
    	if (oldVersion < 4 && newVersion == 4){ // alternate view added to player
    		db.execSQL(PlayerDataBase.ADD_VIEW_COLUMN);
    		upgradeSupported = true;
    	}
    	if (oldVersion < 3 && newVersion == 3) { 	// game DB added
            db.execSQL(GameDataBase.SQL_CREATE_ENTRIES);           
    		upgradeSupported = true;
    	} 
    	if (!upgradeSupported) { // upgrade scheme not supported, recreate DB
    		db.execSQL(PlayerDataBase.SQL_DELETE_ENTRIES);
    		db.execSQL(GameDataBase.SQL_DELETE_ENTRIES);
    		onCreate(db);
    	}
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
}
