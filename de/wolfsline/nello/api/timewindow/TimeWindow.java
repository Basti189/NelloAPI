package de.wolfsline.nello.api.timewindow;

import org.json.simple.JSONObject;

public class TimeWindow {

	private String image;
	private String ical;
	private String name;
	private String state;
	private String id;
	private boolean enabled;
	
	public TimeWindow(JSONObject data) {
		if (data.get("image") != null) {
			image = data.get("image").toString();
		}
		ical = data.get("ical").toString();
		name = data.get("name").toString();
		state = data.get("state").toString();
		id = data.get("id").toString();
		enabled = (boolean) data.get("enabled");
	}

	public String getImage() {
		return image;
	}

	public String getical() {
		return ical;
	}

	public String getName() {
		return name;
	}

	public String getState() {
		return state;
	}

	public String getId() {
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String toString() {
		return "TimeWindow [image=" + image + ", iCal=" + ical.replace("\r\n", "\\r\\n") + ", name=" + name + ", state=" + state + ", id=" + id + ", enabled=" + enabled + "]";
	}
}
