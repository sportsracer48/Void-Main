package graphics;

import graphics.geom.Model;
import graphics.geom.Rectangle;
import graphics.registry.VertexArrayBuilder;

import math.Matrix;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Sprite
{
	static int vao = 0;
	static int vbo = 0;
	static int vboi = 0;
	static Model model;
	static
	{
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		model = new Rectangle(0,0,0,1,1);
		VertexArrayBuilder b = new VertexArrayBuilder(model.nVerticies());
		model.addPositions(b);
		//add color attrib
		b.addAttrib(4, new float[]{
				0,0,1,1,
				0,1,1,1,
				1,1,0,1,
				1,0,1,1
				});
		//add texture coord attrib
		b.addAttrib(2, new float[]{
				0,0,
				0,1,
				1,1,
				1,0
				});
		vbo = b.buildVBO();
		
		vboi = model.bindIndicies();
	}
	
	int pTex;
	public Matrix modelMatrix;
	public Matrix stMatrix;
	public int imWidth,imHeight;
	
	public Sprite(int atlas, float x, float y, float width, float height, int imwidth, int imheight)
	{
		this.imWidth = imwidth;
		this.imHeight = imheight;
		this.pTex = atlas;
		this.modelMatrix = Matrix.scaling(imwidth,imheight,1);
		this.stMatrix = Matrix.scaling(width,height,1).translate(x,y,0);
	}
	
	/**
	 * Draws the sprite. Specifically, feeds a rectangle of size (width) by (height) into the existing model matrix.
	 * The square consists of six triangles, and has it's upper left corner at (0,0). No transformations are appended, and the state of the context is not changed.
	 * @param c the rendering context to use.
	 */
	public void render(Context c)
	{
		c.setSt(stMatrix);
		
		c.pushTransform();
		c.prependTransform(modelMatrix);
		c.updateModelMatrix();
		
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboi);
			
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.nIndicies(), GL11.GL_UNSIGNED_BYTE, 0);
			
		c.popTransform();
	}
}
