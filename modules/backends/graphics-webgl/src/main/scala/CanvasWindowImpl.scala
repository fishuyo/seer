package seer.graphics

import scala.scalajs.js.annotation._
import org.scalajs.dom
import dom.raw.{WebGLRenderingContext, MouseEvent, KeyboardEvent, WheelEvent}
import seer._

/**
 * Canvas Window implementation for WebGL that conforms to the unified Window trait.
 * In the browser, the canvas element serves as the window.
 */
class CanvasWindowImpl(
  val windowConfig: WindowConfig,
  val glConfig: WebGLConfig,
  val canvas: dom.html.Canvas,
  val graphicsModule: WebGLGraphicsModule
) extends Window {
  
  private val windowId = s"canvas-window-${System.identityHashCode(this)}"
  private var _fullscreen = false
  
  override def id: String = windowId
  
  // Setup event handlers
  setupEventHandlers()
  
  private def setupEventHandlers(): Unit = {
    // Window resize (browser window resize)
    dom.window.onresize = { _ =>
      canvas.width = dom.window.innerWidth.toInt
      canvas.height = dom.window.innerHeight.toInt
      graphicsModule.publishWindowEvent(WindowResized(windowId, width, height))
    }
    
    // Keyboard events
    dom.window.onkeydown = (e: KeyboardEvent) => {
      val key = Key(e.key, e.keyCode.toInt, e.keyCode.toInt)
      val modifiers = KeyModifiers(
        shift = e.shiftKey,
        control = e.ctrlKey,
        alt = e.altKey,
        superKey = e.metaKey,
        capsLock = false, // Not easily accessible in browser
        numLock = false
      )
      graphicsModule.publishInputEvent(KeyPressed(windowId, key, modifiers))
      e.preventDefault()
    }
    
    dom.window.onkeyup = (e: KeyboardEvent) => {
      val key = Key(e.key, e.keyCode.toInt, e.keyCode.toInt)
      val modifiers = KeyModifiers(
        shift = e.shiftKey,
        control = e.ctrlKey,
        alt = e.altKey,
        superKey = e.metaKey,
        capsLock = false,
        numLock = false
      )
      graphicsModule.publishInputEvent(KeyReleased(windowId, key, modifiers))
      e.preventDefault()
    }
    
    // Mouse events
    canvas.onmousedown = (e: MouseEvent) => {
      val rect = canvas.getBoundingClientRect()
      val x = (e.clientX - rect.left) / width
      val y = 1.0 - ((e.clientY - rect.top) / height)
      
      val button = e.button match {
        case 0 => MouseLeft
        case 1 => MouseMiddle
        case 2 => MouseRight
        case 3 => MouseButton4
        case 4 => MouseButton5
        case _ => MouseLeft
      }
      
      val modifiers = KeyModifiers(
        shift = e.shiftKey,
        control = e.ctrlKey,
        alt = e.altKey,
        superKey = e.metaKey,
        capsLock = false,
        numLock = false
      )
      
      graphicsModule.publishInputEvent(MousePressed(windowId, button, x, y, modifiers))
      e.preventDefault()
    }
    
    canvas.onmouseup = (e: MouseEvent) => {
      val rect = canvas.getBoundingClientRect()
      val x = (e.clientX - rect.left) / width
      val y = 1.0 - ((e.clientY - rect.top) / height)
      
      val button = e.button match {
        case 0 => MouseLeft
        case 1 => MouseMiddle
        case 2 => MouseRight
        case 3 => MouseButton4
        case 4 => MouseButton5
        case _ => MouseLeft
      }
      
      val modifiers = KeyModifiers(
        shift = e.shiftKey,
        control = e.ctrlKey,
        alt = e.altKey,
        superKey = e.metaKey,
        capsLock = false,
        numLock = false
      )
      
      graphicsModule.publishInputEvent(MouseReleased(windowId, button, x, y, modifiers))
      e.preventDefault()
    }
    
    var lastMouseX = 0.0
    var lastMouseY = 0.0
    
    canvas.onmousemove = (e: MouseEvent) => {
      val rect = canvas.getBoundingClientRect()
      val x = (e.clientX - rect.left) / width
      val y = 1.0 - ((e.clientY - rect.top) / height)
      val dx = x - lastMouseX
      val dy = y - lastMouseY
      lastMouseX = x
      lastMouseY = y
      
      graphicsModule.publishInputEvent(MouseMoved(windowId, x, y, dx, dy))
    }
    
    canvas.onwheel = (e: dom.WheelEvent) => {
      val rect = canvas.getBoundingClientRect()
      val x = (e.clientX - rect.left) / width
      val y = 1.0 - ((e.clientY - rect.top) / height)
      
      graphicsModule.publishInputEvent(MouseScrolled(
        windowId,
        x,
        y,
        e.deltaX,
        e.deltaY
      ))
      e.preventDefault()
    }
    
    // Window focus events
    dom.window.onfocus = (_: dom.FocusEvent) => {
      graphicsModule.publishWindowEvent(WindowFocused(windowId))
    }
    
    dom.window.onblur = (_: dom.FocusEvent) => {
      graphicsModule.publishWindowEvent(WindowUnfocused(windowId))
    }
  }
  
  def destroy(): Unit = {
    // Cleanup event handlers if needed
    // In browser, we typically don't destroy the canvas
  }
  
  override def width: Int = canvas.width
  override def height: Int = canvas.height
  override def bufferWidth: Int = canvas.width // Canvas pixel size
  override def bufferHeight: Int = canvas.height
  
  override def makeCurrent(): Unit = {
    // In WebGL, context is already current for the canvas
    // This is a no-op but kept for API consistency
  }
  
  override def swapBuffers(): Unit = {
    // In WebGL, buffers are swapped automatically by the browser
    // This is a no-op but kept for API consistency
  }
  
  override def shouldClose: Boolean = false // Browser windows don't have a close state
  
  override def setShouldClose(value: Boolean): Unit = {
    if (value) {
      graphicsModule.publishWindowEvent(WindowClosed(windowId))
    }
  }
  
  override def getSize: (Int, Int) = (canvas.width, canvas.height)
  
  override def getBufferSize: (Int, Int) = (canvas.width, canvas.height)
  
  override def setSize(width: Int, height: Int): Unit = {
    canvas.width = width
    canvas.height = height
    graphicsModule.publishWindowEvent(WindowResized(windowId, width, height))
  }
  
  override def isFullscreen: Boolean = _fullscreen
  
  override def setFullscreen(fullscreen: Boolean): Unit = {
    if (fullscreen && !_fullscreen) {
      canvas.requestFullscreen()
      _fullscreen = true
    } else if (!fullscreen && _fullscreen) {
      dom.document.exitFullscreen()
      _fullscreen = false
    }
  }
  
  override def title: String = dom.document.title
  
  override def setTitle(title: String): Unit = {
    dom.document.title = title
  }
}
