package org.kkdt.bike.polo.shuffle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BikePoloDataBaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "players.db";
	private static final int DATABASE_VERSION = 2;
	
	BikePoloDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlayerDataBase.SQL_CREATE_ENTRIES);
    }
	
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
    	db.execSQL(PlayerDataBase.SQL_DELETE_ENTRIES);
    	onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
