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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.neu.ccs.mechanical_turk.TurkApplet.Pair;

/**
 * 
 * @author Yaseen Alkhafaji <alkhafaji.yaseen@gmail.com>
 * @author Michael Barbini
 *
 */

public class Qualification extends TurkApplet {

	public boolean qStage = true;
	public boolean certified = false;

	@Override
	public void init() {
		try {
			super.setUrl("http://st.hzcdn.com/simgs/1f418e7904ff0974_4-6626/contemporary-desks-and-hutches.jpg");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.init();
	}

	public void qualCoord() throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		//The 'designated' pixels- the ones that correctly make up the edge of the bounding box
		int x1 = 167; int y1 = (int) (46 / (360.0 / 490.0));
		int x2 = 303; int y2 = (int) (48 / (360.0 / 490.0));

		//How many pixels that the user can be away from the 'designated' pixel
		int allowance = 30;
		for (Pair p : super.getBoxCoords()) 
		{			
			//The user's inputed coordinates
			Point a = p.getStart();

			System.out.println(a);
			int firstX = (int) a.getX();
			int firstY = (int) a.getY();

			Point b = p.getEnd();
			int secX = (int) b.getX();
			int secY = (int) b.getY();

			//Ensures that no matter where the user draws their first coordinate, 
			//it always reverts back to the top left of the rectangle
			//and that the end coordinate is always the bottom right
			int topLeftX = Math.min(firstX, secX);
			int topLeftY = Math.min(firstY,secY);
			int bottomRightX = Math.max(firstX, secX);
			int bottomRightY = Math.min(firstY,secY);

			//Used to find if coordinates within the allowance
			if(((topLeftX >= x1 - allowance) && (topLeftX <= x1 + allowance)) &&
					((topLeftY >= y1 - allowance) && (topLeftY <= y1 + allowance)) &&
					((bottomRightX >= x2 - allowance) && (bottomRightX <= x2 + allowance)) &&
					((bottomRightY >= y2 - allowance) && (bottomRightY <= y2 + allowance))
					)
			{
				certified = true;
				System.out.println("Certified");

				try {
					String userPath = (System.getProperty("user.home"));
					File file = new File(userPath + "/Downloads/amzBBcertified");
					System.out.println(file);

					if (file.createNewFile()){
						System.out.println("File is created!");
					}else{
						System.out.println("File already exists.");
					}

				} catch (IOException e) {
					System.out.println("failed to create file");
					e.printStackTrace();
				}
			}else{
				System.out.println("you are not certified");
			}
		}
	}
}
