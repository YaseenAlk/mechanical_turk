package org.neu.ccs.mechanical_turk;

import java.awt.List;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
	
	//If in qualification stage
	public boolean qStage = true;
	//If certified
	public boolean certified = false;
	//To check the score to see if certified
	private boolean checkScore = false;

	/*Whether a user is qualified is based on if they achieve a minimum score:
	1 for correct bounding box
	1 for each correct word in the query*/
	private int qualScore = 0;

	@Override
	public void init() {
		//Qualification Image
		super.setUrl("http://i.imgur.com/ZQr0v9C.jpg");
		super.init();
	}

	public void qualCoord() throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		
		ArrayList<Pair>boxC = getBoxCoords();
		for(int i = 0; i < boxC.size(); i ++)
		{
			System.out.println(boxC.size());
			//The user's inputed coordinates
			Pair currentC = boxC.get(i);
			
			//The coordinates that are correct
			//Requires that already know the coordinates from example image
			
			//Monitor
			qualScore += checkCoords(120, 0, 449, 198, currentC);
			
			//Keyboard
			qualScore += checkCoords(451, 178, 163, 290, currentC);
			
			//Red Bull
			qualScore += checkCoords(480, 168, 514, 216, currentC);
			
			//Cup
			qualScore += checkCoords(437, 131, 497, 190, currentC);
			
			//Book
			qualScore += checkCoords(25, 190, 190, 317, currentC);
			
			//Mouse
			qualScore += checkCoords(467, 243, 531, 309, currentC);
			
			checkScore = true;
		}

		//To qualify the user by whether their query is correct 
		for (int i = 0; i < super.getQueries().size(); i ++)
		{
			ArrayList<String> userQuery = super.getQueries();
			//System.out.println(userQuery);
			String query = userQuery.get(i);
			
			//Monitor
			if (query.toLowerCase().contains("monitor") || query.toLowerCase().contains("screen")) 
			{
				qualScore++;
				qualScore += checkForPossibilities(query, "black,center".split(","));
			}
			
			//Mason Book 
			if (query.toLowerCase().contains("book"))
			{
				qualScore++;
				qualScore += checkForPossibilities(query, "white,mason,left".split(","));
			}
			
			//Keyboard
			if (query.toLowerCase().contains("keyboard"))
			{
				qualScore++;
				qualScore += checkForPossibilities(query, "black,center".split(","));
			}
			
			//Mouse
			if (query.toLowerCase().contains("mouse"))
			{
				qualScore++;
				qualScore += checkForPossibilities(query, "black,right".split(","));
			}
			
			//5 Hour Energy Bottle
			if (query.toLowerCase().contains("bottle") || query.toLowerCase().contains("five") || query.toLowerCase().contains("5"))
			{
				qualScore++;
				qualScore += checkForPossibilities(query, "red,orange,right".split(","));
			}
			
			//Cup
			if (query.toLowerCase().contains("cup"))
			{
				qualScore++;
				qualScore += checkForPossibilities(query, "red,right".split(","));
			}
			
			
			checkScore = true;
		}



		//If the user achieves a qualification score of 2 or greater, than they are qualified
		while(checkScore) {
			System.out.println("qualScore: " + qualScore);
			checkScore = false;
			if(qualScore >= 16) //60% correct 16 out of 26 possible points
			{
				certified = true;
				System.out.println("Certified");

				//If the user is certified, it downloads a file 
				//This file will be searched for every time the gui loads in order 
				//to determine whether is needs to run the qualification 
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

	public int checkForPossibilities(String query, String[] possibilities)
	{
		int score = 0;
		for (int i = 0; i < possibilities.length; i++)
		{
			if(query.toLowerCase().contains(possibilities[i])) score++;
		}
		return score;
	}
	
	//Requires that know the ground truth coordinates
	public int checkCoords(int x1, int y1, int x2, int y2, Pair currentC)
	{
		//Gets the individual points from the user bounding box list			
		Point a = currentC.getStart();
		Point b = currentC.getEnd();
		
		int firstX = (int) a.getX();
		int firstY = (int) a.getY();

		int secX = (int) b.getX();
		int secY = (int) b.getY();
		
		//Finding the four coordinates
		//Left
		int leftX = Math.min(firstX, secX);
		
		//Right
		int rightX = Math.max(firstX, secX);
		
		//Top
		int topY = Math.min(firstY,secY);
		
		//Bottom
		int bottomY = Math.max(firstY,secY);
		
		//For debugging purposes
		System.out.println("FirstX " + firstX);
		System.out.println("FirstY " + firstY);
		
		System.out.println("SecondX " + secX);
		System.out.println("SecondY " + secY);
		
		//Finding the userLength and width of the user bounding box
		double userLength =  java.lang.Math.abs(secX - firstX);
		double userWidth =  java.lang.Math.abs(secY - firstY);
		
		//Finding the area
		double userArea = userLength * userWidth;
		
		//Finding length and width of the ground truth rectangle
		double gtLength =  java.lang.Math.abs(x2 - x1);
		double gtWidth =  java.lang.Math.abs(y2 - y1);
		
		//Finding the area
		double gtArea = gtLength * gtWidth;
		
		//Finding the intersection of the rectangles		
		//User Rectangle
		Rectangle userRectangle = new Rectangle(leftX, topY, rightX, bottomY);
		System.out.println(userRectangle.getBounds());
		
		//Ground truth rectangle
		Rectangle gtRectangle = new Rectangle(x1, y1, x2, y2);
		System.out.println(gtRectangle.getBounds());
		
		//Intersection
		Rectangle intRect = userRectangle.intersection(gtRectangle);
		System.out.println(intRect.getBounds());
		
		//Finding the area of non-overlap
		double intWidth = intRect.getWidth();
		double intHeight = intRect.getHeight();
		
		double intArea = intWidth * intHeight;
		
		//Non-overlap of user Rectangle
		double userNO = userArea - intArea;
		//Non-overlap of ground truth rectangle
		double gtNO = gtArea - intArea;

		//Local score to calculate how many points earned for bounding boxes
		int score = 0;
		
		if((userNO + gtNO) / gtArea <= .1)
		{
			score++;
		}
		return score;
	}
}
