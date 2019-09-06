package renderer;

import java.awt.Canvas;

import database.obj.cvrp.CvrpGraph;

public class GraphVisualizer extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	private CvrpGraph graph;
	
	public GraphVisualizer(CvrpGraph graph) {
		this.graph = graph;
	}
	
	
	
	@Override
	public void run() {
		
	}

}
