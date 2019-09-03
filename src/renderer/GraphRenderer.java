package renderer;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;

import database.obj.cvrp.CvrpArc;
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
		
		//drawing arcs
		LinkedList<Integer> ids = graph.getRoutes();
		int lastId = -1;
		for(Integer id: ids) {
			
			if(lastId == -1) {
				lastId = id;
				continue;
			}
			
			CvrpNode n1 = graph.getNodes().get(lastId);
			CvrpNode n2 = graph.getNodes().get(id);
			
			lastId = id;
			
			int x1 = n1.getX();
			int y1 = n1.getY();
			
			int x2 = n2.getX();
			int y2 = n2.getY();
			
			g.setColor(Color.BLACK);
			g.drawLine(x1, y1, x2, y2);
		}
		
		int radius = graph.getMinDist()/2;
		LinkedList<CvrpNode> nodes = graph.getNodesAsList();
		
		for(CvrpNode n : nodes) {
			int x = n.getX();
			int y = n.getY();
			
			if(n.getDemand() == 0) {
				g.setColor(Color.green);
				g.fillRect(x-radius/2, y-radius/2, radius, radius);
				g.setColor(Color.BLACK);
				g.drawRect(x-radius/2, y-radius/2, radius, radius);
			}else {
				g.setColor(Color.red);
				g.fillOval(x-radius/2, y-radius/2, radius, radius);
				g.setColor(Color.BLACK);
				g.drawOval(x-radius/2, y-radius/2, radius, radius);
				
				
			}
			drawNodeInfo(g, n, radius);
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
	
	private static void renderCvrpCosts(CvrpGraph graph, BufferedImage image) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		HashMap<CvrpArc, CvrpCost> costs = graph.getCosts();
		for (Map.Entry<CvrpArc, CvrpCost> entry : costs.entrySet()) {
			
			CvrpNode node1 = entry.getValue().getNode(0);
			CvrpNode node2 = entry.getValue().getNode(1);
			
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
	
	private static void drawNodeInfo(Graphics2D g, CvrpNode node, int radius) {
		FontMetrics metrics = g.getFontMetrics();
		int strW = metrics.stringWidth(node.toString());
		//int strH = metrics.getHeight() + metrics.getAscent();
		
		int x = node.getX() - strW/2;
		int y = node.getY() + radius + 5;
		
		g.setColor(Color.BLACK);
		g.drawString(node.toString(), x, y);
	}
}
