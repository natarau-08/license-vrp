package database.obj.cvrp;

public class CvrpNode {

	private int id;
	private int demand;
	private int x;
	private int y;
	
	public CvrpNode(int id, int demand, int x, int y) {
		this.id = id;
		this.demand = demand;
		this.x = x;
		this.y = y;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getDemand() {
		return this.demand;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CvrpNode) {
			return ((CvrpNode)obj).id == id;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%d(%d)", id, demand);
	}
	
	
}
