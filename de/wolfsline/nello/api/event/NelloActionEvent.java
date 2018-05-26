package de.wolfsline.nello.api.events;

import org.json.simple.JSONObject;

public class NelloActionEvent {
	
	public final static int SWIPE = 0;
	public final static int GEO = 1;
	public final static int TW = 2;
	public final static int DENY = 3;

	private String name = "";
	private String location_id = "";
	private String user_id = "";
	
	private String action = "";
	
	public NelloActionEvent(JSONObject response) {
		JSONObject data = (JSONObject) response.get("data");
		name = jsonToString(data.get("name"));
		//name = data.get("name").toString();
		location_id = jsonToString(data.get("location_id"));
		//location_id = data.get("location_id").toString();
		user_id = jsonToString(data.get("user_id"));
		//user_id = data.get("user_id").toString();
		
		action = response.get("action").toString();
	}
	
	private String jsonToString(Object object) {
		if (object != null) {
			return object.toString();
		}
		return "";
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
	
	public int getActionID() {
		if (action.equals("swipe") ) {
			return SWIPE;
		} else if (action.equals("geo")) {
			return GEO;
		} else if (action.equals("tw")) {
			return TW;
		} else if (action.equals("deny")) {
			return DENY;
		}
		return -1;
	}

	@Override
	public String toString() {
		return "NelloActionEvent [name=" + name + ", location_id=" + location_id + ", user_id=" + user_id + ", action="
				+ action + "]";
	}
}
