package main;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import algorithm.ClarkeWright;
import algorithm.Greedy;
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
		
		try {
			
			//preparing logger
			FileHandler fh = new FileHandler(LOG_FILE_PATH);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			LOGGER.addHandler(fh);
			
			SqliteConnection.init();
			
			int a = 3;
			int vehicleCapacity = 200;
			int nodes = 20;
			int width = 1200, height = 800;
			String graphName = "generated0";
			
			//SqliteManager.clearDatabase();
			switch(a) {
			case -1:
				SqliteManager.clearDatabase();
				break;
			case 0:
				
				SqliteManager.clearDatabase();
				CvrpGraph.createCvrpGraph(graphName, "debug test",  width, height, 32);
				CvrpGraph graph = CvrpGraph.getGraphByName(graphName);
				
				Clock.initClock();
				LOGGER.info("Generating graph...");
				
				CapacityGraphGenerator.generateCvrpGraph(graph, nodes, 0, 0, 10, 50, 0.1f);
				
				LOGGER.info("Generating completed in " + Clock.dumpClock());
				LOGGER.info("Reloading Graph");
				graph = CvrpGraph.getGraphByName(graphName);
				
				ClarkeWright.computeClarkeWrightSolution(graph, vehicleCapacity, 
						ClarkeWright.CLARKE_WRIGHT_PARALLEL);
				
				LOGGER.info(graph.getRoutes().toString());
				GraphRenderer.writeCvrpImage(graph, "par");
				
				break;
				
			case 1:
				graph = CvrpGraph.getGraphByName(graphName);
				ClarkeWright.computeClarkeWrightSolution(graph, vehicleCapacity, 
						ClarkeWright.CLARKE_WRIGHT_PARALLEL);
				GraphRenderer.writeCvrpImage(graph, "par");
				LOGGER.info(graph.getRoutes().toString());
				
				break;
				
			case 2:
				graph = CvrpGraph.getGraphByName(graphName);
				ClarkeWright.computeClarkeWrightSolution(graph, vehicleCapacity, 
						ClarkeWright.CLARKE_WRIGHT_SEQUENTIAL);
				GraphRenderer.writeCvrpImage(graph, "seq");
				LOGGER.info(graph.getRoutes().toString());
				
				break;
				
			case 3:
				graph = CvrpGraph.getGraphByName(graphName);
				Greedy.computeGreedySolution(graph, vehicleCapacity);
				GraphRenderer.writeCvrpImage(graph, "greedy");
				break;
				default: break;
			}
			
			LOGGER.info("Finished");
			
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
