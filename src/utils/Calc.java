package utils;

import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
import database.obj.cvrp.CvrpRoute;

public class Calc {

	public static double dist(Point p1, Point p2) {
		double t1 = Math.pow(p1.x - p2.x, 2);
		double t2 = Math.pow(p1.y - p2.y, 2);
		
		return Math.sqrt(t1 + t2);
	}
	
	public static String mlsToHms(long mls) {
		double seconds = mls / 1000;		
		double minutes = 0;
		double hours = 0.0;
		
		if(seconds > 60) {
			minutes = seconds / 60.0;
			seconds = (minutes - Math.floor(minutes)) * 60;
		}
		
		if(minutes > 60) {
			hours = minutes / 60.0;
			minutes = (hours - Math.floor(hours)) * 60.0;
		}
		
		return String.format("%d:%d:%d", (int)hours, (int)minutes, (int)seconds);
	}
	
	public static int calculateCvrpRouteCost(CvrpRoute r, CvrpGraph graph) {
		int cost = 0;
		int first = -1;
		for(Integer i: r.getNodes()) {
			
			if(first == -1) {
				int dep = graph.getDepot().getId();
				cost += graph.getCost(dep, i).getValue();
				first = i;
				continue;
			}
			
			cost += graph.getCost(i, first).getValue();
			first = i;
		}
		
		cost += graph.getCost(r.getNodes().getLast(), graph.getDepot().getId()).getValue();
		
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
