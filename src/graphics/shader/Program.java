package graphics.shader;

import graphics.Context;

import org.lwjgl.opengl.GL20;

public class Program
{
	int id;
	int attribIndex = 0;
	
	
	public Program(Shader... shaders)
	{
		this.id = GL20.glCreateProgram();
		for(int i = 0; i<shaders.length; i++)
		{
			GL20.glAttachShader(id, shaders[i].id);
		}
	}
	
	public void addAttrib(String name)
	{
		GL20.glBindAttribLocation(id, attribIndex, name);
		attribIndex++;
	}
	
	public Context getContext(String modelName, String viewName, String projectionName, String stName, String colorName, String... flags)
	{
		Uniform[] flagUniforms = new Uniform[flags.length];
		for(int i = 0; i<flags.length; i++)
		{
			flagUniforms[i] = new Uniform(this,flags[i]);
		}
		return new Context(new Uniform(this,modelName),new Uniform(this,viewName),new Uniform(this,projectionName),new Uniform(this,stName),new Uniform(this,colorName),flagUniforms);
	}
	
	public void link()
	{
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
	}
	
	public void use()
	{
		GL20.glUseProgram(id);
	}
}
