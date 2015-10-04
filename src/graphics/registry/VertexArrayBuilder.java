package graphics.registry;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class VertexArrayBuilder
{
	int nVerticies;
	List<float[]> attribs=new ArrayList<>();
	List<Integer> offsets=new ArrayList<>();
	List<Integer> sizes=new ArrayList<>();
	int stride = 0;
	
	public VertexArrayBuilder(int nVerticies)
	{
		this.nVerticies=nVerticies;
	}
	
	public void addAttrib(int length, float[] attrib)
	{
		sizes.add(length);
		offsets.add(stride);
		stride += length*4;
		attribs.add(attrib);
	}
	
	public int buildVBO()
	{
		//assemble the floatbuffer in ram
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(nVerticies*stride);
		for(int vertex = 0; vertex<nVerticies; vertex++)
		{
			for(int attrib = 0; attrib<attribs.size(); attrib++)
			{
				for(int i = 0; i<sizes.get(attrib); i++)
				{
					float val = attribs.get(attrib)[vertex*sizes.get(attrib)+i];
					vertexBuffer.put(val);
				}
			}
		}
		vertexBuffer.rewind();
		
		//declare the floatbuffer in vram
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		//copy the floatbuffer to vram
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
		
		
		//declare the pointers
		for(int i = 0; i<attribs.size(); i++)
		{
			GL20.glVertexAttribPointer(i, sizes.get(i), GL11.GL_FLOAT, false, stride, offsets.get(i));
		}
		return vbo;
	}
}
