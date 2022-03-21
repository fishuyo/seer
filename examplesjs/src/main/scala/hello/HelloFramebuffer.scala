package seer 
package examplesjs

import graphics._
import graphics.webgl._

import runtime.SeerApp
import math.Random

import scala.scalajs.js.annotation._

@JSExportTopLevel("HelloFramebuffer")
object HelloFramebuffer extends SeerApp {

	val graphics = new WebglGraphicsRuntimeModule()

	useModules(graphics :: List())

	var timer = 0.0
  var shader:ShaderProgram = _
  var texshader:ShaderProgram = _
  var mesh:Mesh = _
  var quad:Mesh = _
  val coords = Array(-0.8f,-0.3f,0.0f,  0.3f,-0.3f,0.0f,  0.0f,0.3f,0.0f,  0.2f,0.2f,0.0f, 0.6f,0.6f,0.0f,  0.4f,-0.4f,0.0f)

  var tex:Texture = _
  var fbo:FrameBuffer = _
  var rbo:RenderBuffer = _

  val (w,h) = (64,64)
  val channels = 4
  val bytes = 1
  val image = Image(w,h,channels,bytes)


  graphics.onInit = () => {
    val gl = Graphics().gl
    import gl._ 

    val vert = """
      #version 300 es
      layout(location = 0) in vec3 position;
      void main(){ 
        gl_Position = vec4(position, 1.0); 
      }"""

    val frag = """
      #version 300 es
      precision highp float; 

      uniform vec4 color;
      out vec4 fragColor;
      void main() {
        fragColor = color;
      }"""

    val vertTex = """
      #version 300 es
      layout(location = 0) in vec3 position;
      layout(location = 2) in vec2 uv;
      out vec2 vuv;
      void main(){ 
        vuv = uv;
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragTex = """
      #version 300 es
      precision highp float; 
      in vec2 vuv;
      out vec4 fragColor;
      uniform sampler2D tex0;
      void main() {
        vec4 color = texture(tex0, vuv);
        fragColor = vec4(color);
      }"""
    
    shader = new ShaderProgram().create(vert.stripLeading, frag.stripLeading)
    texshader = new ShaderProgram().create(vertTex.stripLeading, fragTex.stripLeading)
    
    mesh = new Mesh()
    mesh.resize(coords.length / 3)

    quad = new Mesh()
    quad.resize(4, hasTexcoords=true)
    quad.vertices.put(Array(-1.0f,-1.0f,0.0f,  1.0f,-1.0f,0.0f,  1.0f,1.0f,0.0f, -1.0f,1.0f,0.0f))
    quad.texcoords.put(Array(0f,0f,  1.0f,0f,  1.0f,1.0f, 0f,1.0f))
    quad.resizeIndices(6)
    quad.indices.put(Array(0,1,2,2,3,0))
    quad.update()

    fbo = new FrameBuffer().create()
    // rbo = new RenderBuffer().create()
    tex = new Texture().create()
    tex.bind(0)
    tex.update(image)
    // rbo.bind()
    // rbo.resize(w,h)
    // rbo.unbind()
    fbo.bind()
    fbo.attachTexture(tex)
    // fbo.attachRBO(rbo)
    fbo.unbind()

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

    fbo.begin()
    glViewport(0,0,w,h)
    val r = Random.float 
    if(timer == 0.0) glClearColor(r(), r(), r(), 1.0f)
    glClear(GL_COLOR_BUFFER_BIT)

    shader.bind()
    if(timer == 0.0) shader.uniform("color", Array(r(),r(),r(),1.0f), 4)

    mesh.draw()
    fbo.end()


    texshader.bind()
    glViewport(0,0,graphics.w,graphics.h)
    glClear(GL_COLOR_BUFFER_BIT)


    texshader.uniform("tex0", 0)
    tex.bind(0)
    quad.draw()

	}

}