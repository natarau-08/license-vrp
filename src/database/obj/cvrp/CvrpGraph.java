package database.obj.cvrp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

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
			PreparedStatement ps = connection.prepareStatement("INSERT INTO cvrp_graph(name, description, width, height, node_padding) VALUES(?,?,?,?,?);");
			//TODO save Cvrp Graph to db
		}catch(SQLException e) {
			
		}
	}
	
}
