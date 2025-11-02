package seer 
package examples

import graphics._
import math._


object Circle extends SeerApp {

  var timer = 0.0

  var shader:ShaderProgram = _
  var mesh:Mesh = _


  graphics.onCreate = () => {
    val gl = Graphics().gl
    import gl._ 

    val vertText = """
      #version 330 core
      layout(location = 0) in vec3 position;
      // layout(location = 2) in vec2 uv;
      // out vec2 vuv;

      uniform vec2 size;
      void main(){ 
        // vuv = uv;
        vec2 pos = position.xy * vec2(size.y/size.x, 1.0);
        gl_Position = vec4(pos, 0.0, 1.0); 
      }"""

    val fragText = """
      #version 330 core
      // uniform vec2 mouse;
      uniform float time;
      // in vec2 vuv;
      out vec4 fragColor;

      void main() {
        // vec2 uv = vuv; // / vec2(640.,480.);
        // uv.y *= 480./640.;
        float t = mod(time,1.);
        // T = mouse.xy;

        fragColor = vec4(1., t, 0., 1.);
      }"""

    shader = new ShaderProgram().create(vertText, fragText)
    
    val n = 20
    mesh = Mesh.circle(n, 0.1f)
  }

  graphics.onUpdate = (dt:Double) => {
    timer += dt    
    mesh.update()    
  }

  graphics.onDraw = (g:Graphics) => {
    import g.gl._

    val (w,h) = Window().getSize()
    println(s"$w $h")

    shader.bind()
    shader.uniform("time", timer)
    shader.uniform("size", Vec2(w,h))
    // shader.uniform("mouse", Vec2(0.5f,0.5f))

    mesh.draw()
  }



  
}
