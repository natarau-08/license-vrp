package main;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import algorithm.ClarkeWright;
import database.SqliteConnection;
import database.SqliteManager;
import database.obj.cvrp.CvrpGraph;
import generator.CapacityGraphGenerator;
import renderer.GraphRenderer;
import utils.Calc;

public class Main {

	public static final String LOG_FILE_PATH = "log.txt";
	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static void main(String args[]) {
		
		try {
			
			//preparing logger
			FileHandler fh = new FileHandler(LOG_FILE_PATH);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			LOGGER.addHandler(fh);
			
			SqliteConnection.init();
			
			int a = 4;
			//SqliteManager.clearDatabase();
			switch(a) {
			case 0:
				SqliteManager.clearDatabase();
				CvrpGraph.createCvrpGraph("test", "debug test",  800, 800, 32);
				CvrpGraph graph = CvrpGraph.getGraphByName("test");
				
				long mls = System.currentTimeMillis();
				LOGGER.info("Generating graph...");
				
				CapacityGraphGenerator.generateCvrpGraph(graph, 10, 10, 50, 10, 100, 0.5f);
				
				mls = System.currentTimeMillis() - mls;
				LOGGER.info("Generating completed in " + Calc.mlsToHms(mls) + ". Milliseconds: " + mls);
				
				break;
				
			case 1: 
				graph = CvrpGraph.getGraphByName("test");
				GraphRenderer.writeCvrpImage(graph);
				break;
			
			case 2:
				graph = CvrpGraph.getGraphByName("test");
				GraphRenderer.writeCvrpImageWithCosts(graph);
				break;
			
			case 3:
				graph = CvrpGraph.getGraphByName("test");
				
				ClarkeWright.baseCvrp(graph, 100, ClarkeWright.CLARKE_WRIGHT_SQUENTIAL);
				GraphRenderer.writeCvrpImage(graph);
				break;
				
			case 4:
				graph = CvrpGraph.getGraphByName("test");
				
				ClarkeWright.oopCvrp(graph, 100);
				
				break;
				
				default: break;
			}
			
			LOGGER.info("Finished");
			
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
