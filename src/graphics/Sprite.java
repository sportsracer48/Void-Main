package graphics;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Sprite
{
	static int vao = 0;
	static int vbo = 0;
	static int vboi = 0;
	static
	{
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		Model model = new Rectangle(0,0,0,1,1);
		VertexBufferBuilder b = new VertexBufferBuilder(model.nVerticies());
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
		vbo = b.bindVBO();
		
		vboi = model.bindIndicies();
	}
	
	int pTex;
	int imwidth,imheight;
	int hside,vside;
	float hscale,vscale;
	float[] modelMatrix;
	
	public Sprite(String file) throws IOException
	{
		BufferedImage raw = ImageIO.read(new File(file));
		imwidth = raw.getWidth();
		imheight = raw.getHeight();
		
		hside = (int)Math.pow(2,Math.ceil(Math.log(imwidth)/Math.log(2)));
		vside = (int)Math.pow(2,Math.ceil(Math.log(imheight)/Math.log(2)));
		hscale = (float)hside/imwidth;
		vscale = (float)vside/imheight;
		
		modelMatrix = new float[]{
				hside,0,0,0,
				0,vside,0,0,
				0,0,1,0,
				0,0,0,1
		};
		
		ColorModel glColor=new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8,8,8,8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
		WritableRaster raster=Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, hside, vside, 4, null);
		
		BufferedImage texImage=new BufferedImage(glColor,raster,true,new Hashtable<>());
		Graphics g=texImage.getGraphics();
		g.drawImage(raw,0,0,null);
		
		byte[] data=((DataBufferByte)(texImage.getRaster().getDataBuffer())).getData();
		ByteBuffer imageBuffer=BufferUtils.createByteBuffer(data.length);
		imageBuffer.put(data);
		imageBuffer.flip();
		
		pTex=GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pTex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, hside, vside, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,imageBuffer);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	}
	
	
	public void render(Context c)
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pTex);
		
		c.setModel(modelMatrix);
		
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboi);
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);
	}
}
