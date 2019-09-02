package database.obj.cvrp;

import static main.Main.LOGGER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import database.Query;
import database.SqliteConnection;

/**
 * Represents a graph in which the following constraints are applied:
 * <ul>
 * 	<li>Every client has a request</li>
 * 	<li>Every path between two nodes has a cost</li>
 * 	<li>Vehicles have limited capacity</li>
 * </ul>
 * @author Alexandru
 */
public class CvrpGraph {
	
	private HashMap<Integer, CvrpNode> nodes;
	private HashMap<Integer, CvrpCost> costs;
	private HashMap<Integer, Integer> arcs;
	
	private String description, name;
	private int id, width, height, minDist;
	
	public CvrpGraph(int index) throws SQLException {
		
		LOGGER.info("Fetching cvrp_graph from database. Index: " + index);
		
		ResultSet grs = SqliteConnection.query("SELECT * FROM cvrp_graphs WHERE id = ?;", index);
		
		if(!grs.next()) throw new SQLException("No CvrpGraph found for id" + index);
		
		description = grs.getString("description");
		name = grs.getString("name");
		id = grs.getInt("id");
		width = grs.getInt("width");
		height = grs.getInt("height");
		minDist = grs.getInt("mdist");
		
		LOGGER.info("Fetching completed");
		LOGGER.info("Fetching all nodes");
		
		ResultSet nrs = SqliteConnection.query("SELECT id FROM cvrp_nodes WHERE graph = ?;", index);
		
		nodes = new HashMap<>();
		costs = new HashMap<>();
		arcs = new HashMap<>();
		
		while (nrs.next()) {
			int id = nrs.getInt("id");
			CvrpNode n = new CvrpNode(id);
			nodes.put(id, n);
		}
		
		LOGGER.info("All nodes fetched");
		LOGGER.info("Fetching all costs");
		
		ResultSet crs = SqliteConnection.query("SELECT id FROM cvrp_costs WHERE graph = ?;", index);
		
		while (crs.next()) {
			int id = crs.getInt("id");
			costs.put(id, new CvrpCost(id));
		}
		
		
		
		LOGGER.info("All Costs fetched. CvrpGraph loading is complete");
	}
	
	public HashMap<Integer, CvrpNode> getNodes(){
		return nodes;
	}
	
	public HashMap<Integer, CvrpCost> getCosts(){
		return costs;
	}
	
	public HashMap<Integer, Integer> getArcs(){
		return arcs;
	}
	
	public int getId() {
		return id;
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

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getMinDist() {
		return minDist;
	}
	
	public LinkedList<CvrpNode> getNodesAsList(){
		LinkedList<CvrpNode> list = new LinkedList<>();
		
		for(Map.Entry<Integer, CvrpNode> entry : nodes.entrySet()) {
			list.add(entry.getValue());
		}
		
		return list;
	}
	
	public LinkedList<CvrpCost> getCostsAsList(){
		LinkedList<CvrpCost> list = new LinkedList<>();
		
		for(Map.Entry<Integer, CvrpCost> entry : costs.entrySet()) {
			list.add(entry.getValue());
		}
		
		return list;
	}
	
	public int getNodeCount() {
		return nodes.size();
	}
	
	public static int createCvrpGraph(String name, String description, int width, int height, int minDist) throws SQLException{
		
		Query nameQuery = new Query("SELECT COUNT(*) AS c FROM cvrp_graphs WHERE name LIKE ?;", name);
		nameQuery.execute();
		
		if (nameQuery.getResultSet().getInt("c") != 0) {
			throw new SQLException("There is already a graph named " + name);
		}
		
		SqliteConnection.query("INSERT INTO cvrp_graphs (name, description, width, height, mdist) VALUES(?, ?, ?, ?, ?);", name, description, width, height, minDist);
		
		nameQuery.execute();
		
		if (nameQuery.getResultSet().getInt("c") == 0) {
			throw new SQLException("Cannot create graph " + name);
		}
		
		return -1;
	}
	
	/**
	 * Constructs a CvrpGraph object with properties from database by the provided unique parameter<b>name</b>
	 * @param name - unique graph name in database
	 * @return - new CvrpObject
	 * @throws SQLException when there is no such name in database table
	 */
	public static CvrpGraph getGraphByName(String name) throws SQLException{
		ResultSet idSet = SqliteConnection.query("SELECT id FROM cvrp_graphs WHERE name LIKE ?;", name);
		
		if(idSet.next() == false) {
			throw new SQLException("No such CvrpGraph named " + name);
		}
		
		int id = idSet.getInt("id");
		
		return new CvrpGraph(id);
	}
	
	public int hashCode() {
		return id;
	}
}
