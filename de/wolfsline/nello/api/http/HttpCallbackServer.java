package de.wolfsline.nello.api.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.wolfsline.nello.api.NelloAPI;
import de.wolfsline.nello.api.events.NelloActionEvent;
import de.wolfsline.nello.api.interfaces.NelloEvent;

public class HttpCallbackServer {

	private HttpServer mHttpServer;
	private boolean mDebugOutput = false;
	private List<Object> mRegisteredListener = new ArrayList<Object>();
	
	public HttpCallbackServer() {
		
	}
	
	public void setDebugOutput(boolean debug) {
		mDebugOutput = debug;
	}
	
	public void register(Object listener) {
		mRegisteredListener.add(listener);
	}
	
	public void unregister(Object listener) {
		mRegisteredListener.remove(listener);
	}
	
	public void start(int port)  {
		if (mHttpServer == null) {
			log("Starting webserver at ::" + port + "/", NelloAPI.INFO);
			try {
				InetSocketAddress adress = new InetSocketAddress(port);
				mHttpServer = HttpServer.create(adress, 0);
				mHttpServer.createContext("/", handler);
				mHttpServer.start();
				log("Webserver started successfully ", NelloAPI.INFO);
			} catch (Exception e) {
				mHttpServer = null;
				log("Webserver failed to start ", NelloAPI.ERROR);
			}
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
			log("Requested body: " + body, NelloAPI.INFO);
			
	        httpExchange.sendResponseHeaders(200, "".length());
	        OutputStream os = httpExchange.getResponseBody();
	        os.write("".getBytes());
	        os.close();
	        
			try {
				JSONParser parser = new JSONParser();
				JSONObject response = (JSONObject) parser.parse(body);
				distributeData(new NelloActionEvent(response));
			} catch (ParseException e) {
				log(" Can't parse JSON to NelloActionEvent", NelloAPI.ERROR);
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
						log("Wrong number of arguments -> " + method.getName(), NelloAPI.ERROR);
					}
				}
			}
		}
	}
	
	private void log(String msg, int code) {
		if (mDebugOutput) {
			msg = "[NelloAPI] " + msg;
			if (code == NelloAPI.INFO) {
				System.out.println(msg);
			} else if (code == NelloAPI.ERROR) {
				System.err.println(msg);
			}
		}
	}
	
}
