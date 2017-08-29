import org.iiitb.es103_15.traffic.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.*;
public class TestCar53 extends Car 
{
	private TrafficSignal ts;
	private Random randomGenerator;
	private float new_ac;
	private boolean flag = false, signal = false, occupied = false;
	private int remove;
	RoadGrid rg = new RoadGrid();
	handle l = new handle();
	Road poss_turn;
	Object ab = new Object();
	

	public TestCar53()
	{
		super();
		remove = -1;
	}

	private class handle implements TrafficSignal.SignalListener
	{
		public int state;
		public void onChanged(int curr)
		{
			if(curr == 1)
			{
				remove = RoadGrid.getOppDir(getDir());
				turn();	
				signal = false;
			}
		}
	}
	
	public void turn() 
	{
		Intersection end;
		if (getDir() == RoadGrid.NORTH || getDir() == RoadGrid.WEST)
			end = getRoad().getStartIntersection();
		else
			end = getRoad().getEndIntersection();
		Road poss_roads[] = end.getRoads();
		poss_turn = getPossTurn(poss_roads, getRoad().getDir());
		if(poss_turn.getEndIntersection().equals(end)) 
		{
			crossIntersection(end, rg.getOppDir(poss_turn.getDir()));
		}
		else
			crossIntersection(end, poss_turn.getDir());					
	}

	protected void updatePos() 
	{
		super.updatePos();
		Road poss_turn;
		Intersection end;
		Intersection start;
		Coords e1 = getRoad().getEndIntersection().getCoords();
		Coords e2 = getRoad().getStartIntersection().getCoords();
		int mid = (int)Math.sqrt(e1.distSqrd(e1,e2))/2;
		
		new_ac = (getRoad().getSpeedLimit()- getSpeed())*5;
	
		if(remove != -1) 
		{
			ts.removeListener(l, remove);	
			remove = -1;
			flag = false;
		}
		
		if (getDir() == RoadGrid.NORTH || getDir() == RoadGrid.WEST)
		{
			end = getRoad().getStartIntersection();
			start = getRoad().getEndIntersection();
		}
			
		else
		{
			end = getRoad().getEndIntersection();
			start = getRoad().getStartIntersection();
		}
		
		if((int)Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))>mid+10)
			accelerate(new_ac,1000);
		else if(!occupied && !signal && getSpeed()<10 && (int)Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))<=mid+10 )
		{
			//System.out.println("Speed :" + getSpeed()+ " " +this);
			//new_ac = (int)((getRoad().getSpeedLimit()*getRoad().getSpeedLimit())/(2*Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))));
			accelerate(new_ac,1000);
		}
		else if ((int)Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))<=mid+10)
		{
			new_ac = (float) (-(getSpeed()*getSpeed())/(2*(Math.sqrt(getPos().distSqrd(getPos(),end.getCoords())))));
			accelerate(new_ac,1000);
		}
		
		if((int)Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))<mid/2) 
		{
			if (end.getTrafficControl()==null)
			{
				if (!end.isOccupied())
				{
					if((int)Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))<20)
					{
						turn();
					}
				}
			}
			else if (end.getTrafficControl()!=null)
			{
				if((int)Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))<20)
				{
					Road poss_roads[] = end.getRoads();
					TrafficControl tc = end.getTrafficControl();
					if (tc.getType()==0)
					{
						ts = (TrafficSignal)tc;
						if (!end.isOccupied())
						{
							if (ts.getSignalState(rg.getOppDir(getDir())) !=1 && !flag)
							{
								synchronized (ab)
								{
									ts.addListener(l, rg.getOppDir(getDir()));
									flag = true;
									signal = true;
								}
							}
							/*
							else if (new_ac == 0 && Math.sqrt(getPos().distSqrd(getPos(),end.getCoords()))>=20 && ts.getSignalState(rg.getOppDir(getDir())) ==1)
							{
								new_ac = (int)((getRoad().getSpeedLimit()*getRoad().getSpeedLimit())/(2*Math.sqrt(getPos().distSqrd(getPos(),end.getCoords())))); 
								accelerate(new_ac,1000);
								System.out.println("c");
							}*/
							else if(ts.getSignalState(rg.getOppDir(getDir())) ==1)
							{
								turn();
							}
						}
					}
				}	
			}
		}
	}
	
	private Road getPossTurn(Road roads[], int initial_dir)
	{
		randomGenerator = new Random();
		ArrayList<Road> poss = new ArrayList<Road>();
		for(int i = 0;i<=3;i++)
		{
			if (roads[i] != null && !roads[i].equals(getRoad()))
			{
				poss.add(roads[i]);
			}
		}
		int index = randomGenerator.nextInt(poss.size());
		return poss.get(index);
	}
	
	public void carInFront(Car obstacle) 
	{
		if (obstacle!=null)
		{
			Coords front = obstacle.getPos();
			if((int)Math.sqrt(getPos().distSqrd(getPos(),front))<40)
			{
				new_ac = (-(int)(Math.abs((obstacle.getSpeed()*obstacle.getSpeed())-(getSpeed()*getSpeed()))/(5)));
				accelerate(new_ac,1000);
				occupied = true;
			}
		}
		else {
			occupied = false;
		}
	}
	
	protected void crossIntersection(Intersection inter, int dir) 
	{
		super.crossIntersection(inter, dir);
	}

	public void setInitialPos(Road r, Coords loc, int dir) 
	{
		super.setInitialPos(r, loc, dir);
	}

	protected void accelerate(float d, int duration) 
	{
		super.accelerate(d,duration);
	}
	public void paint(Graphics g) {
		g.setColor(Color.orange);
		g.setFont(Font.getFont("Trisha"));
		super.paint(g);
		
	}
}