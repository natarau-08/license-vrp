package database.obj.cvrp;

import java.util.Collection;
import java.util.LinkedList;
import static database.obj.cvrp.CvrpGraph.GRAPH;
public class CvrpRoute {

	private LinkedList<Integer> nodes;
	private int load;
	
	public CvrpRoute() {
		nodes = new LinkedList<>();
		
		load = 0;
	}
	
	public CvrpRoute(Collection<Integer> c) {
		nodes = new LinkedList<>();
		nodes.addAll(c);
		load = 0;
		for(Integer i: c) {
			load += GRAPH.getNodes().get(i).getDemand();
		}
	}
	
	public CvrpRoute(Integer ...n) {
		nodes = new LinkedList<>();
		load = 0;
		for(Integer i: n) {
			nodes.add(i);
			load += GRAPH.getNodes().get(i).getDemand();
		}
	}
	
	/**
	 * @return - first element of this route
	 */
	public int getFirst() {
		return nodes.getFirst();
	}
	
	public int getLast() {
		return nodes.getLast();
	}
	
	public void addLast(int e) {
		nodes.add(e);
		load += GRAPH.getNodes().get(e).getDemand();
	}
	
	public void addFirst(int e) {
		nodes.addFirst(e);
		load += GRAPH.getNodes().get(e).getDemand();
	}
	
	public boolean contains(int e) {
		return nodes.contains(e);
	}

	public void add(int e) {
		nodes.add(e);
		load += GRAPH.getNodes().get(e).getDemand();
	}
	
	public void setLoad(int load) {
		this.load = load;
	}
	
	public int getLoad() {
		return load;
	}
	
	public void addToLoad(int e) {
		load += e;
	}
	
	/**<B><H1>!!!!! Move this to ClarkeWright !!!!!</H1></B>
	 * Attempts to merge this route with another route. 
	 * It fails if the routes have no common nodes(first or last). 
	 * It fails if the quantity exceeds vehicleCapacity. 
	 * @param r - the other route
	 * @param vehicleCapacity
	 * @return - true if merging was successful, false otherwise
	 */
	public boolean mergeRoute(CvrpRoute r, int vehicleCapacity) {
		if(load + r.load > vehicleCapacity) return false;
		
		int tf = getFirst();
		int tl = getLast();
		
		int rf = r.getFirst();
		int rl = r.getLast();
		
		if(tf == rf) {
			r.getNodes().removeFirst();
			nodes.addAll(r.getNodes());
		}else if(tf == rl) {
			r.getNodes().removeLast();
			while(!r.getNodes().isEmpty()) {
				nodes.addFirst(r.getNodes().removeLast());
			}
		}else if(tl == rf) {
			r.getNodes().removeFirst();
			nodes.addAll(r.getNodes());
		}else if(tl == rl) {
			r.getNodes().removeLast();
			while(!r.getNodes().isEmpty()) {
				nodes.add(r.getNodes().removeLast());
			}
		}else {
			return false;
		}
		
		this.load += r.load;
		
		return true;
	}
	
	public LinkedList<Integer> getNodes(){
		return nodes;
	}
	
	@Override
	public String toString() {
		return nodes.toString();
	}
	
	
	
}
