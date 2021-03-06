package org.neu.ccs.mechanical_turk;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.imageio.stream.ImageInputStream;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * 
 * @author Yaseen Alkhafaji <alkhafaji.yaseen@gmail.com>
 * @author Michael Barbini
 */
public class GUI {

	private static final boolean LOAD_FROM_XML = false;
	private static final boolean RESIZABLE = false;
	
	private JFrame frame;
	private volatile boolean undoSubmitLocked, submitted, nextLocked;
	private JButton btnNext, btnSubmit, btnUndo;
	
	private Thread listener;
	private AppletContainer appContainer;

	private volatile static boolean certified; 
	
	private JLabel messLabel;
	String messCert = "Certified user";
	String messnCert = "Not a certified user";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		//Check if certified by whether their exists a file previously downloaded by the program
		String userPath = (System.getProperty("user.home"));
		File dir = new File(userPath+ "/Downloads");
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept
			(File dir, String name) {
				return name.startsWith("amzBBcertified");
			}
		};
		String[] children = dir.list(filter);
		if (children == null) {
			System.out.println("Either you are not certified or your file is not in the correct location!");
		} 
		else {
			for (int i=0; i < children.length; i++) {
				String filename = children[i];
				System.out.println(filename);
				certified = true;
			}
		} 
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if (GUI.LOAD_FROM_XML) {
			
			//load image from XML directory listed below
			//this is completely independent of the project goal; it's just to show
			//example code for loading the image from an XML
			
			try {
				String xmlPath = System.getProperty("user.home");
				File fXmlFile = new File(xmlPath + "/test.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				
				doc.getDocumentElement().normalize();
				System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
				
				appContainer = new AppletContainer(doc.getDocumentElement());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} else {
			//load either qualification or an image to label
			//(depends on if you've passed certification or not)
			
			appContainer = new AppletContainer(certified);
		}
		//Buttons
		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (appContainer.hasMoreImages()) {
					appContainer.loadAnotherImage();
					submitted = false;
				} else {
					JOptionPane.showMessageDialog(appContainer, "Out of images! Thank you.");
				}
			}
		});
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(certified)
				{
					try {
						//appContainer.saveImage();
						appContainer.exportData();
						btnSubmit.setEnabled(true);
					} catch (ParserConfigurationException | TransformerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					submitted = true;
				} else{
					appContainer.Qualify();
					if (appContainer.ruCertified()) {
						messLabel.setText(messCert);
						submitted = true;
					} else {
						messLabel.setText(messnCert);
					}
				}
			}
		});

		btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appContainer.getApp().undo();
			}
		});
		
		//Label that shows user whether certified or not
		messLabel = new JLabel(" ");
		
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(appContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(messLabel))
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnUndo))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap(111, Short.MAX_VALUE)
							.addComponent(btnNext))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap(94, Short.MAX_VALUE)
							.addComponent(btnSubmit)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap(34, Short.MAX_VALUE)
							.addComponent(messLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(appContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnUndo)))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnSubmit)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnNext)
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
		
		int x1 = (int) (1.5 * (appContainer.getBounds().width + btnSubmit.getBounds().width)), 
			y1 = (int) (1.75 * (appContainer.getBounds().height + btnSubmit.getBounds().height + messLabel.getBounds().height));
		frame.setBounds(100, 100, x1, y1);
		frame.setMinimumSize(new Dimension((int)frame.getBounds().getWidth(), (int)frame.getBounds().getHeight()));
		frame.setResizable(GUI.RESIZABLE);
		
		if (certified)
			messLabel.setText(messCert);
		else
			messLabel.setText(messnCert);
		
		startListener();
	}

	private class Listener implements Runnable {

		@Override
		public void run() {
			while (true) {
				if (!certified)
					certified = appContainer.ruCertified();
				if (!appContainer.isLoading())
					updateButtons();
			}
		}
		private void updateButtons() {
			undoSubmitLocked = appContainer.getApp().getBoxCoords().size() < 1 || submitted;
			nextLocked = !submitted;//just for clean naming

			btnUndo.setEnabled(!undoSubmitLocked);
			btnSubmit.setEnabled(!undoSubmitLocked);
			btnNext.setEnabled(!nextLocked);
		}
	}

	private void startListener() {
		if (listener == null || !listener.isAlive()) {
			listener = new Thread(new Listener());
			System.out.println("Starting a Listener");
			listener.start();
		}

	}
}
