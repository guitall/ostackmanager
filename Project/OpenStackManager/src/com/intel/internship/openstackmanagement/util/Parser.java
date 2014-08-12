package com.intel.internship.openstackmanagement.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intel.internship.openstackmanagement.model.AvailabilityZone;
import com.intel.internship.openstackmanagement.model.Flavor;
import com.intel.internship.openstackmanagement.model.Image;
import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.model.Limits;
import com.intel.internship.openstackmanagement.model.Volume;
import com.intel.internship.openstackmanagement.model.VolumeType;

public class Parser {
	
	public static AvailabilityZone parseAvailabilityZone(JSONObject json) throws JSONException {
		AvailabilityZone availabilityZone = new AvailabilityZone();
		availabilityZone.setName(json.getString("zoneName"));
		return availabilityZone;
	}
	
	public static Image parseImage(JSONObject json) throws JSONException {
		Image image= new Image();
		image.setId(json.getString("id"));
		image.setName(json.getString("name"));
		image.setStatus(json.getString("status"));
		image.setSize(json.getInt("size"));
		image.setCreatedDate(json.getString("created_at"));
		image.setUpdatedDate(json.getString("updated_at"));
		return image;
	}
	
	public static Flavor parseFlavor(JSONObject json) throws JSONException{
		Flavor flavor = new Flavor();
		flavor.setId(json.getString("id"));
		flavor.setName(json.getString("name"));
		flavor.setRam(!json.isNull("ram")?"" + json.getInt("ram"):"");
		return flavor;
	}
	
	public static Volume parseVolume(JSONObject json) throws JSONException {
		Volume volume = new Volume();
		volume.setId(json.getString("id"));
		volume.setStatus(json.getString("status"));
		volume.setName(json.getString("name"));
		volume.setDescription(json.isNull("description") ? "" : json.getString("description"));
		volume.setAvailability_zone(json.getString("availability_zone"));
		volume.setBootable(json.getString("bootable"));
		volume.setSize(json.getInt("size"));
		VolumeType vlType = new VolumeType();
		vlType.setName(json.isNull("volume_type") ? "" : json.getString("volume_type"));
		volume.setType(vlType);
		return volume;
	}
	
	public static Limits parseLimits(JSONObject computeLimits, JSONObject volumeLimits) throws JSONException {
		Limits limits = new Limits();
		
		limits.setTotalVolumesUsed(volumeLimits.getInt("totalVolumesUsed"));
		limits.setMaxTotalVolumes(volumeLimits.getInt("maxTotalVolumes"));
		
		limits.setTotalVolumeStoragesUsed(volumeLimits.getInt("totalGigabytesUsed"));
		limits.setMaxTotalVolumeStorages(volumeLimits.getInt("maxTotalVolumeGigabytes"));
		
		limits.setTotalInstancesUsed(computeLimits.getInt("totalInstancesUsed"));
		limits.setMaxTotalInstances(computeLimits.getInt("maxTotalInstances"));
		
		limits.setTotalCoresUsed(computeLimits.getInt("totalCoresUsed"));
		limits.setMaxTotalCores(computeLimits.getInt("maxTotalCores"));
		
		limits.setTotalFlotingIpsUsed(computeLimits.getInt("totalFloatingIpsUsed"));
		limits.setMaxTotalFlotingIps(computeLimits.getInt("maxTotalFloatingIps"));
		
		limits.setTotalRAMused(computeLimits.getInt("totalRAMUsed"));
		limits.setMaxTotalRam(computeLimits.getInt("maxTotalRAMSize"));
		
		limits.setTotalSecurityGroupUsed(computeLimits.getInt("totalSecurityGroupsUsed"));
		limits.setMaxTotalSecurityGroup(computeLimits.getInt("maxSecurityGroups"));
		return limits;
	}
	
	public static Instance parseInstance(JSONObject server) throws JSONException {
		
		Instance instance = new Instance();
		instance.setId(server.getString("id"));
		instance.setName(server.getString("name"));
		instance.setStatus(server.getString("status"));
		instance.setUpdated(server.getString("updated"));
		instance.setCreated(server.getString("created"));
		instance.setAvailabilityZone(server.getString("OS-EXT-AZ:availability_zone"));
		
		//Set the security groups
		String securityGroup = "";
		JSONArray securityGroups = server.getJSONArray("security_groups");
		for (int j = 0; j < securityGroups.length(); j++) {
			securityGroup += securityGroups.getJSONObject(j).getString("name") + ", ";
			
		}
		instance.setSecurityGroupName(securityGroup);
		
		//Set the key name
		instance.setKeyName(server.isNull("key_name") ? "" : server.getString("key_name"));
		
		
		
		//Set private addresses
		String privateAddresses = "";
		JSONObject addresses = server.getJSONObject("addresses");
		JSONArray privateAdrressesArray = !addresses.isNull("private")? addresses.getJSONArray("private"):null;
		if(privateAdrressesArray != null){
			for (int i = 0; i < privateAdrressesArray.length(); i++) {
				privateAddresses += privateAdrressesArray.getJSONObject(i).getString("addr") + "; ";
			}
		}
		instance.setPrivateIp(privateAddresses);
		
		
		return instance;
	}

	public static VolumeType parseVolumeTypes(JSONObject json) throws JSONException {
		VolumeType volumeType = new VolumeType();
		volumeType.setId(json.getString("id"));
		volumeType.setName(json.getString("name"));
		return volumeType;
	}

	
}
