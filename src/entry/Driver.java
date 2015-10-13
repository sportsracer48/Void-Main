package entry;

import static org.lwjgl.opengl.GL11.*;
import graphics.Context;
import graphics.registry.SpriteAtlas;
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

import state.GameState;
import state.workbench.WorkbenchState;
import util.GLU;

public class Driver
{
	boolean fullScreen = true;

	public int wWidth,wHeight;
	public long window;
	public boolean running = false;
	public Program prog;
	public Context context;
	public SpriteAtlas spriteAtlas;
	
	public GlobalInput input;
	public GameState currentState;

	public void init()
	{
		this.running = true;
		initGLFW();
		initGL();
		initShaders();
		initSprites();
		initContext();
		
		initGame();
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

		window = GLFW.glfwCreateWindow(wWidth, wHeight, "Void Main", fullScreen?GLFW.glfwGetPrimaryMonitor():MemoryUtil.NULL, MemoryUtil.NULL);

		if (window == MemoryUtil.NULL)
		{
			System.err.println("Could not create window");
		}
		
		input = new GlobalInput(this);
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
		spriteAtlas = new SpriteAtlas(new File("res\\sprite\\workbench\\"));
		spriteAtlas.build();
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
		context = prog.getContext("modelMatrix", "viewMatrix", "projectionMatrix","stMatrix");
		context.setProjection(Matrix.gluOrtho(
				0, wWidth, 
				wHeight, 0, 
				100, -100));
	}
	
	public void initGame()
	{
		currentState = new WorkbenchState(input);
		currentState.init(spriteAtlas);
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
		currentState.renderAll(context);
		GLFW.glfwSwapBuffers(window);
	}

	public void update(int dt)
	{
		currentState.beforeInput(dt);
		GLFW.glfwPollEvents();
		currentState.update(dt);
	}

	public void run()
	{
		init();
		

		int framesRendered = 0;
		long lastUpdate = System.currentTimeMillis();
		long lastTime = System.currentTimeMillis();
		
		
		while (running)
		{
			int dt = (int) (System.currentTimeMillis()-lastTime);
			if(dt<0)//I just want to make sure, ok? We could have switched cpu cores or something.
			{
				dt = 0;
			}
			lastTime = System.currentTimeMillis();
			update(dt);
			render();
			checkError();
			framesRendered++;
			long timeSinceUpdate = System.currentTimeMillis()-lastUpdate;
			if(timeSinceUpdate>=1000)
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
