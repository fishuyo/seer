
package seer.graphics

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

import scala.util.Using

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
    var fullscreen = false
    var lastSize = (0, 0)
    var lastPosition = (0, 0)
    val mouseState = new MouseState()

    // window callback functions
    var onDraw = (g:Graphics) => {}
    var onResize = (w:Int, h:Int) => {}
    var onFramebufferResize = (w:Int, h:Int) => {}
    var onKeyEvent = (event:KeyEvent) => { defaultKeyHandler(event) }
    var onMouseEvent = (event:MouseEvent) => {}


    private def defaultKeyHandler(event:KeyEvent) = {
        if(event.keycode == GLFW_KEY_ESCAPE && event.action == GLFW_RELEASE)
            if(event.mods == GLFW_MOD_SHIFT)
                this.setShouldClose(true); 
            else
                this.toggleFullscreen()
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
        glfwSetWindowSizeCallback(handle, (window, width, height) => {
            println(s"window size callback: $width x $height")
            onResize(width, height)
        })

        glfwSetFramebufferSizeCallback(handle, (window, width, height) => {
            println(s"framebuffer size callback: $width x $height")
            onFramebufferResize(width, height)
        })


		glfwSetKeyCallback(handle, (window, keycode, scancode, action, mods) => {
            var name:String = null
            try{
                name = glfwGetKeyName(keycode,scancode)
            } catch { case e:Exception => println(e) }

            this.onKeyEvent(KeyEvent(name, keycode, scancode, action, mods))
        })

		glfwSetCharCallback(handle, (window, codepoint) => {
            // println(s"char callback: $codepoint")
        })

        glfwSetCursorPosCallback(handle, (window, xpos, ypos) => {
            mouseState.event = "move"
            mouseState.dx = xpos - mouseState.px
            mouseState.dy = ypos - mouseState.py
            mouseState.px = xpos
            mouseState.py = ypos
            val (w, h) = this.getSize()
            mouseState.x = xpos / w
            mouseState.y = 1.0 - (ypos / h)
            onMouseEvent(MouseEvent(mouseState))
        })

        glfwSetMouseButtonCallback(handle, (window, button, action, mods) => {
            mouseState.event = "button"
            mouseState.button = button
            mouseState.action = action
            mouseState.mods = mods 
            onMouseEvent(MouseEvent(mouseState))
        })
        
        glfwSetScrollCallback(handle, (window, xoff, yoff) => {
            mouseState.event = "scroll"
            mouseState.scrollx = xoff
            mouseState.scrolly = yoff
            onMouseEvent(MouseEvent(mouseState))
        })

        glfwSetCursorEnterCallback(handle, (window, entered) => {
            mouseState.event = "enter"
            mouseState.inside = entered
            onMouseEvent(MouseEvent(mouseState))
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
        glfwPollEvents();
    }


    def getSize(): (Int,Int) = {
        Using(stackPush()){ case stack =>
			val pWidth = stack.mallocInt(1); // int*
			val pHeight = stack.mallocInt(1); // int*

			// Get the framebuffer size in pixels
			glfwGetWindowSize(handle, pWidth, pHeight);
            (pWidth.get(0), pHeight.get(0))
        }.get
    }

    def getBufferSize(): (Int,Int) = {
        Using(stackPush()){ case stack =>
			val pWidth = stack.mallocInt(1); // int*
			val pHeight = stack.mallocInt(1); // int*

			// Get the framebuffer size in pixels
			glfwGetFramebufferSize(handle, pWidth, pHeight);
            (pWidth.get(0), pHeight.get(0))
        }.get
    }

    def toggleFullscreen() = {
        if(!fullscreen){
            lastSize = getSize()
            lastPosition = getPosition()
            val mon = glfwGetCurrentMonitor(handle)
            val mode = glfwGetVideoMode(mon)
            glfwSetWindowMonitor(handle, mon, 0, 0, mode.width, mode.height, mode.refreshRate);
        } else {
            val (x,y) = lastPosition
            val (w,h) = lastSize
            glfwSetWindowMonitor(handle, 0, x, y, w, h, 0);
        }
        fullscreen = !fullscreen
    }


    def getPosition() = {
        Using(stackPush()){ case stack =>
			val pX = stack.mallocInt(1); // int*
			val pY = stack.mallocInt(1); // int*

			// Get the framebuffer size in pixels
			glfwGetWindowPos(handle, pX, pY);
            (pX.get(0), pY.get(0))
        }.get
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




    /** Determines the current monitor that the specified window is being displayed on.
     * If the monitor could not be determined, the primary monitor will be returned.
     * 
     * @param window The window to query
     * @return The current monitor on which the window is being displayed, or the primary monitor if one could not be determined
     * @author <a href="https://stackoverflow.com/a/31526753/2398263">Shmo</a><br>
     * Ported to LWJGL by Brian_Entei */
    @NativeType("GLFWmonitor *")
    def glfwGetCurrentMonitor(window:Long):Long = {
        val wx = Array(0)
        val wy = Array(0)
        val ww = Array(0)
        val wh = Array(0)
        val mx = Array(0)
        val my = Array(0)
        val mw = Array(0)
        val mh = Array(0)

        var (overlap, bestoverlap) = (0,0)
        var bestmonitor = glfwGetPrimaryMonitor()
        var monitors:PointerBuffer = null
        var mode:GLFWVidMode = null

        glfwGetWindowPos(window, wx, wy);
        glfwGetWindowSize(window, ww, wh);
        monitors = glfwGetMonitors();

        while(monitors.hasRemaining()) {
            var monitor:Long = monitors.get();
            mode = glfwGetVideoMode(monitor);
            glfwGetMonitorPos(monitor, mx, my);
            mw(0) = mode.width();
            mh(0) = mode.height();

            overlap =
                    Math.max(0, Math.min(wx(0) + ww(0), mx(0) + mw(0)) - Math.max(wx(0), mx(0))) *
                    Math.max(0, Math.min(wy(0) + wh(0), my(0) + mh(0)) - Math.max(wy(0), my(0)));

            if (bestoverlap < overlap) {
                bestoverlap = overlap;
                bestmonitor = monitor;
            }
        }

        return bestmonitor;
    }
}