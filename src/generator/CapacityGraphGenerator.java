package generator;

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
	
	private CapacityGraphGenerator(Random random, int bx, int by) {
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
	 * @param minDist - [draw] minimum distance between clients
	 * @param distMultiplier - [draw] how much of the distance is added to the cost 
	 * @param spreadX - [draw] from 0 to right
	 * @param spreadY - [draw]
	 * @throws Exception  if anything goes wrong. Bad practice.
	 * 
	 */
	public static void generateCvrpGraph(CvrpGraph graph, int nodeCount, int minCost, int maxCost, int minDemand, int maxDemand,  int minDist, float distMultiplier, int spreadX, int spreadY) throws Exception{
		int gId = graph.getId();
		
		Random rand = new Random();
		CapacityGraphGenerator cgg = new CapacityGraphGenerator(rand, spreadX, spreadY);
		
		
		
		//we'll need all positions
		LinkedList<Point> nodesPos = new LinkedList<Point>();
		
		//make the depot
		Point p = cgg.getRandomPoint();
		
		nodesPos.add(p);
		
		Query nodeInsert = new Query("INSERT INTO cvrp_nodes(posx, posy, demand, graph) VALUES(?, ?, ?, ?);", (int)p.x, (int)p.y, 0, gId);
		
		nodeInsert.execute();
		
		int nodesToAdd = nodeCount;
		int tries = 0;
		
		fp: while(nodesToAdd != 0) {
			tries++;
			
			p = cgg.getRandomPoint();
			
			if(tries > 1000) throw new Exception("[Node] There is no more space to generate more nodes");
			
			for(Point q : nodesPos) {
				double dist = Calc.dist(p, q);
				if(minDist > dist) {
					continue fp;
				}
			}
			
			int demand = minDemand + rand.nextInt(maxDemand - minDemand);
			
			nodeInsert.executeWith((int)p.x, (int)p.y, demand, gId);
			
			nodesPos.add(p);
			tries = 0;
			nodesToAdd --;
			
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
		
		Query insertCost = new Query("INSERT INTO cvrp_costs(val, node1, node2) VALUES(?, ?, ?);");
		
		for(Point r: nodesPos) {
			for(Point s: nodesPos) {
				double dist = Calc.dist(r, s);
				int toAdd = (int)(dist * distMultiplier);
				
				if(toAdd > minCost || true) {
					toAdd = minCost;
				}
				int costValue = minCost - toAdd + rand.nextInt(maxCost - minCost + toAdd);
				
				insertCost.executeWith(s.z, r.z, costValue);
			}
		}
	}
}
