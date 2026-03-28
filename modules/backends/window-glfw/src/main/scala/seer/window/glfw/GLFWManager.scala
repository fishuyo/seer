package seer.window.glfw

import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.system.MemoryUtil._
import seer.graphics.WindowConfig

import collection.mutable.ArrayBuffer

/**
 * GLFW Manager - handles GLFW initialization, lifecycle, and window management.
 * This is backend-agnostic and works with any rendering backend (OpenGL, Vulkan, etc.)
 */
class GLFWManager {
  
  private var _isInitialized = false
  private val windows = ArrayBuffer[GLFWWindow]()
  
  /**
   * Initialize GLFW. Must be called before creating any windows.
   * @throws IllegalStateException if GLFW initialization fails
   */
  def initialize(): Unit = {
    if (_isInitialized) return
    
    // Setup error callback
    GLFWErrorCallback.createPrint(System.err).set()
    
    // Initialize GLFW
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }
    
    _isInitialized = true
  }
  
  /**
   * Check if GLFW is initialized
   */
  def isInitialized: Boolean = _isInitialized
  
  /**
   * Create a new GLFW window with the given configuration and context hints.
   * 
   * @param config Window configuration (title, size, etc.)
   * @param contextHintProvider Provider for backend-specific context hints (OpenGL/Vulkan)
   * @param eventHandler Handler for window and input events
   * @return A new GLFWWindow instance
   */
  def createWindow(
    config: WindowConfig,
    contextHintProvider: ContextHintProvider,
    eventHandler: GLFWEventHandler
  ): GLFWWindow = {
    if (!_isInitialized) {
      initialize()
    }
    
    // Set default window hints
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, if (config.resizable) GLFW_TRUE else GLFW_FALSE)
    glfwWindowHint(GLFW_DECORATED, if (config.decorated) GLFW_TRUE else GLFW_FALSE)
    
    // Apply backend-specific context hints (OpenGL/Vulkan/etc)
    contextHintProvider.applyContextHints()
    
    // Create the window
    val handle = glfwCreateWindow(
      config.width,
      config.height,
      config.title,
      if (config.fullscreen) glfwGetPrimaryMonitor() else NULL,
      NULL
    )
    
    if (handle == NULL) {
      throw new RuntimeException("Failed to create GLFW window")
    }
    
    // Create window wrapper and setup callbacks
    val window = new GLFWWindow(handle, config, eventHandler)
    window.setupCallbacks()
    
    windows += window
    
    // Show window
    glfwShowWindow(handle)
    
    window
  }
  
  /**
   * Poll for events. Should be called regularly in the main loop.
   */
  def pollEvents(): Unit = {
    if (_isInitialized) {
      glfwPollEvents()
    }
  }
  
  /**
   * Get the current time in seconds since GLFW was initialized.
   */
  def getTime(): Double = {
    if (_isInitialized) glfwGetTime() else 0.0
  }
  
  /**
   * Get all managed windows
   */
  def getWindows(): Seq[GLFWWindow] = windows.toSeq
  
  /**
   * Destroy a window
   */
  def destroyWindow(window: GLFWWindow): Unit = {
    if (windows.contains(window)) {
      windows -= window
      window.destroy()
    }
  }
  
  /**
   * Terminate GLFW. Should be called when done with all GLFW operations.
   * All windows should be destroyed before calling this.
   */
  def terminate(): Unit = {
    if (!_isInitialized) return
    
    // Destroy all remaining windows
    windows.foreach(_.destroy())
    windows.clear()
    
    // Terminate GLFW
    glfwTerminate()
    glfwSetErrorCallback(null).free()
    
    _isInitialized = false
  }
}
