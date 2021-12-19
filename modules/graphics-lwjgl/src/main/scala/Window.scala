
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

class Window {

    // The window handle
	var handle:Long = _
    var capabilities:GLCapabilities = _

    var onDraw = (g:Graphics) => {}

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
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

		// Create the window
		handle = glfwCreateWindow(width, height, "seer", NULL, NULL);
		if ( handle == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(handle, (window, key, scancode, action, mods) => {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

        // Make the OpenGL context current
		glfwMakeContextCurrent(handle);
        capabilities = GL.createCapabilities()

        // debug
		val debugProc = GLUtil.setupDebugMessageCallback()

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

    def shouldClose() = glfwWindowShouldClose(handle)

    def swapBuffers() = glfwSwapBuffers(handle)

}