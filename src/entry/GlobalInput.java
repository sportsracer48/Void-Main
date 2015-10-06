package entry;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.system.MemoryUtil;

public class GlobalInput
{
	Driver globalState; //lol I sure do love global state ;)
	
	public GlobalInput(Driver globalState)
	{
		this.globalState = globalState;
		long window = globalState.window;
		GLFW.glfwSetKeyCallback(window,key);
		GLFW.glfwSetCursorPosCallback(window, cursor);
		GLFW.glfwSetMouseButtonCallback(window,click);
		GLFW.glfwSetCharCallback(window, type);
		GLFW.glfwSetScrollCallback(window,scroll);
		GLFW.glfwSetDropCallback(window,drop);
	}
	boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST+1];
	boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST+1];
	float mouseX;
	float mouseY;
	
	public boolean isKeyPressed(int key)
	{
		return keys[key];
	}
	public boolean isButtonPressed(int button)
	{
		return buttons[button];
	}
	public float getMouseX()
	{
		return mouseX;
	}
	public float getMouseY()
	{
		return mouseY;
	}
	
	private GLFWKeyCallback key = new GLFWKeyCallback()
	{
		public void invoke(long window, int key, int scancode, int action,int mods)
		{
			if((action == GLFW.GLFW_REPEAT || action == GLFW.GLFW_PRESS) && key == GLFW.GLFW_KEY_ENTER)
			{
				System.out.println();
			}
			switch(action)
			{
			case GLFW.GLFW_PRESS:
				keys[key] = true;
				globalState.currentState.keyPressed(key);
				break;
			case GLFW.GLFW_RELEASE:
				keys[key] = false;
				globalState.currentState.keyRealeased(key);
				break;
			case GLFW.GLFW_REPEAT:
				keys[key] = true;
				globalState.currentState.keyRepeated(key);
				break;
			}
		}
	};
	
	private GLFWCursorPosCallback cursor = new GLFWCursorPosCallback()
	{
		public void invoke(long window, double xpos, double ypos)
		{
			mouseX = (float)xpos;
			mouseY = (float)ypos;
			
			globalState.currentState.mouseMoved((float)xpos,(float)ypos);
		}
	};
	
	private GLFWMouseButtonCallback click = new GLFWMouseButtonCallback()
	{
		public void invoke(long window, int button, int action, int mods)
		{
			switch(action)
			{
			case GLFW.GLFW_PRESS:
				buttons[button] = true;
				globalState.currentState.mousePressed(button);
				break;
			case GLFW.GLFW_RELEASE:
				buttons[button] = false;
				globalState.currentState.mouseReleased(button);
				break;
			case GLFW.GLFW_REPEAT:
				buttons[button] = true;
				break;
			}
		}
	};
	
	private GLFWCharCallback type = new GLFWCharCallback()
	{
		public void invoke(long window, int codepoint)
		{
			globalState.currentState.charTyped((char)codepoint);
		}
	};
	
	private GLFWScrollCallback scroll = new GLFWScrollCallback()
	{
		public void invoke(long window, double xoffset, double yoffset)
		{
			globalState.currentState.scrollMoved((float)xoffset, (float)yoffset);
		}
	};
	
	private GLFWDropCallback drop = new GLFWDropCallback()
	{
		public void invoke(long window, int count, long names)
		{
			PointerBuffer pointerBuffer = MemoryUtil.memPointerBuffer(names,count);
			for(int i = 0; i<count; i++)
			{
				globalState.currentState.fileDropped
				(
					MemoryUtil.memDecodeUTF8
					(
						MemoryUtil.memByteBufferNT1
						(
							pointerBuffer.get(i)
						)
					)
				);
			}
		}
	};

}
