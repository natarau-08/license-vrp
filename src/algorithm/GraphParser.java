package algorithm;

import static main.Main.LOG;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import database.SqliteConnection;
import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
/**
 * Transforms graphs into a set of matrices
 * @author Alexandru
 */
public class GraphParser {

	/**
	 * Converts database objects into matrices for CvrpGraph<br>
	 * Matrix demand is 2:n<br>
	 * Cost demand is n:n<br>
	 * n - number of nodes in graph
	 * @return - array of matrices. First matrix contains the database id's and the demands. The second contains travel costs
	 */
	public static int[][][] parseCvrpGraph(CvrpGraph graph) throws Exception {
		LinkedList<CvrpNode> nodesList = graph.getNodesAsList();
		
		String demandLog = "";
		
		int[][] nodes = new int[2][nodesList.size()];
		for(int i=0;i<nodesList.size();i++) {
			nodes[0][i] = nodesList.get(i).getId();
			nodes[1][i] = nodesList.get(i).getDemand();
			demandLog += nodes[1][i] + "\t";
		}
		
		int[][] costs = new int[nodesList.size()][nodesList.size()];
		String costLog = "";
		
		for(int i=0;i<costs.length;i++) {
			for(int j=0;j<costs[i].length;j++) {
				if(i<j) {
					int n1id = nodes[0][i];
					int n2id = nodes[0][j];
					
					//should change to nonquery
					ResultSet costValue = SqliteConnection.query("SELECT val FROM cvrp_costs WHERE (node1 = ? AND node2 = ?) OR (node1 = ? AND node2 = ?)", n1id, n2id, n2id, n1id);
					
					if(!costValue.next()) {
						throw new SQLException("Could not find cost of nodes (" + n1id + ", " + n2id + ")");
					}
					
					costs[i][j] = costValue.getInt("val");
					
					if(costValue.next()) {
						throw new SQLException("Found more than one cost of nodes (" + n1id + ", " + n2id + ")");
					}
					
					costLog += costs[i][j] + "\t";
				}else {
					costLog += "-\t";
				}
			}
			
			costLog += "\n";
		}
		
		LOG.info("Demand vector:\n" + demandLog);
		LOG.info("Cost matrix:\n" + costLog);
		return new int[][][] {nodes, costs};
	}
}
