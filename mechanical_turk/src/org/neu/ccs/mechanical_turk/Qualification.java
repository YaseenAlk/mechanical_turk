package org.neu.ccs.mechanical_turk;

import java.awt.List;
import java.awt.Point;
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
	
	public int checkCoords(int x1, int y1, int x2, int y2, Pair currentC)
	{
		//How many pixels that the user can be away from the 'designated' pixel
		//and still be considered a correct bounding box
		int allowance = 15;
				
		Point a = currentC.getStart();
		Point b = currentC.getEnd();
		
		int firstX = (int) a.getX();
		int firstY = (int) a.getY();

		int secX = (int) b.getX();
		int secY = (int) b.getY();
		
		System.out.println("FirstX " + firstX);
		System.out.println("FirstY " + firstY);
		
		System.out.println("SecondX " + secX);
		System.out.println("SecondY " + secY);
		
		//Ensures that no matter where the user draws their first coordinate, 
		//it always reverts back to the top left of the rectangle
		//and that the end coordinate is always the bottom right
		int topLeftX = Math.min(firstX, secX);
		int topLeftY = Math.min(firstY,secY);
		int bottomRightX = Math.max(firstX, secX);
		int bottomRightY = Math.max(firstY,secY);
		
		int score = 0;
		if(((topLeftX >= x1 - allowance) && (topLeftX <= x1 + allowance)) &&
				((topLeftY >= y1 - allowance) && (topLeftY <= y1 + allowance)) &&
				((bottomRightX >= x2 - allowance) && (bottomRightX <= x2 + allowance)) &&
				((bottomRightY >= y2 - allowance) && (bottomRightY <= y2 + allowance))
				)
		{
			score++;
		}
		return score;
	}
}
