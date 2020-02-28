package obj.cvrp;

public class CvrpArc {
	
	private CvrpNode node1, node2;
	
	public CvrpArc(CvrpNode node1, CvrpNode node2) {
		this.node1 = node1;
		this.node2 = node2;
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
		if(node1.getId() > node2.getId()) {
			l = node2.getId();
			g = node1.getId();
		}else {
			l = node1.getId();
			g = node2.getId();
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
		}
		
		CvrpArc a = (CvrpArc) obj;
		return a.hashCode() == this.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", node1.getId(), node2.getId());
	}
	
	public CvrpNode getNode1() {
		return node1;
	}
	
	public CvrpNode getNode2() {
		return node2;
	}
}
