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
import database.obj.cvrp.CvrpRoute;
import static main.Main.LOGGER;

public class GraphRenderer {

	public static BufferedImage renderCvrpGraph(CvrpGraph graph) {
		BufferedImage img = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		//drawing arcs
		LinkedList<CvrpRoute> routes = graph.getRoutes();
		LOGGER.info("Drawing routes\n" + routes);
		CvrpNode dep = graph.getDepot();
		for(CvrpRoute r: routes) {
			LOGGER.info("Drawing route\n" + r);
			CvrpNode prev = null;
			
			for(Integer i: r.getNodes()) {
				
				CvrpNode n = graph.getNodes().get(i);
				
				if(i == r.getFirst()) {
					LOGGER.info("Route's first node is " + n);
					g.setColor(Color.black);
					g.drawLine(n.getX(), n.getY(), dep.getX(), dep.getY());
					
					prev = n;
					continue;
				}
				
				g.setColor(Color.BLACK);
				g.drawLine(n.getX(), n.getY(), prev.getX(), prev.getY());
				prev = n;
				
				if(i == r.getNodes().getLast()) {
					g.drawLine(n.getX(), n.getY(), dep.getX(), dep.getY());
				}
			}
			
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
	
	public static void writeCvrpImage(CvrpGraph graph, String path) throws IOException{
		BufferedImage image = renderCvrpGraph(graph);
		ImageIO.write(image, "png", new File(path + ".png"));
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
	
	public static void writeCvrpImageWithCosts(CvrpGraph graph, String path) throws IOException{
		BufferedImage image = renderCvrpGraph(graph);
		renderCvrpCosts(graph, image);
		ImageIO.write(image, "png", new File(path + ".png"));
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
