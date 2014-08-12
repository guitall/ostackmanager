package com.intel.internship.openstack.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intel.internship.openstackmanagement.model.AvailabilityZone;
import com.intel.internship.openstackmanagement.model.Flavor;
import com.intel.internship.openstackmanagement.model.Image;
import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.Parser;
import com.intel.internship.openstackmanagement.util.UserAuthDataPreferences;

import android.content.Context;
import android.util.Log;

public class ComputeService {

	private Context mContext;
	private ApiRestManager apiRestManager;
	
	
	public ComputeService (Context mContext) {
		this.mContext = mContext;
		apiRestManager = new ApiRestManager(mContext);
	}
	
	private boolean getNewTokenId() {
		LoginService loginService = new LoginService(mContext);
		return loginService.getNewTokenId();
	}
	
	public ArrayList<Instance> getAllInstances() {
		ArrayList<Instance> instances = new ArrayList<>();
		String response;

		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));

		try {

			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/servers/detail", headers, null);

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllInstances(): " + statusCode);

			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllInstances(): " + response);
				
				JSONArray servers = new JSONObject(response).getJSONArray("servers");
				for (int i = 0; i < servers.length(); i++) {
					Instance ins = Parser.parseInstance(servers.getJSONObject(i));
					getImageAndFlavorData(ins, servers.getJSONObject(i));
					instances.add(ins);
				} 
				return instances;
			}
			if (statusCode == 401) { // Unauthorized
				//get token again
				if(getNewTokenId()) {
					return getAllInstances(); 
				}else
					return null;
			}
			if (statusCode == 404) { // Invalid endpoint.
				return null;
			}

		} catch (Exception ex) {
			Log.e("Debug", "Exception on getAllInstanes()", ex);
		}
		// If an error has triggered (Exception, timeout, ...) return null
		return null;
	}
	
	public Instance getInstanceDetail(String instanceId){
		String response;

		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));

		try {

			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/servers/" + instanceId , headers, null);

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getInstancesDetail(): " + statusCode);

			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getInstancesDetail(): " + response);
				JSONObject server = new JSONObject(response).getJSONObject("server");
				Instance ins = Parser.parseInstance(server);
				getImageAndFlavorData(ins, server);
				return ins;
			}
			if (statusCode == 401) { // Unauthorized
				//get token again
				if(getNewTokenId()) {
					return getInstanceDetail(instanceId); 
				}else
					return null;
			}
			if (statusCode == 404) { // Invalid endpoint.
				return null;
			}

		} catch (Exception ex) {
			Log.e("Debug", "Exception on getInstanesDetail()", ex);
		}
		return null;
	}

	public Flavor getFlavor(String flavorId) {
		Flavor flavor= new Flavor();
		String response;

		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try {

			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/flavors/" + flavorId , headers, null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllInstances(): " + statusCode);
			
			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllInstances(): " + response);
				
				return Parser.parseFlavor(new JSONObject(response).getJSONObject("flavor"));
			}
			if (statusCode == 401) { // Unauthorized
				if(getNewTokenId()) {
					return getFlavor(flavorId); 
				}else
					return null;						 
				//get token again
			}
			if (statusCode == 404) { // Invalid endpoint.a
				return null;
			}
			
		} catch (Exception ex) {
			Log.e("Debug", "Exception on getFlavor()", ex);
		}
		// If an error has triggered (Exception, timeout, ...) return null
		return null;
		
	}

	public List<Flavor> getAllFlavors() {
		ArrayList<Flavor> flavors = new ArrayList<>();
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try{
			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/flavors",
					headers,
					null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllFlavors(): " + statusCode);
			
			if(statusCode == 200) {
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllFlavors(): " + response);
				
				JSONArray flavorsArray = new JSONObject(response).getJSONArray("flavors");
				for (int i = 0; i < flavorsArray.length(); i++) {
					Flavor fl = Parser.parseFlavor(flavorsArray.getJSONObject(i));
					flavors.add(fl);
				}
				return flavors;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return getAllFlavors(); 
				}else
					return null;
			}
			if(statusCode == 404) {
				return null;
			}
		}catch(Exception ex){
			Log.e("Debug", "Exception on getAllFlavors()", ex);
		}
		return null;
	}

	public List<AvailabilityZone> getAllAvailabilityZone(){
		ArrayList<AvailabilityZone> zones = new ArrayList<>();
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try{
			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/os-availability-zone",
					headers,
					null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllAvailabilityZone(): " + statusCode);
			
			if(statusCode == 200) {
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllAvailabilityZone(): " + response);
				
				JSONArray availabilityZoneArray = new JSONObject(response).getJSONArray("availabilityZoneInfo");
				for (int i = 0; i < availabilityZoneArray.length(); i++) {
					AvailabilityZone avZ = Parser.parseAvailabilityZone(availabilityZoneArray.getJSONObject(i));
					zones.add(avZ);
				}
				return zones;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return getAllAvailabilityZone(); 
				}else
					return null;
			}
			if(statusCode == 404) {
				return null;
			}
		}catch(Exception ex){
			Log.e("Debug", "Exception on getAllAvailabilityZone()", ex);
		}
		return null;
		
	}
	
	public String createInstance(Instance instance, int instanceCount){
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		headers.add(new BasicNameValuePair("Content-Type", "application/json"));
		
		try{
			JSONObject payload = new JSONObject();
			
			JSONObject server = new JSONObject();
			server.put("name", instance.getName());
			server.put("imageRef", instance.getImage().getId());
			server.put("flavorRef",instance.getFlavor().getId());
			server.put("availability_zone", instance.getAvailabilityZone());
			server.put("max_count", instanceCount);
			server.put("min_count", instanceCount);
			
			payload.put("server", server);
			
			StringEntity entity = new StringEntity(payload.toString());
		
			HttpResponse httpResponse = apiRestManager.doPost(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/servers", entity,
					headers);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on createInstance(): " + statusCode);
			
			if(statusCode == 202) {
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on createInstance(): " + response);
				
				String instanceId = new JSONObject(response).getJSONObject("server").getString("id");
				instance.setId(instanceId);
				return ApiRestManager.OK;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return createInstance(instance, instanceCount); 
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
	
	public boolean doActionOnInstance(Instance instance, int action){
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		headers.add(new BasicNameValuePair("Content-Type", "application/json"));
		
		try{
			JSONObject payload = new JSONObject();
			
			if (action == Instance.START_ACTION) {
				payload.put("os-start", true);
			}else if (action == Instance.STOP_ACTION) {
				payload.put("os-stop", true);
			}
			
			StringEntity entity = new StringEntity(payload.toString());
		
			HttpResponse httpResponse = apiRestManager.doPost(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/servers/" + instance.getId() + "/action",
					entity,
					headers);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on doActionOnInstance(): " + statusCode);
			
			if(statusCode == 202) {
				return true;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return doActionOnInstance(instance, action); 
				}else
					return false;
			}
			
		}catch(Exception ex){
			Log.e("Debug", "Exception on doActionOnInstance", ex);
		}
		return false;
	}
	
	public String deleteInstance(Instance instance) {
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try{
			HttpResponse httpResponse = apiRestManager.doDelete(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/servers/" + instance.getId(),
					headers);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on deleteInstance(): " + statusCode);
			
			if(statusCode == 204) {
				return ApiRestManager.OK;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return deleteInstance(instance); 
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
			Log.e("Debug", "Exception on createInstance()", ex);
		}
		return null;
	}

	public JSONObject getComputeLimit() {
		String response;

		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try {

			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/limits", headers, null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getCoputeLimit(): " + statusCode);
			
			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getCoputeLimit(): " + response);
				
				return new JSONObject(response);
				
			}
			if (statusCode == 401) { // Unauthorized
				if(getNewTokenId()) {
					return getComputeLimit(); 
				}else
					return null;
				//get token again
			}
			if (statusCode == 404) { // Invalid endpoint.a
				return null;
			}
			
		} catch (Exception ex) {
			Log.e("Debug", "Exception on getCoputeLimit()", ex);
		}
		// If an error has triggered (Exception, timeout, ...) return null
		return null;
	}
	
	public JSONObject getTenantUsage(String startDate, String endDate) {
		String response="";
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));

		try {
			
			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.COMPUTE_SERVICE)
					.getPublicUrl() + "/os-simple-tenant-usage/" +
					UserAuthDataPreferences.getInstance(mContext).getTenantId() + 
					"?start=" + startDate + "&end=" + endDate
					, headers, null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getTenantUsage(): " + statusCode);
			
			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getTenantUsage(): " + response);
				
				return new JSONObject(response);
			}
			if (statusCode == 401) { // Unauthorized
				if(getNewTokenId()) {
					return getTenantUsage(startDate, endDate); 
				}else
					return null;
				//get token again
			}
			if (statusCode == 404) { // Invalid endpoint.a
				return null;
			}
			
		} catch (Exception ex) {
			Log.e("Debug", "Exception on getTenantUsage()", ex);
		}
		return null;
	}
	
	private void getImageAndFlavorData(Instance instance, JSONObject server) throws JSONException {
		//Set the image
		String imageId = server.getJSONObject("image").getString("id");
		Image image = new ImageService(mContext).getImage(imageId);
		if(image == null){
			image = new Image();
			image.setName("");
		}
		image.setId(imageId);
		instance.setImage(image);
				
		//Set the flavor
		String flavorId = server.getJSONObject("flavor").getString("id");
		Flavor flavor = getFlavor(flavorId);
		if ( flavor == null) {
			flavor = new Flavor();
			flavor.setId(flavorId);
			flavor.setName("");
		}
		instance.setFlavor(flavor);
	}
}
