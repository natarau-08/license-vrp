package renderer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import main.Cfg;
import obj.cvrp.CvrpGraph;
import obj.cvrp.CvrpNode;
import obj.cvrp.CvrpRoute;

public class CvrpRenderer {
	
	public static void renderGraph(CvrpGraph graph) {
		int nodeDia = Cfg.getInt(Cfg.NODE_DIAMETER);
		
		BufferedImage img = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)img.getGraphics();
		
		//draw background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		g.setColor(Color.BLACK);
		//draw routes
		for(CvrpRoute r: graph.getRoutes()) {
			Integer prev = null;
			for(Integer i: r.getNodes()) {
				if(prev == null) {
					prev = i;
					continue;
				}
				
				CvrpNode n1 = graph.getNodes().get(prev);
				CvrpNode n2 = graph.getNodes().get(i);
				
				g.drawLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
			}
		}
		
		
		//draw nodes
		g.setColor(Color.red);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int id = 1;
		for(CvrpNode n: graph.getNodes().values()) {
			int x = n.getX() - nodeDia / 2;
			int y = n.getY() - nodeDia / 2;
			
			g.setColor(Color.red);
			g.fillOval(x, y, nodeDia, nodeDia);
			g.setColor(Color.black);
			g.drawString(String.format("%d", id), x, y + 20);
			id++;
		}
		
		
		
		try {
			File f = new File(graph.getName() + ".png");
			ImageIO.write(img, "PNG", f);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		JFrame f = new JFrame(graph.getName());
		
		@SuppressWarnings("serial")
		Canvas c = new Canvas() {
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, null);
			}
		};
		
		c.setBackground(Color.black);
		c.setSize(graph.getWidth(), graph.getHeight());
		c.setVisible(true);
		
		f.add(c);
		f.pack();
		
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}
