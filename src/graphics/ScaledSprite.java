package graphics;

import java.awt.image.BufferedImage;

import math.Matrix;

public class ScaledSprite extends Sprite
{

	public ScaledSprite(int atlas, float x, float y, float width, float height, int imwidth, int imheight, float scale, BufferedImage image)
	{
		super(atlas, x, y, width, height, imwidth, imheight,image);
		this.modelMatrix = this.modelMatrix.dot(Matrix.scaling(scale,scale,1));//impervious and jeweled
		this.imHeight *= scale;
		this.imWidth *= scale;
	}
}
