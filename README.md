# --------------- 
# This README is still a work in progress and should be completed soon. Sorry for the inconvenience 
# ---------------
# NU YSP 2016: Interface for Drawing Bounding Boxes in Images

This README will detail the layout and structure of our project, and explain how to get certain data from our code.

The end goal is to have an interface where an unlabelled image is the input and a labelled image (one containing a query and bounding box describing each object) is the output. 

To accomplish this, a few components were programmed sequentially.

# The Java Applet (TurkApplet.java)
To start, we first designed a Java Applet that could be used to draw rectangles on images. The interface is relatively intuitive-- it uses click-and-drag to draw blue rectangles that turn green once they are valid (i.e., a query is associated with them and the mouse button is released).

This applet acts as the central component of the project. If we choose to use a resource such as Amazon Mechanical Turk (AMT), then it is likely that AMT will run the applet directly. 

The applet works by extending a superclass known as JApplet. 
There are constants at the top of the class that control features such as the stroke weight of the bounding boxes, the maximum dimensions of an image loaded (if an image is larger, then it gets scaled down to these dimensions), and the minimum word count for an object query.

Within the applet, there is a subclass called DrawingPanel that extends JPanel. This is the class that handles image display, drawing, and scaling. ...More stuff about drawingPanel here...

# The Applet Container (AppletContainer.java)

There are a few public "getter" and "setter" methods within the applet that can interact with the applet to manipulate data. In order to run the applet as a standalone, we designed a JPanel known as the AppletContainer. 

...more stuff about constructors/qualification/image bank/exporting data/scaled and unscaled images here...

# Qualification

This is still being worked on.

# GUI

Talk about buttons and multithreadedness here

# Tools
# Tool: Quick Image Sort (QuickImageSort.java)

We wrote a quick GUI tool for distinguishing between "good" and "bad" pictures. Like the TurkApplet's GUI container, this tool was also designed with the WindowBuilder eclipse plugin and uses GroupLayout to arrange the components. There is a String constant at the top of the class to control the image directory; when the program is run, a "keep" and "delete" folder are made in the specified directory. The tool then loads every .png image in the directory and allows the user to quickly cycle through each image by clicking either the keep button or the delete button. When a button is clicked, the current image is moved to the associated folder. If there are any more images, then the next image is loaded; else, a pop-up appears signifying that there are no more .png files to move in the folder. 

Note that the tool does not account for images moved into (or out of) the folder after it has already started. If more images need to be moved, then the program must be restarted. 

#Tool: Image Renamer (ImageRenamer.java)

We also wrote a quick tool for renaming pictures. There is a String constant at the top of the class for specifying the image directory. When the tool is run, every .png file in the specified directory is renamed as the last date modified, in the format of "yyyy-MM-dd HH-mm-ss".
