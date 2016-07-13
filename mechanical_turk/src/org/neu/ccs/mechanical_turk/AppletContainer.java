package org.neu.ccs.mechanical_turk;

import java.applet.Applet;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JPanel;

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
}
