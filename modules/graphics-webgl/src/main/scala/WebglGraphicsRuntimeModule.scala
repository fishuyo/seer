
package seer.graphics
package webgl

import seer.runtime._ 

// import scalajs.js
// import scala.scalajs.js.typedarray._
import scala.scalajs.js.annotation._
import org.scalajs.dom
import dom.raw.WebGLRenderingContext
// import dom.raw.WebGLRenderingContext._
import typings.std.WebGL2RenderingContext

// import java.nio.FloatBuffer


class WebglGraphicsRuntimeModule extends RuntimeModule {

  var g:GraphicsWebGLImpl = _
  
  var onInit = () => {}
  var onUpdate = (dt:Double) => {}
  var onDraw = (g:Graphics) => {}

  var (w,h) = (0,0)

  override def init() = {
    println("Initializing WebGL Graphics RuntimeModule..")
    // println("WebGL Version " + Version.getVersion())

    var can: dom.html.Canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    dom.document.body.appendChild(can)
    
    can.width = dom.window.innerWidth.toInt
    can.height = dom.window.innerHeight.toInt
    w = can.width; h = can.height;

    val webgl = can.getContext("webgl2").asInstanceOf[WebGLRenderingContext]     
    val webgl2 = webgl.asInstanceOf[WebGL2RenderingContext]     
    g = new GraphicsWebGLImpl(webgl)
    Graphics() = g


    can.onmousedown = (e:dom.MouseEvent) => { }
    can.onmouseup = (e:dom.MouseEvent) => { }
    can.onmousemove = (e:dom.MouseEvent) => { println(e) }

    onInit()
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