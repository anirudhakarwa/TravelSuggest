package edu.buffalo.cse.ir.wikiindexer.wikipedia;

public class LocationDetail{
	private double latitude;
	private double longitude;
	private String state;
	private String country;
	private String allSections;
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getAllSections() {
		return allSections;
	}
	public void setAllSections(String allSections) {
		this.allSections = allSections;
	}
	public LocationDetail(double latitude, double longitude, String state, String country) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.state = state;
		this.country = country;
		allSections = "default";
	}

}
