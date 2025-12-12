package seer
package examples

import graphics._
import math._

object TestCursor extends SeerApp {

  var shader: ShaderProgram = _
  var mesh: Mesh = _

  graphics.onCreate = () => {
    val gl = Graphics().gl
    import gl._

    val vertText = """
      #version 330 core
      layout(location = 0) in vec3 position;
      layout(location = 1) in vec4 color;
      out vec4 vColor;
      void main(){ 
        vColor = color;
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragText = """
      #version 330 core
      in vec4 vColor;
      out vec4 fragColor;
      void main() {
        fragColor = vColor;
      }"""

    shader = new ShaderProgram().create(vertText, fragText)
    
    // Create mesh with 9 vertices (3 triangles)
    mesh = new Mesh()
    mesh.resize(9, hasColors = true)
    
    // Triangle 1: Red, positioned on the left
    mesh.vertices.put(Array(
      -0.8f, -0.5f, 0.0f,  // vertex 0
      -0.3f, -0.5f, 0.0f,  // vertex 1
      -0.55f, 0.2f, 0.0f   // vertex 2
    ))
    mesh.colors.put(Array(
      1.0f, 0.0f, 0.0f, 1.0f,  // red
      1.0f, 0.0f, 0.0f, 1.0f,  // red
      1.0f, 0.0f, 0.0f, 1.0f   // red
    ))
    
    // Triangle 2: Green, positioned in the center
    mesh.vertices.put(Array(
      -0.2f, -0.5f, 0.0f,  // vertex 3
      0.2f, -0.5f, 0.0f,   // vertex 4
      0.0f, 0.2f, 0.0f     // vertex 5
    ))
    mesh.colors.put(Array(
      0.0f, 1.0f, 0.0f, 1.0f,  // green
      0.0f, 1.0f, 0.0f, 1.0f,  // green
      0.0f, 1.0f, 0.0f, 1.0f   // green
    ))
    
    // Triangle 3: Blue, positioned on the right
    mesh.vertices.put(Array(
      0.3f, -0.5f, 0.0f,   // vertex 6
      0.8f, -0.5f, 0.0f,  // vertex 7
      0.55f, 0.2f, 0.0f   // vertex 8
    ))
    mesh.colors.put(Array(
      0.0f, 0.0f, 1.0f, 1.0f,  // blue
      0.0f, 0.0f, 1.0f, 1.0f,  // blue
      0.0f, 0.0f, 1.0f, 1.0f   // blue
    ))
    
    mesh.update()
  }

  graphics.onUpdate = (dt: Double) => {
    // No animation needed for this simple example
  }

  graphics.onDraw = (g: Graphics) => {
    import g.gl._

    glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
    glClear(GL_COLOR_BUFFER_BIT)

    shader.bind()
    mesh.draw()
  }

}
