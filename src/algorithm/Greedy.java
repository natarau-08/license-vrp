package algorithm;

import java.util.LinkedList;

import obj.cvrp.CvrpGraph;
import obj.cvrp.CvrpNode;
import obj.cvrp.CvrpRoute;
import utils.Calc;
import utils.Clock;

import static main.Main.LOG;

public class Greedy {

	public static void computeGreedySolution(CvrpGraph graph, int maxLoad) {
		CvrpNode depot = graph.getDepot();
		
		LinkedList<CvrpNode> nodes = new LinkedList<>();
		for(CvrpNode n: graph.getNodes().values()) {
			nodes.add(n);
		}
		
		LOG.info("Computing Greedy Solution...");
		Clock.initClock();
		
		nodes.sort((a, b) -> 
				(int)(Calc.dist(a, depot) - Calc.dist(b, depot)) 
			);
		
		LinkedList<CvrpRoute> routes = graph.getRoutes();
		routes.clear();
		routes.add(new CvrpRoute(graph));
		
		for(CvrpNode n: nodes) {
			if(n == depot)
				continue;
			
			if(routes.getLast().getLoad() + n.getDemand() <= maxLoad) {
				routes.getLast().add(n.getId());
			}else {
				routes.add(new CvrpRoute(graph, n.getId()));
			}
		}
		
		LOG.info("Computed Greedy solution" + routes + "\nin " + Clock.dumpClock());
	}
}
