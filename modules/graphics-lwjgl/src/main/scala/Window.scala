
package seer.graphics
package lwjgl

import org.lwjgl._
import org.lwjgl.glfw._
import org.lwjgl.opengl._
import org.lwjgl.system._

import java.nio._

import org.lwjgl.glfw.Callbacks._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryStack._
import org.lwjgl.system.MemoryUtil._

import collection.mutable.ArrayBuffer

object Window {
    val windows = ArrayBuffer[Window]()

    def create(onDraw:(Graphics)=>Unit, width:Int=640, height:Int=480, x:Int=0, y:Int=0) = {
        val window = new Window()
        window.create(width, height)
        window.setCenter(x, y)
        window.onDraw = onDraw
        window.show()
        windows += window
        window
    }

    def destroy(w:Window) = {
        windows -= w
        w.destroy
    }
    
    def apply(index:Int=0) = windows(index)
    def pollEvents() = glfwPollEvents()
}

case class KeyEvent(window:Window, key:String, keycode:Int, scancode:Int, action:Int, mods:Int)
case class MouseEvent(window:Window, state:MouseState)

class MouseState {
    var event = ""
    var x:Double = 0.0
    var y:Double = 0.0
    var dx:Double = 0.0
    var dy:Double = 0.0
    var button:Int = 0
    var action:Int = 0
    var mods:Int = 0
    var scrollx:Double = 0.0
    var scrolly:Double = 0.0
    var inside:Boolean = false
}

class Window {

    // The window handle
	var handle:Long = _
    var capabilities:GLCapabilities = _

    // window callback functions
    var onDraw = (g:Graphics) => {}
    
    var onKeyEvent = (event:KeyEvent) => {
        if(event.keycode == GLFW_KEY_ESCAPE && event.action == GLFW_RELEASE)
            event.window.setShouldClose(true); 
    }

    val mouseState = new MouseState()
    var onMouseEvent = (event:MouseEvent) => {

    }

    def create(width:Int = 640, height:Int = 480) = {
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);         // Ignored when creating ES
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // if OSX, this is a must
        
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE); // so fullcreen does not iconify
        // glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API) //GLFW_OPENGL_API, GLFW_OPENGL_ES_API or GLFW_NO_API
        // glfwWindowHint(GLFW_DECORATED, mDecorated ? GLFW_TRUE : GLFW_FALSE);
        // glfwWindowHint(GLFW_STEREO, should_create_stereo);

		// debug
		// glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

		// Create the window
		handle = glfwCreateWindow(width, height, "seer", NULL, NULL);
		if ( handle == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup callbacks... 
		glfwSetKeyCallback(handle, (window, keycode, scancode, action, mods) => {
            var name:String = null
            try{
                name = glfwGetKeyName(keycode,scancode)
            } catch { case e:Exception => println(e) }

            this.onKeyEvent(KeyEvent(this, name, keycode, scancode, action, mods))
        });

		glfwSetCharCallback(handle, (window, codepoint) => {
            // println(s"char callback: $codepoint")
        });

        glfwSetCursorPosCallback(handle, (window, xpos, ypos) => {
            mouseState.event = "move"
            mouseState.dx = xpos - mouseState.x
            mouseState.dy = ypos - mouseState.y
            mouseState.x = xpos
            mouseState.y = ypos
            onMouseEvent(MouseEvent(this, mouseState))
        })

        glfwSetMouseButtonCallback(handle, (window, button, action, mods) => {
            mouseState.event = "button"
            mouseState.button = button
            mouseState.action = action
            mouseState.mods = mods 
            onMouseEvent(MouseEvent(this, mouseState))
        })
        
        glfwSetScrollCallback(handle, (window, xoff, yoff) => {
            mouseState.event = "scroll"
            mouseState.scrollx = xoff
            mouseState.scrolly = yoff
            onMouseEvent(MouseEvent(this, mouseState))
        })

        glfwSetCursorEnterCallback(handle, (window, entered) => {
            mouseState.event = "enter"
            mouseState.inside = entered
            onMouseEvent(MouseEvent(this, mouseState))
        })

        // Make the OpenGL context current
		glfwMakeContextCurrent(handle);
        capabilities = GL.createCapabilities()

        // debug
		// val debugProc = GLUtil.setupDebugMessageCallback()

        this
    }

    def destroy() = {
        // Free the window callbacks and destroy the window
		glfwFreeCallbacks(handle);
		glfwDestroyWindow(handle);
    }


    def getSize(): (Int,Int) = {
        try {
            val stack = stackPush()
			val pWidth = stack.mallocInt(1); // int*
			val pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(handle, pWidth, pHeight);

            return (pWidth.get(0), pHeight.get(0))
		} // the stack frame is popped automatically
    }

    def setPosition(x:Int, y:Int) = glfwSetWindowPos(handle, x, y)

    def setCenter(x:Int=0, y:Int=0) = {
        val (sw, sh) = Screen.size()
        val (w,h) = getSize()
        setPosition((sw - w)/2 + x, (sh - h)/2 + y)
    }


    def show() = {
		// Enable v-sync
		glfwSwapInterval(1);
		// Make the window visible
		glfwShowWindow(handle);
    }

    def setShouldClose(close:Boolean=true) = glfwSetWindowShouldClose(handle, close)
    def shouldClose() = glfwWindowShouldClose(handle)

    def swapBuffers() = glfwSwapBuffers(handle)

}