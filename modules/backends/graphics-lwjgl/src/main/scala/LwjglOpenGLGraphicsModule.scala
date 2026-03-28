package seer.graphics

import org.lwjgl._
import org.lwjgl.opengl._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL

import collection.mutable.ArrayBuffer
import seer._
import seer.graphics.lwjgl.{GraphicsLwjglImpl, GLFWEventPublisher}
import seer.window.glfw.{GLFWManager, GLFWWindow, OpenGLContextHintProvider}

/**
 * LWJGL OpenGL Graphics Module implementing the unified GraphicsModule trait.
 * Provides OpenGL rendering via LWJGL on the JVM platform.
 */
class LwjglOpenGLGraphicsModule(val config: OpenGLConfig) extends GraphicsModule {
  
  override def id: String = "lwjgl-opengl"
  override def name: String = "LWJGL OpenGL Graphics Module"
  
  private var _isCreated = false
  private var _isRunning = false
  private var context: Option[GraphicsContext] = None
  private val windows = collection.mutable.ArrayBuffer[Window]()
  private var graphicsImpl: GraphicsLwjglImpl = _
  
  // GLFW Manager for window creation and management
  private val glfwManager = new GLFWManager()
  private var eventPublisher: Option[GLFWEventPublisher] = None
  
  private var lastTime = 0.0
  
  // Runtime reference for event publishing (set by runtime)
  private var runtime: Option[SeerRuntime] = None
  
  def setRuntime(rt: SeerRuntime): Unit = {
    runtime = Some(rt)
    // Create event publisher if GLFW is already initialized
    if (_isCreated && eventPublisher.isEmpty) {
      eventPublisher = Some(new GLFWEventPublisher(rt))
    }
  }
  
  private def publish(event: Event): Unit = {
    runtime.foreach(_.publish(event))
  }
  
  override def isCreated: Boolean = _isCreated
  override def isRunning: Boolean = _isRunning
  
  override def create(): Unit = {
    if (_isCreated) return
    
    println(s"Initializing $name..")
    println("LWJGL Version " + Version.getVersion())
    
    // Initialize GLFW through GLFWManager
    glfwManager.initialize()
    
    // Create event publisher for GLFW events
    runtime.foreach { rt =>
      eventPublisher = Some(new GLFWEventPublisher(rt))
    }
    
    // Create graphics implementation
    graphicsImpl = new GraphicsLwjglImpl()
    Graphics() = graphicsImpl
    
    _isCreated = true
    publish(ModuleCreated(this))
  }
  
  override def start(): Unit = {
    if (!_isCreated) create()
    if (_isRunning) return
    
    _isRunning = true
    publish(ModuleStarted(this))
    
    // Start main loop
    startMainLoop()
  }
  
  private def startMainLoop(): Unit = {
    var destroyList = List[Window]()
    
    while (_isRunning && windows.nonEmpty) {
      // Poll GLFW events
      glfwManager.pollEvents()
      
      val time = glfwManager.getTime()
      val dt = if (lastTime > 0) time - lastTime else 0.0
      lastTime = time
      
      // Update callback
      onUpdate(dt)
      
      // Render each window
      windows.foreach { window =>
        window.makeCurrent()
        
        // Draw callback
        onDraw(graphicsImpl)
        
        window.swapBuffers()
        
        if (window.shouldClose) {
          destroyList = window :: destroyList
        }
      }
      
      // Destroy windows that should close
      destroyList.foreach { w =>
        destroyWindow(w)
      }
      destroyList = List()
      
      // Small delay to prevent CPU spinning
      Thread.sleep(1)
    }
    
    _isRunning = false
  }
  
  override def stop(): Unit = {
    if (!_isRunning) return
    
    _isRunning = false
    publish(ModuleStopped(this))
  }
  
  override def destroy(): Unit = {
    if (!_isCreated) return
    
    // Destroy all windows
    windows.foreach(destroyWindow)
    windows.clear()
    
    // Terminate GLFW through GLFWManager
    glfwManager.terminate()
    
    _isCreated = false
    publish(ModuleDestroyed(this))
  }
  
  override def update(dt: Double): Unit = {
    // Update is handled in the main loop
  }
  
  override def createContext(): GraphicsContext = {
    if (!_isCreated) create()
    
    val ctx = new OpenGLContext(config)
    context = Some(ctx)
    ctx
  }
  
