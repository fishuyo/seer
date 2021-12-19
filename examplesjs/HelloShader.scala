package seer 
package examplesjs

import graphics._
import graphics.webgl._

import runtime.SeerApp
import math.Random

import scala.scalajs.js.annotation._

@JSExportTopLevel("HelloShader")
object HelloShader extends SeerApp {

	val graphics = new WebglGraphicsRuntimeModule()

	useModules(graphics :: List())

	var timer = 0.0
  var shader:ShaderProgram = _
  var mesh:Mesh = _
  val coords = Array(-0.8f,-0.3f,0.0f,  0.3f,-0.3f,0.0f,  0.0f,0.3f,0.0f,  0.2f,0.2f,0.0f, 0.6f,0.6f,0.0f,  0.4f,-0.4f,0.0f)


  graphics.onInit = () => {
    val gl = Graphics().gl
    import gl._ 

    val vertText = """#version 300 es
      layout(location = 0) in vec3 position;
      void main(){ 
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragText = """#version 300 es
      precision highp float; 

      uniform vec4 color;
      out vec4 fragColor;
      void main() {
        fragColor = color;
      }"""

    shader = new ShaderProgram().create(vertText, fragText)

    mesh = new Mesh() 
    mesh.vertices = BufferUtils.newFloatBuffer(coords.length)

  }

	graphics.onUpdate = (dt:Double) => {
		timer += dt
		if(timer > 0.5) timer = 0.0

    for(i <- 0 until coords.length) coords(i) += Random.float(-0.005f, 0.005f)()
    mesh.vertices.put(coords)
    mesh.vertices.rewind()
    mesh.update()

	}

	graphics.onDraw = (g:Graphics) => {
		import g.gl._

    val r = Random.float 
    if(timer == 0.0) glClearColor(r(), r(), r(), 1.0f)
    glClear(GL_COLOR_BUFFER_BIT)

    shader.bind()
    if(timer == 0.0) shader.uniform("color", Array(r(),r(),r(),1.0f))

    mesh.draw()
	}

}