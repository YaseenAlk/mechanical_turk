package org.neu.ccs.mechanical_turk;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
	private DrawingPanel dp;
	
	private ArrayList<Pair> boxCoordinates;
	private ArrayList<String> queries;
	
	public void init() {	
		boxCoordinates = new ArrayList<>();
		queries = new ArrayList<>();

		//will we need to receive any parameters?
		
		try {
			dp = new DrawingPanel(determineUrl());
			getContentPane().add(dp);
			setSize(dp.getPreferredSize());
			setMinimumSize(dp.getPreferredSize());
		} catch (IOException e) {
			e.printStackTrace();
		}
		queries.add(null);
		boxCoordinates.add(null);
		undo();
	}
	
	public String determineUrl() {
		String urlParam;
		try {
			urlParam = getParameter("imgURL");
		} catch (NullPointerException npe) { urlParam = null; }
		
		String defaultUrl = "http://images.media-allrecipes.com/userphotos/250x250/00/64/20/642001.jpg",
				url = (urlParam == null || urlParam.isEmpty() ? defaultUrl : urlParam);
		
		return url;
	}
	
	public void undo() {
		boxCoordinates.remove(boxCoordinates.size()-1);
		queries.remove(queries.size()-1);
		try {
			getContentPane().removeAll();
			dp = new DrawingPanel(determineUrl());
			getContentPane().add(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		revalidate();
	}
	
	public ArrayList<String> getQueries() {
		return this.queries;
	}
	
	public ArrayList<Pair> getBoxCoords() {
		return this.boxCoordinates;
	}
	
	public void setUrl(String url) throws MalformedURLException {
		imgURL = new URL(url);
	}
	
	public String getImageID() {
		//TODO: code image IDs
		return "";
	}
	
	private class DrawingPanel extends JPanel {
		
		private Point press, release, current;

		private BufferedImage img;
		
		private int imgW, imgH;
		
		public DrawingPanel() throws IOException {
			this(null);
			// TODO Auto-generated catch block
		}
		
		public DrawingPanel(String url) throws IOException {
			loadImage(url);
			
			MyMouseAdapter mma = new MyMouseAdapter();
			addMouseMotionListener(mma);
			addMouseListener(mma);
		}
		
		private void loadImage(String URL) throws IOException {
			setUrl(URL);
			BufferedImage bImg = ImageIO.read(imgURL);
			
			imgW = bImg.getWidth();
			imgH = bImg.getHeight();
			setPreferredSize(new Dimension(imgW, imgH));
			img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
			
			Graphics g = img.getGraphics();
			g.drawImage(bImg, 0, 0, this);
			for (Pair p : boxCoordinates) {
				g.setColor(Color.green);
				drawRect(g, p.getStart(), p.getEnd());
			}
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
	
	public class Pair {
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
