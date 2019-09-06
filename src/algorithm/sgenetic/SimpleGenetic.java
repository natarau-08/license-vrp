package algorithm.sgenetic;

import java.util.LinkedList;

import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
import database.obj.cvrp.CvrpRoute;

public class SimpleGenetic {

	public static void computeSolution(CvrpGraph graph, int individuals, float mutationChance, 
			float selectUnfitChance) throws Exception{
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		LinkedList<CvrpRoute> routes = graph.getRoutes();
		
		//solution check
		if(routes.size() == 0) {
			throw new Exception("An initial solution must be provided");
		}
		
		
		for(CvrpNode n: nodes) {
			
			boolean found = false;
			
			for(CvrpRoute r: routes) {
				if(r.getNodes().contains(n.getId())) {
					found = true;
					break;
				}
			}
			
			if(!found) {
				throw new Exception("Provided solution does not cover node " + n);
			}
		}
		
		//initialization, making the individuals
	}
	
	/**
	 * Generates a solution in which a vehicle 
	 * visits one customer.
	 * @param graph
	 */
	public static void generateBasicSolution(CvrpGraph graph) {
		graph.getNodes().forEach((e, f) -> {
			graph.getRoutes().add(new CvrpRoute(f.getId()));
		});
	}
	
	
}
