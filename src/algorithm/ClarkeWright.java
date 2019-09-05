package algorithm;

import static main.Main.LOGGER;

import java.util.LinkedList;

import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
import database.obj.cvrp.CvrpReduction;
import database.obj.cvrp.CvrpRoute;
import utils.Clock;

/**
 * Compute Clarke-Wright solution for a graph object.
 * The graph object is converted into multidimensional arrays and solution is computed for them.
 * The solution will be added to the graph object so it can be drawn
 * @author dsd_user1 (a.k.a. Alexandru(a.k.a. natarau-08))
 */
public class ClarkeWright {
	
	public static final int CLARKE_WRIGHT_SEQUENTIAL = 0;
	public static final int CLARKE_WRIGHT_PARALLEL = 1;
	
	private static CvrpGraph graph;
	private static int maxLoad;
	
	public static void computeClarkeWrightSolution(CvrpGraph graph, int vehicleCapacity, int algorithm) {
		ClarkeWright.graph = graph;
		maxLoad = vehicleCapacity;
		
		LOGGER.info("Fetching demands and nodes");
		LinkedList<CvrpReduction> reductions = new LinkedList<>();
		LinkedList<CvrpRoute> routes = new LinkedList<>();
		
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		
		LOGGER.info("Computing reductions");
		Clock.initClock();
		
		for(int i=0;i<nodes.size()-1;i++) {
			for(int j=i+1;j<nodes.size();j++) {
				int id1 = nodes.get(i).getId();
				int id2 = nodes.get(j).getId();
				
				if(id1 == graph.getDepot().getId() || id2 == graph.getDepot().getId() || id1 == id2) {
					continue;
				}
				CvrpReduction red = new CvrpReduction(id1, id2);
				if(red.getValue() <= 0) {
					//LOGGER.severe("Reduction " + red + " has negative value? How is it possible?\nPossibly from converting double to int\nWill not be added");
					continue;
				}
				reductions.add(red);
			}
		}
		
		LOGGER.info("Reductions computed: \n" + reductions);
		
		reductions.sort((a, b) -> b.getValue() - a.getValue());
		
		LOGGER.info("Sorting reductions: \n" + reductions);
			
		if(algorithm == CLARKE_WRIGHT_PARALLEL) {
			compCWoopPar(nodes, reductions, routes);
			
		}else if(algorithm == CLARKE_WRIGHT_SEQUENTIAL) {
			compCWoopSeq(nodes, reductions, routes);
		}
		
		LOGGER.info("Algorithm completed with solution:\n" + routes + "\nin " + Clock.dumpClock());
		
		//adding routes to graph
		graph.getRoutes().addAll(routes);
	}
	
