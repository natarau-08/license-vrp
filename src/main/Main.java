package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import algorithm.ClarkeWright;
import obj.cvrp.CvrpGraph;
import renderer.CvrpRenderer;

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
			
			//CvrpGraph graph = CvrpGraph.generateRandom(10, "refactoring-test", "none", 800, 800, 2, 10, true);
			//graph.save();
			CvrpGraph graph = new CvrpGraph("refactoring-test");
			//ClarkeWright.computeClarkeWrightSolution(graph, 30, ClarkeWright.CLARKE_WRIGHT_PARALLEL);
			CvrpRenderer.renderGraph(graph);
			
			connection.close();
			LOG.info("Finished");
			
		}catch(Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
