package algorithm;

import static main.Main.LOGGER;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import database.obj.cvrp.CvrpArc;
import database.obj.cvrp.CvrpCost;
import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
import database.obj.cvrp.CvrpReduction;
import database.obj.cvrp.CvrpRoute;
import utils.Calc;

/**
 * Compute Clarke-Wright solution for a graph object.
 * The graph object is converted into multidimensional arrays and solution is computed for them.
 * The solution will be added to the graph object so it can be drawn
 * @author dsd_user1 (a.k.a. Alexandru(a.k.a. natarau-08))
 */
public class ClarkeWright {
	
	public static final int CLARKE_WRIGHT_SQUENTIAL = 0;
	public static final int CLARKE_WRIGHT_PARALLEL = 1;
	
	public static final int CLARKE_WRIGHT_OOP_APPROACH = 1;
	
	private static CvrpGraph graph;
	private static int maxLoad;
	
	public static void computeClarkeWrightSolution(CvrpGraph graph, int vehicleCapacity, int algorithm, int approach) {
		ClarkeWright.graph = graph;
		maxLoad = vehicleCapacity;
		
		LOGGER.info("Fetching demands and nodes");
		LinkedList<CvrpReduction> reductions = new LinkedList<>();
		LinkedList<CvrpRoute> routes = new LinkedList<>();
		
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		
		LOGGER.info("Computing reductions");
		long start = System.currentTimeMillis();
		
		for(int i=0;i<nodes.size()-1;i++) {
			for(int j=i+1;j<nodes.size();j++) {
				int id1 = nodes.get(i).getId();
				int id2 = nodes.get(j).getId();
				
				if(id1 == graph.getDepot().getId() || id2 == graph.getDepot().getId() || id1 == id2) {
					continue;
				}
				
				reductions.add(new CvrpReduction(id1, id2));
			}
		}
		
		LOGGER.info("Reductions computed: \n" + reductions);
		
		reductions.sort((a, b) -> b.getValue() - a.getValue());
		
		LOGGER.info("Sorting reductions: \n" + reductions);
		
		
		if(approach == CLARKE_WRIGHT_OOP_APPROACH) {
			
			if(algorithm == CLARKE_WRIGHT_PARALLEL) {
				compCWoopPar(nodes, reductions, routes);
				
			}
		}
		
		long delta = System.currentTimeMillis() - start;
		LOGGER.info("Algorithm completed with solution:\n" + routes + "\nin " + Calc.mlsToHms(delta) + "\tmillis: " + delta);
		
		//adding routes to graph
		graph.getRoutes().addAll(routes);
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
					
//					if(!addNodeToRoute(rid0, rid1, ro)) {
//						break;
//					}
					
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
					addNodeToRoute(rid0, rid1, inRoutes.getFirst());
					break;
				
				case 2:
					inRoutes.getFirst().mergeRoute(inRoutes.getLast(), maxLoad);
					break;
				
				default:
					LOGGER.severe("Reduction " + r + " is found in routes " + inRoutes);
					break;
			}
			
			//clean "inRoutes"
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
	private static boolean addNodeToRoute(int n1, int n2, CvrpRoute rou) {
		int routeFirst = rou.getFirst();
		int routeLast = rou.getLast();
		
		int dem1 = graph.getNodes().get(n1).getDemand();
		int dem2 = graph.getNodes().get(n2).getDemand();
		
		if((routeFirst == n1 &&  routeLast == n2) || (routeFirst == n2 && routeLast == n1)) {
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
	 * removes all reductions that have at least one common node referenced in toRemove list.
	 * How it works:
	 * <ul>
	 * 	<li>Compare every object <b>o</b> from <em>reductionList</em> 
	 * with every object <b>p</b> from <em>toRemove</em> list</li>
	 * 	<li>If <b>o</b> has at least one node equal to a node of <b>p</b>
	 * then, remove <b>o</b> from its list</li>
	 * <li>After all <b>o</b>'s are removed, clear <em>toRemove</em></li>
	 * </ul>
	 * @param reductions - reductions list
	 * @param toRemove - reductions to remove
	 */
	private static void removeReductions(LinkedList<CvrpReduction> reductions, LinkedList<CvrpReduction> toRemove) {
		for(CvrpReduction r: toRemove) {
			reductions.removeIf(p -> (
					(p.getNode(0).getId() == r.getNode(0).getId()) || 
					(p.getNode(0).getId() == r.getNode(1).getId()) ||
					(p.getNode(1).getId() == r.getNode(0).getId()) ||
					(p.getNode(1).getId() == r.getNode(1).getId())
					));
		}
		
		toRemove.clear();
	}

//	/**
//	 * See {@link #removeReductions(LinkedList, LinkedList)}
//	 * @param reductions
//	 * @param toRemove
//	 */
//	private static void removeReduction(LinkedList<CvrpReduction> reductions, CvrpReduction toRemove) {
//		reductions.removeIf(p -> (
//				(p.getNode(0).getId() == toRemove.getNode(0).getId()) || 
//				(p.getNode(0).getId() == toRemove.getNode(1).getId()) ||
//				(p.getNode(1).getId() == toRemove.getNode(0).getId()) ||
//				(p.getNode(1).getId() == toRemove.getNode(1).getId())
//				));
//	}
}
