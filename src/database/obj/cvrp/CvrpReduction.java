package database.obj.cvrp;
import static database.obj.cvrp.CvrpGraph.GRAPH;

public class CvrpReduction {
	
	private int value;
	private CvrpArc arc;
	
	public CvrpReduction(CvrpArc arc) {
		this.arc = arc;
		calcValue();
	}
	
	public CvrpReduction(int id1, int id2) {
		this.arc = new CvrpArc(id1, id2);
		calcValue();
	}
	
	private void calcValue() {
		int depId = GRAPH.getDepot().getId();
		/**
		 * reduction value is c_di + c_jd - c_ij
		 */
		value = GRAPH.getCosts().get(new CvrpArc(depId, arc.getNodeId(0))).getValue() + 
				GRAPH.getCosts().get(new CvrpArc(depId, arc.getNodeId(1))).getValue() -
				GRAPH.getCosts().get(new CvrpArc(arc.getNodeId(1), arc.getNodeId(0))).getValue();
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
