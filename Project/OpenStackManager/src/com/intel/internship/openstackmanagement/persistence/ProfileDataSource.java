package com.intel.internship.openstackmanagement.persistence;

import java.util.LinkedList;
import java.util.List;

import com.intel.internship.openstackmanagement.model.Profile;
import com.intel.internship.openstackmanagement.persistence.MySQLiteHelper.ProfileTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileDataSource {
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	private String [] columns = { ProfileTable.COLUMN_ID, ProfileTable.COLUMN_ENDPOINT, 
			ProfileTable.COLUMN_USERNAME, ProfileTable.COLUMN_PASSWORD, ProfileTable.COLUMN_TENANT_NAME, 
			ProfileTable.COLUMN_PROFILE_NAME };
	
	public ProfileDataSource(Context context){
		dbHelper = new MySQLiteHelper(context);
	}
	
	private SQLiteDatabase openWritableConnection() {
		return dbHelper.getWritableDatabase();
	}
	
	private SQLiteDatabase openReadableConnection() {
		return dbHelper.getReadableDatabase();
	}
	
	private void closeConnection() {
		database.close();
	}
	
	public void insertProfile(Profile profile) {
		database = openWritableConnection();
		ContentValues values = new ContentValues();
		values.put(ProfileTable.COLUMN_PROFILE_NAME, profile.getName());
		values.put(ProfileTable.COLUMN_ENDPOINT, profile.getEndpoint());
		values.put(ProfileTable.COLUMN_USERNAME, profile.getUsername());
		values.put(ProfileTable.COLUMN_PASSWORD, profile.getPassword());
		values.put(ProfileTable.COLUMN_TENANT_NAME, profile.getTenantName());
		database.insert(ProfileTable.TABLE_NAME, null, values);
		closeConnection();
	}
	
	public void updateProfile(Profile updatedProfile, int idProfile){
		database = openWritableConnection();
		ContentValues values = new ContentValues();
		values.put(ProfileTable.COLUMN_PROFILE_NAME, updatedProfile.getName());
		values.put(ProfileTable.COLUMN_ENDPOINT, updatedProfile.getEndpoint());
		values.put(ProfileTable.COLUMN_USERNAME, updatedProfile.getUsername());
		values.put(ProfileTable.COLUMN_PASSWORD, updatedProfile.getPassword());
		values.put(ProfileTable.COLUMN_TENANT_NAME, updatedProfile.getTenantName());
		database.update(ProfileTable.TABLE_NAME, values, 
				ProfileTable.COLUMN_ID + "=?", 
				new String[]{String.valueOf(idProfile)});
		closeConnection();
	}
	
	public void deleteProfile(Profile profile) {
		database = openWritableConnection();
		database.delete(ProfileTable.TABLE_NAME, ProfileTable.COLUMN_ID + "=?", 
				new String[]{ String.valueOf(profile.getId()) } );
		closeConnection();
	}
	
	public List<Profile> getAllProfile() {
		List<Profile> profiles = new LinkedList<Profile>();
		database = openReadableConnection();
		Cursor cursor = database.query(ProfileTable.TABLE_NAME, columns, null, null, null, null, null);
		cursor.moveToFirst();
		while (! cursor.isAfterLast()) {
			Profile profile = new Profile();
			profile.setId(cursor.getInt(0));
			profile.setEndpoint(cursor.getString(1));
			profile.setUsername(cursor.getString(2));
			profile.setPassword(cursor.getString(3));
			profile.setTenantName(cursor.getString(4));
			profile.setName(cursor.getString(5));
			profiles.add(profile);
			cursor.moveToNext();
		}
		cursor.close();
		closeConnection();
		return profiles;
	}
	
	public Profile getProfile(int profileId) {
		Profile profile = null;
		database = openReadableConnection();
		Cursor cursor = database.query(ProfileTable.TABLE_NAME, columns, ProfileTable.COLUMN_ID + "=?",
				new String[]{ String.valueOf(profileId) } , null, null, null);
		if (cursor.moveToFirst()){
			profile = new Profile();
			profile.setId(cursor.getInt(0));
			profile.setEndpoint(cursor.getString(1));
			profile.setUsername(cursor.getString(2));
			profile.setPassword(cursor.getString(3));
			profile.setTenantName(cursor.getString(4));
			profile.setName(cursor.getString(5));
		}
		cursor.close();
		closeConnection();
		return profile;
	}
	
	
	
}
