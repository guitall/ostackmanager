package com.intel.internship.openstack.services;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intel.internship.openstackmanagement.R;
import com.intel.internship.openstackmanagement.model.APIService;
import com.intel.internship.openstackmanagement.model.Profile;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.UserAuthDataPreferences;

import android.content.Context;
import android.util.Log;

public class LoginService {
	
	private Context mContext;
	private ApiRestManager apiRestManager;
	
	public LoginService(Context mContext) {
		this.mContext = mContext;
		apiRestManager = new ApiRestManager(mContext);
	}
	
	/**
	 * This method allows get a token for a connection profile.
	 * 
	 * @param profile
	 * @return String that describes the request result.
	 */
	public String login(Profile profile) {
		String response = "";
		ApiRestManager.APIServices.clear();
		try {
			JSONObject payload = new JSONObject();

			JSONObject auth = new JSONObject();

			JSONObject passwordCredentials = new JSONObject();
			passwordCredentials.put("username", profile.getUsername());
			passwordCredentials.put("password", profile.getPassword());

			auth.put("passwordCredentials", passwordCredentials);
			auth.put("tenantName", profile.getTenantName());

			payload.put("auth", auth);

			StringEntity entity = new StringEntity(payload.toString());

			ArrayList<BasicNameValuePair> headers = new ArrayList<>();
			headers.add(new BasicNameValuePair("Content-Type",
					"application/json"));

			HttpResponse httpResponse = apiRestManager.doPost(profile.getEndpoint()
					+ "/v2.0/tokens", entity, headers);

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName() + ": onLogin(): "
					+ statusCode);

			response = EntityUtils.toString(httpResponse.getEntity());
			Log.i("Debug", ApiRestManager.class.getName() + " on Login(): "
					+ response);

			if (statusCode == 200) { // Ok
				JSONObject result = new JSONObject(response);
				setSessionData(result, profile);
				return ApiRestManager.OK;
			}
			if (statusCode == 401) { // Invaild User / password for its
										// tenantName
				return mContext.getString(R.string.invalidUser);
			}
			if (statusCode == 404) { // Invalid endpoint.
				return mContext.getString(R.string.invalidEndpoint);
			}

		} catch (Exception ex) {
			Log.e("Debug", "Exception onLogin()", ex);
		}
		// If an error has triggered (Exception, timeout, ...) return null
		return null;
	}
	
	private void setSessionData(JSONObject result, Profile profile)
			throws JSONException {

		JSONObject access = result.getJSONObject("access");
		JSONObject token = access.getJSONObject("token");
		JSONArray serviceCatalog = access.getJSONArray("serviceCatalog");

		// get token
		final String idToken = token.getString("id");
		//get tenantId
		final String tenantId =  token.getJSONObject("tenant").getString("id");

		// Set session parameter
		UserAuthDataPreferences.getInstance(mContext).setTokenId(idToken);
		UserAuthDataPreferences.getInstance(mContext)
				.setCurrentConnectionProfileId(profile.getId());
		UserAuthDataPreferences.getInstance(mContext).setTenantId(tenantId);

		// Get API endpoint
		for (int i = 0; i < serviceCatalog.length(); i++) {

			JSONObject item = serviceCatalog.getJSONObject(i);
			String tag = "";
			String type = item.getString("type");

			if (type.compareToIgnoreCase(ApiRestManager.COMPUTE_SERVICE) == 0) {
				tag = ApiRestManager.COMPUTE_SERVICE;
			} else if (type.compareToIgnoreCase(ApiRestManager.COMPUTE_V3_SERVICE) == 0) {
				tag = ApiRestManager.COMPUTE_V3_SERVICE;
			} else if (type.compareToIgnoreCase(ApiRestManager.VOLUMEN_SERVICE) == 0) {
				tag = ApiRestManager.VOLUMEN_SERVICE;
			} else if (type.compareToIgnoreCase(ApiRestManager.VOLUMEN_V2_SERVICE) == 0) {
				tag = ApiRestManager.VOLUMEN_V2_SERVICE;
			} else if (type.compareToIgnoreCase(ApiRestManager.IMAGE_SERVICE) == 0) {
				tag = ApiRestManager.IMAGE_SERVICE;
			} else if (type.compareToIgnoreCase(ApiRestManager.IDENTITY_SERVICE) == 0) {
				tag = ApiRestManager.IDENTITY_SERVICE;
			} else {
				continue;
			}

			APIService apiService = new APIService();
			JSONObject endpoint = item.getJSONArray("endpoints").getJSONObject(0);

			apiService.setAdminUrl(endpoint.getString("adminURL"));
			apiService.setPublicUrl(endpoint.getString("publicURL"));
			apiService.setInternalUrl(endpoint.getString("internalURL"));
			apiService.setType(type);
			apiService.setName(item.getString("name"));
			ApiRestManager.APIServices.put(tag, apiService);
		}

	}
	
	public boolean getNewTokenId(){
		String response = "";
		Profile looggedProfile = UserAuthDataPreferences.getInstance(mContext).getCurrentConnectionProfile();
		
		try {
			JSONObject payload = new JSONObject();
			JSONObject auth = new JSONObject();

			JSONObject passwordCredentials = new JSONObject();
			passwordCredentials.put("username", looggedProfile.getUsername());
			passwordCredentials.put("password", looggedProfile.getPassword());

			auth.put("passwordCredentials", passwordCredentials);
			auth.put("tenantName", looggedProfile.getTenantName());

			payload.put("auth", auth);
			StringEntity entity = new StringEntity(payload.toString());

			ArrayList<BasicNameValuePair> headers = new ArrayList<>();
			headers.add(new BasicNameValuePair("Content-Type",
					"application/json"));

			HttpResponse httpResponse = apiRestManager.doPost(looggedProfile.getEndpoint()
					+ "/v2.0/tokens", entity, headers);

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName() + ": on getNewTokenId(): "
					+ statusCode);
			
			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName() + " on getNewTokenId(): "
						+ response);
				
				final String newTokenId = new JSONObject(response).getJSONObject("access")
						.getJSONObject("token").getString("id");
				UserAuthDataPreferences.getInstance(mContext).setTokenId(newTokenId);
				return true;
			}
		} catch (Exception e) {
			Log.e("Debug", "Exception onLogin()", e);
		}
		return false;	
	}
}
