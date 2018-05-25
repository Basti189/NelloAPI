package de.wolfsline.nello.api;

import java.util.ArrayList;
import java.util.List;

public class NelloBase {

	protected final static int INFO = 0;
	protected final static int ERROR = 1;
	
	private static boolean mDebugOutput = false;
	
	protected static List<Object> mRegisteredListener = new ArrayList<Object>();
	
	public void setDebugOutput(boolean debug) {
		mDebugOutput = debug;
	}
	
	public void register(Object listener) {
		mRegisteredListener.add(listener);
	}
	
	public void unregister(Object listener) {
		mRegisteredListener.remove(listener);
	}
	
	protected void log(String msg, int code) {
		if (mDebugOutput) {
			msg = "[NelloAPI] " + msg;
			if (code == INFO) {
				System.out.println(msg);
			} else if (code == ERROR) {
				System.err.println(msg);
			}
		}
	}
}
