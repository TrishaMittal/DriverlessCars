package org.iiitb.es103_15.traffic;

import java.awt.Graphics;

public abstract class TrafficControl {
	public final static int SIGNAL_LIGHT = 0;
	public final static int STOP_SIGN = 1;
	
	// the type of the control, as above
	public abstract int getType();
	
	public void startSimulation() {
		
	}
	public void paint(Graphics g) {
		
	}
}


