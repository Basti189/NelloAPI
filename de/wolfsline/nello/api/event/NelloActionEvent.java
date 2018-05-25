package de.wolfsline.nello.api.events;

import org.json.simple.JSONObject;

public class NelloActionEvent {

	private String name = "";
	private String location_id = "";
	private String user_id = "";
	
	private String action = "";
	
	public NelloActionEvent(JSONObject response) {
		JSONObject data = (JSONObject) response.get("data");
		name = data.get("name").toString();
		location_id = data.get("location_id").toString();
		user_id = data.get("user_id").toString();
		
		action = response.get("action").toString();
	}

	public String getName() {
		return name;
	}

	public String getLocation_id() {
		return location_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getAction() {
		return action;
	}

	@Override
	public String toString() {
		return "NelloActionEvent [name=" + name + ", location_id=" + location_id + ", user_id=" + user_id + ", action="
				+ action + "]";
	}
}
