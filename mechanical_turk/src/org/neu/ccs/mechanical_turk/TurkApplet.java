package org.neu.ccs.mechanical_turk;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JApplet;

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
	
	public void init() {
		timer = new Timer();
		addMouseListener(this);
		loadImage();
		//will we need to receive any parameters?
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		drawImage(img, g);
		g.setColor(Color.GREEN);
		if (press != null && release != null) {
			System.out.println("Drawing...");
			int topLeftX, topLeftY, width, height;
			topLeftX = (int) ((press.getX() < release.getX()) ? press.getX() : release.getX());
			topLeftY = (int) ((press.getY() < release.getY()) ? press.getY() : release.getY());
			width = (int) Math.abs(press.getX() - release.getX());
			height = (int) Math.abs(press.getY() - release.getY());
			
			g.drawRect(topLeftX, topLeftY, width, height);
		}
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
	}
	
	private class Updater extends TimerTask {

		@Override
		public void run() {
			release = getMousePosition();
			repaint();
		}
		
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

}
