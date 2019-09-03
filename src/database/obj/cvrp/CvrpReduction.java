package database.obj.cvrp;

import java.util.HashMap;
import static database.obj.cvrp.CvrpGraph.GRAPH;

public class CvrpReduction {
	
	private int value;
	private CvrpArc arc;
	
	public CvrpReduction(CvrpArc arc) {
		
		this.arc = arc;
		HashMap<CvrpArc, CvrpCost> costs = GRAPH.getCosts();
		int depId = GRAPH.getDepot().getId();
		int n1Id = arc.getNodeId(0);
		int n2Id = arc.getNodeId(1);
		
		//costs from depot to first node and back + cost from depot to second node and back
		int sepCost = costs.get(new CvrpArc(depId, n1Id)).getCost() * 2 + costs.get(new CvrpArc(depId, n2Id)).getCost() * 2;
		
		//cost fro depot to first node + cost from first node to second node + cost from second node to depot
		int redCost = costs.get(new CvrpArc(depId, n1Id)).getCost() + costs.get(new CvrpArc(n1Id, n2Id)).getCost() + costs.get(new CvrpArc(n2Id, depId)).getCost();
		
		//reduction is sepCost - redCost (should always be positive)
		value = sepCost - redCost;
	}
	
	public int getValue() {
		return value;
	}
	
	public CvrpArc getArc() {
		return arc;
	}
	
	/**
	 * @param pos - the position of the node in arc. Can be 0 or 1
	 * @return
	 */
	public CvrpNode getNode(int pos) {
		return arc.getNode(pos);
	}
	
	@Override
	public String toString() {
		return String.format("%d%s", value, arc.toString());
	}
}
