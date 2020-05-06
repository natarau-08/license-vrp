package algorithm;

import java.util.ArrayList;
import java.util.Random;

import obj.cvrp.CvrpGraph;
import obj.cvrp.CvrpNode;
import obj.cvrp.CvrpRoute;

/**<b>References:</b><br>
 * Optimised crossover genetic algorithm for capacitated vehiclerouting problem
 * Habibeh Nazifa, Lai Soon Lee<br>
 * <i>1-s2.0-S0307904X11005105-main.pdf</i>
*/
public class Genetic {
	
	/**Probability of selection(75% default),
	 *The probability that a fitter individual will be chosen as parent for next generation*/
	public static float ps = 0.75f;
	
	/**Generates random routes in graph and computes genetic solution<br>
	 * @param generationSize - number of individuals in a generation
	 * @param generations - stop after a number of generations
	 * <br><br>
	 * String is used instead of CvrpRoute to get intentional "error" where coding a route and decoding might not be reversible*/
	public static void randomGeneticSolution(CvrpGraph graph, int generationSize, int generations, int vehicleCapacity) {
		
		Random rand = new Random();
		
		// storing individuals
		String[] indivs = new String[generationSize];
		
		// generate each individual. One individual is generated when all nodes in graph are added
		for(int i=0;i<generationSize;i++) {
			CvrpRoute route = new CvrpRoute(graph);
			
			ArrayList<Integer> nodes = new ArrayList<>();
			for(CvrpNode n: graph.getNodes().values()) {
				if(n == graph.getDepot()) continue;
				nodes.add(n.getId());
			}
			
			while(nodes.size() > 0) {
				route.add(nodes.remove(rand.nextInt(nodes.size())));
			}
			
			indivs[i] = route.toString().replaceAll("[^0-9|,]", "");
		}
		
		// random individuals have been generated
		
		
	}
}
