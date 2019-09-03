package database.obj.cvrp;

import static database.obj.cvrp.CvrpGraph.GRAPH;

public class CvrpArc {
	
	private int id1, id2;
	
	public CvrpArc(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
	}
	
	public CvrpArc(CvrpNode n1, CvrpNode n2) {
		this.id1 = n1.getId();
		this.id2 = n2.getId();
	}
	
	public CvrpArc(CvrpReduction red) {
		this.id1 = red.getNode(0).getId();
		this.id2 = red.getNode(1).getId();
	}
	
	public CvrpNode getNode(int index) {
		if(index == 0) return GRAPH.getNodes().get(id1);
		return GRAPH.getNodes().get(id2);
	}
	
	public int getNodeId(int index) {
		if(index == 0) return id1;
		return id2;
	}

	/**
	 * Hash code for this object is computed with formula <em>(l + g)*(l + g + 1)/2 + g</em>
	 * where l is the lower node index and g is the greater node index.
	 * The formula gives a unique integer. In this case the formula is used
	 *  with ordering the node id's so that hashCode() for arc 
	 *  (i, j) equals hashCode() for arc (j, i) 
	 */
	@Override
	public int hashCode() {
		//always lesser + greater!
		int l, g;
		if(id1 > id2) {
			l = id2;
			g = id1;
		}else {
			l = id1;
			g = id2;
		}
		
		return (l + g)*(l + g + 1)/2 + g;
	}

	/**
	 * Assuming this arc has pair nodes (i, j), 
	 * the given arc must have pair (i,j) or (j, i) 
	 * for this to return true.
	 */
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof CvrpArc) ) {
			return false;
		}else {
			CvrpArc a = (CvrpArc) obj;
			if((a.id1 == id1 && a.id2 == id2) || (a.id2 == id1 && a.id1 == id2)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", id1, id2);
	}
}
