
package seer.graphics
package lwjgl

import seer.runtime._ 

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

class LwjglGraphicsRuntimeModule extends RuntimeModule {

  var g:GraphicsLwjglImpl = _

	var onInit = () => {}
	var onDraw = (g:Graphics) => {}
	var onUpdate = (dt:Double) => {}

  override def init() = {
    println("Initializing LWJGL Graphics RuntimeModule..")
    println("LWJGL Version " + Version.getVersion())

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		g = new GraphicsLwjglImpl()
		Graphics() = g

		// val fakeWindow = new Window().create()
		Window.create(onDraw)
		
		onInit()

		// fakeWindow.destroy()

		// Create default window if no windows created
		if(Window.windows.length == 0) Window.create(onDraw)

  }

	var lastTime = 0.0

  override def start() = startBlocking()
  override def startBlocking() = {
    // This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		// GL.createCapabilities();

		while(Window.windows.length > 0){

			Window.pollEvents
			
			val time = glfwGetTime()
			val dt = time - lastTime
			lastTime = time

			this.onUpdate(dt)

			Window.windows.foreach { 
				case null =>
				case window  =>
					glfwMakeContextCurrent(window.handle);
					GL.setCapabilities(window.capabilities);

					window.onDraw(g)

					window.swapBuffers() // swap the color buffers

					if(window.shouldClose){
						Window.destroy(window)
					}
			}

		}

		GL.setCapabilities(null);
  }

  override def cleanup() = {
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
  }

}