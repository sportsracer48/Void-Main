package math;

public class Rectangle
{
	public final float x,y,width,height;
	Matrix corner,diag;
	
	public Rectangle(float x, float y, float width, float height)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		corner = new Matrix(new float[]{x,y,0,1});//w=1 for point, affected by translation
		diag = new Matrix(new float[]{width,height,0,0});//w=0 for vector, unaffected by translation
	}

	
	public Rectangle(Matrix corner, Matrix diag)
	{
		this.corner = corner;
		this.diag = diag;
		this.x = corner.x();
		this.y = corner.y();
		this.width = diag.x();
		this.height = diag.y();
	}
	
	public Rectangle transform(Matrix transform)
	{
		return new Rectangle(transform.dot(corner),transform.dot(diag));
	}
	
	/**
	 * <pre>
	 * inclusive
	 *            ---------
	 *            |       |
	 *            |
	 *            |       |         
	 *            |         
	 *            |       |
	 *            - - - - -
	 *                      exclusive
	 * </pre>
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(float x, float y)
	{
		return 
				x>=this.x && 
				x<this.x+width && 
				y>=this.y && 
				y<this.y+height; 
	}
	
	public boolean contains(Matrix model,float x, float y)
	{
		return transform(model).contains(x, y);
	}
	
	public String toString()
	{
		return String.format("%.2fx%.2f @ (%.2f,%.2f)", width,height,x,y);
	}
}
