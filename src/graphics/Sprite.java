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
	Matrix modelMatrix;
	public Matrix stMatrix;
	
//	public Sprite(String file) throws IOException
//	{
//		BufferedImage raw = ImageIO.read(new File(file));
//		int imwidth = raw.getWidth();
//		int imheight = raw.getHeight();
//		
//		int hside = (int)Math.pow(2,Math.ceil(Math.log(imwidth)/Math.log(2)));
//		int vside = (int)Math.pow(2,Math.ceil(Math.log(imheight)/Math.log(2)));
//		
//		modelMatrix = new Matrix(new float[]{
//				hside,0,0,0,
//				0,vside,0,0,
//				0,0,1,0,
//				0,0,0,1
//		},4);
//		
//		ColorModel glColor=new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8,8,8,8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
//		WritableRaster raster=Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, hside, vside, 4, null);
//		
//		BufferedImage texImage=new BufferedImage(glColor,raster,true,new Hashtable<>());
//		Graphics g=texImage.getGraphics();
//		g.drawImage(raw,0,0,null);
//		
//		byte[] data=((DataBufferByte)(texImage.getRaster().getDataBuffer())).getData();
//		ByteBuffer imageBuffer=BufferUtils.createByteBuffer(data.length);
//		imageBuffer.put(data);
//		imageBuffer.flip();
//		
//		pTex=GL11.glGenTextures();
//		
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pTex);
//		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, hside, vside, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,imageBuffer);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
//	}
	
	public Sprite(int atlas, float x, float y, float width, float height, int imwidth, int imheight)
	{
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
