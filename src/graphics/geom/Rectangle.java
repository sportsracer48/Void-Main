package graphics;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;


public class Rectangle implements Model
{
	final float[] verticies;
	final byte[] indicies;
	
	public Rectangle(float x, float y,float z, float width, float height)
	{
		verticies = new float[]{
				x,y,z,1, //left, top
				x,y+height,z,1,//left, bottom
				x+width,y+height,z,1,//right,bottom
				x+width,y,z,1};//right,top
		indicies = new byte[]{
				0,1,2,
				2,3,0
		};
	}
	
	public void addPositions(VertexBufferBuilder builder)
	{
		builder.addAttrib(4, verticies);
	}
	
	public int nVerticies()
	{
		return 4;
	}
	
	public int bindIndicies()
	{
		ByteBuffer indexBuffer = BufferUtils.createByteBuffer(indicies.length);
		indexBuffer.put(indicies);
		indexBuffer.flip();
		
		int vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
		
		return vboiId;
	}
}
