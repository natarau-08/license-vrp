package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Configuration {
	private static HashMap<String, String> cfg = getConfigurations();
	
	private static HashMap<String, String> getConfigurations() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("configuration.cfg"));
			
			String line;
			while((line = br.readLine()) != null) {
				int del = line.indexOf("=");
				String key = line.substring(0, del);
				String val = line.substring(del + 1);
				cfg.put(key, val);
			}
			
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getString(String key) {
		return cfg.get(key);
	}
}
