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

import de.wolfsline.nello.api.http.HttpCallbackServer;

public class NelloAPI {
	
	public final static int INFO = 0;
	public final static int ERROR = 1;

	private boolean mDebugOutput = false;
	
	private HttpCallbackServer mHttpCallbackServer = new HttpCallbackServer();
	
	public NelloAPI () {
		
	}
	
	public void setDebugOutput(boolean debug) {
		mDebugOutput = debug;
		mHttpCallbackServer.setDebugOutput(debug);
	}
	
	public void register(Object listener) {
		mHttpCallbackServer.register(listener);
	}
	
	public void unregister(Object listener) {
		mHttpCallbackServer.unregister(listener);
	}
	
	public void startServer(int port) {
		mHttpCallbackServer.start(port);
	}
	
	public void stopServer() {
		mHttpCallbackServer.stop();
	}
	
	public String requestTokenClientCredentials(String client_id, String client_secret) throws Exception {
		String url = "https://auth.nello.io/oauth/token/";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String urlParameters = "grant_type=client_credentials&client_id=" + client_id + "&client_secret=" + client_secret;
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		int responseCode = con.getResponseCode();
		log("Sending 'POST' request to URL : " + url, NelloAPI.INFO);
		log("Post parameters : " + urlParameters, NelloAPI.INFO);
		log("Response Code : " + responseCode, NelloAPI.INFO);
		log("Response: " + response, NelloAPI.INFO);
		
		if (responseCode == 200) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
			return jsonObject.get("access_token").toString();
		}
		log("invalid_request or invalid_client", NelloAPI.ERROR);
		return null;
	}
	
	public List<Location> getLocations(String token) throws Exception {
		String url = "https://public-api.nello.io/v1/locations/";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty ("Authorization", "Bearer " + token);
		con.setRequestMethod("GET");
		
		int responseCode = con.getResponseCode();
		
		log("Sending 'GET' request to URL : " + url, NelloAPI.INFO);
		log("Response Code : " + responseCode, NelloAPI.INFO);
		

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		if (responseCode == 200) {
			JSONParser parser = new JSONParser();
			JSONObject responseObject = (JSONObject) parser.parse(response.toString());
			JSONArray dataArray = (JSONArray) responseObject.get("data");
			List<Location> listLocations = new ArrayList<Location>();
			if (dataArray != null) {
				for (int i = 0 ; i < dataArray.size() ; i++) {
					Location location = new Location((JSONObject) dataArray.get(i));
					listLocations.add(location);
					log("[" + (i+1) + "] " + location.toString(), NelloAPI.INFO);
				}
				return listLocations;
			} else {
				log("No addresses available", NelloAPI.ERROR);
			}
		} else {
			log("The server could not verify that you are authorized to access the URL requested", NelloAPI.ERROR);
		}
		return null;
		
	}
	
	//Create new TimeWindow
	//TODO
	
	//Delete a Time Window
	//TODO
	
	public boolean openDoor(String token, Location location) throws Exception {
		String url = "https://public-api.nello.io/v1/locations/" + location.getLocation_id() + "/open/";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty ("Authorization", "Bearer " + token);
		con.setRequestMethod("PUT");
		
		int responseCode = con.getResponseCode();
		log("Sending 'PUT' request to URL : " + url, NelloAPI.INFO);
		log("Response Code : " + responseCode, NelloAPI.INFO);

		if (responseCode == 200) {
			log("Door has been opened successfully!", NelloAPI.INFO);
			return true;
		}
		log("The server could not verify that you are authorized to access the URL requested", NelloAPI.ERROR);
		return false;
	}

	public boolean setWebhook(String token, Location location, String webhook_url) throws Exception {
		return setWebhook(token, location, webhook_url, true, true, true, true);
	}
	
	public boolean setWebhook(String token, Location location, String webhook_url, boolean swipe, boolean geo, boolean tw, boolean deny) throws Exception {
		if (!(swipe || geo || tw || deny)) {
			log("All values = \"false\" is not allowed", NelloAPI.ERROR);
			return false;
		}
		
		String url = "https://public-api.nello.io/v1/locations/" + location.getLocation_id() + "/webhook/";
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
		
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(request);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		log("Sending 'PUT' request to URL : " + url, NelloAPI.INFO);
		log("Request: " + request, NelloAPI.INFO);
		log("Response Code : " + responseCode, NelloAPI.INFO);

		if (responseCode == 200) {
			log("Webhook was modified successfully", NelloAPI.INFO);
			return true;
		}
		log("The server could not verify that you are authorized to access the URL requested", NelloAPI.ERROR);
		return false;
	}
	
	public boolean deleteWebhook(String token, Location location) throws Exception {
		String url = "https://public-api.nello.io/v1/locations/" + location.getLocation_id() + "/webhook/";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestProperty ("Authorization", "Bearer " + token);
		con.setRequestMethod("DELETE");
		con.connect();
		
		int responseCode = con.getResponseCode();
		log("Sending 'GET' request to URL : " + url, NelloAPI.INFO);
		log("Response Code : " + responseCode, NelloAPI.INFO);
		
		if (responseCode == 200) {
			log("Webhook was deleted successfully", NelloAPI.INFO);
			return true;
		}
		log("The server could not verify that you are authorized to access the URL requested", NelloAPI.ERROR);
		return false;
	}
	
	private void log(String msg, int code) {
		if (mDebugOutput) {
			if (code == NelloAPI.INFO) {
				System.out.println(msg);
			} else if (code == NelloAPI.ERROR) {
				System.err.println(msg);
			}
		}
	}
	
}
