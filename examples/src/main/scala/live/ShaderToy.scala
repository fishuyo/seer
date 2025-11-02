package seer 
package examples

import graphics._
import math._
import actor._

import scala.io.Source

object ShaderToy extends SeerApp {

  var timer = 0.0
  var shader:ShaderProgram = _
  val shaderpath = os.pwd / "src/main/scala/live/shaders"


  var vertcode = os.read(shaderpath / "test.vert")
  var fragcode = os.read(shaderpath / "test.frag")

  var mesh:Mesh = _
  var texture:Texture = _

  graphics.onCreate = () => {
    val gl = Graphics().gl
    import gl._ 

    shader = new ShaderProgram()
    shader.setCode(vertcode, fragcode)

    // FileMonitor(shaderpath){ (paths:Set[os.Path]) => 
    FileMonitor(shaderpath.toString){ (f) => 
      println("files changed: " + f.toString)
      vertcode = os.read(shaderpath / "test.vert")
      fragcode = os.read(shaderpath / "test.frag")
      shader.setCode(vertcode, fragcode)
    }
    
    mesh = new Mesh()
    mesh.resize(4, hasTexcoords=true)
    mesh.vertices.put(Array(-1.0f,-1.0f,0.0f,  1.0f,-1.0f,0.0f,  1.0f,1.0f,0.0f, -1.0f,1.0f,0.0f))
    mesh.texcoords.put(Array(0f,0f,  1.0f,0f,  1.0f,1.0f, 0f,1.0f))
    mesh.resizeIndices(6)
    mesh.indices.put(Array(0,1,2,2,3,0))
    mesh.update()
  }

  graphics.onUpdate = (dt:Double) => {
    timer += dt
  }

  graphics.onDraw = (g:Graphics) => {
    import g.gl._

    shader.bind()
    shader.uniform("time", timer)
    mesh.draw() 
  }

}
