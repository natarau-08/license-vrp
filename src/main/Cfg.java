package main;

import java.io.BufferedReader;
import java.io.FileReader;

public class Cfg {

	public static final String CONNECTION_STRING = "CONNECTION_STRING";
	public static final String SQL_BATCH_COUNT = "SQL_BATCH_COUNT";
	public static final String NODE_MARGIN = "NODE_MARGIN";
	public static final String NODE_DIAMETER = "NODE_DIAMETER";
	public static final String MAX_NODE_DRAW_TRIES = "MAX_NODE_DRAW_TRIES";
//	public static final String RENDER_NODE_COLOR = "RENDER_NODE_COLOR";
//	public static final String RENDER_BACKGROUND_COLOR = "RENDER_BACKGROUND_COLOR";
	
	private static String getConfigurationString(String passedKey) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("configuration.cfg"));
			
			String line;
			while((line = br.readLine()) != null) {
				
				if(line.startsWith("#") || line.trim().length() == 0 || !line.contains("=")) continue;
				
				int del = line.indexOf("=");
				String key = line.substring(0, del);
				String val = line.substring(del + 1);
				
				if(key.contentEquals(passedKey)) {
					br.close();
					return val;
				}
			}
			
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getString(String key) {
		return getConfigurationString(key);
	}
	
	public static int getInt(String key) {
		return Integer.parseInt(getConfigurationString(key));
	}
	
	public static long getLong(String key) {
		return Long.parseLong(getConfigurationString(key));
	}
}
