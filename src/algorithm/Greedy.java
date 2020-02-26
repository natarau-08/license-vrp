package algorithm;

import java.util.LinkedList;

import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
import database.obj.cvrp.CvrpRoute;
import utils.Calc;
import utils.Clock;

import static main.Main.LOG;

public class Greedy {

	public static void computeGreedySolution(CvrpGraph graph, int maxLoad) {
		CvrpNode depot = graph.getDepot();
		
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		
		LOG.info("Computing Greedy Solution...");
		Clock.initClock();
		
		nodes.sort((a, b) -> 
				(int)(Calc.dist(a, depot) - Calc.dist(b, depot)) 
			);
		
//		nodes.forEach(e -> {
//			System.out.println(Calc.dist(e, depot));
//		});
		
		LinkedList<CvrpRoute> routes = graph.getRoutes();
		routes.clear();
		routes.add(new CvrpRoute());
		
		for(CvrpNode n: nodes) {
			if(n == depot)
				continue;
			
			if(routes.getLast().getLoad() + n.getDemand() <= maxLoad) {
				routes.getLast().add(n.getId());
			}else {
				routes.add(new CvrpRoute(n.getId()));
			}
		}
		
		LOG.info("Computed Greedy solution" + routes + "\nin " + Clock.dumpClock());
	}
}
