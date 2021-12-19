


// package seer.runtime

// import seer.graphics.lwjgl._

// import org.lwjgl._
// import org.lwjgl.glfw._
// import org.lwjgl.opengl._
// import org.lwjgl.system._

// import java.nio._

// import org.lwjgl.glfw.Callbacks._
// import org.lwjgl.glfw.GLFW._
// import org.lwjgl.opengl.GL11._
// import org.lwjgl.system.MemoryStack._
// import org.lwjgl.system.MemoryUtil._


// object MainLoop {

//     def init() = {
// 		println("LWJGL Version " + Version.getVersion());

// 		// Setup an error callback. The default implementation
// 		// will print the error message in System.err.
// 		GLFWErrorCallback.createPrint(System.err).set();

// 		// Initialize GLFW. Most GLFW functions will not work before doing this.
// 		if ( !glfwInit() )
// 			throw new IllegalStateException("Unable to initialize GLFW");

// 		// Create default window
//         Window.create(100,100,-100)
//         Window.create(100,100,100)

//     }

//     def loop() = {

//         // This line is critical for LWJGL's interoperation with GLFW's
// 		// OpenGL context, or any context that is managed externally.
// 		// LWJGL detects the context that is current in the current thread,
// 		// creates the GLCapabilities instance and makes the OpenGL
// 		// bindings available for use.
// 		// GL.createCapabilities();

// 		while(Window.windows.length > 0){

// 			Window.pollEvents
// 			Window.windows.foreach { case window =>

// 				glfwMakeContextCurrent(window.handle);
//                 GL.setCapabilities(window.capabilities);

// 				// Set the clear color
// 				glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
// 				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

// 				window.swapBuffers() // swap the color buffers

// 				if(window.shouldClose){
// 					Window.destroy(window)
// 				}
// 			}

// 		}

// 		GL.setCapabilities(null);

// 		end()
//     }

//     def end() = {
// 		// Terminate GLFW and free the error callback
// 		glfwTerminate();
// 		glfwSetErrorCallback(null).free();

//     }
// }