  override def destroyContext(ctx: GraphicsContext): Unit = {
    if (context.contains(ctx)) {
      context = None
    }
  }
  
  override def getCurrentContext(): Option[GraphicsContext] = context
  
  override def createWindow(windowConfig: WindowConfig): Window = {
    if (!_isCreated) create()
    
    // Ensure event publisher exists
    if (eventPublisher.isEmpty && runtime.isDefined) {
      eventPublisher = Some(new GLFWEventPublisher(runtime.get))
    }
    
    val eventHandler = eventPublisher.getOrElse(
      throw new IllegalStateException("Runtime not set - cannot create window without event handler")
    )
    
    // Create OpenGL context hint provider
    val contextHintProvider = new OpenGLContextHintProvider(config)
    
    // Create window through GLFWManager
    val glfwWindow = glfwManager.createWindow(windowConfig, contextHintProvider, eventHandler)
    
    // Make context current and create OpenGL capabilities
    glfwWindow.makeContextCurrent()
    val capabilities = GL.createCapabilities()
    
    // Set vsync
    glfwWindow.setSwapInterval(if (config.vsync) 1 else 0)
    
    // Wrap GLFWWindow with OpenGL-specific wrapper if needed
    // For now, we'll use GLFWWindow directly since it implements the Window trait
    val window = new OpenGLGLFWWindow(glfwWindow, capabilities)
    
    windows += window
    publish(WindowCreated(window.id, window.width, window.height))
    window
  }
  
  override def destroyWindow(window: Window): Unit = {
    windows.find(_ == window).foreach { w =>
      windows -= w
      publish(WindowDestroyed(window.id))
      w match {
        case glWindow: OpenGLGLFWWindow =>
          glfwManager.destroyWindow(glWindow.glfwWindow)
        case _ =>
      }
    }
  }
  
  override def getWindows(): Seq[Window] = windows.toSeq
  
  override def getWindow(id: String): Option[Window] = {
    windows.find(_.id == id)
  }
  
  override def clear(color: Color): Unit = {
    clearColor(color.r, color.g, color.b, color.a)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
  }
  
  override def clearColor(r: Float, g: Float, b: Float, a: Float): Unit = {
    glClearColor(r, g, b, a)
  }
  
  override def clearDepth(depth: Double): Unit = {
    glClearDepth(depth)
  }
  
  override def setViewport(x: Int, y: Int, width: Int, height: Int): Unit = {
    glViewport(x, y, width, height)
  }
  
  override def graphics: Graphics = graphicsImpl
  
}

/**
 * OpenGL-specific wrapper around GLFWWindow that manages OpenGL capabilities.
 */
class OpenGLGLFWWindow(
  val glfwWindow: GLFWWindow,
  private val capabilities: GLCapabilities
) extends Window {
  
  override def id: String = glfwWindow.id
  override def width: Int = glfwWindow.width
  override def height: Int = glfwWindow.height
  override def bufferWidth: Int = glfwWindow.bufferWidth
  override def bufferHeight: Int = glfwWindow.bufferHeight
  
  override def makeCurrent(): Unit = {
    glfwWindow.makeContextCurrent()
    GL.setCapabilities(capabilities)
  }
  
  override def swapBuffers(): Unit = glfwWindow.swapBuffers()
  override def shouldClose: Boolean = glfwWindow.shouldClose
  override def setShouldClose(value: Boolean): Unit = glfwWindow.setShouldClose(value)
  override def getSize: (Int, Int) = glfwWindow.getSize
  override def getBufferSize: (Int, Int) = glfwWindow.getBufferSize
  override def setSize(width: Int, height: Int): Unit = glfwWindow.setSize(width, height)
  override def isFullscreen: Boolean = glfwWindow.isFullscreen
  override def setFullscreen(fullscreen: Boolean): Unit = glfwWindow.setFullscreen(fullscreen)
  override def title: String = glfwWindow.title
  override def setTitle(title: String): Unit = glfwWindow.setTitle(title)
}

// OpenGL Context implementation
class OpenGLContext(val config: OpenGLConfig) extends GraphicsContext {
  override def id: String = s"opengl-context-${System.identityHashCode(this)}"
  override def isValid: Boolean = true
}
