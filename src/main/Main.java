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
			
			int a = 0;
			int vehicleCapacity = 100;
			int nodes = 20;
			int width = 1600, height = 800;
			String graphName = "test";
			
			//SqliteManager.clearDatabase();
			switch(a) {
			case 0:
				SqliteManager.clearDatabase();
				CvrpGraph.createCvrpGraph(graphName, "debug test",  width, height, 32);
				CvrpGraph graph = CvrpGraph.getGraphByName(graphName);
				
				long mls = System.currentTimeMillis();
				LOGGER.info("Generating graph...");
				
				CapacityGraphGenerator.generateCvrpGraph(graph, nodes, 0, 0, 10, 50, 0.1f);
				
				mls = System.currentTimeMillis() - mls;
				LOGGER.info("Generating completed in " + Calc.mlsToHms(mls) + ". Milliseconds: " + mls);
				LOGGER.info("Reloading Graph");
				graph = CvrpGraph.getGraphByName(graphName);
				
				ClarkeWright.oopCvrp(graph, 100, ClarkeWright.CLARKE_WRIGHT_SQUENTIAL);
				LOGGER.info(graph.getRoutes().toString());
				
				GraphRenderer.writeCvrpImage(graph);
				
				break;
				
			case 1:
				graph = CvrpGraph.getGraphByName(graphName);
				ClarkeWright.oopCvrp(graph, vehicleCapacity, ClarkeWright.CLARKE_WRIGHT_SQUENTIAL);
				GraphRenderer.writeCvrpImage(graph);
				LOGGER.info(graph.getRoutes().toString());
				
				break;
				
			case 2:
				graph = CvrpGraph.getGraphByName(graphName);
				GraphRenderer.writeCvrpImage(graph);
				break;
				default: break;
			}
			
			LOGGER.info("Finished");
			
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
