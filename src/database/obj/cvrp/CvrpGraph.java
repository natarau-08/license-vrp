package database.obj.cvrp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import database.Query;
import database.SqliteConnection;

public class CvrpGraph {
	
	private HashMap<Integer, CvrpNode> nodes;
	private HashMap<Integer, CvrpCost> costs;
	
	private String description, name;
	private int id;
	
	public CvrpGraph(int index) throws Exception {
		
		ResultSet grs = SqliteConnection.query("SELECT * FROM cvrp_graphs WHERE id = ?;", index);
		
		description = grs.getString("description");
		name = grs.getString("name");
		id = grs.getInt("id");
		
		ResultSet nrs = SqliteConnection.query("SELECT id FROM cvrp_nodes WHERE graph = ?;", index);
		
		while (nrs.next()) {
			int id = nrs.getInt("id");
			CvrpNode n = new CvrpNode(id);
			nodes.put(id, n);
		}
		
		ResultSet crs = SqliteConnection.query("SELECT cc.id AS ccid FROM cvrp_costs cc JOIN cvrp_nodes cn ON cc.node1 = cn.id WHERE cn.graph = ?", index);
		
		while (crs.next()) {
			int id = crs.getInt("ccid");
			costs.put(id, new CvrpCost(id, this));
		}
	}
	
	public int getId() {
		return id;
	}

	public CvrpNode getNode(int key) {
		return nodes.get(key);
	}
	
	public CvrpCost getCost(int key) {
		return costs.get(key);
	}
		
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) throws SQLException{
		SqliteConnection.query("UPDATE cvrp_graphs SET description = ? WHERE name LIKE = ?;", description, name);
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws SQLException{
		SqliteConnection.query("UPDATE cvrp_graphs SET name = ? WHERE name LIKE = ?;", name, name);
		this.name = name;
	}

	public static int createNewCvrpGraph(String name, String description) throws Exception{
		
		Query nameQuery = new Query("SELECT COUNT(*) AS c FROM cvrp_graphs WHERE name LIKE ?;", name);
		nameQuery.execute();
		
		if (nameQuery.getResultSet().getInt("c") != 0) {
			throw new Exception("There is already a graph named " + name);
		}
		
		SqliteConnection.query("INSERT INTO cvrp_graphs (name, description) VALUES(?, ?);", name, description);
		
		nameQuery.execute();
		
		if (nameQuery.getResultSet().getInt("c") == 0) {
			throw new Exception("Cannot create graph " + name);
		}
		
		return -1;
	}
	
	public static CvrpGraph getGraphByName(String name) throws Exception{
		ResultSet idSet = SqliteConnection.query("SELECT id FROM cvrp_graphs WHERE name LIKE ?;", name);
		
		int id = idSet.getInt("id");
		if(id == 0) throw new Exception ("There is no CvrpGraph named " + name + " in database");
		
		return new CvrpGraph(id);
	}
}
