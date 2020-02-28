package obj.cvrp;

import java.util.LinkedList;
public class CvrpRoute {

	private LinkedList<Integer> nodes;
	private int load;
	private CvrpGraph graph;
	
	public CvrpRoute(CvrpGraph graph) {
		nodes = new LinkedList<>();
		this.graph = graph;
		load = 0;
	}
	
	public CvrpRoute(CvrpGraph graph, LinkedList<Integer> c) {
		nodes = new LinkedList<>();
		nodes.addAll(c);
		load = 0;
		this.graph = graph;
		for(Integer i: c) {
			load += graph.getNodes().get(i).getDemand();
		}
	}
	
	public CvrpRoute(CvrpGraph graph, Integer ...n) {
		nodes = new LinkedList<>();
		this.graph = graph;
		load = 0;
		for(Integer i: n) {
			nodes.add(i);
			load += graph.getNodes().get(i).getDemand();
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
		load += graph.getNodes().get(e).getDemand();
	}
	
	public void addFirst(int e) {
		nodes.addFirst(e);
		load += graph.getNodes().get(e).getDemand();
	}
	
	public boolean contains(int e) {
		return nodes.contains(e);
	}

	public void add(int e) {
		nodes.add(e);
		load += graph.getNodes().get(e).getDemand();
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
