package seer.window.glfw

import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil._

import java.nio._
import scala.util.Using
import seer._
import seer.graphics.WindowConfig
import seer.{Key, KeyModifiers, MouseButton, MouseLeft, MouseRight, MouseMiddle, MouseButton4, MouseButton5}

/**
 * GLFW Window wrapper - backend-agnostic window implementation.
 * Handles window operations and callback setup, but doesn't depend on rendering backend.
 */
class GLFWWindow(
  private val handle: Long,
  val config: WindowConfig,
  private val eventHandler: GLFWEventHandler
) extends seer.graphics.Window {
  
  private var _fullscreen = config.fullscreen
  private var _lastSize = (0, 0)
  private var _lastPosition = (0, 0)
  private val mouseState = new MouseState()
  
  private val windowId = s"glfw-window-${System.identityHashCode(this)}"
  
  override def id: String = windowId
  
  /**
   * Setup all GLFW callbacks for this window.
   */
  def setupCallbacks(): Unit = {
    // Window size callback
    glfwSetWindowSizeCallback(handle, (window, width, height) => {
      eventHandler.handleWindowEvent(WindowResized(windowId, width, height))
    })
    
    // Framebuffer size callback
    glfwSetFramebufferSizeCallback(handle, (window, width, height) => {
      // Framebuffer size changed - can be used for viewport updates
    })
    
    // Key callback
    glfwSetKeyCallback(handle, (window, keycode, scancode, action, mods) => {
      val keyName = try {
        glfwGetKeyName(keycode, scancode)
      } catch {
        case _: Exception => null
      }
      
      val key = Key(keyName, keycode, scancode)
      val modifiers = KeyModifiers(
        shift = (mods & GLFW_MOD_SHIFT) != 0,
        control = (mods & GLFW_MOD_CONTROL) != 0,
        alt = (mods & GLFW_MOD_ALT) != 0,
        superKey = (mods & GLFW_MOD_SUPER) != 0,
        capsLock = (mods & GLFW_MOD_CAPS_LOCK) != 0,
        numLock = (mods & GLFW_MOD_NUM_LOCK) != 0
      )
      
      action match {
        case GLFW_PRESS =>
          eventHandler.handleInputEvent(KeyPressed(windowId, key, modifiers))
        case GLFW_RELEASE =>
          eventHandler.handleInputEvent(KeyReleased(windowId, key, modifiers))
        case GLFW_REPEAT =>
          eventHandler.handleInputEvent(KeyRepeated(windowId, key, modifiers))
        case _ =>
      }
    })
    
    // Mouse position callback
    glfwSetCursorPosCallback(handle, (window, xpos, ypos) => {
      val size = getSize
      val w = size._1
      val h = size._2
      val dx = xpos - mouseState.px
      val dy = ypos - mouseState.py
      mouseState.px = xpos
      mouseState.py = ypos
      
      eventHandler.handleInputEvent(MouseMoved(
        windowId,
        xpos / w,
        1.0 - (ypos / h),
        dx,
        dy
      ))
    })
    
    // Mouse button callback
    glfwSetMouseButtonCallback(handle, (window, button, action, mods) => {
      val size = getSize
      val w = size._1
      val h = size._2
      val (x, y) = (mouseState.px / w, 1.0 - (mouseState.py / h))
      
      val mouseButton = button match {
        case GLFW_MOUSE_BUTTON_LEFT => MouseLeft
        case GLFW_MOUSE_BUTTON_RIGHT => MouseRight
        case GLFW_MOUSE_BUTTON_MIDDLE => MouseMiddle
        case GLFW_MOUSE_BUTTON_4 => MouseButton4
        case GLFW_MOUSE_BUTTON_5 => MouseButton5
        case _ => MouseLeft
      }
      
      val modifiers = KeyModifiers(
        shift = (mods & GLFW_MOD_SHIFT) != 0,
        control = (mods & GLFW_MOD_CONTROL) != 0,
        alt = (mods & GLFW_MOD_ALT) != 0,
        superKey = (mods & GLFW_MOD_SUPER) != 0,
        capsLock = (mods & GLFW_MOD_CAPS_LOCK) != 0,
        numLock = (mods & GLFW_MOD_NUM_LOCK) != 0
      )
      
      action match {
        case GLFW_PRESS =>
          eventHandler.handleInputEvent(MousePressed(windowId, mouseButton, x, y, modifiers))
        case GLFW_RELEASE =>
          eventHandler.handleInputEvent(MouseReleased(windowId, mouseButton, x, y, modifiers))
        case _ =>
      }
    })
    
    // Scroll callback
    glfwSetScrollCallback(handle, (window, xoff, yoff) => {
      val size = getSize
      val w = size._1
      val h = size._2
      val (x, y) = (mouseState.px / w, 1.0 - (mouseState.py / h))
      
      eventHandler.handleInputEvent(MouseScrolled(windowId, x, y, xoff, yoff))
    })
    
    // Cursor enter callback
    glfwSetCursorEnterCallback(handle, (window, entered) => {
      if (entered) {
        eventHandler.handleWindowEvent(WindowFocused(windowId))
      } else {
        eventHandler.handleWindowEvent(WindowUnfocused(windowId))
      }
    })
    
    // Window close callback
    glfwSetWindowCloseCallback(handle, (window) => {
      eventHandler.handleWindowEvent(WindowClosed(windowId))
    })
  }
  
  /**
   * Get the native GLFW window handle.
   * This is exposed for backend-specific operations (e.g., context creation).
   */
  def getHandle: Long = handle
  
  /**
   * Destroy this window and free all callbacks.
   */
  def destroy(): Unit = {
    if (handle != 0) {
      org.lwjgl.glfw.Callbacks.glfwFreeCallbacks(handle)
      glfwDestroyWindow(handle)
      glfwPollEvents()
    }
  }
  
  override def width: Int = getSize._1
  override def height: Int = getSize._2
  override def bufferWidth: Int = getBufferSize._1
  override def bufferHeight: Int = getBufferSize._2
  
  /**
   * Make this window's context current.
   * Backend-specific implementations should override this if needed.
   */
  def makeContextCurrent(): Unit = {
    if (handle != 0) {
      glfwMakeContextCurrent(handle)
    }
  }
  
  override def makeCurrent(): Unit = makeContextCurrent()
  
  override def swapBuffers(): Unit = {
    if (handle != 0) {
      glfwSwapBuffers(handle)
    }
  }
  
  override def shouldClose: Boolean = {
    if (handle != 0) glfwWindowShouldClose(handle) else true
  }
  
  override def setShouldClose(value: Boolean): Unit = {
    if (handle != 0) {
      glfwSetWindowShouldClose(handle, value)
    }
  }
  
  override def getSize: (Int, Int) = {
    if (handle == 0) (0, 0)
    else {
      Using(MemoryStack.stackPush()) { stack =>
        val pWidth = stack.mallocInt(1)
        val pHeight = stack.mallocInt(1)
        glfwGetWindowSize(handle, pWidth, pHeight)
        (pWidth.get(0), pHeight.get(0))
      }.get
    }
  }
  
  override def getBufferSize: (Int, Int) = {
    if (handle == 0) (0, 0)
    else {
      Using(MemoryStack.stackPush()) { stack =>
        val pWidth = stack.mallocInt(1)
        val pHeight = stack.mallocInt(1)
        glfwGetFramebufferSize(handle, pWidth, pHeight)
        (pWidth.get(0), pHeight.get(0))
      }.get
    }
  }
  
  override def setSize(width: Int, height: Int): Unit = {
    if (handle != 0) {
      glfwSetWindowSize(handle, width, height)
    }
  }
  
  override def isFullscreen: Boolean = _fullscreen
  
  override def setFullscreen(fullscreen: Boolean): Unit = {
    if (handle == 0) return
    
    if (fullscreen && !_fullscreen) {
      _lastSize = getSize
      _lastPosition = getPosition
      val monitor = glfwGetPrimaryMonitor()
      val mode = glfwGetVideoMode(monitor)
      glfwSetWindowMonitor(handle, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate())
      _fullscreen = true
    } else if (!fullscreen && _fullscreen) {
      val (x, y) = _lastPosition
      val (w, h) = _lastSize
      glfwSetWindowMonitor(handle, NULL, x, y, w, h, 0)
      _fullscreen = false
    }
  }
  
  override def title: String = config.title
  
  override def setTitle(title: String): Unit = {
    if (handle != 0) {
      glfwSetWindowTitle(handle, title)
    }
  }
  
  /**
   * Set swap interval (VSync). 0 = off, 1 = on.
   */
  def setSwapInterval(interval: Int): Unit = {
    if (handle != 0) {
      glfwSwapInterval(interval)
    }
  }
  
  private def getPosition: (Int, Int) = {
    if (handle == 0) return (0, 0)
    Using(MemoryStack.stackPush()) { stack =>
      val pX = stack.mallocInt(1)
      val pY = stack.mallocInt(1)
      glfwGetWindowPos(handle, pX, pY)
      (pX.get(0), pY.get(0))
    }.get
  }
}

/**
 * Internal mouse state tracking
 */
private class MouseState {
  var px: Double = 0.0
  var py: Double = 0.0
}
