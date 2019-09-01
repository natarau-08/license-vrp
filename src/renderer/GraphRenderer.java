package renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;

import database.obj.cvrp.CvrpCost;
import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;

public class GraphRenderer {

	public static BufferedImage renderCvrpGraph(CvrpGraph graph) {
		BufferedImage img = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		int radius = graph.getMinDist()/2;
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		
		for(CvrpNode n : nodes) {
			if(n.getDemand() == 0) {
				g.setColor(Color.green);
			}else {
				g.setColor(Color.red);
			}
			
			int x = n.getX();
			int y = n.getY();
			
			g.fillOval(x-radius/2, y-radius/2, radius, radius);
			
		}
		
		BufferedImage background = new BufferedImage(graph.getWidth() + 2, graph.getHeight() + 2, BufferedImage.TYPE_3BYTE_BGR);
		g = (Graphics2D) background.getGraphics();
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, background.getWidth(), background.getHeight());
		g.drawImage(img, 1, 1, img.getWidth(), img.getHeight(), null);
		
		return background;
	}
	
	public static void writeImage(BufferedImage image) throws IOException{
		ImageIO.write(image, "png", new File("test.png"));
	}
	
	public static void writeCvrpImage(CvrpGraph graph) throws IOException{
		BufferedImage image = renderCvrpGraph(graph);
		ImageIO.write(image, "png", new File("test.png"));
	}
	
	public static void renderCvrpCosts(CvrpGraph graph, BufferedImage image) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		HashMap<Integer, CvrpCost> costs = graph.getCosts();
		for (Map.Entry<Integer, CvrpCost> entry : costs.entrySet()) {
			int[] n = entry.getValue().getNodesIndexes();
			
			CvrpNode node1 = graph.getNodes().get(n[0]);
			CvrpNode node2 = graph.getNodes().get(n[1]);
			
			int x = (node1.getX() + node2.getX()) / 2;
			int y = (node1.getY() + node2.getY()) / 2;
			
			g.setColor(Color.BLACK);
			g.drawString("" + entry.getValue().getCost(), x, y);
		}
	}
	
	public static void writeCvrpImageWithCosts(CvrpGraph graph) throws IOException{
		BufferedImage image = renderCvrpGraph(graph);
		renderCvrpCosts(graph, image);
		writeImage(image);
	}
}