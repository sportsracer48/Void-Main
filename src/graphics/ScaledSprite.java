package graphics;

import math.Matrix;

public class ScaledSprite extends Sprite
{

	public ScaledSprite(int atlas, float x, float y, float width, float height, int imwidth, int imheight, float scale)
	{
		super(atlas, x, y, width, height, imwidth, imheight);
		this.modelMatrix = this.modelMatrix.dot(Matrix.scaling(scale,scale,scale));//impervious and jeweled
		this.imHeight *= scale;
		this.imWidth *= scale;
	}

}
