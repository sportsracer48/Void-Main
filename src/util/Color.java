package util;

import math.Matrix;

public class Color extends Matrix
{
	public static Color white = new Color(1,1,1,1);
	public static Color red = new Color(0x9c071d);
	public static Color green = new Color(0x5ca242);
	public static Color blue = new Color(0x3f469e);
	public static Color cyan = new Color(0x2a86d1);
	public static Color magenta = new Color(0xb4748e);
	public static Color yellow = new Color(0xcfa618);
	public static Color black = new Color(0,0,0,1);
	
	public static Color orange = new Color(0xcf794a);
	public static Color brown = new Color(0x574231);
	
	public Color(float r, float g, float b, float a)
	{
		super(new float[]{r,g,b,a});
	}
	
	public Color(int code)
	{
		this(((code>>>16) & 0xFF)/255f,((code>>>8) & 0xFF)/255f,((code>>>0) & 0xFF)/255f,1);
	}
}
