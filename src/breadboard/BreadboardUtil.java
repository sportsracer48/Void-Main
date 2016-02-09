package breadboard;

import game.item.Pin;

import java.util.List;

public class BreadboardUtil
{
	public static void linkHoriz(List<Pin> pins, int start, int num)
	{
		boolean ground = false;
		int potential = 0;
		for(int i = start; i<start+num; i++)
		{
			ground |= pins.get(i).leadsToGround();
			potential = Math.max(potential,pins.get(i).getReceivedPotential());
		}
		for(int i = start; i<start+num; i++)
		{
			pins.get(i).setTotalPotential(potential);
			pins.get(i).setTotalGrounding(ground);
		}
	}
	public static void linkVert(List<Pin> pins, int start, int num, int width)
	{
		boolean ground = false;
		int potential = 0;
		for(int i = 0; i<num; i++)
		{
			int pinId = start+i*width;
			ground |= pins.get(pinId).leadsToGround();
			potential = Math.max(potential, pins.get(pinId).getReceivedPotential());
		}
		for(int i = 0; i<num; i++)
		{
			int pinId = start+i*width;
			pins.get(pinId).setTotalPotential(potential);
			pins.get(pinId).setTotalGrounding(ground);
		}
	}
}
