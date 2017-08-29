package org.iiitb.es103_15.traffic;

import java.awt.Graphics;

public class Intersection {
	
	private Coords loc;
	private Road roads[] = new Road[4];    // roads in the 4 directions, N-E-S-W
	private TrafficControl tControl = null;
	private int inCars;
	
	public Intersection(int x, int y) {
		loc = new Coords(x, y);
	}
	
	public Intersection(Coords coords) {
		this(coords.x, coords.y);
	}
	
	 // position of the center of the intersection on the global grid
	public Coords getCoords() {
		return new Coords(loc.x, loc.y);
	}
	
	public void setRoad(Road r, int dir) {
		roads[dir] = r;
	}
	
	// find the four roads connecting at the intersection. 
	// One or more of the entries could be null.
	// The order corresponds to roads going E, N, W, S
	public Road[] getRoads() {
		return roads;
	} 
	
	public Road getRoad(int dir) {
		return roads[dir];
	}
	
	public void setTrafficControl(TrafficControl tc){
		tControl = tc;
	}
	
	// signal/sign at the intersection - null if there is none
	public TrafficControl getTrafficControl() {
		return tControl;
	} 

	// override these to improve quality of traffic information at intersections
	// 
	// if there is already a vehicle in the intersection
	public boolean isOccupied() {
		return (inCars > 0);
	} 

	void enter() {  // notify intersection that you are entering or leaving inter.
		inCars++;
	}
	
	void exit() {
		inCars--;
	}
		
	public void startSimulation() {
		// start the threads going, if any
		if ((tControl != null))
			tControl.startSimulation();
	}

	void paint(Graphics g) {
		
		g.drawOval(loc.x-RoadGrid.LANE_WIDTH, loc.y-RoadGrid.LANE_WIDTH, 
				2*RoadGrid.LANE_WIDTH, 2*RoadGrid.LANE_WIDTH);
		if(tControl != null)
			tControl.paint(g);
	}
}

