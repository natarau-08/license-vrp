package utils;

import obj.cvrp.CvrpGraph;
import obj.cvrp.CvrpNode;
import obj.cvrp.CvrpRoute;

public class Calc {

	public static double dist(Point p1, Point p2) {
		double t1 = Math.pow(p1.x - p2.x, 2);
		double t2 = Math.pow(p1.y - p2.y, 2);
		
		return Math.sqrt(t1 + t2);
	}
	
	public static double dist(CvrpNode p1, CvrpNode p2) {
		double t1 = Math.pow(p1.getX() - p2.getX(), 2);
		double t2 = Math.pow(p1.getY() - p2.getY(), 2);
		
		return Math.sqrt(t1 + t2);
	}
	
	public static int calculateCvrpRouteCost(CvrpRoute r, CvrpGraph graph) {
		int cost = 0;
		int first = -1;
		for(Integer i: r.getNodes()) {
			
			if(first == -1) {
				int dep = graph.getDepot().getId();
				cost += graph.getCostValue(dep, i);
				first = i;
				continue;
			}
			
			cost += graph.getCostValue(i, first);
			first = i;
		}
		
		cost += graph.getCostValue(r.getNodes().getLast(), graph.getDepot().getId());
		
		return cost;
	}
	
	public static Point computeRouteCenterOfMass(CvrpRoute r, CvrpGraph g) {
		
		CvrpNode dep = g.getDepot();
		
		//calculating area
		double cx = 0, cy = 0;
		
		int[] nodes = new int[r.getNodes().size() + 2];
		nodes[0] = dep.getId();
		nodes[nodes.length-1] = dep.getId();
		
		int nodesIndex = 1;
		for(Integer i: r.getNodes()) {
			nodes[nodesIndex] = i;
			nodesIndex++;
		}
		
		for(int i=0;i<nodes.length;i++) {
			
			int id = nodes[i];
			
			CvrpNode n = g.getNodes().get(id);
			
			double y = (double) n.getY();
			double x = (double) n.getX();
			
			cx += x;
			cy += y;
		}
		
		cx = cx / (nodes.length);
		cy = cy / (nodes.length);
		
		return new Point(cx, cy);
	}
}
