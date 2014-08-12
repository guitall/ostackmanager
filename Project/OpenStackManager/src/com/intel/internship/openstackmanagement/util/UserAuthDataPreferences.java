package com.intel.internship.openstackmanagement.util;

import java.util.Date;

import com.intel.internship.openstackmanagement.model.Profile;
import com.intel.internship.openstackmanagement.persistence.ProfileDataSource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class UserAuthDataPreferences {
	
	private static final String TOKEN_ID = "token_id";
	private static final String PROFILE_ID = "profile_id";
	private static final String TENANT_ID = "tenant_id";	
	
	
	private static UserAuthDataPreferences _instance;
	private SharedPreferences sharedPreferences;
	private Context mContext;
	
	private UserAuthDataPreferences(Context context){
		this.mContext = context;
		this.sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
		
	}
	
	public static UserAuthDataPreferences getInstance(Context context){
		if (_instance == null) {
			_instance = new UserAuthDataPreferences(context);
		}

		return _instance;
	}
	
	public String getTokenId(){
		return sharedPreferences.getString(TOKEN_ID, "");
	}
	
	public String getTenantId() {
		return sharedPreferences.getString(TENANT_ID, "");
	}
	
	public void setTokenId(String tokenId){
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString(TOKEN_ID, tokenId);
		edit.commit();
	}
	
	public void setTenantId(String tenantId) {
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString(TENANT_ID, tenantId);
		edit.commit();
	}
	
	public Profile getCurrentConnectionProfile() {
		final int profileId = sharedPreferences.getInt(PROFILE_ID, 0);
		ProfileDataSource data = new ProfileDataSource(mContext);
		Profile currentProfile = data.getProfile(profileId);
		return currentProfile;
	}
	
	public void setCurrentConnectionProfileId(int profileId) {
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putInt(PROFILE_ID, profileId);
		edit.commit();
	}
	

}
