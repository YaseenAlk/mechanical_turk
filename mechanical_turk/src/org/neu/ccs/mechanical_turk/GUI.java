package org.neu.ccs.mechanical_turk;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JLabel;

public class GUI {

	private JFrame frame;
	private volatile boolean undoSubmitLocked, submitted, nextLocked;
	private JButton btnNext, btnSubmit, btnUndo;

	private Thread listener;
	private AppletContainer appContainer;

	private volatile static boolean certified; 
	private boolean foundFile;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		//Check if certified
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

		appContainer = new AppletContainer(certified);

		btnNext = new JButton("Next");

		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitted = true;
				try {
					if(certified)
					{
						//appContainer.saveImage();
						appContainer.exportData();
					} 
					else
					{
						appContainer.Qualify();
					}
				} catch (ParserConfigurationException | TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appContainer.getApp().undo();
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(appContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
										.addComponent(btnUndo))
								.addGroup(groupLayout.createSequentialGroup()
										.addContainerGap(329, Short.MAX_VALUE)
										.addComponent(btnNext))
								.addGroup(groupLayout.createSequentialGroup()
										.addContainerGap(312, Short.MAX_VALUE)
										.addComponent(btnSubmit)))
						.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(appContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
										.addContainerGap()
										.addComponent(btnUndo)))
						.addPreferredGap(ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
						.addComponent(btnSubmit)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnNext)
						.addContainerGap())
				);

		frame.getContentPane().setLayout(groupLayout);
		startListener();

		int x1 = 100 + appContainer.getBounds().width + btnSubmit.getBounds().width, 
				y1 = 100 + appContainer.getBounds().height + btnSubmit.getBounds().height;
		frame.setBounds(100, 100, x1, y1);
		frame.setMinimumSize(new Dimension((int)frame.getBounds().getWidth(), (int)frame.getBounds().getHeight()));
	}

	private class Listener implements Runnable {

		@Override
		public void run() {
			while (true) {
				updateButtons();
				if(certified)
				{
					//System.out.println("GUI thinks you are certified");
					appContainer = new AppletContainer(certified);
				}
				else 
					certified = appContainer.ruCertified();
			}
		}
		private void updateButtons() {
			undoSubmitLocked = appContainer.getApp().getBoxCoords().size() < 1;
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
