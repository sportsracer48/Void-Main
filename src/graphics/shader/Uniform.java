package graphics;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

public class Uniform
{
	Program program;
	int id;
	
	public Uniform(Program program, String name)
	{
		this.program = program;
		this.id = GL20.glGetUniformLocation(program.id, name);
	}
	
	public void setMat4(FloatBuffer buf)
	{
		GL20.glUniformMatrix4fv(id, true, buf);
	}
}
