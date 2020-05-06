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
import utils.Utils;
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
	private int id, width, height, nodeMargin, nodePadding;
	
	public Point lastFailedNode;
	
 	public CvrpGraph(String name) {
		nodes = new HashMap<>();
		costs = new HashMap<>();
		routes = new LinkedList<>();
		
		this.name = name;
		
		//fetching from database
		try {
			//getting graph data
			PreparedStatement ps = connection.prepareStatement("SELECT id, description, width, height, margin, node_diameter FROM cvrp_graph WHERE name=?;");
			ps.setString(1, name);
			ResultSet res = ps.executeQuery();
			if(res.next()) {
				this.id = res.getInt("id");
				this.description = res.getString("description");
				this.width = res.getInt("width");
				this.height = res.getInt("height");
				this.nodeMargin = res.getInt("margin");
				this.nodePadding = res.getInt("node_diameter");
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
	
	public CvrpGraph(String name, String description) {
		nodes = new HashMap<>();
		costs = new HashMap<>();
		routes = new LinkedList<>();
		
		this.name = name;
		this.description = description;
		this.nodeMargin = Cfg.getInt(Cfg.NODE_MARGIN);
		this.nodePadding = Cfg.getInt(Cfg.NODE_PADDING);
		this.width = Cfg.getInt(Cfg.GRAPH_WIDTH);
		this.height = Cfg.getInt(Cfg.GRAPH_HEIGHT);
		this.id = 0;
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
	public int getNodePadding() {return nodePadding;}
	public int getNodeMargin() {return nodeMargin;}
	
	public int getCostValue(int i, int j) {
		return costs.get(new CvrpArc(nodes.get(i), nodes.get(j))).getValue();
	}
	
	public boolean save() {
		try {
			connection.setAutoCommit(false);
			
			//check if graph exists
			PreparedStatement exists = connection.prepareStatement("SELECT id FROM cvrp_graph WHERE name=?;");
			exists.setString(1, name);
			ResultSet res = exists.executeQuery();
			int rowsFound = -1;
			if(res.next()) {
				rowsFound = 1;
				id = res.getInt("id");
			}else {
				rowsFound = 0;
			}
			
			PreparedStatement ps;
			if(rowsFound == 1) {
				ps = connection.prepareStatement("UPDATE cvrp_graph SET name=?, description=?, width=?, height=?, node_diameter=?, margin=? WHERE id=?;");
				ps.setInt(7, id);
			}else {
				ps = connection.prepareStatement("INSERT INTO cvrp_graph(name, description, width, height, node_diameter, margin) VALUES(?,?,?,?,?,?);");
			}
			
			ps.setString(1, name);
			ps.setString(2, description);
			ps.setInt(3, width);
			ps.setInt(4, height);
			ps.setInt(5, nodePadding);
			ps.setInt(6, nodeMargin);
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
	
	public static void generateRandom(int nodeCount, int minDemand, int maxDemand, boolean depotInMiddle, CvrpGraph graph) {
		LOG.info("Generating Random CVRP Graph\nFetching configurations...");
		final long maxTries = Cfg.getLong(Cfg.MAX_NODE_DRAW_TRIES);
		final int nodeSleep = Cfg.getInt(Cfg.ON_NODE_GENERATED_SLEEP);
		final int nodeFailSleep = Cfg.getInt(Cfg.ON_NODE_FAILED_SLEEP);
		final int marginCorrect = Cfg.getInt(Cfg.NODE_MARGIN_DAMP);
		LOG.info(String.format("Node Options:\nNode Margin: %d, Node Padding: %d, Max Tries: %d", graph.nodeMargin, graph.nodePadding, maxTries));
		
		Random random = new Random();
		if(depotInMiddle) {
			int demand = minDemand + random.nextInt(maxDemand - minDemand);
			CvrpNode dep = new CvrpNode(graph.nodes.size(), demand, graph.getWidth()/2, graph.getHeight()/2);
			graph.nodes.put(dep.getId(), dep);
			graph.depot = dep;
		}
		
		for(int i=0;i<nodeCount - (depotInMiddle ? 1 : 0);i++) {
			
			long tries = 0;
			
			CvrpNode node;
			boolean validNode = true;
			do {
				// generate random coordinates and clamp coordinates to edges
				double x = random.nextDouble() * (double)graph.getWidth();
				
				if(x - graph.nodeMargin <= 0) {
					x = graph.nodeMargin;
				}
				
				if(x + graph.nodeMargin >= graph.getWidth()) {
					x = graph.getWidth() - graph.nodeMargin;
				}
				
				double y = random.nextDouble() * graph.getHeight();
				
				if(y - graph.nodeMargin <= 0) {
					y = graph.nodeMargin;
				}
				
				if(y + graph.nodeMargin >= graph.getHeight()) {
					y = graph.getHeight() - graph.nodeMargin;
				}
				
				node = new CvrpNode(graph.nodes.size(), minDemand + random.nextInt(maxDemand - minDemand), (int)x, (int)y);
				tries++;
				
				// check if generated node is too close to another node
				validNode = true;
				for(CvrpNode n: graph.nodes.values()) {
					Point p1 = new Point(n.getX(), n.getY());
					Point p2 = new Point(x, y);
					
					double d = Utils.dist(p1, p2);
					
					if(d < graph.nodeMargin - marginCorrect) {
						validNode = false;
						graph.lastFailedNode = p2;
						
						LOG.info(String.format("Cannot create node at %s, already a node at %s. Distance between is %s. Try #%d", p2, p1, (int)d, tries));
						
						if(nodeFailSleep != 0) {
							try {
								Thread.sleep(nodeFailSleep);
							}catch(Exception e) {
								e.printStackTrace();
							}
						}
						
						break;
					}
				}
				
				if(tries > maxTries && maxTries != 0) {
					LOG.info(String.format("Cannot generate CvrpGraph. Tried %d times to generate node and no space could be found.", tries));
					throw new RuntimeException("\n\nFATAL ERROR:\nREACHED MAX TRIES FOR GENERATING A NODE FOR CVRP GRAPH");
				}
				
			}while(!validNode);
			
			if(nodeSleep != 0) {
				try {
					Thread.sleep(nodeSleep);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			graph.nodes.put(node.getId(), node);
		}
		
		//TODO - genereate costs between nodes
		// costs will be equal to distance
		// cost from i to j is equal to cost from j to i so only one pair will be generated
		for(int i=0;i<graph.nodes.size() - 1;i++) {
			for(int j=i+1;j<graph.nodes.size();j++) {
				CvrpNode n1 = graph.nodes.get(i);
				CvrpNode n2 = graph.nodes.get(j);
				
				CvrpArc arc = new CvrpArc(n1, n2);
				CvrpCost cost = new CvrpCost(graph.costs.size(), (int)Utils.dist(n1, n2), arc);
				graph.costs.put(arc, cost);
			}
		}
		
		if(!depotInMiddle) {
			CvrpNode dep = graph.nodes.get(random.nextInt(graph.nodes.size()));
			graph.depot = dep;
		}
	}
}
