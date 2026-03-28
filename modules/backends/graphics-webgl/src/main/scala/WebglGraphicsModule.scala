package seer.graphics

import scala.scalajs.js.annotation._
import org.scalajs.dom
import dom.raw.WebGLRenderingContext
import typings.std.WebGL2RenderingContext
import seer._
import seer.graphics.webgl.GraphicsWebGLImpl
import collection.mutable.ArrayBuffer

/**
 * WebGL Graphics Module implementing the unified GraphicsModule trait.
 * Provides WebGL rendering in the browser via Scala.js.
 */
class WebGLGraphicsModule(val config: WebGLConfig) extends GraphicsModule {
  
  override def id: String = "webgl"
  override def name: String = "WebGL Graphics Module"
  
  private var _isCreated = false
  private var _isRunning = false
  private var context: Option[GraphicsContext] = None
  private val windows = collection.mutable.ArrayBuffer[Window]()
  private var graphicsImpl: GraphicsWebGLImpl = _
  
  private var lastTime = 0.0
  
  // Runtime reference for event publishing
  private var runtime: Option[SeerRuntime] = None
  
  def setRuntime(rt: SeerRuntime): Unit = {
    runtime = Some(rt)
  }
  
  private def publish(event: Event): Unit = {
    runtime.foreach(_.publish(event))
  }
  
  override def isCreated: Boolean = _isCreated
  override def isRunning: Boolean = _isRunning
  
  override def create(): Unit = {
    if (_isCreated) return
    
    println(s"Initializing $name..")
    
    // Find or create canvas container
    val container = Option(dom.document.getElementById("glcanvas")).getOrElse {
      val elem = dom.document.createElement("canvas")
      elem.id = "glcanvas"
      dom.document.body.appendChild(elem)
      elem
    }
    
    val canvas = container.asInstanceOf[dom.html.Canvas]
    
    // Set canvas size
    canvas.width = dom.window.innerWidth.toInt
    canvas.height = dom.window.innerHeight.toInt
    
    // Get WebGL context
    val contextOptions = new dom.raw.WebGLContextAttributes {
      alpha = config.alpha
      antialias = config.antialias
      premultipliedAlpha = config.premultipliedAlpha
      preserveDrawingBuffer = config.preserveDrawingBuffer
      depth = true
      stencil = true
    }
    
    val webglContext = (if (config.version == 2) {
      canvas.getContext("webgl2", contextOptions)
    } else {
      canvas.getContext("webgl", contextOptions)
    }).asInstanceOf[WebGLRenderingContext]
    
    if (webglContext == null) {
      throw new RuntimeException("Failed to create WebGL context")
    }
    
    val webgl2 = webglContext.asInstanceOf[WebGL2RenderingContext]
    
    // Create graphics implementation
    graphicsImpl = new GraphicsWebGLImpl(webglContext)
    Graphics() = graphicsImpl
    
    // Create context
    context = Some(new WebGLContext(config))
    
    _isCreated = true
    publish(ModuleCreated(this))
  }
  
  override def start(): Unit = {
    if (!_isCreated) create()
    if (_isRunning) return
    
    _isRunning = true
    publish(ModuleStarted(this))
    
    // Start animation loop
    startAnimationLoop()
  }
  
  private def startAnimationLoop(): Unit = {
    def loop(time: Double): Unit = {
      if (!_isRunning) return
      
      val dt = if (lastTime > 0) (time - lastTime) * 0.001 else 0.0
      lastTime = time
      
      // Update callback
      onUpdate(dt)
      
      // Render each window
      windows.foreach {
        case window: CanvasWindowImpl =>
          window.makeCurrent()
          onDraw(graphicsImpl)
        case _ =>
      }
      
      dom.window.requestAnimationFrame(loop)
    }
    
    dom.window.requestAnimationFrame(loop)
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
    
    _isCreated = false
    publish(ModuleDestroyed(this))
  }
  
  override def update(dt: Double): Unit = {
    // Update is handled in animation loop
  }
  
  override def createContext(): GraphicsContext = {
    if (!_isCreated) create()
    context.getOrElse {
      val ctx = new WebGLContext(config)
      context = Some(ctx)
      ctx
    }
  }
  
  override def destroyContext(ctx: GraphicsContext): Unit = {
    if (context.contains(ctx)) {
      context = None
    }
  }
  
  override def getCurrentContext(): Option[GraphicsContext] = context
  
  override def createWindow(windowConfig: WindowConfig): Window = {
    if (!_isCreated) create()
    
    // For WebGL, we use the canvas element as the window
    // In browser, typically there's one canvas = one window
    val canvas = dom.document.getElementById("glcanvas").asInstanceOf[dom.html.Canvas]
    
    val window = new CanvasWindowImpl(windowConfig, config, canvas, this)
    windows += window
    
    // Set canvas size
    canvas.width = windowConfig.width
    canvas.height = windowConfig.height
    
    publish(WindowCreated(window.id, window.width, window.height))
    window
  }
  
  override def destroyWindow(window: Window): Unit = {
    windows.find(_ == window).foreach { w =>
      windows -= w
      publish(WindowDestroyed(window.id))
      w match {
        case canvasWindow: CanvasWindowImpl =>
          canvasWindow.destroy()
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
    graphicsImpl.gl.glClear(graphicsImpl.gl.GL_COLOR_BUFFER_BIT | graphicsImpl.gl.GL_DEPTH_BUFFER_BIT)
  }
  
  override def clearColor(r: Float, g: Float, b: Float, a: Float): Unit = {
    graphicsImpl.gl.glClearColor(r, g, b, a)
  }
  
  override def clearDepth(depth: Double): Unit = {
    graphicsImpl.gl.glClearDepthf(depth.toFloat)
  }
  
  override def setViewport(x: Int, y: Int, width: Int, height: Int): Unit = {
    graphicsImpl.gl.glViewport(x, y, width, height)
  }
  
  override def graphics: Graphics = graphicsImpl
  
  // Internal method to publish window events
  def publishWindowEvent(event: WindowEvent): Unit = {
    publish(event)
  }
  
  // Internal method to publish input events
  def publishInputEvent(event: InputEvent): Unit = {
    publish(event)
  }
}

// WebGL Context implementation
class WebGLContext(val config: WebGLConfig) extends GraphicsContext {
  override def id: String = s"webgl-context-${System.identityHashCode(this)}"
  override def isValid: Boolean = true
}
