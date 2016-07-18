package org.neu.ccs.mechanical_turk;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI {

	private JFrame frame;
	private volatile boolean undoSubmitLocked, submitted, nextLocked;
	private TurkApplet app;
	private JButton btnNext, btnSubmit, btnUndo;
	
	private Thread listener;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		frame.setBounds(100, 100, 407, 362);
		frame.setMinimumSize(new Dimension((int)frame.getBounds().getWidth(), (int)frame.getBounds().getHeight()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		AppletContainer panel = new AppletContainer();
		app = panel.getApp();
		
		btnNext = new JButton("Next");
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitted = true;
				app.qualCoord();
			}
		});
		
		btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.undo();
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
							.addComponent(btnUndo))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap(321, Short.MAX_VALUE)
							.addComponent(btnNext))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap(321, Short.MAX_VALUE)
							.addComponent(btnSubmit)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnUndo)))
					.addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
					.addComponent(btnSubmit)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnNext)
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
		startListener();
	}
	
	 private class Listener implements Runnable {

			@Override
			public void run() {
				while (true) {
					updateButtons();
				}
			}
			private void updateButtons() {
				undoSubmitLocked = app.getBoxCoords().size() < 1;
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
