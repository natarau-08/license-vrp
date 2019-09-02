package algorithm;

import static main.Main.LOGGER;

import java.util.ArrayList;

import database.obj.cvrp.CvrpGraph;
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
	
	public static void cvrp(CvrpGraph graph, int truckCapacity, int alg) throws Exception{
		int[][][] simpl = GraphParser.parseCvrpGraph(graph);
		int n = graph.getNodeCount();
		int[][] nodes = simpl[0];
		int[][] costs = simpl[1];
		
		simpl = null;
		
		long t0 = System.currentTimeMillis();
		LOGGER.info("Computing Clarke-Wright Solution:");
		
		//compute cost reduction by visiting two clients per route
		//int reductions[][] = new int[n][n];
		
		int reductions[][] = new int[(n-2)*(n-1)/2][3];
		int reductionsIndex = 0;
		String logRed = "Reduction matrix:\n";
		
		//computing reductions
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				if(i<j) {
					int reduction = (costs[0][i] * 2 + costs[0][j] * 2) - (costs[0][j] + costs[i][j] + costs[0][i]);
					logRed += reduction + "\t";
					LOGGER.info(String.format("Reduction is %d = %d + %d + %d for pair (%d, %d)", reduction, costs[0][j], costs[0][i], costs[i][j], i, j));
					
					if(reduction != 0) {
						reductions[reductionsIndex] = new int[]{i, j, reduction};
						reductionsIndex++;
					}
					
				}else {
					logRed += "-\t";
				}
			}
			logRed += "\n";
		}
		
		LOGGER.info(logRed);
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
		logRed = "";
		for(int i=0;i<reductions.length;i++) {
			int red = reductions[i][2];//reduction ammount
			int i1 = reductions[i][0];//local id
			int i2 = reductions[i][1];//de otha local id
			
			int id1 = nodes[0][reductions[i][0]];//database node id
			int id2 = nodes[0][reductions[i][1]];//database node id
			
			int dem1 = nodes[1][reductions[i][0]];//demand for first node
			int dem2 = nodes[1][reductions[i][1]];//demand for second node
			
			logRed += String.format("%d(%d, %d), ", red, i1, i2);
			LOGGER.info(String.format("Reduction %d(%d, %d) has database ids %d and %d with demands %d and %d", red, i1, i2, id1, id2, dem1, dem2));
		}
		
		LOGGER.info(logRed);
		LOGGER.info("Coumputing routes:");
		
		boolean allClientsVisited = false;
		boolean startNewRoute = true;//flag to know when a new route should start
		int spaceOccupied = 0;//from truck capacity
		
		while (!allClientsVisited){
			//choosing first unused reduction
			
			for(int i=0;i<reductions.length;i++) {
				int id1 = nodes[0][reductions[i][0]];//database node id
				int id2 = nodes[0][reductions[i][1]];//database node id
				
				int i1 = reductions[i][0];//local id
				int i2 = reductions[i][1];//de otha local id
				
				LOGGER.info(String.format("Building route %d <-> %d", id1, id2));
				
				int crrNode = -1;//the arc that is added to routes
				if(graph.getArcs().containsKey(id1)) {
					crrNode = id2;//I swapped them so I can add the quantity more easily
				}else if(graph.getArcs().containsKey(id2)) {
					crrNode = id1;
				}
				
				if(crrNode == -1 && !startNewRoute) {
					continue;
				}else if(startNewRoute) {
					graph.getArcs().put(nodes[0][0], id1);
					graph.getArcs().put(id1, id2);
					
					spaceOccupied += nodes[1][i1];
					spaceOccupied += nodes[1][i2];
					
					startNewRoute = false;
				}
				
				
				int toAdd = nodes[1][i1] + nodes[1][i2];//add requests to truck
				if(toAdd + spaceOccupied > truckCapacity) {
					LOGGER.info("Cannot add " + toAdd + " to the truck load. Already loaded with " + spaceOccupied + " units\nReturning to depot");
				}else {
					LOGGER.info("Will add " + toAdd + " to truck load. Continuing route");
					spaceOccupied += toAdd;
					graph.getArcs().put(id1, id2);
				}
				
				System.out.println();
				
			}
		}
		
		long t1 = System.currentTimeMillis() - t0;
		LOGGER.info("Clarke-Wright solution computed in " + Calc.mlsToHms(t1));
	}
}
