package seer.graphics

import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl._
import org.lwjgl.opengl.GL11._
import org.lwjgl.system._
import org.lwjgl.system.MemoryStack._
import org.lwjgl.system.MemoryUtil._

import java.nio._
import scala.util.Using
import seer._
import seer.graphics.{Window, WindowConfig, OpenGLConfig}
import seer.{Key, KeyModifiers, MouseButton, MouseLeft, MouseRight, MouseMiddle, MouseButton4, MouseButton5}

/**
 * GLFW Window implementation that conforms to the unified Window trait.
 */
class GLFWWindowImpl(
  val windowConfig: WindowConfig,
  val glConfig: OpenGLConfig,
  val graphicsModule: LwjglOpenGLGraphicsModule
) extends seer.graphics.Window {
  
  private var _handle: Long = 0
  private var _capabilities: GLCapabilities = _
  private var _fullscreen = false
  private var _lastSize = (0, 0)
  private var _lastPosition = (0, 0)
  private val mouseState = new MouseState()
  
  private val windowId = s"glfw-window-${System.identityHashCode(this)}"
  
  override def id: String = windowId
  
  // Create the window
  createWindow()
  
  private def createWindow(): Unit = {
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, if (windowConfig.resizable) GLFW_TRUE else GLFW_FALSE)
    glfwWindowHint(GLFW_DECORATED, if (windowConfig.decorated) GLFW_TRUE else GLFW_FALSE)
    
    // OpenGL version
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, glConfig.version._1)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, glConfig.version._2)
    
    // Profile
    glConfig.profile match {
      case OpenGLCoreProfile =>
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE) // Required on macOS
      case OpenGLCompatibilityProfile =>
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE)
      case OpenGLESProfile =>
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API)
    }
    
    // Debug context
    if (glConfig.debug) {
      glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
    }
    
    // MSAA
    if (glConfig.msaa > 0) {
      glfwWindowHint(GLFW_SAMPLES, glConfig.msaa)
    }
    
    // Create window
    _handle = glfwCreateWindow(
      windowConfig.width,
      windowConfig.height,
      windowConfig.title,
      if (windowConfig.fullscreen) glfwGetPrimaryMonitor() else NULL,
      NULL
    )
    
    if (_handle == NULL) {
      throw new RuntimeException("Failed to create GLFW window")
    }
    
    // Setup callbacks
    setupCallbacks()
    
    // Make context current and create capabilities
    glfwMakeContextCurrent(_handle)
    _capabilities = GL.createCapabilities()
    
    // Set vsync
    glfwSwapInterval(if (glConfig.vsync) 1 else 0)
    
    // Show window
    glfwShowWindow(_handle)
    
    if (windowConfig.fullscreen) {
      _fullscreen = true
    }
  }
  
  private def setupCallbacks(): Unit = {
    // Window size callback
    glfwSetWindowSizeCallback(_handle, (window, width, height) => {
      graphicsModule.publishWindowEvent(WindowResized(windowId, width, height))
    })
    
    // Framebuffer size callback
    glfwSetFramebufferSizeCallback(_handle, (window, width, height) => {
      // Framebuffer size changed
    })
    
    // Key callback
    glfwSetKeyCallback(_handle, (window, keycode, scancode, action, mods) => {
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
          graphicsModule.publishInputEvent(KeyPressed(windowId, key, modifiers))
        case GLFW_RELEASE =>
          graphicsModule.publishInputEvent(KeyReleased(windowId, key, modifiers))
        case GLFW_REPEAT =>
          graphicsModule.publishInputEvent(KeyRepeated(windowId, key, modifiers))
        case _ =>
      }
    })
    
    // Mouse position callback
    glfwSetCursorPosCallback(_handle, (window, xpos, ypos) => {
      val size = getSize()
      val w = size._1
      val h = size._2
      val dx = xpos - mouseState.px
      val dy = ypos - mouseState.py
      mouseState.px = xpos
      mouseState.py = ypos
      
      graphicsModule.publishInputEvent(MouseMoved(
        windowId,
        xpos / w,
        1.0 - (ypos / h),
        dx,
        dy
      ))
    })
    
    // Mouse button callback
    glfwSetMouseButtonCallback(_handle, (window, button, action, mods) => {
      val size = getSize()
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
          graphicsModule.publishInputEvent(MousePressed(windowId, mouseButton, x, y, modifiers))
        case GLFW_RELEASE =>
          graphicsModule.publishInputEvent(MouseReleased(windowId, mouseButton, x, y, modifiers))
        case _ =>
      }
    })
    
    // Scroll callback
    glfwSetScrollCallback(_handle, (window, xoff, yoff) => {
      val size = getSize()
      val w = size._1
      val h = size._2
      val (x, y) = (mouseState.px / w, 1.0 - (mouseState.py / h))
      
      graphicsModule.publishInputEvent(MouseScrolled(windowId, x, y, xoff, yoff))
    })
    
    // Cursor enter callback
    glfwSetCursorEnterCallback(_handle, (window, entered) => {
      if (entered) {
        graphicsModule.publishWindowEvent(WindowFocused(windowId))
      } else {
        graphicsModule.publishWindowEvent(WindowUnfocused(windowId))
      }
    })
    
    // Window close callback
    glfwSetWindowCloseCallback(_handle, (window) => {
      graphicsModule.publishWindowEvent(WindowClosed(windowId))
    })
  }
  
  def destroy(): Unit = {
    if (_handle != 0) {
      org.lwjgl.glfw.Callbacks.glfwFreeCallbacks(_handle)
      glfwDestroyWindow(_handle)
      glfwPollEvents()
      _handle = 0
    }
  }
  
  override def width: Int = getSize._1
  override def height: Int = getSize._2
  override def bufferWidth: Int = getBufferSize._1
  override def bufferHeight: Int = getBufferSize._2
  
  override def makeCurrent(): Unit = {
    if (_handle != 0) {
      glfwMakeContextCurrent(_handle)
      GL.setCapabilities(_capabilities)
    }
  }
  
  override def swapBuffers(): Unit = {
    if (_handle != 0) {
      glfwSwapBuffers(_handle)
    }
  }
  
  override def shouldClose: Boolean = {
    if (_handle != 0) glfwWindowShouldClose(_handle) else true
  }
  
  override def setShouldClose(value: Boolean): Unit = {
    if (_handle != 0) {
      glfwSetWindowShouldClose(_handle, value)
    }
  }
  
  override def getSize: (Int, Int) = {
    if (_handle == 0) (0, 0)
    else {
      Using(MemoryStack.stackPush()) { stack =>
        val pWidth = stack.mallocInt(1)
        val pHeight = stack.mallocInt(1)
        glfwGetWindowSize(_handle, pWidth, pHeight)
        (pWidth.get(0), pHeight.get(0))
      }.get
    }
  }
  
  override def getBufferSize: (Int, Int) = {
    if (_handle == 0) (0, 0)
    else {
      Using(MemoryStack.stackPush()) { stack =>
        val pWidth = stack.mallocInt(1)
        val pHeight = stack.mallocInt(1)
        glfwGetFramebufferSize(_handle, pWidth, pHeight)
        (pWidth.get(0), pHeight.get(0))
      }.get
    }
  }
  
  override def setSize(width: Int, height: Int): Unit = {
    if (_handle != 0) {
      glfwSetWindowSize(_handle, width, height)
    }
  }
  
  override def isFullscreen: Boolean = _fullscreen
  
  override def setFullscreen(fullscreen: Boolean): Unit = {
    if (_handle == 0) return
    
    if (fullscreen && !_fullscreen) {
      _lastSize = getSize
      _lastPosition = getPosition
      val monitor = glfwGetPrimaryMonitor()
      val mode = glfwGetVideoMode(monitor)
      glfwSetWindowMonitor(_handle, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate())
      _fullscreen = true
    } else if (!fullscreen && _fullscreen) {
      val (x, y) = _lastPosition
      val (w, h) = _lastSize
      glfwSetWindowMonitor(_handle, NULL, x, y, w, h, 0)
      _fullscreen = false
    }
  }
  
  override def title: String = windowConfig.title
  
  override def setTitle(title: String): Unit = {
    if (_handle != 0) {
      glfwSetWindowTitle(_handle, title)
    }
  }
  
  private def getPosition: (Int, Int) = {
    if (_handle == 0) return (0, 0)
    Using(MemoryStack.stackPush()) { stack =>
      val pX = stack.mallocInt(1)
      val pY = stack.mallocInt(1)
      glfwGetWindowPos(_handle, pX, pY)
      (pX.get(0), pY.get(0))
    }.get
  }
}
