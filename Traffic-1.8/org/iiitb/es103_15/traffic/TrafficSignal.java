package org.iiitb.es103_15.traffic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class TrafficSignal extends TrafficControl {

	public final static int RED_LIGHT = 0;
	public final static int GREEN_LIGHT = 1;

	Intersection inters;
	
	// private class SignalLight is used to manage listeners in each direction
	private SignalLight signalLights[] = new SignalLight[4];
	int del; // duration of each signal in millisecs
	private int currGreenDir; 
	
	public TrafficSignal(Intersection inter, int delay) {
		inters = inter;
		del = delay;
		Road[] roads = inter.getRoads();
		boolean first = true;
		for (int i=0;i<4;i++) {
			if(roads[i] != null) {
				signalLights[i] = new SignalLight();
				if(first) {
					first = false;
					currGreenDir = i;
					signalLights[i].setSignal(GREEN_LIGHT);
				}
			}
			else 
				signalLights[i] = null;
		}
	}
	
	public Intersection getIntersection() {
		return inters;
	}
	
	public int getType() {
		return SIGNAL_LIGHT;
	}
	
	private void changeSignal() {
		// move green light clockwise
		signalLights[currGreenDir].setSignal(RED_LIGHT);
		currGreenDir = RoadGrid.getLeftDir(currGreenDir);
		while(signalLights[currGreenDir] == null) {
			currGreenDir = RoadGrid.getLeftDir(currGreenDir);
		}
		signalLights[currGreenDir].setSignal(GREEN_LIGHT);
	}
	
	
	// for intersections with SIGNAL_LIGHTS, returns
	// status of signal in a given direction.
	public int getSignalState(int dir) {
		if(dir == currGreenDir)
			return GREEN_LIGHT; 
		else
			return RED_LIGHT;
	}

	public void startSimulation() {
		// start the timers going
		Thread t = new Thread() {
			public void run() {
				try {
					while(true) {
						changeSignal();
						Thread.sleep(del - 100);
					}
				} catch (RuntimeException  e) {  // prevent it from crashing when multiple cars are running
					System.err
							.println("Exception while changing signal");
					e.printStackTrace();
					throw e;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public void paint(Graphics g) {
		
		Coords loc = inters.getCoords();
		g.setColor(Color.GREEN);
		switch(currGreenDir) {
		case RoadGrid.NORTH:
			g.fillOval(loc.x-RoadGrid.LANE_HALF_WIDTH, loc.y - (RoadGrid.LANE_WIDTH+RoadGrid.LANE_HALF_WIDTH), 
					RoadGrid.LANE_WIDTH, RoadGrid.LANE_WIDTH);
			break;
		case RoadGrid.EAST:
			g.fillOval(loc.x + RoadGrid.LANE_HALF_WIDTH, loc.y-RoadGrid.LANE_HALF_WIDTH, 
					RoadGrid.LANE_WIDTH, RoadGrid.LANE_WIDTH);
			break;
		case RoadGrid.SOUTH:
			g.fillOval(loc.x-RoadGrid.LANE_HALF_WIDTH, loc.y + RoadGrid.LANE_HALF_WIDTH, 
					RoadGrid.LANE_WIDTH, RoadGrid.LANE_WIDTH);
			break;
		case RoadGrid.WEST:
			g.fillOval(loc.x - (RoadGrid.LANE_WIDTH+RoadGrid.LANE_HALF_WIDTH), loc.y-RoadGrid.LANE_HALF_WIDTH,
					RoadGrid.LANE_WIDTH, RoadGrid.LANE_WIDTH);
			break;
		}
		g.setColor(RoadGrid.DEFAULT_COLOR);
	}


	// observes a traffic signal at an intersection
	public interface SignalListener {	 

		public void onChanged(int currState); // called with new state of signal light
	
	}

	public void addListener(SignalListener listener, int dir) {
		if(signalLights[dir] != null)
			signalLights[dir].addListener(listener);
		else {
			System.out.println("Installing listener in wrong direction: " + dir);
		}
		
	}

	public void removeListener(SignalListener listener, int dir) {
		signalLights[dir].removeListener(listener);
	}
	
	//private class for TrafficSignal, managing signal in each direction
	private class SignalLight {
		private ArrayList<SignalListener> signalListeners; 
		int lightColor;
		
		SignalLight() {
			signalListeners = new ArrayList<SignalListener>();
			lightColor = TrafficSignal.RED_LIGHT;
		}
		void setSignal(int lightState) {
			if(lightColor != lightState) {
				lightColor = lightState;
				for(SignalListener listener: signalListeners)
					listener.onChanged(lightColor);
			}
		}
		
		
		public void addListener(SignalListener listener) {
			signalListeners.add(listener);
			
		}

		public void removeListener(SignalListener listener) {
			if(signalListeners.indexOf(listener) < 0) {
				System.err.println("Trying to remove listener that does not exist");
			}
			signalListeners.remove(listener);
		}
	}


}

