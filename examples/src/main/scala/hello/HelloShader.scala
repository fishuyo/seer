package seer 
package examples

import graphics._
import math.Random


object HelloShader extends SeerApp {

	var timer = 0.0
  var shader:ShaderProgram = _
  var mesh:Mesh = _
  val coords = Array(-0.8f,-0.3f,0.0f,  0.3f,-0.3f,0.0f,  0.0f,0.3f,0.0f,  0.2f,0.2f,0.0f, 0.6f,0.6f,0.0f,  0.4f,-0.4f,0.0f)


  graphics.onInit = () => {
    val gl = Graphics().gl
    import gl._ 

    val vertText = """
      #version 330 core
      layout(location = 0) in vec3 position;
      void main(){ 
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragText = """
      #version 330 core

      uniform vec4 color;
      out vec4 fragColor;
      void main() {
        fragColor = color;
      }"""

    shader = new ShaderProgram().create(vertText, fragText)
    
    mesh = new Mesh()
    mesh.resize(coords.length / 3)
  }

	graphics.onUpdate = (dt:Double) => {
		timer += dt
		if(timer > 0.5) timer = 0.0

    for(i <- 0 until coords.length) coords(i) += Random.float(-0.005f, 0.005f)()
    mesh.vertices.put(coords)
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