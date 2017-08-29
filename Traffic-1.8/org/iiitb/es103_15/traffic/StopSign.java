package org.iiitb.es103_15.traffic;

import java.awt.Graphics;

public class StopSign extends TrafficControl {

	public int getType() {
		return STOP_SIGN;
	}
	
	// mechanism for these to be implemented
	// at STOP_SIGNS, returns the Car that currently has priority (arrived first)
	public Car getFirst() {
		return null;
	} 

	public void paint(Graphics g) {
		// TODO:
	}
}