	private static void compCWoopSeq(LinkedList<CvrpNode> nodes, LinkedList<CvrpReduction> reductions,
			LinkedList<CvrpRoute> routes) {
		
		LinkedList<CvrpReduction> toRemove = new LinkedList<>();
		LinkedList<CvrpRoute> inRoutes = new LinkedList<>();
		
		CvrpRoute currentRoute;
		
		while(!reductions.isEmpty()) {
			
			
			currentRoute = new CvrpRoute();
			
			for(CvrpReduction r: toRemove) {
				reductions.remove(r);
			}
			
			toRemove.clear();
			
			for(CvrpReduction r: reductions) {
				
				int node0 = r.getNode(0).getId();
				int node1 = r.getNode(1).getId();
				
				int dem0 = r.getNode(0).getDemand();
				int dem1 = r.getNode(1).getDemand();
				
				
				inRoutes.clear();
				
				for(CvrpRoute route: routes) {
					if(routeContains(node0, node1, route)) {
						inRoutes.add(route);
					}
				}
				
				if(routeContains(node0, node1, currentRoute)) {
					inRoutes.add(currentRoute);
				}
				
				if(inRoutes.size() == 0 && currentRoute.getNodes().isEmpty()) {
					
					if(dem0 + dem1 < maxLoad) {
						currentRoute.add(node0);
						currentRoute.add(node1);
						toRemove.add(r);
					}
					continue;
				}
				
				switch(inRoutes.size()) {
				case 0:
					continue;
					
				case 1:
					addNodesToRoute(node0, node1, inRoutes.getFirst());
					break;
					
				case 2:
					//mergeRoutes(inRoutes.getFirst(), inRoutes.getLast());
					break;
				
				default:
					LOGGER.info("More than two routes? Impossible!");
					break;
				}
				
				toRemove.add(r);
				
			}
			
			if(!currentRoute.getNodes().isEmpty())
				routes.add(currentRoute);
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
				routes.add(new CvrpRoute(n.getId()));
			}
		}
	}

	private static void compCWoopPar(LinkedList<CvrpNode> nodes, LinkedList<CvrpReduction> reductions, LinkedList<CvrpRoute> routes) {
		
		LinkedList<CvrpRoute> inRoutes = new LinkedList<>();
		
		for(CvrpReduction r: reductions) {
			
			int dem0 = r.getNode(0).getDemand();
			int dem1 = r.getNode(1).getDemand();
			
			int rid0 = r.getNode(0).getId();
			int rid1 = r.getNode(1).getId();
			
			if(routes.isEmpty()) {
				if(dem0 + dem1 <= maxLoad) {
					CvrpRoute ro = new CvrpRoute(rid0, rid1);
					routes.add(ro);
					continue;
				}
				
			}
			
			//route is no longer empty
			for(CvrpRoute ro: routes) {
				if(routeContains(rid0, rid1, ro)) {
					inRoutes.add(ro);
				}
			}
			
			if(inRoutes.isEmpty() && rid0 + rid1 < maxLoad) {
				CvrpRoute route = new CvrpRoute(rid0, rid1);
				routes.add(route);
				continue;
			}
			
			//this reduction is found in one or more routes
			//it can be found in one route or two routes. Three or more
			//means that two or more routes have common nodes which
			//is a fault
			switch(inRoutes.size()) {
				case 1:
					addNodesToRoute(rid0, rid1, inRoutes.getFirst());
					break;
				
				case 2:
					LOGGER.info("Attempting to merge " + inRoutes.getFirst() + " with " + inRoutes.getLast() + "\nReduction:" + r);
					if(mergeRoutes(inRoutes.getFirst(), inRoutes.getLast()))
						LOGGER.info("-----------------------------------------------------------------Merged two routes");
					break;
				
				default:
					LOGGER.severe("Reduction " + r + " is found in routes " + inRoutes);
					break;
			}
			
			inRoutes.clear();
		}
		
		inRoutes.clear();
		boolean isIn = false;
		for(CvrpNode n: nodes) {
			for(CvrpRoute r: routes) {
				if(r.contains(n.getId())) {
					isIn = true;
					break;
				}
				
			}
			
			if(!isIn && n.getDemand() > 0) {
				inRoutes.add(new CvrpRoute(n.getId()));
				isIn = false;
				break;
			}
		}
		
		routes.addAll(inRoutes);
		
	}
	
	private static boolean routeContains(int n1, int n2, CvrpRoute r) {
		return (r.contains(n1) || r.contains(n2));
	}
	
	/**
	 * Adds one of the two nodes to the route
	 * @param n1
	 * @param n2
	 * @param rou
	 * @return - true if node was added
	 */
	private static boolean addNodesToRoute(int n1, int n2, CvrpRoute rou) {
		int routeFirst = rou.getFirst();
		int routeLast = rou.getLast();
		
		int dem1 = graph.getNodes().get(n1).getDemand();
		int dem2 = graph.getNodes().get(n2).getDemand();
		
		if(rou.contains(n1) && rou.contains(n2)) {
			return false;
		}
		
		if(routeFirst == n1) {
			if(dem2 + rou.getLoad() <= maxLoad) {
				rou.addFirst(n2);
				return true;
			}
			return false;
		}else if(routeFirst == n2) {
			if(dem1 + rou.getLoad() <= maxLoad) {
				rou.addFirst(n1);
				return true;
			}
			return false;
		}else if(routeLast == n1) {
			if(dem2 + rou.getLoad() <= maxLoad) {
				rou.addLast(n2);
				return true;
			}
			return false;
		}else if(routeLast == n2) {
			if(dem1 + rou.getLoad() <= maxLoad) {
				rou.addLast(n1);
				return true;
			}
			return false;
		}
		
		return false;
	}

	/**
	 * Attempts to merge this route with another route. 
	 * It fails if the routes have no common nodes(first or last). 
	 * It fails if the quantity exceeds vehicleCapacity. 
	 * @param r - the other route
	 * @param vehicleCapacity
	 * @return - true if merging was successful, false otherwise
	 */
	public static boolean mergeRoutes(CvrpRoute r1, CvrpRoute r2) {
		if(r1.getLoad() + r2.getLoad() > maxLoad) {
			LOGGER.info("Merge failed: " + r1.getLoad() + " + " + r2.getLoad() + " = " + (r1.getLoad() + r2.getLoad()) + " > " + maxLoad);
			return false;
		}
		
		int r1f = r1.getFirst();
		int r1l = r1.getLast();
		
		int r2f = r2.getFirst();
		int r2l = r2.getLast();
		
		if(r1f == r2f) {
			r2.getNodes().removeFirst();
			r1.getNodes().addAll(r2.getNodes());
		}else if(r1f == r2l) {
			r2.getNodes().removeLast();
			while(!r2.getNodes().isEmpty()) {
				r1.getNodes().addFirst(r2.getNodes().removeLast());
			}
		}else if(r1l == r2f) {
			r2.getNodes().removeFirst();
			r1.getNodes().addAll(r2.getNodes());
		}else if(r1l == r2l) {
			r2.getNodes().removeLast();
			while(!r2.getNodes().isEmpty()) {
				r1.getNodes().add(r2.getNodes().removeLast());
			}
		}else {
			LOGGER.info("Merge failed: reduction connects one or two nodes that are inside the route");
			return false;
		}
		
		r1.setLoad(r1.getLoad() + r2.getLoad());
		return true;
	}
}
