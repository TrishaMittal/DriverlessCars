package org.iiitb.es103_15.traffic;

import java.awt.Graphics;
import java.util.ArrayList;


public class Road {
	private int roadDir;
	private Intersection startInt, endInt;
	private boolean[] entryAllowed = new boolean[2];
	private int speedLimit = 40;
	private ArrayList<Car> upCars; // cars in the direction of the road
	private ArrayList<Car> downCars; // cars in opposite direction
	
	public Road(int dir, Intersection start, Intersection end){
		this(dir, start, end, true, true);
	}
	
	// always specified as N start and S end (or) W start and E end
	public Road(int dir, Intersection start, Intersection end, boolean entryStart, boolean entryEnd){
		roadDir = dir;
		startInt = start;
		endInt = end;
		entryAllowed[0] = entryStart;
		entryAllowed[1] = entryEnd;
		upCars = new ArrayList<Car>(4);
		downCars = new ArrayList<Car>(4);
		
		if(dir == RoadGrid.SOUTH) {
			start.setRoad(this,  RoadGrid.SOUTH);
			end.setRoad(this,  RoadGrid.NORTH);
		} else {
			start.setRoad(this,  RoadGrid.EAST);
			end.setRoad(this,  RoadGrid.WEST);
		}
	}
	
	public void setSpeedLimit(int limit) {
		speedLimit = limit;
	}
	// returns SOUTH (meaning N-S road) or EAST (meaning W-E road)
	public int getDir() {
		return roadDir;
	} 

	// returns reference to start and end Intersections
	public Intersection getStartIntersection() {
		return startInt;
	} 

	public Intersection getEndIntersection() {
		return endInt;
	}
	
	// if entry is allowed in desired direction.
	// false implies no entry
	// Not yer used
	public boolean entryAllowed(int dir) {
		return ((dir == roadDir) ? entryAllowed[0]: entryAllowed[1]);
		
	}

	// speed limit for current road
	public int getSpeedLimit() {
		return speedLimit;
	} 

	public void add(Car car, int dir) {
		synchronized (this) {
			if (dir == roadDir) {
				upCars.add(car);
			} else {
				downCars.add(car);
			}
		}
	}
	
	public void remove(Car car, int dir) {
		synchronized (this) {
			if (dir == roadDir) {
				upCars.remove(car);
			} else {
				downCars.remove(car);
			}
		}
	}

	public ArrayList<Car> findCars(int dir) {
		if(dir == roadDir) {
			return new ArrayList<Car>(upCars);
		} else {
			return new ArrayList<Car>(downCars);
		}
	}
	
	protected ArrayList<Car> getCarsL(int dir) {  // meant for fast local access
		if(dir == roadDir) {
			return upCars;
		} else {
			return downCars;
		}
	}
	
	public synchronized void checkCollisions() {
		
	}
	
	public void startSimulation () {
		Thread t = new Thread() {
			public void run() {
				try {
					while(true) {
						checkCollisions();
						Thread.sleep(500);
					}
				} catch (RuntimeException  e) {  // prevent it from crashing when multiple cars are running
					System.err
							.println("Exception while checking for collisions");
					e.printStackTrace();
					throw e;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	void paint(Graphics g) {
		Coords startCoords = startInt.getCoords();
		Coords endCoords = endInt.getCoords();
		g.drawLine(startCoords.x,startCoords.y, endCoords.x, endCoords.y);
	}
	
}
