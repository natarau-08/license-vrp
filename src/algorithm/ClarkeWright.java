package algorithm;

import static main.Main.LOGGER;

import java.util.Comparator;
import java.util.LinkedList;

import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
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
	public static void baseCvrp(CvrpGraph graph, int truckCapacity, int alg) throws Exception{
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
		
		long t1 = System.currentTimeMillis() - t0;
		LOGGER.info("Clarke-Wright solution computed in " + Calc.mlsToHms(t1));
	}

	public static void oopCvrp(CvrpGraph graph, int vehicleCapacity) {
		//fetching demands
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		nodes.sort((CvrpNode n1, CvrpNode n2) -> n2.getDemand() - n1.getDemand());
		
		
	}
}
