package generator;

import static main.Main.LOGGER;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Random;

import database.Query;
import database.SqliteConnection;
import database.obj.cvrp.CvrpGraph;
import utils.Calc;
import utils.Point;

public class CapacityGraphGenerator {
	
	public Random random;
	public int boundX, boundY;
	
	public static final int MAX_NODE_TRIES = 1000;
	
	protected CapacityGraphGenerator(Random random, int bx, int by) {
		this.random = random;
		this.boundX = bx;
		this.boundY = by;
	}
	
	private Point getRandomPoint() {
		 return new Point(random.nextInt(boundX), random.nextInt(boundY));
	}
	
	/**
	 * [draw] - useful for drawing
	 * @param graph - the graph
	 * @param nodeCount - how many clients
	 * @param minCost - minimum cost a route between two nodes can have
	 * @param maxCost - 
	 * @param minDemand -
	 * @param maxDemand -
	 * @param distMultiplier - [draw] how much of the distance is added to the cost 
	 * @throws Exception  if anything goes wrong. Bad practice.
	 * 
	 */
	public static void generateCvrpGraph(CvrpGraph graph, int nodeCount, 
			int minCost, int maxCost, int minDemand, int maxDemand,  
			float distMultiplier, boolean depotInMiddle) throws Exception{		
		
		int gId = graph.hashCode();
		
		Random rand = new Random();
		CapacityGraphGenerator cgg = new CapacityGraphGenerator(rand, graph.getWidth(), graph.getHeight());
		
		//we'll need all positions
		LinkedList<Point> nodesPos = new LinkedList<Point>();
		
		Point p;
		//make the depot
		if(depotInMiddle) {
			p = new Point(graph.getWidth()/2,graph.getHeight()/2);
		}else {
			p = cgg.getRandomPoint();
		}
		
		nodesPos.add(p);
		
		LOGGER.info("Creating depot at " + p);
		
		Query nodeInsert = new Query("INSERT INTO cvrp_nodes(posx, posy, demand, graph) VALUES(?, ?, ?, ?);", (int)p.x, (int)p.y, 0, gId);
		
		nodeInsert.execute();
		
		LOGGER.info("Depot created");
		LOGGER.info("Generating " + nodeCount + " nodes");
		
		int nodesToAdd = nodeCount;
		int tries = 0;
		
		fp: while(nodesToAdd != 0) {
			tries++;
			
			p = cgg.getRandomPoint();
			LOGGER.info("Attemting to make node at " + p);
			
			if(tries > MAX_NODE_TRIES) throw new Exception("[Node] There is no more space to generate more nodes");
			
			for(Point q : nodesPos) {
				double dist = Calc.dist(p, q);
				if(graph.getMinDist() > dist) {
					LOGGER.info("Cannot place node at " + p + ". Another node is too close. Min dist: " + graph.getMinDist());
					LOGGER.info("Tries left: " + (MAX_NODE_TRIES - tries));
					continue fp;
				}
			}
			
			LOGGER.info("Node can be placed at " + p);
			LOGGER.info("Computing demand");
			
			int demand = minDemand + rand.nextInt(maxDemand - minDemand);
			
			LOGGER.info("Demand for " + p + " node is " + demand);
			
			nodeInsert.executeWith((int)p.x, (int)p.y, demand, gId);
			
			LOGGER.info(p + " node created");
			
			nodesPos.add(p);
			tries = 0;
			nodesToAdd --;
			
			LOGGER.info("Remaining nodes to add " + nodesToAdd);
		}
		
		//nodes are added at this point, the hard part is to generate costs
		ResultSet nodes = SqliteConnection.query("SELECT id, posx, posy FROM cvrp_nodes WHERE graph = ? ORDER BY id;", gId);
		
		nodesPos = new LinkedList<Point>();
		
		while (nodes.next()) {
			int x = nodes.getInt("posx");
			int y = nodes.getInt("posy");
			int id = nodes.getInt("id");
			
			p = new Point(x, y, id);
			
			nodesPos.add(p);
		}
		
		LOGGER.info("Generating costs");
		Query insertCost = new Query("INSERT INTO cvrp_costs(val, graph, node1, node2) VALUES(?, ?, ?, ?);");
		
		for(int i=0;i<nodesPos.size()-1;i++) {
			for(int j=i+1;j<nodesPos.size();j++) {
				
				Point r = nodesPos.get(i);
				Point s = nodesPos.get(j);
				
				double dist = Calc.dist(r, s);
				
//				if(toAdd > minCost) {
//					toAdd = minCost;
//				}
				
				int costValue = (int) (dist * distMultiplier);
				/*
				 * TODO complex costs
				 */
				
				insertCost.executeWith(costValue, gId, s.z, r.z);
				
				LOGGER.info(String.format("Generated cost value %d for pair (%s, %s)", costValue, r, s));
			}
		}
	}
}
