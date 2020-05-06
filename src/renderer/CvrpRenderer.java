package renderer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import main.Cfg;
import obj.cvrp.CvrpGraph;
import obj.cvrp.CvrpNode;
import obj.cvrp.CvrpRoute;

@SuppressWarnings("serial")
public class CvrpRenderer extends Canvas implements Runnable{

	private boolean running = true;
	private int targetFPS = 30;
	private boolean antialias = false;
	
	private boolean saveImage = false;
	private BufferedImage image;
	
	private CvrpGraph graph;
	
	public CvrpRenderer(CvrpGraph graph) {
		this.graph = graph;
		this.setSize(graph.getWidth(), graph.getHeight());
		this.setBackground(Color.BLACK);
		
		this.antialias = Cfg.getBoolean(Cfg.ANTIALIASING);
		
		JFrame f = new JFrame();
		
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				running = false;
			}
		});
		
		// menu
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem saveJPG = new JMenuItem("Save Image");
		saveJPG.addActionListener(e -> {saveImage = true;});
		JMenuItem anti = new JMenuItem("Antialiasing: " + (antialias ? "ON" : "OFF"));
		file.add(saveJPG);
		anti.addActionListener(e -> {
			antialias = !antialias;
			anti.setText("Antialiasing: " + (antialias ? "ON" : "OFF"));
		});
		file.add(anti);
		
		bar.add(file);
		
		f.setJMenuBar(bar);
		
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
						
						
						if(saveImage) {
							image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
							g = (Graphics2D)image.getGraphics();
							g.setColor(Color.WHITE);
							g.fillRect(0, 0, graph.getWidth(), graph.getHeight());
						}
						else {
							g.setColor(Color.WHITE);
							g.fillRect(0, 0, graph.getWidth(), graph.getHeight());
							g.setColor(Color.BLACK);
							g.drawString(String.format("FPS: %d/%d", lastFps, targetFPS), 0, 12);
							g.fillArc(0, 15, 10, 10, 0, arcAngle);
							arcAngle = arcAngle + deltaAngle > 360 ? 0 : arcAngle + deltaAngle;
						}
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
						
						CvrpNode[] nodes = graph.getNodes().values().toArray(new CvrpNode[0]);
						
						//drawing routes
						for(int i=0;i<graph.getRoutes().size();i++) {
							CvrpRoute r = graph.getRoutes().get(i);
							CvrpNode depot = graph.getDepot();
							
							g.setColor(Color.BLACK);
							if(r.getNodes().size()  == 1) {
								CvrpNode n = graph.getNodes().get(r.getFirst());
								g.drawLine(depot.getX(), depot.getY(), n.getX(), n.getY());
							}
							else {
								for(int j=1;j<r.getNodes().size();j++) {
									CvrpNode n1 = graph.getNodes().get(r.getNodes().get(j-1));
									CvrpNode n2 = graph.getNodes().get(r.getNodes().get(j));
									g.drawLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
								}
								
								CvrpNode n1 = graph.getNodes().get(r.getFirst());
								CvrpNode n2 = graph.getNodes().get(r.getLast());
								g.drawLine(n1.getX(), n1.getY(), depot.getX(), depot.getY());
								g.drawLine(n2.getX(), n2.getY(), depot.getX(), depot.getY());
							}
						}
						
						//drawing nodes
						for(int i=0;i<nodes.length;i++) {
							g.setColor(Color.red);
							
							if(nodes[i] == graph.getDepot()) {
								g.setColor(Color.BLUE);
								g.fillRect(nodes[i].getX() - graph.getNodePadding() / 2, nodes[i].getY() - graph.getNodePadding() / 2, graph.getNodePadding(), graph.getNodePadding());
							}
							else
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
						
						if(saveImage && image != null) {
							saveImage = false;
							String fileName = UUID.randomUUID() + ".png";
							try {
								ImageIO.write(image, "PNG", new File(fileName));
							}catch(Exception e) {
								e.printStackTrace();
							}
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
