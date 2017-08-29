package org.iiitb.es103_15.traffic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class RoadGrid {
	// direction of travel
	public final static int NORTH = 0;
	public final static int EAST = 1;
	public final static int SOUTH = 2;
	public final static int WEST = 3;
	
	public final static Color DEFAULT_COLOR = Color.BLACK;

	// left and right lanes of a road, relative to direction of travel
	public final static int LEFT = 0;
	public final static int RIGHT = 1;

	public final static int LANE_WIDTH = 12; // default size of lanes
	public final static int LANE_HALF_WIDTH = LANE_WIDTH/2;

	private ArrayList<Intersection> intersections;
	private ArrayList<Road> roads;
	private ArrayList<Car> cars;
	
	private final static int[] oppDirs = {SOUTH, WEST, NORTH, EAST};
	private final static int[] leftDirs = {WEST, NORTH, EAST, SOUTH};
	private final static int[] rightDirs = {EAST, SOUTH, WEST, NORTH};
	
	public RoadGrid() {
		intersections = new ArrayList<Intersection>();
		roads = new ArrayList<Road>();
		cars = new ArrayList<Car>();
	}
	
	// convenience functions to get dir relative to curr direction
	public static int getOppDir(int dir) {
		
		return oppDirs[dir];
	}
	
	public static int getLeftDir(int dir) {
		
		return leftDirs[dir];
	}
	
	public static int getRightDir(int dir){
		
		return rightDirs[dir];
	}
	
	
	public void paint(Graphics g) {
		
		g.setColor(DEFAULT_COLOR);
		for(Intersection inter: intersections)
			inter.paint(g);

		for(Road road: roads)
			road.paint(g);

		for(Car car: cars)
			car.paint(g);
		
		
	}
	
	public void add(Intersection inter){
		intersections.add(inter);
	}
	
	public void add(Road road){
		roads.add(road);
	}
	public void add(Car car){
		cars.add(car);
	}

	public void startSimulation() {
		// trigger to start all the components
		for(Intersection inter: intersections)
			inter.startSimulation();

		for(Road road: roads)
			road.startSimulation();

		for(Car car: cars)
			car.drive();
	}
}

	