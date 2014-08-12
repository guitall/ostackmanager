package com.intel.internship.openstackmanagement.model;

public class Instance {
	
	//Instance Actions 
	public static final int STOP_ACTION = 1;
	public static final int START_ACTION = 2;
	public static final int CREATE_ACTION = 3;
	
	//Instance Status
	public static final String BUILD_STATUS = "BUILD";
	public static final String ACTIVE_STATUS = "ACTIVE";
	public static final String SHUTOFF_STATUS = "SHUTOFF";
	
	private String id;
	private String name;
	private Image image;
	private Flavor flavor;
	private String securityGroupName;
	private String availabilityZone;
	private String keyName;
	private String status;
	private String privateIp;
	private String publicIp;
	private String updated;
	private String created;
	
	private String uptime;
	
	
	public Instance(){
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public String getSecurityGroupName() {
		return securityGroupName;
	}
	public void setSecurityGroupName(String securityGroupName) {
		this.securityGroupName = securityGroupName;
	}
	public String getAvailabilityZone() {
		return availabilityZone;
	}
	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPrivateIp() {
		return privateIp;
	}
	public void setPrivateIp(String privateIp) {
		this.privateIp = privateIp;
	}
	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}

	public Flavor getFlavor() {
		return flavor;
	}

	public void setFlavor(Flavor flavor) {
		this.flavor = flavor;
	}
	
	
	
	
	
}
