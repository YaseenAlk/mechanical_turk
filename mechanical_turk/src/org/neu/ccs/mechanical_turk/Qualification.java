package org.neu.ccs.mechanical_turk;

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
		//The coordinates that are correct
		//Requires that already know the coordinates from example image
		int x1 = 334; int y1 = 231;
		int x2 = 567; int y2 = 363;

		//How many pixels that the user can be away from the 'designated' pixel
		//and still be considered a correct bounding box
		int allowance = 30;

		for (Pair p : super.getBoxCoords()) 
		{

			//The user's inputed coordinates
			Point a = p.getStart();
			Point b = p.getEnd();
			//System.out.println("User Point a " + a);
			//System.out.println("User Point b " + b);

			int firstX = (int) a.getX();
			int firstY = (int) a.getY();

			int secX = (int) b.getX();
			int secY = (int) b.getY();
			
			//Ensure that if the bounding box is drawn outside the image frame
			//it is still registered as the max frame width / height
			if(firstX > 640) firstX = 640;
			else if (firstX < 0) firstX = 0;
			
			if(secX > 640) secX = 640;
			else if (secX < 0) secX = 0;
			
			if(firstY > 360) firstY = 360;
			else if (firstY < 0) firstY = 0;
			
			if(secY > 360) secY = 360;
			else if (secY < 0) secY = 0;

			//Ensures that no matter where the user draws their first coordinate, 
			//it always reverts back to the top left of the rectangle
			//and that the end coordinate is always the bottom right
			int topLeftX = Math.min(firstX, secX);
			int topLeftY = Math.min(firstY,secY);
			int bottomRightX = Math.max(firstX, secX);
			int bottomRightY = Math.max(firstY,secY);

			//Used to find if coordinates within the allowance
			if(((topLeftX >= x1 - allowance) && (topLeftX <= x1 + allowance)) &&
					((topLeftY >= y1 - allowance) && (topLeftY <= y1 + allowance)) &&
					((bottomRightX >= x2 - allowance) && (bottomRightX <= x2 + allowance)) &&
					((bottomRightY >= y2 - allowance) && (bottomRightY <= y2 + allowance))
					)
			{
				qualScore++;
				checkScore = true;
			} 
			else 
			{
				checkScore = true;
			}
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
			if(qualScore >= 16)
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
}
