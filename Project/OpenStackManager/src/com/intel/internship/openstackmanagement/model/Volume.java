package com.intel.internship.openstackmanagement.model;

public class Volume {
	
	public static final int CREATE_ACTION = 1;
	
	public static final String CREATING_STATUS = "CREATING"; 
	
	private String id;
	private String name;
	private String description;
	private int size;
	private String status;
	private String availability_zone;
	private String bootable;
	private VolumeType type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAvailability_zone() {
		return availability_zone;
	}
	public void setAvailability_zone(String availability_zone) {
		this.availability_zone = availability_zone;
	}
	public String getBootable() {
		return bootable;
	}
	public void setBootable(String bootable) {
		this.bootable = bootable;
	}
//	public String getType() {
//		return type;
//	}
//	public void setType(String type) {
//		this.type = type;
//	}
	public VolumeType getType() {
		return type;
	}
	public void setType(VolumeType type) {
		this.type = type;
	}
}
