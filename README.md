#To do list
Things we couldn't get to will be listed here. 
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

# Qualification (Qualification.java) 

Qualification extends TurkApplet. This is so that the Qualification class can implement `setUrl()` `init()` `getBoxCoords` `getQueries`  This essentially allows Qualification to run as the turkApplet with just an additional test. 

Qualification is based on a scoring system out of 26.

Currently 16 out of 26 points, or 60%, is required to qualify.

1 point is awarded for a correct bounding box, and 1 point for a correct "key word" from their query. Both values can be modified using the `qualScore` variable. 

All user bounding boxes and queries are called from `qualCoord()` using `getBoxCoords` and `getQueries`

All of these ground truth values and key words are stored within `qualCoord()`, and need to be manually modified in their respective methods if the image is changed. This can be done within `init()` and this is currently done through only a web address. Two coordinates need to be entered and need to be diagnol from each other. The query words can be any desired word or number. It is recomended that they have a "descriptor", the name of the object , and the location of the object relative to others. 

Because all queries are called from within a list, they are first taken apart into pairs, which are made of two diaganol coordinates. Therefore, in order to check all the coordinates, a loop is run for every bounding box pair. Every user bounding box is then compared to all ground truth bounding boxes. This works the same for the queries where a loop is run for every query and checks with desired key words. 

However, for the user bounding box coordinates and the queries to be checked, they each have their own method `checkCoords(int x1, y1, int x2, int y2, Pair currentC)` and `checkForPossibilities(String query, String[] possibilities)` respectivley. The parameters `int x1` , `int y1` , `int x2` , `int y2` are the ground truth coordinates, found diagonaly on the ground truth bounding box. `Pair currentC` is the user's Pair that will be compared with the ground truth coordinates. For `checkForPossibilities(String query, String[] possibilities)` , `String query` is the user query for one object, and `String[] possibilities` is the possibilites for an object that it will be checked against. 

# GUI (GUI.java)

Talk about buttons and multithreadedness here

# Tools
# Tool: Quick Image Sort (QuickImageSort.java)

We wrote a quick GUI tool for distinguishing between "good" and "bad" pictures. Like the TurkApplet's GUI container, this tool was also designed with the WindowBuilder eclipse plugin and uses GroupLayout to arrange the components. There is a String constant at the top of the class to control the image directory; when the program is run, a "keep" and "delete" folder are made in the specified directory. The tool then loads every .png image in the directory and allows the user to quickly cycle through each image by clicking either the keep button or the delete button. When a button is clicked, the current image is moved to the associated folder. If there are any more images, then the next image is loaded; else, a pop-up appears signifying that there are no more .png files to move in the folder. 

Note that the tool does not account for images moved into (or out of) the folder after it has already started. If more images need to be moved, then the program must be restarted. 

#Tool: Image Renamer (ImageRenamer.java)

We also wrote a quick tool for renaming pictures. There is a String constant at the top of the class for specifying the image directory. When the tool is run, every .png file in the specified directory is renamed as the last date modified, in the format of "yyyy-MM-dd HH-mm-ss".
