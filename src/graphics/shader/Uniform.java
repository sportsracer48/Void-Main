package graphics.shader;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

public class Uniform
{
	Program program;
	int id;
	String name;
	
	public Uniform(Program program, String name)
	{
		this.program = program;
		this.id = GL20.glGetUniformLocation(program.id, name);
		this.name = name;
	}
	
	public void setMat4(FloatBuffer buf)
	{
		GL20.glUniformMatrix4fv(id, true, buf);
	}

	public void setVec4(FloatBuffer buf)
	{
		GL20.glUniform4fv(id, buf);
	}
	
	public void setBoolean(boolean val)
	{
		GL20.glUniform1i(id, val?1:0);
	}
}
