package obj.cvrp;

public class CvrpCost {

	private int id;
	private int value;
	private CvrpArc arc;
	
	public CvrpCost(int id, int value, CvrpArc arc) {
		this.id = id;
		this.value = value;
		this.arc = arc;
	}
	
	public CvrpArc getArc() {
		return arc;
	}
	
	public int getValue() {
		return value;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CvrpCost) {
			return ((CvrpCost)obj).id == id;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("%d(%d, %d)", value, arc.getNode1().getId(), arc.getNode2().getId());
	}
	
}
