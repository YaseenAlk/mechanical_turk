package org.neu.ccs.mechanical_turk.tools;

import java.io.File;
import java.text.SimpleDateFormat;

public class ImageRenamer {

	private final static String imageDir = System.getProperty("user.home") + "/Desk Auto 15/";
	
	public static void main(String[] args) {
		File dir = new File(imageDir);
		System.out.println(dir.getAbsolutePath());
		File[] files = dir.listFiles();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".png")) {
				String oldName = files[i].getName();
				System.out.print(oldName);
				System.out.print(" --> ");
				String newName = sdf.format(files[i].lastModified());
				System.out.println(newName);
				
				if (!files[i].renameTo(new File(files[i].getParent() + "/" + newName))) {
					System.err.println("Failed to rename " + oldName + " to " + newName);
				} else {
					//System.out.println("Successfully renamed "  + oldName + " to " + newName);
				}
			}
		}
	}

}
