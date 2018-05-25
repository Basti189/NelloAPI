package de.wolfsline.nello.api;

import org.json.simple.JSONObject;

public class Location {

	private String zip = "";
	private String street = "";
	private String city = "";
	private String number = "";
	private String country = "";
	private String state = "";
	
	private String location_id = "";
	
	public Location(JSONObject data) {
		JSONObject adress = (JSONObject) data.get("address");
		zip = adress.get("zip").toString();
		street = adress.get("street").toString();
		city = adress.get("city").toString();
		number = adress.get("number").toString();
		country = adress.get("country").toString();
		state = adress.get("state").toString();
		
		location_id = data.get("location_id").toString();
	}

	public String getZip() {
		return zip;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getNumber() {
		return number;
	}

	public String getCountry() {
		return country;
	}

	public String getState() {
		return state;
	}

	public String getLocation_id() {
		return location_id;
	}

	@Override
	public String toString() {
		return "Location [zip=" + zip + ", street=" + street + ", city=" + city + ", number=" + number + ", country="
				+ country + ", state=" + state + ", location_id=" + location_id + "]";
	}
}
