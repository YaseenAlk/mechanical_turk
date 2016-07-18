package org.neu.ccs.mechanical_turk;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.neu.ccs.mechanical_turk.TurkApplet.Pair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AutoElement 
{
	
	static ArrayList<String> query = new ArrayList<String>();
	ArrayList<Pair> coords;
	static int size;
	
	public static void sort()
	{
		query.add("a");
		query.add("b");
		size = query.size();
		
	}
	public static void main(String[] args)
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		sort();
		try 
		{
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("trainingdata");
			doc.appendChild(rootElement);

			// id elements
			Element id = doc.createElement("id");
			rootElement.appendChild(id);
			
			// set attribute to id element
			Attr number = doc.createAttribute("id_number");
			number.setValue("some_number");
			id.setAttributeNode(number);

			for (int i = 0; i < size; i +=1)
			{
				String value = query.get(i);
				// query elements
				Element query = doc.createElement("query");
				id.appendChild(query);

				// set attribute to query element
				Attr description = doc.createAttribute("natural_language_description");
				description.setValue(value);
				query.setAttributeNode(description);
				System.out.println(value);

				// coordinate elements
				Element coordinates = doc.createElement("coordinates");
				id.appendChild(coordinates);

				// set attribute to coordinate element
				Attr coords = doc.createAttribute("coordinate");
				coords.setValue("X1_Y1_X2_Y2");
				coordinates.setAttributeNode(coords);
			}
			

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:\\file.xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");


		} 
		catch (ParserConfigurationException pce) 
		{
			pce.printStackTrace();
		} 
		catch (TransformerException tfe) 
		{
			tfe.printStackTrace();
		}
	}

}
