
import java.util.ArrayList;

import org.iiitb.es103_15.traffic.*;

public class SmartRoad extends Road {
	ArrayList<Car> upCars;
	ArrayList<Car> downCars;


	public SmartRoad(int dir, Intersection start, Intersection end) {
		super(dir, start, end);
		// TODO Auto-generated constructor stub
		upCars = new ArrayList<Car>();
		downCars = new ArrayList<Car>();

	}
	
	private boolean IsCarInFront(Car c1, Car c2) {
		Coords pos1 = c1.getPos();
		Coords pos2 = c2.getPos();
		int dir = c1.getDir();
		if(dir == RoadGrid.NORTH) {
			if(pos1.y > pos2.y)
				return true;
			else
				return false;
			}
		else if(dir == RoadGrid.SOUTH) {
			if(pos1.y < pos2.y)
				return true;
			else
				return false;
			}
		else if(dir == RoadGrid.EAST) {
			if(pos1.x < pos2.x)
				return true;
			else
				return false;
			}
		else {
			if(pos1.x > pos2.x)
				return true;
			else
				return false;
			}
		}
	public synchronized void checkCollisions() {
		upCars = this.getCarsL(RoadGrid.getOppDir(getDir()));
		downCars = this.getCarsL(getDir());
		Coords pos;
		int min;
		Car closestCar = null;
		Coords c = new Coords(400,400);

		if(upCars.size() >= 1) 
			upCars.get(0).carInFront(null);
		for(int i = 1; i < upCars.size();i++) {
			upCars.get(i).carInFront(upCars.get(i-1));
		}
		if(downCars.size() >= 1) { 
			downCars.get(0).carInFront(null);
			System.out.print(" " + downCars.get(0));
		}
		for(int i = 1; i < downCars.size();i++) {
			downCars.get(i).carInFront(downCars.get(i-1));
			System.out.print(" " + downCars.get(i));

		}
		System.out.println();
		/*if(upCars.size() == 1) 
			upCars.get(0).carInFront(null);
		else {
			for(Car curr: upCars) {
				pos = curr.getPos();
				min = Coords.distSqrd(getStartIntersection().getCoords(), getEndIntersection().getCoords()) + 1;
				closestCar = null;
				for(Car c: upCars) 
					if(!c.equals(curr) && IsCarInFront(curr, c) && Coords.distSqrd(pos, c.getPos()) < min) {
						min = Coords.distSqrd(pos, c.getPos());
						closestCar = c;
					}
				curr.carInFront(closestCar);
			}
		}*/
		
		/*else {
			for(Car curr: downCars) {
				pos = curr.getPos();
				min = Coords.distSqrd(getStartIntersection().getCoords(), getEndIntersection().getCoords()) + 1;
				for(Car c: upCars) 
					if(!c.equals(curr) && IsCarInFront(curr, c) && Coords.distSqrd(pos, c.getPos()) < min) {
						min = Coords.distSqrd(pos, c.getPos());
						closestCar = c;
					}
			}
		}*/
	}
}
