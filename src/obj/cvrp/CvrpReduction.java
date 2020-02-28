package obj.cvrp;

import java.util.HashMap;

public class CvrpReduction {
	
	private int value;
	private CvrpArc arc;
	
	public CvrpReduction(CvrpGraph graph, CvrpArc arc) {
		this.arc = arc;
		
		HashMap<CvrpArc, CvrpCost> costs = graph.getCosts();
		
		/*Reduction value = c_di + c_jd - c_ij*/
		value = costs.get(new CvrpArc(graph.getDepot(), arc.getNode1())).getValue() +
				costs.get(new CvrpArc(graph.getDepot(), arc.getNode2())).getValue() -
				costs.get(new CvrpArc(arc.getNode1(), arc.getNode2())).getValue();
	}
	
	public int getValue() {
		return value;
	}
	
	public CvrpArc getArc() {
		return arc;
	}
	
	@Override
	public String toString() {
		return String.format("%d%s", value, arc.toString());
	}
}
