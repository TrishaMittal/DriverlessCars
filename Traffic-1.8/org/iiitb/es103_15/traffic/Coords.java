package org.iiitb.es103_15.traffic;

public class Coords {
	public int x, y;
	
	public Coords(int xc, int yc) {
		x = xc; y = yc;
	}
	
	public static int distSqrd(Coords c1, Coords c2) {
		int delx, dely;
		
		delx = c1.x - c2.x;
		dely = c1.y - c2.y;
		
		return delx*delx + dely*dely;
		
	}
	
}
