package org.neu.ccs.mechanical_turk;

import java.applet.Applet;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.neu.ccs.mechanical_turk.TurkApplet.Pair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AppletContainer extends JPanel {

	private TurkApplet app;
	
	public AppletContainer() {
		app = new TurkApplet();
		app.init();
		add(app);
		System.out.println("Yes! Size: " + app.getSize());
		setSize(app.getSize());
		setVisible(true);
	}
	
	public TurkApplet getApp() {
		return app;
	}
	
	public void exportData() throws ParserConfigurationException, TransformerException {
		ArrayList<Pair> boxCoords = app.getBoxCoords();
		ArrayList<String> queries = app.getQueries();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("training_data");
		doc.appendChild(rootElement);
		
		// id elements
		Element id = doc.createElement("id");
		rootElement.appendChild(id);
					
		// set attribute to id element
		Attr number = doc.createAttribute("id_number");
		number.setValue(app.getImageID());		//TODO: code image ID system
		id.setAttributeNode(number);
		
		Element boxContainer = doc.createElement("boxes");
		id.appendChild(boxContainer);
		
		for (int i = 0; i < boxCoords.size(); i +=1)
		{
			Element box = doc.createElement("box_" + i);
			boxContainer.appendChild(box);
			
			String value = queries.get(i);

			Attr description = doc.createAttribute("query");
			description.setValue(value);
			box.setAttributeNode(description);
			
			Attr startCoord = doc.createAttribute("startCoord");
			startCoord.setValue(boxCoords.get(i).getStart().getX()+ "," + boxCoords.get(i).getStart().getY());
			box.setAttributeNode(startCoord);
			
			Attr endCoord = doc.createAttribute("endCoord");
			endCoord.setValue(boxCoords.get(i).getEnd().getX() + "," + boxCoords.get(i).getEnd().getY());
			box.setAttributeNode(endCoord);
			
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("/home/ur5/test.xml"));

		transformer.transform(source, result);

		System.out.println("File saved!");
	}
	
	public void saveImage() throws IOException {
		BufferedImage image = app.getImage();
		File outputfile = new File("/home/ur5/exportedImage.png");
	    ImageIO.write(image, "png", outputfile);
	    
	    BufferedImage unscaled = app.getUnscaledImage();
		File unscaledFile = new File("/home/ur5/unscaledImage.png");
	    ImageIO.write(unscaled, "png", unscaledFile);
	    
	}
}
