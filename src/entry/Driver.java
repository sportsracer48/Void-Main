package entry;

import static org.lwjgl.opengl.GL11.*;
import graphics.Context;
import graphics.registry.RegisteredFont;
import graphics.registry.SpriteAtlas;
import graphics.registry.UtilSprites;
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
import state.programming.ProgrammingState;
import state.viewport.ViewportState;
import state.workbench.WorkbenchState;
//import state.workbench.WorkbenchState;
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
		wWidth = vidmode.width();
		wHeight = vidmode.height();
		
		window = GLFW.glfwCreateWindow(wWidth, wHeight, "Void Main", fullScreen?GLFW.glfwGetPrimaryMonitor():MemoryUtil.NULL, MemoryUtil.NULL);

		if (window == MemoryUtil.NULL)
		{
			System.err.println("Could not create window");
		}
		
		input = new GlobalInput(this);
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwShowWindow(window);
		
		//GLFW.glfwSetInputMode(window,GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_DISABLED);
	}
	
	public void initGL()
	{
		GL.createCapabilities();
		
		glClearColor(0, 0, 0, 0);
		glClearDepth(0);
		glEnable(GL_BLEND);
		GL14.glBlendFuncSeparate(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA,GL_ZERO,GL_ZERO);
		GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD,GL14.GL_FUNC_ADD);
	}
	
	public void initSprites()
	{
		spriteAtlas = new SpriteAtlas(new File("res/sprite/workbench/"));
		spriteAtlas.addAllChildren(new File("res/sprite/util/"));
		spriteAtlas.addAllChildren(new File("res/sprite/level/"));
		RegisteredFont defaultFont = spriteAtlas.addFont(new File("res/font/TERMINALVECTOR.TTF"), Font.PLAIN, 12);
		RegisteredFont defaultFontOutline = new RegisteredFont("TerminalVector$o", defaultFont.metrics, spriteAtlas);
		RegisteredFont.setDefault(defaultFont);
		RegisteredFont.setDefaultOutline(defaultFontOutline);
		spriteAtlas.build();
		UtilSprites.init(spriteAtlas);
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
				10000, -10000));
	}
	
	public void initGame()
	{
		GlobalState.init();
		GlobalState.currentProgramming = new ProgrammingState(input,window);
		GlobalState.currentProgramming.init(spriteAtlas);
		GlobalState.currentWorkbench = new WorkbenchState(input,window);
		GlobalState.currentWorkbench.init(spriteAtlas);
		PythonInit.init();
		currentState = new ViewportState(input,window);//GlobalState.currentWorkbench;
		//currentState = GlobalState.currentWorkbench;
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
		GlobalState.coordinator.act(dt);
		currentState.update(dt);
	}

	public void run()
	{
		init();
		

		int framesRendered = 0;
		long renderTime = 0;
		long updateTime = 0;
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
			long beforeTime = System.currentTimeMillis();
			update(dt);
			updateTime += System.currentTimeMillis()-beforeTime;
			beforeTime = System.currentTimeMillis();
			render();
			renderTime += System.currentTimeMillis()-beforeTime;
			checkError();
			framesRendered++;
			long timeSinceUpdate = System.currentTimeMillis()-lastUpdate;
			if(timeSinceUpdate>=1000)
			{
				long sleepTime = timeSinceUpdate - renderTime-updateTime;
				System.out.format("%d FPS (%.2f update, %.2f render, %.2f sleep) %s%n", framesRendered,
						(double)updateTime/timeSinceUpdate,
						(double)renderTime/timeSinceUpdate,
						(double)sleepTime/timeSinceUpdate,
						currentState.getPerformanceString());
				renderTime = 0;
				updateTime = 0;
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
