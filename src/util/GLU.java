package util;

import java.util.Hashtable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

public class GLU
{
	static Hashtable<Integer, String> errorTable = new Hashtable<>();
	static
	{
		errorTable.put(GL11.GL_INVALID_ENUM,"Invalid enum");
		errorTable.put(GL11.GL_INVALID_VALUE,"Invalid value");
		errorTable.put(GL11.GL_INVALID_OPERATION,"Invalid operation");
		errorTable.put(GL11.GL_STACK_OVERFLOW,"Invalid overflow");
		errorTable.put(GL11.GL_STACK_UNDERFLOW,"Invalid underflow");
		errorTable.put(GL11.GL_OUT_OF_MEMORY,"Out of memory");
		errorTable.put(GL30.GL_INVALID_FRAMEBUFFER_OPERATION,"Invalid framebuffer operation");
		errorTable.put(GL45.GL_CONTEXT_LOST,"Context lost");
	}
	
	public static String gluErrorString(int glError)
	{
		if(errorTable.containsKey(glError))
		{
			return errorTable.get(glError);
		}
		return String.format("Unknown error %s", Integer.toHexString(glError));
	}
}
