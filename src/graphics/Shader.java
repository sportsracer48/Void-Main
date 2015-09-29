package graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader
{
	int id;
	int type;
	public Shader(String path, int type) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		StringBuilder srcBuilder = new StringBuilder();
		
		
		String line;
		while((line = reader.readLine())!= null)
		{
			srcBuilder.append(line).append('\n');
		}
		
		reader.close();
		
		this.type = type;
		id = GL20.glCreateShader(type);
		GL20.glShaderSource(id, srcBuilder);
		GL20.glCompileShader(id);
		
		if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("Compilation error in shader");
		}
	}
}
