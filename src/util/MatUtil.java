package util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import graphics.Uniform;

public class MatUtil
{
	static FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
	
	public static float[] gluOrtho(float left, float right, float bottom, float top, float near, float far)
	{
		return new float[]
			{
				2f/(right-left),	0,				0,				-(right+left)/(right-left),
				0,				2f/(top-bottom),	0,				-(top+bottom)/(top-bottom),
				0,				0,				    -2f/(far-near),	-(far+near)/(far-near),
				0,				0,					0,				1f
			};
	}
	
	public static float[] identity()
	{
		return new float[]
			{
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				0,0,0,1
			};
	}
	
	public static void uniformMatrix(float[] matrix, Uniform location)
	{
		tempMatrix.clear();
		tempMatrix.put(matrix);
		tempMatrix.flip();
		
		location.setMat4(tempMatrix);
	}
}
