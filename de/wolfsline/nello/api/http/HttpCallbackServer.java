package de.wolfsline.nello.api.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.wolfsline.nello.api.NelloBase;
import de.wolfsline.nello.api.events.NelloActionEvent;
import de.wolfsline.nello.api.interfaces.NelloEvent;

public class HttpCallbackServer extends NelloBase {
	
	private HttpServer mHttpServer;
	
	public HttpCallbackServer() {
		
	}
	
	public void start(int port)  {
		if (mHttpServer == null) {
			log("Starting webserver at ::" + port + "/", INFO);
			try {
				InetSocketAddress adress = new InetSocketAddress(port);
				mHttpServer = HttpServer.create(adress, 0);
				mHttpServer.createContext("/", handler);
				mHttpServer.start();
				log("Webserver started successfully ", INFO);
			} catch (Exception e) {
				mHttpServer = null;
				log("Webserver failed to start ", ERROR);
			}
		}
	}
	
	public void triggerNelloActionEvent(int actionID) {
		try {
			String testJSON = "";
			JSONParser parser = new JSONParser();
			switch (actionID) {
				case NelloActionEvent.SWIPE:
					testJSON = "{\"data\": {\"name\": \"Adam Smith\", \"location_id\": \"095c56a8-6056-11e8-9c2d-fa7ae01bbebc\", \"user_id\": \"33561bd8-6056-11e8-9c2d-fa7ae01bbebc\"}, \"action\": \"swipe\"}";
					break;
				case NelloActionEvent.GEO:
					testJSON = "{\"data\": {\"name\": \"Adam Smith\", \"location_id\": \"095c56a8-6056-11e8-9c2d-fa7ae01bbebc\", \"user_id\": \"33561bd8-6056-11e8-9c2d-fa7ae01bbebc\"}, \"action\": \"geo\"}";
					break;
				case NelloActionEvent.TW:
					testJSON = "{\"data\":{\"name\":\"A sample TW\",\"location_id\":\"91c3e72e-2f1d-4bef-8290-9bee61c70b06\"},\"action\":\"tw\"}";
					break;
				case NelloActionEvent.DENY:
					testJSON = "{\"data\":{\"location_id\":\"91c3e72e-2f1d-4bef-8290-9bee61c70b06\"},\"action\":\"deny\"}";
					break;
			}
			JSONObject response = (JSONObject) parser.parse(testJSON);
			distributeData(new NelloActionEvent(response));
		} catch (Exception e) {
			log(" Can't parse JSON to NelloActionEvent", ERROR);
		}
	}
	
	public void stop() {
		if (mHttpServer != null) {
			mHttpServer.stop(0);
			mHttpServer = null;
		}
	}
	
	private HttpHandler handler = new HttpHandler() {
		
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(),"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String body = br.readLine();
			log("Requested body: " + body, INFO);
			
	        httpExchange.sendResponseHeaders(200, "".length());
	        OutputStream os = httpExchange.getResponseBody();
	        os.write("".getBytes());
	        os.close();
	        
			try {
				JSONParser parser = new JSONParser();
				JSONObject response = (JSONObject) parser.parse(body);
				distributeData(new NelloActionEvent(response));
			} catch (ParseException e) {
				log(" Can't parse JSON to NelloActionEvent", ERROR);
			}
		}
	};
	
	public void distributeData(NelloActionEvent event) {
		for (Object listener : mRegisteredListener) {
			Class<? extends Object> listenerClass = listener.getClass();
			for (Method method : listenerClass.getDeclaredMethods()) {
				if (method.getAnnotation(NelloEvent.class) != null) {
					try {
						method.invoke(listener, event);
					} catch (Exception e) {
						log("Wrong number of arguments -> " + method.getName(), ERROR);
					}
				}
			}
		}
	}
}
