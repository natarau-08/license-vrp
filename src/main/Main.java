package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import obj.cvrp.CvrpGraph;

public class Main {

	public static final String LOG_FILE_PATH = "log.txt";
	public static final Logger LOG = Logger.getLogger(Main.class.getName());
	
	public static Connection connection;
	
	public static void main(String args[]) {
		
		try {
			connection = DriverManager.getConnection(Cfg.getString("CONNECTION_STRING"));
			//preparing logger
			FileHandler fh = new FileHandler(LOG_FILE_PATH);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			LOG.addHandler(fh);
			
			CvrpGraph graph = CvrpGraph.generateRandom(10, "refactoring-test", "none", 500, 500, true);
			graph.save();
			
			connection.close();
			LOG.info("Finished");
			
		}catch(Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
