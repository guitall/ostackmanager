package com.intel.internship.openstack.services;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.intel.internship.openstackmanagement.model.Image;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.Parser;
import com.intel.internship.openstackmanagement.util.UserAuthDataPreferences;

import android.content.Context;
import android.util.Log;

public class ImageService {

	private Context mContext;
	private ApiRestManager apiRestManager;
	
	public ImageService(Context mContext) {
		this.mContext = mContext;
		apiRestManager = new ApiRestManager(mContext);
	}
	
	private boolean getNewTokenId() {
		LoginService loginService = new LoginService(mContext);
		return loginService.getNewTokenId();
	}
	
	public Image getImage(String imageId) {
		Image image = new Image();
		String response;

		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try {

			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.IMAGE_SERVICE)
					.getPublicUrl() + "/v2/images/" + imageId, headers, null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllInstances(): " + statusCode);
			
			if (statusCode == 200) { // Ok
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllInstances(): " + response);
				
				return Parser.parseImage(new JSONObject(response));
				
			}
			if (statusCode == 401) { // Unauthorized
				if(getNewTokenId()) {
					return getImage(imageId); 
				}else
					return null;
				//get token again
			}
			if (statusCode == 404) { // Invalid endpoint.a
				return null;
			}
			
		} catch (Exception ex) {
			Log.e("Debug", "Exception on getAllInstanes()", ex);
		}
		// If an error has triggered (Exception, timeout, ...) return null
		return null;
		
	}
	
	public ArrayList<Image> getAllImages() {
		ArrayList<Image> images = new ArrayList<>();
		String response;
		
		ArrayList<BasicNameValuePair> headers = new ArrayList<>();
		headers.add(new BasicNameValuePair("X-Auth-Token",
				UserAuthDataPreferences.getInstance(mContext).getTokenId()));
		
		try{
			HttpResponse httpResponse = apiRestManager.doGet(ApiRestManager
					.APIServices.get(ApiRestManager.IMAGE_SERVICE)
					.getPublicUrl() + "/v2/images",
					headers,
					null);
			
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i("Debug", ApiRestManager.class.getName()
					+ ": on getAllImages(): " + statusCode);
			
			if(statusCode == 200) {
				response = EntityUtils.toString(httpResponse.getEntity());
				Log.i("Debug", ApiRestManager.class.getName()
						+ " on getAllImages(): " + response);
				
				JSONArray imagesArray = new JSONObject(response).getJSONArray("images");
				for (int i = 0; i < imagesArray.length(); i++) {
					Image img = Parser.parseImage(imagesArray.getJSONObject(i));
					images.add(img);
				}
				return images;
			}
			if(statusCode == 401) {
				if(getNewTokenId()) {
					return getAllImages(); 
				}else
					return null;
			}
			if(statusCode == 404) {
				return null;
			}
		}catch(Exception ex){
			Log.e("Debug", "Exception on getAllImages()", ex);
		}
		return null;
	}
}
