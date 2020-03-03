package obj.cvrp;

import static main.Main.LOG;
import static main.Main.connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;

import main.Cfg;
import utils.Calc;
import utils.Point;

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
	private HashMap<CvrpArc, CvrpCost> costs;
	private LinkedList<CvrpRoute> routes;
	private CvrpNode depot;
	private String description, name;
	private int id, width, height;
		
	public CvrpGraph(String name) {
		nodes = new HashMap<>();
		costs = new HashMap<>();
		routes = new LinkedList<>();
		
		this.name = name;
		
		//fetching from database
		try {
			//getting graph data
			PreparedStatement ps = connection.prepareStatement("SELECT id, description, width, height FROM cvrp_graph WHERE name=?;");
			ps.setString(1, name);
			ResultSet res = ps.executeQuery();
			if(res.next()) {
				this.id = res.getInt("id");
				this.description = res.getString("description");
				this.width = res.getInt("width");
				this.height = res.getInt("height");
			}
			res.close();
			ps.close();
		
			//getting depot id
			int depot = -1;
			ps = connection.prepareStatement("SELECT node FROM cvrp_graph_depot WHERE graph=?;");
			ps.setInt(1, id);
			res = ps.executeQuery();
			if(res.next()) {
				depot = res.getInt("node");
			}
			res.close();
			ps.close();
			
			//loading nodes
			ps = connection.prepareStatement("SELECT id, x, y, demand FROM cvrp_node WHERE graph=? ORDER BY id;");
			ps.setInt(1, id);
			res = ps.executeQuery();
			
			while(res.next()) {
				CvrpNode n = new CvrpNode(res.getInt("id"), res.getInt("demand"), res.getInt("x"), res.getInt("y"));
				nodes.put(n.getId(), n);
				
				if(n.getId() == depot) {
					this.depot = n;
				}
			}
			res.close();
			ps.close();
			
			//getting costs
			ps = connection.prepareStatement("SELECT id, val, node1, node2 FROM cvrp_cost WHERE graph=? ORDER BY id;");
			ps.setInt(1, id);
			res = ps.executeQuery();
			
			while(res.next()) {
				int n1 = res.getInt("node1");
				int n2 = res.getInt("node2");
				CvrpArc arc = new CvrpArc(nodes.get(n1), nodes.get(n2));
				CvrpCost cost = new CvrpCost(res.getInt("id"), res.getInt("value"), arc);
				costs.put(arc, cost);
			}
			
			res.close();
			ps.close();
			
		}catch(SQLException sqlex) {
			LOG.log(Level.SEVERE, sqlex.getMessage(), sqlex);
		}
	}
	
	protected CvrpGraph() {
		nodes = new HashMap<>();
		costs = new HashMap<>();
		routes = new LinkedList<>();
	}
	
	public int hashCode() {
		return id;
	}
	
	public HashMap<Integer, CvrpNode> getNodes(){
		return nodes;
	}
	
	public HashMap<CvrpArc, CvrpCost> getCosts(){
		return costs;
	}
	
	public LinkedList<CvrpRoute> getRoutes(){
		return routes;
	}
	
	public CvrpNode getDepot() {
		return depot;
	}
	
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public String getName() {return name;}
	public String getDescription() {return description;}
	
	public int getCostValue(int i, int j) {
		return costs.get(new CvrpArc(nodes.get(i), nodes.get(j))).getValue();
	}
	
	public boolean save() {
		try {
			connection.setAutoCommit(false);
			
			//check if graph exists
			PreparedStatement exists = connection.prepareStatement("SELECT COUNT(*) AS c FROM cvrp_graph WHERE name=?;");
			exists.setString(1, name);
			ResultSet res = exists.executeQuery();
			int rowsFound = res.getInt("c");
			
			PreparedStatement ps;
			if(rowsFound == 1) {
				ps = connection.prepareStatement("UPDATE cvrp_graph SET name=?, description=?, width=?, height=?, node_padding=? WHERE id=?;");
				ps.setInt(6, id);
			}else {
				ps = connection.prepareStatement("INSERT INTO cvrp_graph(name, description, width, height) VALUES(?,?,?,?);");
			}
			
			ps.setString(1, name);
			ps.setString(2, description);
			ps.setInt(3, width);
			ps.setInt(4, height);
			ps.execute();
			ps.close();
			
			if(rowsFound == 0) {
				ps = connection.prepareStatement("SELECT id FROM cvrp_graph WHERE name=?;");
				ps.setString(1, name);
				res = ps.executeQuery();
				res.next();
				this.id = res.getInt("id");
				res.close();
				ps.close();
			}
			
			//clearing database - deleting depot
			ps = connection.prepareStatement("DELETE FROM cvrp_graph_depot WHERE graph=?;");
			ps.setInt(1, id);
			ps.execute();
			ps.close();
			
			//deleting nodes
			ps = connection.prepareStatement("DELETE FROM cvrp_node WHERE graph=?;");
			ps.setInt(1, id);
			ps.execute();
			ps.close();
			
			//deleting costs
			ps = connection.prepareStatement("DELETE FROM cvrp_cost WHERE graph=?;");
			ps.setInt(1, id);
			ps.execute();
			ps.close();
			
			//inserting nodes
			final int batchCount = Cfg.getInt(Cfg.SQL_BATCH_COUNT);
			ps = connection.prepareStatement("INSERT INTO cvrp_node(x, y, demand, graph) VALUES(?,?,?,?);");
			int j = 1;
			for(CvrpNode n: nodes.values()) {
				j++;
				
				ps.setInt(1, n.getX());
				ps.setInt(2, n.getY());
				ps.setInt(3, n.getDemand());
				ps.setInt(4, id);
				
				ps.addBatch();
				
				if(j == batchCount) {
					ps.executeBatch();
					j = 1;
				}
			}
			
			if(j != 1) {
				ps.executeBatch();
			}
			ps.close();
			
			//inserting depot association
			ps = connection.prepareStatement("INSERT INTO cvrp_graph_depot(graph, node) VALUES(?,?);");
			ps.setInt(1,id);
			ps.setInt(2, depot.getId());
			ps.execute();
			ps.close();
			
			//inserting costs
			j = 1;
			ps = connection.prepareStatement("INSERT INTO cvrp_cost(graph, val, node1, node2) VALUES(?,?,?,?);");
			for(CvrpCost c: costs.values()) {
				j++;
				
				ps.setInt(1, id);
				ps.setInt(2, c.getValue());
				ps.setInt(3, c.getArc().getNode1().getId());
				ps.setInt(4, c.getArc().getNode2().getId());
				ps.addBatch();
				
				if(j == batchCount) {
					ps.executeBatch();
					j = 1;
				}
			}
			
			if(j != 1) {
				ps.executeBatch();
			}
			ps.close();
			
			connection.commit();
			connection.setAutoCommit(true);
			return true;
		}catch(SQLException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			try {
				connection.setAutoCommit(true);
			}catch(SQLException sqlex) {
				LOG.log(Level.SEVERE, sqlex.getMessage(), sqlex);
			}
		}
		
		return false;
	}
	
	public static CvrpGraph generateRandom(int nodeCount, String graphName, String graphDescription, int width, int height, int minDemand, int maxDemand, boolean depotInMiddle) {
		LOG.info("Generating Random CVRP Graph\nFetching configurations...");
		int nodeMargin = Cfg.getInt(Cfg.NODE_MARGIN);
		int nodeDiameter = Cfg.getInt(Cfg.NODE_DIAMETER);
		long maxTries = Cfg.getLong(Cfg.MAX_NODE_DRAW_TRIES);
		LOG.info(String.format("Node Options:\nNode Margin: %d, Node Diameter: %d, Max Tries: %d", nodeMargin, nodeDiameter, maxTries));
		
		//the minimum distance between nodes and area boundaries
		int outerRadius = nodeDiameter / 2 + nodeMargin;
		
		Random random = new Random();
		CvrpGraph graph = new CvrpGraph();
		graph.name = graphName;
		graph.description = graphDescription;
		graph.width = width;
		graph.height = height;
		graph.id = 0;
		
		if(depotInMiddle) {
			int demand = minDemand + random.nextInt(maxDemand - minDemand);
			CvrpNode dep = new CvrpNode(graph.nodes.size(), demand, width/2, height/2);
			graph.nodes.put(dep.getId(), dep);
			graph.depot = dep;
		}
		
		for(int i=0;i<nodeCount;i++) {
			boolean validNode = true;
			long tries = 0;
			
			int x, y;
			CvrpNode node;
			
			do {
				x = random.nextInt(width);
				
				if(x - outerRadius <= 0) {
					x += (x - outerRadius) * -1;
				}
				
				if(x + outerRadius >= width) {
					x -= outerRadius + (x + outerRadius - width);
				}
				
				y = random.nextInt(height);
				
				if(y - outerRadius <= 0) {
					y += (y - outerRadius) * -1;
				}
				
				if(y + outerRadius >= height) {
					y -= outerRadius + (y + outerRadius - height);
				}
				
				//Point pc = new Point(x, y);
				node = new CvrpNode(graph.nodes.size(), minDemand + random.nextInt(maxDemand - minDemand), x, y);
				tries++;
				
				for(CvrpNode n: graph.nodes.values()) {
					Point p1 = new Point(n.getX(), n.getY());
					Point p2 = new Point(node.getX(), node.getY());
					
					if(Calc.dist(p1, p2) < outerRadius * 2) {
						validNode = false;
						break;
					}
				}
				
				if(tries > maxTries) {
					LOG.info(String.format("Cannot generate CvrpGraph. Tried %d times to generate node and no space could be found.", tries));
					throw new RuntimeException("\n\nFATAL ERROR:\nREACHED MAX TRIES FOR GENERATING A NODE FOR CVRP GRAPH");
				}
				
			}while(!validNode);
			
			graph.nodes.put(node.getId(), node);
			
		}
		
		if(!depotInMiddle) {
			CvrpNode dep = graph.nodes.get(random.nextInt(graph.nodes.size()));
			graph.depot = dep;
		}
		
		if(graph.save()) {
			return new CvrpGraph(graphName);
		}
		
		return null;
	}
}
