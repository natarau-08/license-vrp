package renderer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import obj.cvrp.CvrpGraph;
import obj.cvrp.CvrpNode;

@SuppressWarnings("serial")
public class CvrpRealRenderer extends Canvas implements Runnable{

	private boolean running = true;
	private int targetFPS = 30;
	
	private CvrpGraph graph;
	
	public CvrpRealRenderer(CvrpGraph graph) {
		this.graph = graph;
		this.setSize(graph.getWidth(), graph.getHeight());
		this.setBackground(Color.BLACK);
		
		JFrame f = new JFrame();
		
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				running = false;
			}
		});
		
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.add(this);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		
		this.createBufferStrategy(3);
		
		new Thread(this).start();
	}
	
	public void run() {
		
		long t0 = System.currentTimeMillis();
		int fps = 0;
		int lastFps = 0;
		int arcAngle = 0;
		int deltaAngle = 1;
		try {
			while(running) {
				do {
					do {
						Graphics2D g = (Graphics2D)this.getBufferStrategy().getDrawGraphics();
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						
						g.setColor(Color.WHITE);
						g.fillRect(0, 0, graph.getWidth(), graph.getHeight());
						
						g.setColor(Color.BLACK);
						g.drawString(String.format("FPS: %d/%d", lastFps, targetFPS), 0, 12);
						g.fillArc(0, 15, 10, 10, 0, arcAngle);
						arcAngle = arcAngle + deltaAngle > 360 ? 0 : arcAngle + deltaAngle;
						
						CvrpNode[] nodes = graph.getNodes().values().toArray(new CvrpNode[0]);
						
						for(int i=0;i<nodes.length;i++) {
							g.setColor(Color.red);
							g.fillOval(nodes[i].getX() - graph.getNodePadding() / 2, nodes[i].getY() - graph.getNodePadding() / 2, graph.getNodePadding(), graph.getNodePadding());
							
							g.setColor(Color.magenta);
							g.drawOval(nodes[i].getX() - graph.getNodeMargin() / 2, nodes[i].getY() - graph.getNodeMargin() / 2, graph.getNodeMargin(), graph.getNodeMargin());
							
							if(graph.lastFailedNode == null) continue;
							
							int x = (int) graph.lastFailedNode.x - graph.getNodePadding() / 2;
							int y = (int) graph.lastFailedNode.y - graph.getNodePadding() / 2;
							g.setColor(Color.CYAN);
							g.fillOval(x - graph.getNodePadding()/2, y - graph.getNodePadding()/2, graph.getNodePadding(), graph.getNodePadding());
							g.drawOval(x - graph.getNodeMargin()/2, y - graph.getNodeMargin()/2, graph.getNodeMargin(), graph.getNodeMargin());
							
							g.setColor(Color.BLACK);
							g.fillRect(nodes[i].getX(), nodes[i].getY(), 1, 1);
						}
						
						g.dispose();
					}while(this.getBufferStrategy().contentsRestored());
					
					this.getBufferStrategy().show();
					
				}while(this.getBufferStrategy().contentsLost());
				
				fps++;
				if(System.currentTimeMillis() - t0 >= 1000) {
					lastFps = fps;
					fps = 0;
					t0 = System.currentTimeMillis();
				}
				Thread.sleep(1000 / targetFPS - 1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
