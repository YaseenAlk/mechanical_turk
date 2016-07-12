package org.neu.ccs.mechanical_turk;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * @author Yaseen Alkhafaji <alkhafaji.yaseen@gmail.com>
 * @author Michael Barbini
 *
 */

public class TurkApplet extends JApplet {
	private URL imgURL;
	
	private ArrayList<Pair> boxCoordinates;
	private ArrayList<String> queries;
	
	public void init() {	
		boxCoordinates = new ArrayList<>();
		queries = new ArrayList<>();
		
		String urlParam;
		try {
			urlParam = getParameter("imgURL");
		} catch (NullPointerException npe) { urlParam = null; }
		
		String defaultUrl = "http://images.media-allrecipes.com/userphotos/250x250/00/64/20/642001.jpg",
				url = (urlParam == null || urlParam.isEmpty() ? defaultUrl : urlParam);

		//will we need to receive any parameters?
		
		try {
			DrawingPanel dp = new DrawingPanel(url);
			getContentPane().add(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public void undo() {
		boxCoordinates.remove(boxCoordinates.size()-1);
		queries.remove(queries.size()-1);	
	}
	
	public ArrayList<String> getQueries() {
		return queries;
	}
	
	public ArrayList<Pair> getBoxCoords() {
		return this.boxCoordinates;
	}
	
	private class DrawingPanel extends JPanel {
		
		private Point press, release, current;

		private BufferedImage img;
		
		private int imgW, imgH;
		
		public DrawingPanel() throws IOException {
			this(null);
		}
		
		public DrawingPanel(String url) throws IOException {
			loadImage(url);
			
			MyMouseAdapter mma = new MyMouseAdapter();
			addMouseMotionListener(mma);
			addMouseListener(mma);
		}
		
		private void loadImage(String URL) throws IOException {
			imgURL = new URL(URL);
			BufferedImage bImg = ImageIO.read(imgURL);
			
			imgW = bImg.getWidth();
			imgH = bImg.getHeight();
			img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
			
			Graphics g = img.getGraphics();
			g.drawImage(bImg, 0, 0, this);
		    g.dispose();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img != null) {
				g.drawImage(img, 0, 0, this);
			}

			if (press != null && current != null) {
				g.setColor(Color.blue);
				drawRect(g, press, current);
			}
		}
		
		private void drawRect(Graphics g, Point start, Point end) {
			Point press = start, release = end;
			int topLeftX = (int) Math.min(press.getX(), release.getX()),
				topLeftY = (int) Math.min(press.getY(), release.getY()),
				width = (int) Math.abs(press.getX() - release.getX()),
				height = (int) Math.abs(press.getY() - release.getY());
				
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g.drawRect(topLeftX, topLeftY, width, height);

		}
		
		public void drawToBackground() {
		      Graphics g = img.getGraphics();
		      g.setColor(Color.green);
		      drawRect(g, press, release);
		      g.dispose();
		      
		      String query = JOptionPane.showInputDialog(this, "Natural Language Query?");
		      queries.add(query);
		      boxCoordinates.add(new Pair(press, release));
		      
		      press = null;
		      
		      repaint();
		   }
		
		private class MyMouseAdapter extends MouseAdapter {
			@Override
		    public void mouseDragged(MouseEvent mEvt) {
				current = mEvt.getPoint();
				DrawingPanel.this.repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent mEvt) {
				press = mEvt.getPoint();
			}
			
			@Override
			public void mouseReleased(MouseEvent mEvt) {
				release = mEvt.getPoint();
				current = null;
				drawToBackground();
			}

			
		}
		
	}
	
	private class Pair {
		private Point start;	// starting point (not necessarily top left)
		private Point end; 		// ending point (not necessarily bottom right)
		
		public Pair(Point s, Point e) {
			start = s;
			end = e;
		}
		
		public Point getStart() {
			return start;
		}
		
		public Point getEnd() {
			return end;
		}
	}

}
