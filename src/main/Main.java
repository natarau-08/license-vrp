package main;

import database.SqliteConnection;
import database.obj.cvrp.CvrpGraph;
import generator.CapacityGraphGenerator;
import utils.Clock;

public class Main {

	public static void main(String args[]) {
		SqliteConnection.init();
		
		try {
			System.out.println("Generating...");
			Clock c = new Clock();
			c.start();
			
			//CvrpGraph.createNewCvrpGraph("test", "debug rtest");
			CvrpGraph graph = CvrpGraph.getGraphByName("test");
			CapacityGraphGenerator.generateCvrpGraph(graph, 100, 10, 50, 10, 100, 10, 0f, 800, 800);
			
			c.stop();
			System.out.println("Finished!");
			
			System.out.println(c.getWaitInfo());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
