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
	
	public LinkedList<Integer> getNodes(){
		return nodes;
	}
	
	@Override
	public String toString() {
		return nodes.toString();
	}
	
	
	
}
