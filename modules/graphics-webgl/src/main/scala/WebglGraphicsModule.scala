
package seer
package graphics

import webgl._

// import scalajs.js
// import scala.scalajs.js.typedarray._
import scala.scalajs.js.annotation._
import org.scalajs.dom
import dom.raw.WebGLRenderingContext
// import dom.raw.WebGLRenderingContext._
import typings.std.WebGL2RenderingContext

// import java.nio.FloatBuffer


class WebglGraphicsModule extends Module {

  var g:GraphicsWebGLImpl = _
  
  var onInit = () => {}
  var onUpdate = (dt:Double) => {}
  var onDraw = (g:Graphics) => {}

  var onKeyEvent = (event:KeyEvent) => {}
  var onMouseEvent = (event:MouseEvent) => {}
  var onTouchEvent = (name:String) => (event:dom.raw.TouchEvent) => {}
  var mouseState = new MouseState()


  var (w,h) = (0,0)

  override def init() = {
    println("Initializing WebGL Graphics RuntimeModule..")
    // println("WebGL Version " + Version.getVersion())

    val container = Option(dom.document.getElementById("glcanvas")).getOrElse {
      val elem = dom.document.createElement("canvas")
      elem.id = "glcanvas"
      dom.document.body.appendChild(elem)
      elem
    }

    val can = container.asInstanceOf[dom.html.Canvas]
    
    can.width = dom.window.innerWidth.toInt
    can.height = dom.window.innerHeight.toInt
    w = can.width; h = can.height;

    val webgl = can.getContext("webgl2").asInstanceOf[WebGLRenderingContext]     
    val webgl2 = webgl.asInstanceOf[WebGL2RenderingContext]     
    g = new GraphicsWebGLImpl(webgl)
    Graphics() = g

    webgl.getExtension("EXT_color_buffer_float")

    can.onmousedown = (e:dom.MouseEvent) => { mouse2mouse(e,"down") }
    can.onmouseup = (e:dom.MouseEvent) => { mouse2mouse(e,"up") }
    can.onmousemove = (e:dom.MouseEvent) => { mouse2mouse(e,"move") }

    // can.ontouchstart = (e:dom.TouchEvent) => { println(e) }
    can.addEventListener("touchstart", onTouchEvent("start"), false)
    can.addEventListener("touchmove", onTouchEvent("move"), false)
    can.addEventListener("touchend", onTouchEvent("end"), false)
    can.addEventListener("touchcancel", onTouchEvent("cancel"), false)

    onInit()
  }

  def mouse2mouse(e:dom.MouseEvent, name:String) = {
    mouseState.event = name
    mouseState.dx = e.clientX - mouseState.px
    mouseState.dy = e.clientY - mouseState.py
    mouseState.px = e.clientX
    mouseState.py = e.clientY
    mouseState.x = e.clientX / w
    mouseState.y = 1.0f - (e.clientY / h)
    onMouseEvent(MouseEvent(mouseState))
  }

  override def start() = run(0.0f)
  // override def startBlocking() = {}

  var lastTime = 0.0

  def run(time:Double):Unit = {
    val dt = (time - lastTime) * 0.001 // to seconds
    lastTime = time

    onUpdate(dt)
    onDraw(g)
    
    dom.window.requestAnimationFrame(run)
  }

  override def cleanup() = {
  }

}