import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import org.iiitb.es103_15.traffic.*;
import org.iiitb.es103_15.traffic.TrafficSignal.*;

public class TestCar08 extends Car {
	float a,dist;
	Intersection start, end;
	Listener lis;
	TrafficSignal ts;
	int listenerDir;
	Road r;
	boolean crossed, atInter,occupied;
	Car obs;
	//Color[] color = {Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.MAGENTA, Color.RED,Color.YELLOW, Color.PINK, Color.GREEN};
	//static int c = 0;
	public TestCar08() {
		super();
		crossed = false;
		atInter = false;
		obs = null;
	}
	private class Listener implements SignalListener {
		public void onChanged(int state) {
			if(state == TrafficSignal.GREEN_LIGHT) {
				turn();
				crossed = true;
				atInter = false;
				occupied = false;
			}
		}
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.RED);
		super.paint(g);
	}
	public void drive() {
		super.drive();
		r = getRoad();
		if(getDir() == RoadGrid.NORTH || getDir() == RoadGrid.WEST) {
			end = r.getStartIntersection();
			start = r.getEndIntersection();
		}
		else {
			start = r.getStartIntersection();
			end = r.getEndIntersection();
		}
		start.getCoords();
		dist = (float) Math.sqrt(Coords.distSqrd(start.getCoords(), end.getCoords()));
	}
	public void setInitialPos(Road r, Coords loc, int dir) {
		super.setInitialPos(r, loc, dir);
	}
	
	public void accelerate(float a, int duration) {
		if(getSpeed()+a*0.1 > getRoad().getSpeedLimit())
			accelerate((getRoad().getSpeedLimit() - getSpeed())*10);
		else
			super.accelerate(a, duration);
	}
	
	// called at regular intervals. Do any processing here
	protected void updatePos() {
		super.updatePos();
		float v = getSpeed();
		float diste, dists;
		Coords pos = getPos();
		Road r = getRoad();
		dists = (float) Math.sqrt(Coords.distSqrd(pos, start.getCoords()));
		diste = (float) Math.sqrt(Coords.distSqrd(pos, end.getCoords()));
		if(crossed) {
			synchronized(ts) {
				//System.out.println("REMOVE "+this+" dir " + listenerDir);
				ts.removeListener(lis, listenerDir);
				crossed = false;
				obs = null;
			}
		}
		else if(obs!=null) {
			float d = (float) Math.abs(Math.sqrt(Coords.distSqrd(getPos(), obs.getPos())) - 2*obs.getLength());
			if(d == 0)
				d = 1;
			a = (float) (Math.pow(obs.getSpeed(),2) - v*v)/(2*d);
			//System.out.println("Obstacle Detected!"+obs.getSpeed()+" "+getSpeed()+" " + this + " car: "+obs+" dist = "+d + " a = "+ a);
			//if(obs.getSpeed() < v)
				//a = (float) (obs.getSpeed() - v)*5;
			//System.out.println(obs.getSpeed()+" "+v);
		}
		else if((diste < 1.5*RoadGrid.LANE_WIDTH && !atInter) || diste+dists > dist) {
			if( v!= 0) {
				if((diste-RoadGrid.LANE_HALF_WIDTH) == 0)
					a = (float) Math.floor(-Math.pow(v,2)/2);
				else
					a = (float) Math.floor((-Math.pow(v,2)/(2*(diste-RoadGrid.LANE_HALF_WIDTH))));
			}
			atInter = true;
			atIntersection();
		}
		else if(atInter) {
			//accelerate(0);
			if(occupied) {
				occupied = false;
				atIntersection();
			}
		}
		else if(diste < dist/4) {
			//System.out.println("one-fourth" + this);
			if(v>=10) {
				if((diste-RoadGrid.LANE_HALF_WIDTH) == 0)
					a = (float) Math.floor(-Math.pow(v,2)/2);
				else
					a = (float) Math.floor((-Math.pow(v,2)/(2*(diste-RoadGrid.LANE_HALF_WIDTH))));
			}
			else if(v<10 && obs==null)
				a = (float)(r.getSpeedLimit() - v)*5;
		}
		else {
			a = (float)(r.getSpeedLimit() - v)*5;
		}
		accelerate(a,0);
		
	}	
	
	public void atIntersection() {
		TrafficControl tc = end.getTrafficControl();
		if(end.isOccupied() || obs != null) {
			atInter = true;
			occupied = true;
			//System.out.println("Intersection Occupied!!!");
		}
		else {
			if(tc == null) {
					turn();
					atInter = false;
			}
			else {
				if(tc.getType() == TrafficControl.SIGNAL_LIGHT) {
					lis = new Listener();
					ts = (TrafficSignal) tc;
					if(ts.getSignalState(RoadGrid.getOppDir(getDir())) == TrafficSignal.GREEN_LIGHT) {
						turn();
						atInter = false;
					}
					else {
						synchronized(ts) {
						ts.addListener(lis, RoadGrid.getOppDir(getDir()));
						listenerDir = RoadGrid.getOppDir(getDir());
						//System.out.println("Add: "+this+" Dir: "+listenerDir);
						}
					}
				}
				else {
					//STOP SIGN!
					}
				
			}
		}
	}
	
	public void turn() {
		Random rand = new Random();
		Road [] rd = end.getRoads();
		Road currRoad = getRoad();
		ArrayList<Road> validRoads = new ArrayList<Road>();
		for(Road r: rd) 
			if(r!=null && !r.equals(currRoad))
				validRoads.add(r);
		Road r = validRoads.get(rand.nextInt(validRoads.size()));
		if(r.getEndIntersection().equals(end)) 
			crossIntersection(end, RoadGrid.getOppDir(r.getDir()));
		else
			crossIntersection(end, r.getDir());	
		
		r = getRoad();
		if(getDir() == RoadGrid.NORTH || getDir() == RoadGrid.WEST) {
			end = r.getStartIntersection();
			start = r.getEndIntersection();
		}
		else {
			start = r.getStartIntersection();
			end = r.getEndIntersection();
		}
		start.getCoords();
		dist = (float) Math.sqrt(Coords.distSqrd(start.getCoords(), end.getCoords()));
	}

	public void carInFront(Car obstacle) {
		obs = null;
		//if(obstacle != null)
			//System.out.println("Car in front..."+this+ " "+obstacle);
		if(obstacle != null) {
			float d = (float) Math.sqrt(Coords.distSqrd(getPos(), obstacle.getPos())) - 8*obstacle.getLength();
			if(d <= 0){ 
				obs = obstacle;
			}
		}
	}
}
