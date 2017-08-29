package org.iiitb.es103_15.traffic;

import java.awt.Graphics;


//The base class Car provides the basic functionality of a car platform - no
//smarts built in. The derived classes are expected to provide the intelligence
//of autonomy.

public class Car {

	static int baseCarIds = 1;  // unique numbering for each car
	private int carId;			// id of this car
	private Road currRoad;
	private Coords currPos;
	private int carDir;
	private int lane;
	private float currSpeed;
	private float accel;
	private long lastUpdateTime; // when updatePos was last called
	private float distCovered;
	private int penalty;
	private int bonus;
	private boolean hasCrashed;
	private String lastViolation = "";

	public final static int WIDTH = 8;
	public final static int HALF_WIDTH = WIDTH/2;
	
	public Car() {
		carId = baseCarIds++;
	}

	public String toString() {
		return "Car " + carId;
	}
	
	public int getId() {
		return carId;
	}
	// location of the mid point of the front of the car
	public final Coords getPos() {
		return new Coords(currPos.x, currPos.y);
	}

	
	// direction the car is pointing to
	public final int getDir() {
		return carDir;
	}

	// road it is on
	public Road getRoad() {
		return currRoad;
	}

	public String getLastViolation() {
		return lastViolation;
	}
	// is it in the left or right lane. Not used yet
	public final int getLane() {
		return lane;
	}

	// current speed of car
	public final float getSpeed() {
		return currSpeed;
	}

	// returns array of 3 values: penalties, bonus, performance
	public final int[] getScore() {
		int[] score = new int[4];
		score[0] = penalty;
		score[1] = bonus;
		score[2] = (int) distCovered;
		score[3] = (hasCrashed? 1 : 0);
		return score;
	}

	public void setRoad(Road road, int dir) {
		if(currRoad != null) {
			currRoad.remove(this, carDir);
		}
		currRoad = road;
		carDir = dir;
		road.add(this, carDir);
	}
	
	// set initial position of car.
	// Cannot be called once drive() is invoked
	public void setInitialPos(Road r, Coords loc, int dir) {
		currPos = new Coords(loc.x, loc.y);
		setRoad(r, dir);
	}

	protected boolean isAccelerating() {
		return (accel > 0.1 || accel < -0.1);
	}
	
	private void moveCar(int dist) {
		switch (carDir) {
		case RoadGrid.WEST:
			currPos.x -= dist;
			break;
		case RoadGrid.NORTH:
			currPos.y -= dist;
			break;
		case RoadGrid.EAST:
			currPos.x += dist;
			break;
		case RoadGrid.SOUTH:
			currPos.y += dist;
			break;
		}
		distCovered += dist;
	}

	
	// base class methods used by derived classes to navigate the car.
	// Use getPos, getDir etc to get current
	// location of car
	// Any overriding of these methods should first invoke the super method

	// The base class updates position of the car using stored parameters
	// Expected to be invoked at short time intervals (e.g. 0.2 secs)
	protected void updatePos() {
		long timeNow = System.currentTimeMillis();
		int dt = 0;
		if (lastUpdateTime > 0) {  // has already been initialized. Move the car
			dt = (int) (timeNow - lastUpdateTime);
			float newSpeed = currSpeed + accel * dt / 1000;
			if (newSpeed < 0.0f) {
				newSpeed = 0.0f;
				accel = 0.0f;
			}
			float dist = (currSpeed + newSpeed) * dt / 2000; // avg speed * time
			currSpeed = newSpeed;
			if (currSpeed > getRoad().getSpeedLimit() * 1.2) {
				System.out.println("Violation: Exceeded speed limit: " + this);
				penalty++;
				// lastViolation = "Speed Limit";
			}
			moveCar((int) dist);
		}
		lastUpdateTime = timeNow;
	}

	// set acceleration rate. negative for deceleration
	// applied until it is changed
	// stops deceleration when speed reaches 0
	protected void accelerate(float d) {
		accel = d;
		// save the current speed and time
		// will be used in the next update (triggered by timer)

	}

	protected void accelerate(float d, int duration) {
		accelerate(d);
	}
	
