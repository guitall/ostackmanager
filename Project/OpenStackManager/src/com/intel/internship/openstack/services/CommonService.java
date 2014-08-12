package com.intel.internship.openstack.services;

import org.json.JSONObject;

import com.intel.internship.openstackmanagement.model.Limits;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.Parser;

import android.content.Context;

public class CommonService {
	
	private Context mContext;
	private ApiRestManager apiRestManager;
	
	public CommonService (Context mContext){
		this.mContext = mContext;
		apiRestManager = new ApiRestManager(mContext);
	}
	
	public Limits getAllLimits() {
		JSONObject jsonComputeLimit = new ComputeService(mContext).getComputeLimit();
		JSONObject jsonVolumeLimit = new VolumeService(mContext).getVolumeLimits();
		
		if(jsonComputeLimit != null && jsonVolumeLimit !=null) {
			try {
				return Parser.parseLimits(jsonComputeLimit.getJSONObject("limits").getJSONObject("absolute")
						, jsonVolumeLimit.getJSONObject("limits").getJSONObject("absolute"));
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

}
