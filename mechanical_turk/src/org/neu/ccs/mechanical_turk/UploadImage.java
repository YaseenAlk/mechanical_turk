package org.neu.ccs.mechanical_turk;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class UploadImage 
{
	String imageURL = null;
	URL url = null;
	File file = null;
	public void importImage() 
	{
		String serverURL = "";
		for(int i = 0; i <1000; i+=1)
		{
			imageURL = serverURL + Integer.toString(i);
			System.out.println(imageURL);
		}
	}
	
	public void readImage()
	{
		BufferedImage img = null;
		file = new File(imageURL);
		try 
		{
			img = ImageIO.read(file);  
		} 
		catch (IOException e) 
		{
		}
	}
}
