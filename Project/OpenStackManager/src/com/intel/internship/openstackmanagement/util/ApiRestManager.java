package com.intel.internship.openstackmanagement.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intel.internship.openstackmanagement.R;
import com.intel.internship.openstackmanagement.model.AvailabilityZone;
import com.intel.internship.openstackmanagement.model.Flavor;
import com.intel.internship.openstackmanagement.model.Image;
import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.model.Limits;
import com.intel.internship.openstackmanagement.model.Profile;
import com.intel.internship.openstackmanagement.model.APIService;
import com.intel.internship.openstackmanagement.model.Volume;
import com.intel.internship.openstackmanagement.persistence.ProfileDataSource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.nfc.tech.IsoDep;
import android.preference.PreferenceActivity.Header;
import android.util.Log;

@SuppressLint("NewApi")
public class ApiRestManager {

	public static final String OK = "ok";
	public static final String UNOK = "unok";

	public static final String COMPUTE_SERVICE = "compute";
	public static final String COMPUTE_V3_SERVICE = "computev3";
	public static final String VOLUMEN_SERVICE = "volume";
	public static final String VOLUMEN_V2_SERVICE = "volumev2";
	public static final String IMAGE_SERVICE = "image";
	public static final String IDENTITY_SERVICE = "identity";

	public static HashMap<String, APIService> APIServices = new HashMap<>(); // This
																				// contains
																				// the
																				// API
																				// endpoints
																				// of
																				// the
																				// selected
																				// connection
																				// profile.
	private Context mContext;

	public ApiRestManager(Context context) {
		this.mContext = context;
	}

	/**
	 * Method that executes an Http get request with QueryParams
	 * 
	 * @param url
	 * @param tokenId
	 * @param userId
	 * @param entityParams
	 * @return {@link HttpResponse}
	 */
	public HttpResponse doGet(String url, List<BasicNameValuePair> headers,
			List<NameValuePair> params) {

		try {
			HttpParams httpParams = new BasicHttpParams();

			ConnManagerParams.setTimeout(httpParams, 10000);
			HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
			HttpConnectionParams.setSoTimeout(httpParams, 15000);

			if (params != null) {
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
			}

			final HttpClient client = new DefaultHttpClient(httpParams);
			final HttpGet request = new HttpGet(url);

			// Set the corresponding headers
			if (headers != null) {
				for (int i = 0; i < headers.size(); i++) {
					BasicNameValuePair header = headers.get(i);
					request.addHeader(header.getName(), header.getValue());
				}
			}

			// Execute the HTTP Get request to the URL
			final HttpResponse response = client.execute(request);

			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Method that executes an HTTP post request.
	 * 
	 * @param url
	 *            String url
	 * @param tokenId
	 *            , maybe null
	 * @param userId
	 *            , maybe null
	 * @return HttpResponse
	 */
	public HttpResponse doPost(String url, StringEntity entity,
			ArrayList<BasicNameValuePair> headers) {

		try {

			HttpParams httpParams = new BasicHttpParams();

			ConnManagerParams.setTimeout(httpParams, 10000);
			HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
			HttpConnectionParams.setSoTimeout(httpParams, 15000);

			final HttpClient client = new DefaultHttpClient(httpParams);
			final HttpPost request = new HttpPost(url);

			// Set the corresponding headers
			if (headers != null) {
				for (int i = 0; i < headers.size(); i++) {
					BasicNameValuePair header = headers.get(i);
					request.addHeader(header.getName(), header.getValue());
				}
			}

			if (entity != null) {
				// Set the entity with the parameters
				request.setEntity(entity);
			}

			// Execute the HTTP Post request to the URL
			final HttpResponse response = client.execute(request);
			// client.getConnectionManager().shutdown();
			return response;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public HttpResponse doDelete(String url, ArrayList<BasicNameValuePair> headers) {

		try {

			HttpParams httpParams = new BasicHttpParams();

			ConnManagerParams.setTimeout(httpParams, 10000);
			HttpConnectionParams.setConnectionTimeout(httpParams, 25000);
			HttpConnectionParams.setSoTimeout(httpParams, 25000);

			final HttpClient client = new DefaultHttpClient(httpParams);
			final HttpDelete request = new HttpDelete(url);
			
			// Set the corresponding headers
			if (headers != null) {
				for (int i = 0; i < headers.size(); i++) {
					BasicNameValuePair header = headers.get(i);
					request.addHeader(header.getName(), header.getValue());
				}
			}
			
			// Execute the HTTP Post request to the URL
			final HttpResponse response = client.execute(request);
			// client.getConnectionManager().shutdown();
			return response;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
