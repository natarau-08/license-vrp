package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

	public static final String LOG_FILE_PATH = "log.txt";
	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static Connection connection;
	
	public static void main(String args[]) {
		
		try {
			connection = DriverManager.getConnection(Configuration.getString("CONNECTION_STRING"));
			//preparing logger
			FileHandler fh = new FileHandler(LOG_FILE_PATH);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			LOGGER.addHandler(fh);
			
			connection.close();
			LOGGER.info("Finished");
			
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
