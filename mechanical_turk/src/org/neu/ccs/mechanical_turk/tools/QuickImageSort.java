package org.neu.ccs.mechanical_turk.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.neu.ccs.mechanical_turk.TurkApplet;

import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

/**
 * 
 * @author Yaseen Alkhafaji <alkhafaji.yaseen@gmail.com>
 *
 */
public class QuickImageSort {

	private JFrame frame;
	
	private final static String imageDir = System.getProperty("user.home") + "/Desktop/";
	
	private static ArrayList<File> images;
	private ImageContainer panel;
	private static int currentImg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		File dir = new File(imageDir);
		new File(imageDir + "/keep").mkdir();
		new File(imageDir + "/delete").mkdir();
		
		File[] files = dir.listFiles();
		images = new ArrayList<>(files.length);
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".png"))
				images.add(files[i]);
		}
		
		currentImg = -1;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					QuickImageSort window = new QuickImageSort();
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
	public QuickImageSort() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnKeep = new JButton("Keep");
		btnKeep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentImg < images.size()) {
					File afile = images.get(currentImg);
		    		afile.renameTo(new File(imageDir + "/keep/"+ afile.getName()));
		    		panel.loadNextImage();
				}
			}
		});
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (currentImg < images.size()) {
					File afile = images.get(currentImg);
			    	afile.renameTo(new File(imageDir + "/delete/"+ afile.getName()));
					panel.loadNextImage();
				}
			}
		});
		
		panel = new ImageContainer();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnKeep, Alignment.TRAILING)
						.addComponent(btnDelete, Alignment.TRAILING))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnDelete)
							.addPreferredGap(ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
							.addComponent(btnKeep)))
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
		
		panel.loadNextImage();
		
		//int x1 = (int) (panel.getBounds().width + btnDelete.getBounds().width), 
		//		y1 = (int) (panel.getBounds().height + btnKeep.getBounds().height);
		frame.setBounds(100, 100, 760, 400);
		frame.setMinimumSize(new Dimension((int)frame.getBounds().getWidth(), (int)frame.getBounds().getHeight()));
			
	}
	
	private class ImageContainer extends JPanel {
		BufferedImage img;
		
		private void loadNextImage() {
			currentImg++;
			if (currentImg >= images.size()) {
				JOptionPane.showMessageDialog(this,"All done!");
				return;
			}
			try {
				img = ImageIO.read(images.get(currentImg));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				img = null;
			}
			
			//check scaling
			if (img.getWidth() > 640) {
				double scalingFactorX = 640/((double)img.getWidth());
				img = scale(img, BufferedImage.TYPE_INT_ARGB, 640, img.getHeight(), scalingFactorX, 1);
			}
			if (img.getHeight() > 360) {
				double scalingFactorY = 360/((double) img.getHeight());
				img = scale(img, BufferedImage.TYPE_INT_ARGB, img.getWidth(), 360, 1, scalingFactorY);
			}
			
			//set size of the image
			int imgW = img.getWidth();
			int imgH = img.getHeight();
			setPreferredSize(new Dimension(imgW, imgH));
			
			repaint();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img != null) {
				g.drawImage(img, 0, 0, this);
			}
		}
		
		public BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
			BufferedImage dbi = null;
			if(sbi != null) {
				dbi = new BufferedImage(dWidth, dHeight, imageType);
				Graphics2D g = dbi.createGraphics();
				AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
				g.drawRenderedImage(sbi, at);
			}
			return dbi;
		}
		
	}
	
	
}
