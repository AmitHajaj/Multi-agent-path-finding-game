package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 *Frame to hold the panel.
 * All of the GUI components are in myPanel.
 * We only have here a constructor which call myPanel.
 *
 */
public class MyFrame extends JFrame{
	MyPanel panel;

	public MyFrame(String a){
		super(a);
		panel = new MyPanel();
		this.add(panel);
	}
}
