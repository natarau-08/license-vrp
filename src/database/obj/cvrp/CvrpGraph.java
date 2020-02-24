package database.obj.cvrp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import static main.Main.LOGGER;

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
	
	public HashMap<Integer, CvrpNode> nodes;
	public HashMap<CvrpArc, CvrpCost> costs;
	public LinkedList<CvrpRoute> routes;
	public CvrpNode depot;
	 
	private String description, name;
	private int id, width, height, nodePadding;
	
	private CvrpGraph(String name, String description, int width, int height, int nodePadding) {
		nodes = new HashMap<>();
		costs = new HashMap<>();
		routes = new LinkedList<>();
		depot = null;
		
		this.name = name;
		this.description = description;
		this.width = width;
		this.height = height;
		this.nodePadding = nodePadding;
	}
	
	public int hashCode() {
		return id;
	}
	
	public void save(Connection connection) {
		try {
			//check if graph exists
			PreparedStatement exists = connection.prepareStatement("SELECT COUNT(*) AS c FROM cvrp_graph WHERE id=?;");
			exists.setInt(1, id);
			ResultSet res = exists.executeQuery();
			int rowsFound = res.getInt("c");
			
			PreparedStatement ps;
			if(rowsFound == 1) {
				ps = connection.prepareStatement("UPDATE cvrp_graph SET name=?, description=?, width=?, height=?, node_padding=? WHERE id=?;");
				ps.setInt(6, id);
			}else {
				ps = connection.prepareStatement("INSERT INTO cvrp_graph(name, description, width, height, node_padding) VALUES(?,?,?,?,?);");
			}
			
			ps.setString(1, name);
			ps.setString(2, description);
			ps.setInt(3, width);
			ps.setInt(4, height);
			ps.setInt(5, nodePadding);
			
			ps.execute();
		}catch(SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
