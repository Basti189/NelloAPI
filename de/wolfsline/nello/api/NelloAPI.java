package de.wolfsline.nello.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.wolfsline.nello.api.http.HttpCallbackServer;
import de.wolfsline.nello.api.location.Location;

public class NelloAPI extends NelloBase {
	
	private HttpCallbackServer mHttpCallbackServer = new HttpCallbackServer();
	
	public NelloAPI () {
		
	}
	
	public String getVersion() {
		return "0.9.6";
	}
	
	public void startServer(int port) {
		mHttpCallbackServer.start(port);
	}
	
	public void triggerServer() {
		mHttpCallbackServer.trigger();
	}
	
	public void stopServer() {
		mHttpCallbackServer.stop();
	}
	
	public String requestTokenClientCredentials(String client_id, String client_secret) {
		int responseCode = -1;
		StringBuffer response = new StringBuffer();
		try {
			String url = "https://auth.nello.io/oauth/token/";
			log("Sending 'POST' request to URL : " + url, INFO);
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			String urlParameters = "grant_type=client_credentials&client_id=" + client_id + "&client_secret=" + client_secret;
			log("Post parameters : " + urlParameters, INFO);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			responseCode = con.getResponseCode();

			log("Response Code : " + responseCode, INFO);
			log("Response: " + response, INFO);
			
		} catch (Exception e) {
			
		}

		if (responseCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
				return jsonObject.get("access_token").toString();
			} catch (Exception e) {
				
			}
			
		}
		log("invalid_request or invalid_client", ERROR);
		return null;
	}
	
	public List<Location> getLocations(String token) {
		int responseCode = -1;
		StringBuffer response = new StringBuffer();
		try {
			String url = "https://public-api.nello.io/v1/locations/";
			log("Sending 'GET' request to URL : " + url, INFO);
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty ("Authorization", "Bearer " + token);
			con.setRequestMethod("GET");
			
			responseCode = con.getResponseCode();
			
			log("Response Code : " + responseCode, INFO);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			
		}
		
		
		if (responseCode == 200) {
			JSONArray dataArray = null;
			try {
				JSONParser parser = new JSONParser();
				JSONObject responseObject = (JSONObject) parser.parse(response.toString());
				dataArray = (JSONArray) responseObject.get("data");
			} catch (ParseException e) {
				
			}
			if (dataArray != null) {
				List<Location> listLocations = new ArrayList<Location>();
				for (int i = 0 ; i < dataArray.size() ; i++) {
					Location location = new Location((JSONObject) dataArray.get(i));
					listLocations.add(location);
					log("[" + (i+1) + "] " + location.toString(), INFO);
				}
				return listLocations;
			} else {
				log("No addresses available", ERROR);
			}
		} else {
			log("The server could not verify that you are authorized to access the URL requested", ERROR);
		}
		return null;
	}
	
	//Create new TimeWindow
	//TODO
	
	//Delete a Time Window
	//TODO
	
	public boolean openDoor(String token, Location location) {
		int responseCode = -1;
		try {
			String url = "https://public-api.nello.io/v1/locations/" + location.getLocation_id() + "/open/";
			log("Sending 'PUT' request to URL : " + url, INFO);
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty ("Authorization", "Bearer " + token);
			con.setRequestMethod("PUT");
			
			responseCode = con.getResponseCode();
			
			log("Response Code : " + responseCode, INFO);
		} catch (Exception e) {
			
		}
		
		if (responseCode == 200) {
			log("Door has been opened successfully!", INFO);
			return true;
		}
		log("The server could not verify that you are authorized to access the URL requested", ERROR);
		return false;
	}

	public boolean setWebhook(String token, Location location, String webhook_url) {
		return setWebhook(token, location, webhook_url, true, true, true, true);
	}
	
	public boolean setWebhook(String token, Location location, String webhook_url, boolean swipe, boolean geo, boolean tw, boolean deny) {
		if (!(swipe || geo || tw || deny)) {
			log("All values = \"false\" is not allowed", ERROR);
			return false;
		}
		int responseCode = -1;
		try {
			
			String url = "https://public-api.nello.io/v1/locations/" + location.getLocation_id() + "/webhook/";
			log("Sending 'PUT' request to URL : " + url, INFO);
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty ("Authorization", "Bearer " + token);
			con.setRequestMethod("PUT");
			
			String request = "{\"url\":\"" + webhook_url + "\",\"actions\":[";
			if (swipe) {
				request += "\"swipe\",";
			}
			if (geo) {
				request += "\"geo\",";
			} 
			if (tw) {
				request += "\"tw\",";
			}
			if (deny) {
				request += "\"deny\",";
			}
			request = request.substring(0, request.length()-1);
			request += "]}";
			
			log("Request: " + request, INFO);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(request);
			wr.flush();
			wr.close();
			
			responseCode = con.getResponseCode();
			
			log("Response Code : " + responseCode, INFO);
		} catch (Exception e) {
			
		}
		
		if (responseCode == 200) {
			log("Webhook was modified successfully", INFO);
			return true;
		}
		log("The server could not verify that you are authorized to access the URL requested", ERROR);
		return false;
	}
	
	public boolean deleteWebhook(String token, Location location) {
		int responseCode = -1;
		try {
			String url = "https://public-api.nello.io/v1/locations/" + location.getLocation_id() + "/webhook/";
			log("Sending 'DELETE' request to URL : " + url, INFO);
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestProperty ("Authorization", "Bearer " + token);
			con.setRequestMethod("DELETE");
			con.connect();
			
			responseCode = con.getResponseCode();
			
			log("Response Code : " + responseCode, INFO);
		} catch (Exception e) {
			
		}
		
		if (responseCode == 200) {
			log("Webhook was deleted successfully", INFO);
			return true;
		}
		log("The server could not verify that you are authorized to access the URL requested", ERROR);
		return false;
	}
}
