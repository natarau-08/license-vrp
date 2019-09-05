package renderer;

import static main.Main.LOGGER;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import database.obj.cvrp.CvrpGraph;
import database.obj.cvrp.CvrpNode;
import database.obj.cvrp.CvrpRoute;
import utils.Calc;
import utils.Point;

public class GraphRenderer {

	/**
	 * The space between node string and node geometry
	 */
	public static final int NODE_INFO_DELTA_Y = 5;
	public static int renderPadding = 30;
	
	//for getting the font width and height
	private static final BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
	private static final Graphics2D g = (Graphics2D) temp.getGraphics();
	
	public static BufferedImage renderCvrpGraph(CvrpGraph graph) {
		
		BufferedImage routesImg = renderGraphInfo(graph);
		
		BufferedImage img = new BufferedImage(graph.getWidth() + renderPadding , 
				graph.getHeight() + renderPadding + routesImg.getHeight() + 20, BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		//offset everything
		AffineTransform offsetTransform = AffineTransform.getTranslateInstance(renderPadding/2, renderPadding/2);
		g.setTransform(offsetTransform);
		
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, graph.getWidth(), graph.getHeight());
		
		//drawing routes
		LinkedList<CvrpRoute> routes = graph.getRoutes();
		LOGGER.info("Drawing routes\n" + routes);
		CvrpNode dep = graph.getDepot();
		for(CvrpRoute r: routes) {
			LOGGER.info("Drawing route\n" + r);
			CvrpNode prev = null;
			
			LinkedList<Point> coords = new LinkedList<>();
			
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
				
				coords.add(new Point(n.getX(), n.getY()));
				
				if(i == r.getNodes().getLast()) {
					g.drawLine(n.getX(), n.getY(), dep.getX(), dep.getY());
				}
			}
			
			//draw route cost
			Point p = Calc.computeRouteCenterOfMass(r, graph);
			int cost = Calc.calculateCvrpRouteCost(r, graph);
			LOGGER.info("Drawing cost at " + p);
			g.setColor(Color.BLACK);
			g.drawString(String.format("%d", cost), (int)p.x, (int)p.y);
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
		
		//drawing routes string
		g.drawImage(routesImg, 0, img.getHeight() - routesImg.getHeight() - renderPadding, null);
		
		BufferedImage background = new BufferedImage(img.getWidth() + 2, img.getHeight() + 2, BufferedImage.TYPE_3BYTE_BGR);
		g = (Graphics2D) background.getGraphics();
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, background.getWidth(), background.getHeight());
		g.drawImage(img, 1, 1, img.getWidth(), img.getHeight(), null);
		
		return background;
	}
	
	private static BufferedImage renderGraphInfo(CvrpGraph graph) {
		final int padding = 10;
		int maxStrWidth = graph.getWidth() - padding;
		
		LinkedList<String> routes = new LinkedList<>();
		for(CvrpRoute r: graph.getRoutes()) {
			
			String routeString = r.toString();
			
			if(routes.isEmpty()) {
				if(strWidth(routeString) <= maxStrWidth) {
					routes.add(routeString);
					
				}else {
					renderGraphInfo_splitRoute(routeString, routes, maxStrWidth);
				}
				
				continue;
			}
			
			if(strWidth(routes.getLast() + ", " + routeString) <= maxStrWidth) {
				String last = routes.removeLast();
				routes.add(last + ", " + routeString);
			}else {
				if(strWidth(routeString) > maxStrWidth) {
					renderGraphInfo_splitRoute(routeString, routes, maxStrWidth);
				}else {
					routes.add(routeString);
				}
			}
		}
		
		//adding other info
		routes.addFirst("Routes:");
		routes.addFirst(String.format("Graph - name: %s, description: %s,  number of nodes: %d, of routes: %d", 
				graph.getName(), graph.getDescription(), graph.getNodeCount(), graph.getRoutes().size()));
		
		int height = routes.size() * strHeight() + 9;
		BufferedImage img = new BufferedImage (graph.getWidth(), height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)img.getGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);
		
		g.setColor(Color.BLACK);
		
		for(int i=0;i<routes.size();i++) {
			g.drawString(routes.get(i), padding/2, (1+i) * strHeight());
		}
		
		return img;
	}

	private static void renderGraphInfo_splitRoute(String routeString, LinkedList<String> routes, int width) {
		String[] rs = routeString.split(" ");
		routes.add(rs[0]);
		for(int i=1;i<rs.length;i++) {
			if(strWidth(routes.getLast() + rs[i]) <= width) {
				String last = routes.removeLast();
				routes.add(last + rs[i]);
			}else {
				routes.add(rs[i]);
			}	
		}
	}
	
	private static int strWidth(String str) {
		return g.getFontMetrics().stringWidth(str);
	}
	
	private static int strHeight() {
		return g.getFontMetrics().getHeight();
	}
	
	public static void writeCvrpImage(CvrpGraph graph, String path) throws IOException{
		BufferedImage image = renderCvrpGraph(graph);
		ImageIO.write(image, "png", new File(path + ".png"));
	
	}
	
	private static void drawNodeInfo(Graphics2D g, CvrpNode node, int radius) {
		FontMetrics metrics = g.getFontMetrics();
		int strW = metrics.stringWidth(node.toString());
		//int strH = metrics.getHeight() + metrics.getAscent();
		
		int x = node.getX() - strW/2;
		int y = node.getY() + radius + NODE_INFO_DELTA_Y;
		
		g.setColor(Color.BLACK);
		g.drawString(node.toString(), x, y);
	}

	public static void writeRenderedRoutes(CvrpGraph graph, String path) throws IOException{
		BufferedImage image = renderGraphInfo(graph);
		ImageIO.write(image, "png", new File(path + ".png"));
	}
}
