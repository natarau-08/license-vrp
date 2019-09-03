package database.obj.cvrp;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.SqliteConnection;

public class CvrpCost {

	private int id;
	private int val;
	private CvrpArc arc;
	
	public CvrpCost(int id) throws SQLException{
		ResultSet drs = SqliteConnection.query("SELECT * FROM cvrp_costs WHERE id = ?;", id);
		
		this.val = drs.getInt("val");
		int n1 = drs.getInt("node1");
		int n2 = drs.getInt("node2");
		arc = new CvrpArc(n1, n2);
		this.id = id;
	}
	
	public CvrpArc getArc() {
		return arc;
	}
	
	public CvrpNode getNode(int pos) {
		return arc.getNode(pos);
	}
	
	public int getCost() {
		return val;
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
		return String.format("%d(%d, %d)", val, arc.getNodeId(0), arc.getNodeId(1));
	}
	
}
