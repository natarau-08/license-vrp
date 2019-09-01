package main;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import database.SqliteConnection;
import database.SqliteManager;
import database.obj.cvrp.CvrpGraph;
import generator.CapacityGraphGenerator;
import renderer.GraphRenderer;
import utils.Clock;

public class Main {

	public static final String LOG_FILE_PATH = "log.txt";
	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static void main(String args[]) {
		Clock c = new Clock();
		try {
			
			//preparing logger
			FileHandler fh = new FileHandler(LOG_FILE_PATH);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			LOGGER.addHandler(fh);
			
			SqliteConnection.init();
			
			c.start();
			
			int a = 2;
			
			switch(a) {
			case 0:
				SqliteManager.clearDatabase();
				CvrpGraph.createCvrpGraph("test", "debug test",  800, 800, 10);
				CvrpGraph graph = CvrpGraph.getGraphByName("test");
				CapacityGraphGenerator.generateCvrpGraph(graph, 10, 10, 50, 10, 100, 0f);
				break;
				
			case 1: 
				graph = CvrpGraph.getGraphByName("test");
				GraphRenderer.writeCvrpImage(graph);
				break;
			
			case 2:
				graph = CvrpGraph.getGraphByName("test");
				GraphRenderer.writeCvrpImageWithCosts(graph);
				break;
				default: break;
			}
			
			c.stop();
			LOGGER.info("Finished");
			
			LOGGER.info(c.getWaitInfo());
		}catch(Exception e) {
			c.stop();
			LOGGER.info(c.getWaitInfo() + " Milliseconds: " + c.getMills());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
