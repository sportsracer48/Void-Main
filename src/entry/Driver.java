package entry;

import static org.lwjgl.opengl.GL11.*;
import graphics.Context;
import graphics.Renderlist;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlasBuilder;
import graphics.shader.Program;
import graphics.shader.Shader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import math.Matrix;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import util.GLU;

public class Driver
{

	public int wWidth,wHeight;
	public long window;
	public boolean running = false;
	public Program prog;
	public Context context;
	public Renderlist renderlist;
	public SpriteAtlasBuilder spriteAtlas;
	
	
	public Entity test;
	
	long lastUpdate;
	int framesRendered = 0;

	public void init()
	{
		this.running = true;
		initGLFW();
		initGL();
		initShaders();
		initSprites();
		initContext();
	}
	
	public void initGLFW()
	{
		if (GLFW.glfwInit() != GL_TRUE)
		{
			System.err.println("GLFW Window Handler Library failed to initialize");
		}
		
		
		GLFW.glfwSetErrorCallback(Callbacks.errorCallbackPrint(System.err));
		ByteBuffer vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		wWidth = GLFWvidmode.width(vidmode);
		wHeight = GLFWvidmode.height(vidmode);

		window = GLFW.glfwCreateWindow(wWidth, wHeight, "Void Main", GLFW.glfwGetPrimaryMonitor(), MemoryUtil.NULL);

		if (window == MemoryUtil.NULL)
		{
			System.err.println("Could not create window");
		}

		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwShowWindow(window);
	}
	
	public void initGL()
	{
		GLContext.createFromCurrent();

		glClearColor(0, .1f, .05f, 0);
		glEnable(GL_BLEND);
		GL14.glBlendFuncSeparate(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA,GL_ZERO,GL_ZERO);
		GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD,GL14.GL_FUNC_ADD);
	}
	
	public void initSprites()
	{
		spriteAtlas = new SpriteAtlasBuilder(new File("res\\sprite\\workbench\\"));
		spriteAtlas.build();
		
		test = new Entity(100,100,0,spriteAtlas.getSprite("res\\sprite\\workbench\\background.png"));
	}
	
	public void initShaders()
	{
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
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void initContext()
	{
		spriteAtlas.bind();
		prog.use();
		renderlist = new Renderlist();
		context = prog.getContext("modelMatrix", "viewMatrix", "projectionMatrix","stMatrix");
		context.setProjection(Matrix.gluOrtho(
				0, wWidth, 
				wHeight, 0, 
				1, -1));
		
	}
	
	public void checkError()
	{
		int error;
		while((error = GL11.glGetError())!=GL11.GL_NO_ERROR)
		{
			System.err.println(GLU.gluErrorString(error));
		}
	}
	
	public void render()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		context.setView(Matrix.scaling(1,1,1));
		context.setModel(Matrix.identity(4));
		renderlist.render(context);
		GLFW.glfwSwapBuffers(window);
	}

	public void update()
	{
		GLFW.glfwPollEvents();
	}

	public void run()
	{
		init();
		lastUpdate = System.currentTimeMillis();
		while (running)
		{
			update();
			render();
			checkError();
			framesRendered++;
			long time = System.currentTimeMillis();
			long dt = time-lastUpdate;
			if(dt>=1000)
			{
				System.out.format("%d FPS%n", framesRendered);
				framesRendered=0;
				lastUpdate = System.currentTimeMillis();
			}

			if (GLFW.glfwWindowShouldClose(window) == GL_TRUE)
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
