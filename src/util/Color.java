package util;

import math.Matrix;

public class Color extends Matrix
{
	public static Color white = new Color(1,1,1,1);
	
	public Color(float r, float g, float b, float a)
	{
		super(new float[]{r,g,b,a});
	}
}
