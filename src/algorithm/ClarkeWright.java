package algorithm;

import static main.Main.LOGGER;

import java.util.LinkedList;

import database.obj.cvrp.CvrpArc;
import database.obj.cvrp.CvrpCost;
import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
import database.obj.cvrp.CvrpReduction;
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
	
	/**
	 * Solution based on integer sets
	 * @param graph
	 * @param truckCapacity
	 * @param alg
	 * @throws Exception
	 */
	public static void basicCvrp(CvrpGraph graph, int truckCapacity, int alg) throws Exception{
		int[][][] simpl = GraphParser.parseCvrpGraph(graph);
		int n = graph.getNodeCount();
		int[][] nodes = simpl[0];
		int[][] costs = simpl[1];
		
		simpl = null;
		
		String log = "Local id(Database id): \n";
		for(int i=0;i<n;i++) {
			log += String.format("%d(%d), ", i, nodes[0][i]);
		}
		
		LOGGER.info(log);
		
		long t0 = System.currentTimeMillis();
		LOGGER.info("Computing Clarke-Wright Solution:");
		
		//compute cost reduction by visiting two clients per route
		//int reductions[][] = new int[n][n];
		
		int reductions[][] = new int[(n-2)*(n-1)/2][3];
		int reductionsIndex = 0;
		log = "Reduction matrix:\n";
		
		//computing reductions
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				if(i<j) {
					int reduction = (costs[0][i] * 2 + costs[0][j] * 2) - (costs[0][j] + costs[i][j] + costs[0][i]);
					log += reduction + "\t";
					LOGGER.info(String.format("Reduction is %d = %d + %d + %d for pair (%d, %d)", reduction, costs[0][j], costs[0][i], costs[i][j], i, j));
					
					if(reduction != 0) {
						reductions[reductionsIndex] = new int[]{i, j, reduction};
						reductionsIndex++;
					}
					
				}else {
					log += "-\t";
				}
			}
			log += "\n";
		}
		
		LOGGER.info(log);
		LOGGER.info("Sorting reductions");
		
		//reordering reductions desc
		for(int i=0;i<reductions.length-1;i++) {
			for(int j=i+1;j<reductions.length;j++) {
				if(reductions[i][2] < reductions[j][2]) {
					int temp[] = reductions[i];
					reductions[i] = reductions[j];
					reductions[j] = temp;
				}
			}
		}
		
		//pure logging
		log = "";
		for(int i=0;i<reductions.length;i++) {
			int red = reductions[i][2];//reduction ammount
			int i1 = reductions[i][0];//local id
			int i2 = reductions[i][1];//de otha local id
			
			int id1 = nodes[0][reductions[i][0]];//database node id
			int id2 = nodes[0][reductions[i][1]];//database node id
			
			int dem1 = nodes[1][reductions[i][0]];//demand for first node
			int dem2 = nodes[1][reductions[i][1]];//demand for second node
			
			log += String.format("%d(%d, %d), ", red, i1, i2);
			LOGGER.info(String.format("Reduction %d(%d, %d) has database ids %d and %d with demands %d and %d", red, i1, i2, id1, id2, dem1, dem2));
		}
		
		LOGGER.info(log);
		LOGGER.info("Coumputing routes:");
		
		//TODO
		
		long deltaT = System.currentTimeMillis() - t0;
		LOGGER.info("Time Passed: " + Calc.mlsToHms(deltaT) + "\nmls: " + deltaT);
	}

	public static void oopCvrp(CvrpGraph graph, int vehicleCapacity, int algorithm) {
		//fetching demands
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		LinkedList<CvrpCost> costs = graph.getCostsAsList();
		LinkedList<CvrpReduction> reductions = new LinkedList<>();
		
		int[] nodesIds = new int[nodes.size()];
		int depId = graph.getDepot().getId();
		
		LinkedList<LinkedList<Integer>> routes = new LinkedList<>();
		LinkedList<Integer> route = new LinkedList<>();
		LinkedList<CvrpReduction> toRemove = new LinkedList<>();
		
		for(int i=0;i<nodesIds.length;i++) {
			nodesIds[i] = nodes.get(i).getId();
		}
		
		LOGGER.info("Computing Clarke-Wright OOP variant.");
		long t0 = System.currentTimeMillis();
		
		String log = "";
		
		for(CvrpNode n: nodes) {
			log += String.format("%s, ", n.toString());
		}
		LOGGER.info("Nodes list nodeId(demand):\n" + log);
		
		log = "";
		for(CvrpCost c: costs) {
			log += String.format("%s, ", c.toString());
		}
		LOGGER.info("Cost list:\n" + log);
		LOGGER.info("Computing reduction list...");
		log = "";
		for(int i=0;i<nodesIds.length-1;i++) {
			for(int j=0;j<nodesIds.length;j++) {
				if(i < j && nodesIds[i] != depId && nodesIds[j] != depId) {
					LOGGER.info(String.format("Computing reduction for pair (%d, %d)", nodesIds[i], nodesIds[j]));
					CvrpReduction red = new CvrpReduction(new CvrpArc(nodesIds[i], nodesIds[j]));
					if(red.getValue() > 0) {
						reductions.add(red);
						log += red.toString() + ", ";
					}
				}
			}
		}
		
		LOGGER.info("Reductions:\n" + log);
		LOGGER.info("Sorting reductions: ");
		reductions.sort((a, b) -> b.getValue() - a.getValue());
		LOGGER.info(reductions.toString());
		
		if(algorithm == CLARKE_WRIGHT_SQUENTIAL) {
			computeOopCwSequentialSolution(graph, vehicleCapacity, reductions, toRemove, route);
			for(int i=0;i<nodesIds.length;i++) {
				if(!graph.getRoutes().contains(nodesIds[i])) {
					graph.getRoutes().add(depId);
					graph.getRoutes().add(nodesIds[i]);
					graph.getRoutes().add(depId);
					break;
				}
			}
		}else if(algorithm == CLARKE_WRIGHT_PARALLEL) {
			computeOopCwParallelSolution(graph, vehicleCapacity, reductions, toRemove, routes);
		}
		
		long deltaT = System.currentTimeMillis() - t0;
		LOGGER.info("Time passed: " + Calc.mlsToHms(deltaT) + "\nmls:" + deltaT);
		
	}
	
	private static void computeOopCwSequentialSolution(CvrpGraph graph, int vehicleCapacity, LinkedList<CvrpReduction> reductions, 
			LinkedList<CvrpReduction> toRemove, LinkedList<Integer> route) {
		int load = 0;
		int depId = graph.getDepot().getId();
		
		while(!reductions.isEmpty()) {
			
			//removing added reductions
			for(CvrpReduction r: toRemove) {
				reductions.removeIf(p -> (
						(p.getNode(0).getId() == r.getNode(0).getId()) || 
						(p.getNode(0).getId() == r.getNode(1).getId()) ||
						(p.getNode(1).getId() == r.getNode(0).getId()) ||
						(p.getNode(1).getId() == r.getNode(1).getId())
						));
			}
			
			toRemove.clear();
			
			for(CvrpReduction r: reductions) {
				
				/*conditions for adding reduction's nodes
					* vehicle can support the additional load
					* if route is not new, the reduction must have a node that is a margin of current route 
				*/
				
				int id0 = r.getNode(0).getId();//nodes to add to the route
				int id1 = r.getNode(1).getId();
				
				int dem0 = r.getNode(0).getDemand();//nodes demand
				int dem1 = r.getNode(1).getDemand();
				
				if(load == 0) {
					if(dem0 + dem1 <= vehicleCapacity && !graph.getRoutes().contains(id0) && !graph.getRoutes().contains(id1)) {
						route.add(id0);
						route.add(id1);
						toRemove.add(r);
						load += dem0 + dem1;
						
						if(reductions.size() == 1) {
							route.add(depId);
							route.addFirst(depId);
							graph.getRoutes().addAll(route);
							route.clear();
						}
					}
					
					continue;
					
				}
				
				int routeFirst = route.getFirst();//they must remain here god dammit!
				int routeLast = route.getLast();
				
				//take out reductions that contain as nodes the end and the beginning of route
				//maybe using more ifs is better
				if(r.getArc().equals(new CvrpArc(routeFirst, routeLast))) {
					toRemove.add(r);
					continue;
				}
				
				if(load + dem0 <= vehicleCapacity) {
					if(routeFirst == id1 && !graph.getRoutes().contains(id0)) {
						route.addFirst(id0);
						load += dem0;
						toRemove.add(r);
					}else if (routeLast == id1 && !graph.getRoutes().contains(id0)) {
						route.addLast(id0);
						load += dem0;
						toRemove.add(r);
					}
				}else if (load + dem1 <= vehicleCapacity) {
					if(routeFirst == id0 && !graph.getRoutes().contains(id1)) {
						route.addFirst(id1);
						load += dem1;
						toRemove.add(r);
					}else if(routeLast == id0 && !graph.getRoutes().contains(id1)) {
						route.add(id1);
						load += dem1;
						toRemove.add(r);
					}
				}
				
				if(r == reductions.getLast()) {
					route.add(depId);
					route.addFirst(depId);
					graph.getRoutes().addAll(route);
					route.clear();
					load = 0;
				}
				
			}
		}
		
	}

	private static void computeOopCwParallelSolution(CvrpGraph graph, int vehicleCapacity, LinkedList<CvrpReduction> reductions,
			LinkedList<CvrpReduction> toRemove, LinkedList<LinkedList<Integer>> routes) {
		System.out.println("Not implemented");
	}
}
