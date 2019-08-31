package database.obj.cvrp;

import java.sql.ResultSet;

import database.SqliteConnection;

public class CvrpCost {

	private int val;
	private CvrpNode n1;
	private CvrpNode n2;
	
	public CvrpCost(int id, CvrpGraph graph) throws Exception{
		ResultSet drs = SqliteConnection.query("SELECT * FROM cvrp_costs WHERE id = ?;", id);
		
		this.val = drs.getInt("val");
		n1 = graph.getNode(drs.getInt("node1"));
		n2 = graph.getNode(drs.getInt("node2"));
	}
}
