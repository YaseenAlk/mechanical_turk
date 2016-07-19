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
import java.awt.geom.AffineTransform;
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
	
	public boolean qStage = true;
	
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
		try 
		{
//			if(qStage)
//			{
//				urlParam = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRKw8tFPYyC_02MMpIZ2tbRF1nasGQCiYdPKBl1Z2XH2HlVi4hr";
//			}
//			else
//			{
			urlParam = getParameter("imgURL");
//			}
			
		} catch (NullPointerException npe) { urlParam = null; }
		String defaultUrl = "http://hdwallpaperia.com/wp-content/uploads/2014/01/Windows-3D-1920x1080-Wallpaper-Background.jpg",
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
	
	public void qualCoord()
	{
		Point[] firstCoord = new Point[25];
		Point[] secondCoord = new Point[25];
		for (Pair p : this.getBoxCoords()) 
		{
			int x1 = 0; int y1 = 0;
			int x2 = 10; int y2 = 10;
			int allowance = 5;
			for(int i = 0; i < allowance; i++)
			{
				for (int t = 0; t < allowance; t ++)
				{
				firstCoord[i * 5 + t] = new Point(x1 + i, y1 + t);
				secondCoord[i * 5 + t] = new Point(x2 + i, y2 + t);		

				//System.out.print(firstCoord[i * 5 + t]);
				}
			}
			Point a = p.getStart();
			Point b = p.getEnd();
			
		}
	}
	
	public void setUrl(String url) throws MalformedURLException {
		imgURL = new URL(url);
	}
	
	public String getImageID() {
		//TODO: code image IDs
		return "";
	}
	
	public BufferedImage getImage() {
		return dp.img;
	}
	
	public BufferedImage getUnscaledImage() {
		if (dp.scaledX || dp.scaledY) {
			BufferedImage ret = dp.originalImg;
			
			Graphics g = ret.getGraphics();
			for (Pair p : boxCoordinates) {
				g.setColor(Color.green);
				Point scaledStart = new Point((int)(p.getStart().getX() / dp.scalingFactorX), 
						(int)(p.getStart().getY() / dp.scalingFactorY));
				Point scaledEnd = new Point((int)(p.getEnd().getX() / dp.scalingFactorX), 
						(int)(p.getEnd().getY() / dp.scalingFactorY));
				
				dp.drawRect(g, scaledStart, scaledEnd);
			}
		    g.dispose();
		    
		    
		    return ret;
		} else {
			return dp.img;
		}
	}
	
	private class DrawingPanel extends JPanel {
		
		private Point press, release, current;

		private BufferedImage img, originalImg;
		private boolean scaledX, scaledY;
		private double scalingFactorX, scalingFactorY; 
		
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
			originalImg = bImg;
			if (bImg.getWidth() > 640) {
				scaledX = true;
				scalingFactorX = 640.0/bImg.getWidth();
				bImg = scale(bImg, BufferedImage.TYPE_INT_ARGB, 640, bImg.getHeight(), scalingFactorX, 1);
			}
			if (bImg.getHeight() > 480) {
				scaledY = true;
				scalingFactorY = 360.0/bImg.getHeight();
				bImg = scale(bImg, BufferedImage.TYPE_INT_ARGB, bImg.getWidth(), 360, 1, scalingFactorY);
			}
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
		
		/**
		 * Credit: A4L of StackOverflow (http://stackoverflow.com/questions/15558202)
		 * 
		 * scale image
		 * 
		 * @param sbi image to scale
		 * @param imageType type of image
		 * @param dWidth width of destination image
		 * @param dHeight height of destination image
		 * @param fWidth x-factor for transformation / scaling
		 * @param fHeight y-factor for transformation / scaling
		 * @return scaled image
		 */
		public BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
		    BufferedImage dbi = null;
		    if(sbi != null) {
		        dbi = new BufferedImage(dWidth, dHeight, imageType);
		        Graphics2D g = dbi.createGraphics();
		        AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
		        g.drawRenderedImage(sbi, at);
		    }
		    return dbi;
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
