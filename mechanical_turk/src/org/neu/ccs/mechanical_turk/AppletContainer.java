package org.neu.ccs.mechanical_turk;

import java.applet.Applet;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AppletContainer extends JPanel {

	private TurkApplet app;
	
	public AppletContainer() {
		this(null);
	}
	
	public AppletContainer(Node XMLimageNode) {
		app = new TurkApplet();
		if (XMLimageNode != null)
			loadFromXML(XMLimageNode);
		app.init();
		add(app);
		setSize(app.getSize());
		setVisible(true);
	}
	
	public TurkApplet getApp() {
		return app;
	}
	
	public void loadFromXML(Node imageNode) {

		ArrayList<Pair> boxCoords = new ArrayList<>();
		ArrayList<String> queries = new ArrayList<>();
		String imgURL = "";

		Node url = ((Element) imageNode).getElementsByTagName("image_name").item(0);
		System.out.println(url.getTextContent());
		imgURL = url.getTextContent();

		NodeList objects = ((Element) imageNode).getElementsByTagName("object");

		System.out.println("----------------------------");

		for (int i = 0; i < objects.getLength(); i++) {

			Node obj = objects.item(i);

			if (obj.getNodeType() == Node.ELEMENT_NODE) {

				Element element = (Element) obj;
				int x0, y0, x1, y1;
				x0 = Integer.parseInt(element.getElementsByTagName("box_x0").item(0).getTextContent());
				y0 = Integer.parseInt(element.getElementsByTagName("box_y0").item(0).getTextContent());
				x1 = Integer.parseInt(element.getElementsByTagName("box_x1").item(0).getTextContent());
				y1 = Integer.parseInt(element.getElementsByTagName("box_y1").item(0).getTextContent());

				Point start = new Point(x0, y0), end = new Point(x1, y1);
				
				Pair coord = app.new Pair(start, end);
				boxCoords.add(coord);

				String query;
				query = element.getElementsByTagName("query").item(0).getTextContent();
				queries.add(query);
			}
		}
		
		try {
			app.setUrl(imgURL);
		} catch (MalformedURLException e) {
			System.out.println("Image loading failed: bad url");
		}
		app.setBoxCoords(boxCoords, false);
		app.setQueries(queries);
	}
	
	public void exportData() throws ParserConfigurationException, TransformerException {
		ArrayList<Pair> boxCoords = app.getUnscaledBoxCoords();
		ArrayList<String> queries = app.getQueries();
		
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("image");
		doc.appendChild(rootElement);
					
		// set attribute to id element
		Attr number = doc.createAttribute("id");
		number.setValue(app.getImageID());		//TODO: code image ID system
		rootElement.setAttributeNode(number);
		
		Element imgName = doc.createElement("image_name");
		imgName.appendChild(doc.createTextNode(app.getUrl().toString()));
		rootElement.appendChild(imgName);
		
		Element imgSize = doc.createElement("image_size");
		imgSize.appendChild(doc.createTextNode(app.getOriginalImage().getWidth() + "x" + app.getOriginalImage().getHeight()));
		rootElement.appendChild(imgSize);
		
		Element annotatedBy = doc.createElement("annotated_by");
		annotatedBy.appendChild(doc.createTextNode(this.getAnnotaterName()));
		rootElement.appendChild(annotatedBy);
		
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		
		Element date = doc.createElement("date");
		date.appendChild(doc.createTextNode(formatter.format(today)));
		rootElement.appendChild(date);
		
		formatter = new SimpleDateFormat("hh:mm:ss");
		Element time = doc.createElement("time");
		time.appendChild(doc.createTextNode(formatter.format(today)));
		rootElement.appendChild(time);
		
		for (int i = 0; i < boxCoords.size(); i +=1)
		{
			Element obj = doc.createElement("object");
			rootElement.appendChild(obj);
			
			Element num = doc.createElement("obj_no");
			num.appendChild(doc.createTextNode(Integer.toString(i)));
			obj.appendChild(num);
			
			Pair box = boxCoords.get(i);
			
			Element box_x0 = doc.createElement("box_x0");
			box_x0.appendChild(doc.createTextNode(Integer.toString((int)box.getStart().getX())));
			obj.appendChild(box_x0);
			
			Element box_y0 = doc.createElement("box_y0");
			box_y0.appendChild(doc.createTextNode(Integer.toString((int)box.getStart().getY())));
			obj.appendChild(box_y0);
			
			Element box_x1 = doc.createElement("box_x1");
			box_x1.appendChild(doc.createTextNode(Integer.toString((int)box.getEnd().getX())));
			obj.appendChild(box_x1);
			
			Element box_y1 = doc.createElement("box_y1");
			box_y1.appendChild(doc.createTextNode(Integer.toString((int)box.getEnd().getY())));
			obj.appendChild(box_y1);
			
			String value = queries.get(i);
			
			Element description = doc.createElement("query");
			description.appendChild(doc.createTextNode(value));
			obj.appendChild(description);
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("/home/ur5/test.xml"));

		transformer.transform(source, result);

		System.out.println("File saved!");
	}

	
	public String getAnnotaterName() {
		//may change if we use AMT.
		//may also need to be put into the applet if we use AMT (assuming we don't use this JPanel)
		return System.getProperty("user.name");
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
