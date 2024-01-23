package seer
package graphics
package webgl

import org.scalajs.dom
import dom.raw.WebGLRenderingContext
// import typings.std.WebGL2RenderingContext

class GraphicsWebGLImpl(webgl:WebGLRenderingContext) extends Graphics {

  val gl:GLES30 = new GLES30WebGLImpl(webgl) 

}