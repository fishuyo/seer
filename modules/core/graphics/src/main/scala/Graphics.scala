
package seer
package graphics


object Graphics {
  var interface:Option[Graphics] = None
  def apply() = interface.get
  def update(g:Graphics) = interface = Some(g)
}

trait Graphics {
  val gl:GLES30

  val shaderGen = new ShaderGenerator()
  
  // Basic rendering methods (delegated to GraphicsModule in practice)
  def clear(color: Color): Unit = {
    clearColor(color.r, color.g, color.b, color.a)
    gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT)
  }
  
  def clearColor(r: Float, g: Float, b: Float, a: Float): Unit = {
    gl.glClearColor(r, g, b, a)
  }
  
  def clearDepth(depth: Double): Unit = {
    gl.glClearDepthf(depth.toFloat)
  }
  
  def setViewport(x: Int, y: Int, width: Int, height: Int): Unit = {
    gl.glViewport(x, y, width, height)
  }
}