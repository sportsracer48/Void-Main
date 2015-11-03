package entry;

import static org.lwjgl.opengl.GL11.*;
import graphics.Context;
import graphics.registry.RegisteredFont;
import graphics.registry.SpriteAtlas;
import graphics.shader.Program;
import graphics.shader.Shader;

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import math.Matrix;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
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
		
		
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		wWidth = vidmode.getWidth();
		wHeight = vidmode.getHeight();

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
		GL.createCapabilities();
		
		System.out.println(GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE));
		
		glClearColor(0, 0, 0, 0);
		glClearDepth(0);
		glEnable(GL_BLEND);
		GL14.glBlendFuncSeparate(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA,GL_ZERO,GL_ZERO);
		GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD,GL14.GL_FUNC_ADD);
	}
	
	public void initSprites()
	{
		spriteAtlas = new SpriteAtlas(new File("res/sprite/workbench/"));
		RegisteredFont defaultFont = spriteAtlas.addFont(new File("res/font/TERMINALVECTOR.TTF"), Font.PLAIN, 12);
		RegisteredFont.setDefault(defaultFont);
		spriteAtlas.build();
	}
	
	public void initShaders()
	{
		Shader vs, fs;
		try
		{
			vs = new Shader("res/shader/vs.glsl",GL20.GL_VERTEX_SHADER);
			fs = new Shader("res/shader/fs.glsl",GL20.GL_FRAGMENT_SHADER);
			prog = new Program(vs,fs);
			prog.addAttrib("in_Position");
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
		context = prog.getContext("modelMatrix", "viewMatrix", "projectionMatrix","stMatrix","color","yToAlpha");
		context.setProjection(Matrix.gluOrtho(
				0, wWidth, 
				wHeight, 0, 
				100, -100));
		context.addProjection(Matrix.rpgOrtho(
				0, wWidth, 
				wHeight, 0, 
				1000, -1000));
	}
	
	public void initGame()
	{
		currentState = new WorkbenchState(input,window);
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
