package com.intel.internship.openstackmanagement.model;

public class Flavor {
	
	private String id;
	private String name;
	private String ram;
	
	public Flavor(){
		
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
	public void setName(String name) {
		this.name = name;
	}
	public String getRam() {
		return ram;
	}
	public void setRam(String ram) {
		this.ram = ram;
	}

	@Override
	public String toString() {
		
		return name;
	}
	
	
	
	

}
