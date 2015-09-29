package entry;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import graphics.Context;
import graphics.Program;
import graphics.Shader;
import graphics.Sprite;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import util.MatUtil;

public class Driver
{

	public int width = 800, height = 600;

	public long window;
	public boolean running = false;
	public Program prog;
	public Context context;
	
	
	public Sprite test;
	
	long lastUpdate;
	int framesRendered = 0;

	public void init()
	{
		this.running = true;

		if (glfwInit() != GL_TRUE)
		{
			System.err.println("GLFW Window Handler Library failed to initialize");
		}
		
		
		glfwSetErrorCallback(Callbacks.errorCallbackPrint(System.err));
		
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwWindowHint(GLFW_DECORATED, GL_FALSE);

		glfwWindowHint(GLFW_RED_BITS, GLFWvidmode.redBits(vidmode));
		glfwWindowHint(GLFW_GREEN_BITS, GLFWvidmode.greenBits(vidmode));
		glfwWindowHint(GLFW_BLUE_BITS, GLFWvidmode.blueBits(vidmode));
		glfwWindowHint(GLFW_REFRESH_RATE, GLFWvidmode.refreshRate(vidmode));

		width = GLFWvidmode.width(vidmode);
		height = GLFWvidmode.height(vidmode);

		window = glfwCreateWindow(width, height, "Void Main", NULL, NULL);

		if (window == NULL)
		{
			System.err.println("Could not create our window");
		}

		// creates a bytebuffer object 'vidmode' which then queries
		// to see what the primary monitor is.

		// Sets the initial position of our game window.
		glfwSetWindowPos(window, 0, 0);
		// Sets the context of GLFW, this is vital for our program to work.
		glfwMakeContextCurrent(window);
		// finally shows our created window in all it's glory.
		glfwShowWindow(window);

		GLContext.createFromCurrent();

		glClearColor(0, 0, 0, 0);
		
		Shader vs, fs;
		
		try
		{
			vs = new Shader("res\\shader\\vs.glsl",GL20.GL_VERTEX_SHADER);
			fs = new Shader("res\\shader\\fs.glsl",GL20.GL_FRAGMENT_SHADER);
			prog = new Program(vs,fs);
			prog.addAttrib("in_Position");
			prog.addAttrib("in_Color");
			prog.addAttrib("in_TextureCoord");
			prog.link();
			context = prog.getContext("modelMatrix", "viewMatrix", "projectionMatrix");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			test = new Sprite("res\\sprite\\workbench\\ouino.png");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("OpenGL: " + glGetString(GL_VERSION));

	}

	public void render()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		context.setProjection(MatUtil.gluOrtho(
				0, width, 
				height, 0, 
				1, -1));
		context.setView(MatUtil.identity());
		
		prog.use();
		test.render(context);
		glfwSwapBuffers(window);
	}

	public void update()
	{
		glfwPollEvents();
	}

	public void run()
	{
		init();
		lastUpdate = System.currentTimeMillis();
		while (running)
		{
			update();
			render();
			framesRendered++;
			long time = System.currentTimeMillis();
			long dt = time-lastUpdate;
			if(dt>=1000)
			{
				System.out.format("%d FPS%n", framesRendered);
				framesRendered=0;
				lastUpdate = System.currentTimeMillis();
			}

			if (glfwWindowShouldClose(window) == GL_TRUE)
			{
				running = false;
			}
		}
	}

	public static void main(String args[])
	{
		Driver driver = new Driver();
		driver.run();
	}

}