	// cross or turn in given direction
	// Should be called when exactly at intersection
	protected void crossIntersection(Intersection inter, int dir) {
		// first check if running a red light
		TrafficControl tc = inter.getTrafficControl();
		if (tc != null && tc.getType() == TrafficControl.SIGNAL_LIGHT) {
			int oppDir = RoadGrid.getOppDir(getDir());
			TrafficSignal ts = (TrafficSignal)tc;
			if(ts.getSignalState(oppDir) == TrafficSignal.RED_LIGHT) {
				// violation
				System.out.println("Violation: Running a Red light: " + this);
				penalty += 10;
				lastViolation = "Red Light";
			}
		}
		
		// check if intersection is already occupied - should not have been called.
		if(inter.isOccupied()) {
			// violation
			System.out.println("Violation: Entering occupied intersection:" + this);
			lastViolation = "Blocking intersection";
			penalty += 5;
		}
		
		inter.enter(); // record that you are using the intersection
		
		// jumps forward to the turning point and points in the desired
				// direction
		Coords loc = inter.getCoords();
		
		currPos.x = loc.x;
		currPos.y = loc.y;
	
		Road newRoad = inter.getRoad(dir);
		if(newRoad == null){  // bad turn - no road here
			System.out.println("Crashed: No road to turn into");
			hasCrashed = true;
		} else {
			setRoad(newRoad, dir);
			moveCar(RoadGrid.LANE_WIDTH); // make the corner turn look a bit better
		}
		inter.exit();
	}

	public void carInFront(Car obstacle) {
		
	}
	
	// moves to the right lane in the next time step. Not yet implemented
	protected void moveRight() {
	}

	// moves to the left lane in the next time step. Not yet implemented
	protected void moveLeft() {
	}

	// customize behaviour of the subclass
	// length of car
	public int getLength() {
		return 12;
	}

	// get position and sizes of car in screen coordinates to enable drawing
	private void getRectVals(int[] sizes){
		switch(carDir) {
		case RoadGrid.NORTH:
			sizes[0] = currPos.x - (RoadGrid.LANE_HALF_WIDTH + HALF_WIDTH);
			sizes[1] = currPos.y;
			sizes[2] = WIDTH;
			sizes[3] = getLength();
			break;
		case RoadGrid.EAST:
			sizes[0] = currPos.x - getLength();
			sizes[1] = currPos.y - (RoadGrid.LANE_HALF_WIDTH + HALF_WIDTH);
			sizes[2] = getLength();
			sizes[3] = WIDTH;
			break;
		case RoadGrid.SOUTH:
			sizes[0] = currPos.x + (RoadGrid.LANE_HALF_WIDTH - HALF_WIDTH);
			sizes[1] = currPos.y - getLength();
			sizes[2] = WIDTH;
			sizes[3] = getLength();

			break;
		case RoadGrid.WEST:
			sizes[0] = currPos.x;
			sizes[1] = currPos.y + (RoadGrid.LANE_HALF_WIDTH - HALF_WIDTH);
			sizes[2] = getLength();
			sizes[3] = WIDTH;
			break;
		}
		
	}
	// display the car graphically using Swing facilities
	// default method draws a rectangle in the correct position and
	// orientation
	// You should minimally display something that makes it easy to identify the
	// car
	public void paint(Graphics g) {
		int sizes[] = new int[4];
		getRectVals(sizes);
		
		g.fillRect(sizes[0],sizes[1], sizes[2], sizes[3]);
		g.drawString(toString(),sizes[0], sizes[1]);
		
	}

	// main method that initializes the navigation of the car. Called once.
	// Essentially "starts the engine" - triggers calls to updatePos
	// should be called by derived methods if overridden
	
	public void drive() {
		Thread t = new Thread() {
			public void run() {
				try {
					lastUpdateTime = 0; // Starting calls to updatePos
					while (!hasCrashed) {
						updatePos();
						Thread.sleep(100);
					}
				} catch (RuntimeException e) { // prevent it from crashing when
												// multiple cars are running
					System.err
							.println("Exception while processing updatePos for car: "
									+ toString());
					e.printStackTrace();
					throw e;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
}
