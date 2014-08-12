package com.intel.internship.openstackmanagement.persistence;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "openStackManagement";
	private static final int DATABASE_VERSION = 1;
	
	public static class ProfileTable {
		
		public static final String TABLE_NAME = "profile";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_PROFILE_NAME = "name";
		public static final String COLUMN_ENDPOINT = "endpoint";
		public static final String COLUMN_USERNAME = "username";
		public static final String COLUMN_PASSWORD = "password";
		public static final String COLUMN_TENANT_NAME = "tenant_name";
		
		public static final String CRATING_SCRIPT = "CREATE TABLE " + TABLE_NAME + " ("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_PROFILE_NAME + " text, " 
				+ COLUMN_ENDPOINT + " text, "
				+ COLUMN_USERNAME + " text, "
				+ COLUMN_PASSWORD + " text, "
				+ COLUMN_TENANT_NAME + " text "
				+ ");";
		
		public static final String DELETED_SCRIPT = "DROP TABLE IF EXITIS " + TABLE_NAME + ";";
		
	}
	
	public static final String DATABASE_CREATING_SCRIPT = ProfileTable.CRATING_SCRIPT;
	
	
	public MySQLiteHelper(Context context) {
		super (context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATING_SCRIPT);
		Log.i("DEBUG", MySQLiteHelper.class.getName() + ", onCreate(): DATABSE has been created");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(ProfileTable.DELETED_SCRIPT);
		onCreate(database);
	}
	
	

}
