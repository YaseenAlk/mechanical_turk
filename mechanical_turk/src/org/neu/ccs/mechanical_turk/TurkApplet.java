package org.neu.ccs.mechanical_turk;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

/**
 * 
 * @author Yaseen Alkhafaji <alkhafaji.yaseen@gmail.com>
 * @author Michael Barbini
 *
 */

public class TurkApplet extends JApplet implements MouseListener {
	
	private Timer timer;
	private TimerTask task;
	
	private Point press, release;
	
	private BufferedImage img;
	
	private ArrayList<Pair> boxCoordinates;
	private ArrayList<String> queries;
	
	public void init() {
		timer = new Timer();
		addMouseListener(this);
		
		boxCoordinates = new ArrayList<>();
		queries = new ArrayList<>();
		
		loadImage();
		//will we need to receive any parameters?
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		drawImage(img, g);
		checkAndDrawRect(g);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		press = getMousePosition();
		task = new Updater();
		timer.scheduleAtFixedRate(task, 0, 10);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		task.cancel();
		String query = JOptionPane.showInputDialog(this, "Natural Language Query?");
		queries.add(query);
		boxCoordinates.add(new Pair(press, release));
		
		press = null; release = null;
	}
	
	private class Updater extends TimerTask {

		@Override
		public void run() {
			release = getMousePosition();
			repaint();
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
	
	private void checkAndDrawRect(Graphics g) {
		g.setColor(Color.green);
		for (Pair rect : boxCoordinates)
			drawRect(g, rect.getStart(), rect.getEnd());
		if (press != null && release != null) {
			drawRect(g, press, release);
		}
	}
	
	private void drawRect(Graphics g, Point start, Point end) {
		Point press = start, release = end;
		int topLeftX = (int) ((press.getX() < release.getX()) ? press.getX() : release.getX()),
			topLeftY = (int) ((press.getY() < release.getY()) ? press.getY() : release.getY()),
			width = (int) Math.abs(press.getX() - release.getX()),
			height = (int) Math.abs(press.getY() - release.getY());
			
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));
		g.drawRect(topLeftX, topLeftY, width, height);

	}
	
	private void loadImage() {
		try {
			img = ImageIO.read(new URL("http://images.media-allrecipes.com/userphotos/250x250/00/64/20/642001.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void drawImage(BufferedImage img, Graphics g) {
		g.drawImage(img, 0, 0, null);
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

}
