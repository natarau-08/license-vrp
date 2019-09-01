package database.obj.cvrp;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.SqliteConnection;

public class CvrpCost {

	private int val;
	private int n1;
	private int n2;
	
	public CvrpCost(int id) throws SQLException{
		ResultSet drs = SqliteConnection.query("SELECT * FROM cvrp_costs WHERE id = ?;", id);
		
		this.val = drs.getInt("val");
		n1 = drs.getInt("node1");
		n2 = drs.getInt("node2");
	}
	
	public int[] getNodesIndexes() {
		return new int[] {n1, n2};
	}
	
	public int getCost() {
		return val;
	}
}
