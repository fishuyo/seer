package seer 
package examples

import graphics._
import graphics.lwjgl._

import runtime._
import math._
import actor._

import scala.io.Source

object ShaderToy extends SeerApp {

	val graphics = new LwjglGraphicsRuntimeModule()

	useModules(graphics :: List())

	var timer = 0.0
  var shader:ShaderProgram = _
  val vertpath = "src/main/scala/live/shaders/vert.glsl"
  val fragpath = "src/main/scala/live/shaders/frag.glsl"

  var vertcode = Source.fromFile(vertpath).mkString
  var fragcode = Source.fromFile(fragpath).mkString

  var mesh:Mesh = _
  var texture:Texture = _

  graphics.onInit = () => {
    val gl = Graphics().gl
    import gl._ 

    shader = new ShaderProgram()
    shader.setCode(vertcode, fragcode)

    FileMonitor(vertpath){ (f) => 
      vertcode = Source.fromFile(f.pathAsString).mkString
      shader.setCode(vertcode, fragcode)
    }
    FileMonitor(fragpath){ (f) => 
      fragcode = Source.fromFile(f.pathAsString).mkString
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
    mesh.draw()
	}

}
