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

The applet works by extending a superclass known as JApplet. ...More stuff about drawingPanel and constants here...

# The Applet Container (AppletContainer.java)

There are a few public "getter" and "setter" methods within the applet that can interact with the applet to manipulate data. In order to run the applet as a standalone, we designed a JPanel known as the AppletContainer. 

...more stuff about constructors/qualification/image bank/exporting data/scaled and unscaled images here...

# Qualification

This is still being worked on.

# GUI

Talk about buttons and multithreadedness here

# Tools
# Tool: Quick Image Sort (QuickImageSort.java)

Talk about quick image sorting here

#Tool: Image Renamer (ImageRenamer.java)

Talk about the image renamer here
