package com.intel.internship.openstack.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.model.Volume;
import com.intel.internship.openstackmanagement.model.VolumeType;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.Parser;
import com.intel.internship.openstackmanagement.util.UserAuthDataPreferences;

import android.content.Context;
import android.util.Log;

public class VolumeService {
	
	private Context mContext;
	private ApiRestManager apiRestManager;
	
	public VolumeService (Context mContext) {
		this.mContext = mContext;
		apiRestManager = new ApiRestManager(mContext);
	}
	
	private boolean getNewTokenId() {
		LoginService loginService = new LoginService(mContext);
		return loginService.getNewTokenId();
	}
	
	public ArrayList<Volume> getAllVolumes() {
		ArrayList<Volume> volumes = new ArrayList<>();
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try{
			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.VOLUMEN_V2_SERVICE)
					.getPublicUrl() + "/volumes/detail",
					headers,
					null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllVolume(): " + statusCode);
			
			if(statusCode == 200) {
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllVolume(): " + response);
				
				JSONArray flavorsArray = new JSONObject(response).getJSONArray("volumes");
				for (int i = 0; i < flavorsArray.length(); i++) {
					Volume vl = Parser.parseVolume(flavorsArray.getJSONObject(i));
					volumes.add(vl);
				}
				return volumes;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return getAllVolumes(); 
				}else
					return null;
			}
			if(statusCode == 404) {
				return null;
			}
		}catch(Exception ex){
			Log.e("Debug", "Exception on getAllVolumes()", ex);
		}
		return null;
	}
	
	public ArrayList<VolumeType> getAllVolumeTypes() {
		
		ArrayList<VolumeType> volumes = new ArrayList<>();
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try{
			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.VOLUMEN_V2_SERVICE)
					.getPublicUrl() + "/types",
					headers,
					null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllVolumeTypes(): " + statusCode);
			
			if(statusCode == 200) {
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllVolumeTypes(): " + response);
				
				JSONArray volumeTypesArray = new JSONObject(response).getJSONArray("volume_types");
				for (int i = 0; i < volumeTypesArray.length(); i++) {
					VolumeType vl = Parser.parseVolumeTypes(volumeTypesArray.getJSONObject(i));
					volumes.add(vl);
				}
				return volumes;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return getAllVolumeTypes(); 
				}else
					return null;
			}
			if(statusCode == 404) {
				return null;
			}
		}catch(Exception ex){
			Log.e("Debug", "Exception on getAllVolumeTypes()", ex);
		}
		return null;
	}

	public JSONObject getVolumeLimits() {
		String response;

		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try {

			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.VOLUMEN_SERVICE)
					.getPublicUrl() + "/limits", headers, null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getVolumeLimits(): " + statusCode);
			
			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getVolumeLimits(): " + response);
				
				return new JSONObject(response);
			}
			if (statusCode == 401) { // Unauthorized
				if(getNewTokenId()) {
					return getVolumeLimits(); 
				}else
					return null;
				//get token again
			}
			if (statusCode == 404) { // Invalid endpoint.a
				return null;
			}
			
		} catch (Exception ex) {
			Log.e("Debug", "Exception on getVolumeLimits()", ex);
		}
		// If an error has triggered (Exception, timeout, ...) return null
		return null;
	}

	public String createVolume(Volume newVolume) {
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		headers.add(new BasicNameValuePair("Content-Type", "application/json"));
		
		try{
			JSONObject payload = new JSONObject();
			
			JSONObject volume = new JSONObject();
			volume.put("name", newVolume.getName());
			volume.put("description", newVolume.getDescription());
			volume.put("size", newVolume.getSize());
			
			if(newVolume.getType() != null)
				volume.put("volume_type", newVolume.getType().getId());
			
			volume.put("availability_zone", newVolume.getAvailability_zone());
			
			
			payload.put("volume", volume);
			
			StringEntity entity = new StringEntity(payload.toString());
		
			HttpResponse httpResponse = apiRestManager.doPost(ApiRestManager
					.APIServices.get(ApiRestManager.VOLUMEN_V2_SERVICE)
					.getPublicUrl() + "/volumes", entity,
					headers);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on createVolume(): " + statusCode);
			
			if(statusCode == 202) {
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on createVolume(): " + response);
				
				JSONObject json = new JSONObject(response).getJSONObject("volume");
				String volumeId = json.getString("id");
				String status = json.getString("status");
				newVolume.setId(volumeId);
				newVolume.setStatus(status);
				newVolume.setBootable(json.getBoolean("bootable") ? "true" : "false");
				return ApiRestManager.OK;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return createVolume(newVolume); 
				}else
					return null;
			}
			if(statusCode == 413){
				response = EntityUtils.toString(httpResponse.getEntity());
				return new JSONObject(response).getJSONObject("overLimit").getString("message");
			}
			if(statusCode == 404) {
				return null;
			}
		}catch(Exception ex){
			Log.e("Debug", "Exception on createInstance()", ex);
		}
		return null;
	}

	public Volume getVolumeDetail(String id) {
		String response;

		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));

		try {

			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.VOLUMEN_V2_SERVICE)
					.getPublicUrl() + "/volumes/" + id , headers, null);

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getVolumeDetail(): " + statusCode);

			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getVolumeDetail(): " + response);
				JSONObject json = new JSONObject(response).getJSONObject("volume");
				Volume vol = Parser.parseVolume(json);
				
				return vol;
			}
			if (statusCode == 401) { // Unauthorized
				//get token again
				if(getNewTokenId()) {
					return getVolumeDetail(id); 
				}else
					return null;
			}
			if (statusCode == 404) { // Invalid endpoint.
				return null;
			}

		} catch (Exception ex) {
			Log.e("Debug", "Exception on getVolumeDetail()", ex);
		}
		return null;
	}

	public String deleteVolume(Volume volume) {
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try{
			HttpResponse httpResponse = apiRestManager.doDelete(ApiRestManager
					.APIServices.get(ApiRestManager.VOLUMEN_V2_SERVICE)
					.getPublicUrl() + "/volumes/" + volume.getId(),
					headers);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on deleteVolume(): " + statusCode);
			
			if(statusCode == 202) {
				return ApiRestManager.OK;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return deleteVolume(volume);
				}else
					return null;
			}
			if(statusCode == 413){
				response = EntityUtils.toString(httpResponse.getEntity());
				return new JSONObject(response).getJSONObject("overLimit").getString("message");
			}
			if(statusCode == 503) {
				return "Service Unavailable. Try again later";
			}
		}catch(Exception ex){
			Log.e("Debug", "Exception on deleteVolume()", ex);
		}
		return null;
		
	}

}
