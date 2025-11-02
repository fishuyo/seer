package seer 
package examples

import graphics._
import math.Random


object Diffusion extends SeerApp {

  var timer = 0.0
  var fluidShader:ShaderProgram = _
  var texShader:ShaderProgram = _
  var quad:Mesh = _

  var fbo:FrameBuffer = _
  var tex0:Texture = _
  var tex1:Texture = _

  val (w,h) = (640,480)
  val channels = 4
  val bytes = 4
  val image = Image(w,h,channels,bytes)


  graphics.onCreate = () => {
    val gl = Graphics().gl
    import gl._ 

    val vert = """
      #version 330 core
      layout(location = 0) in vec3 position;
      layout(location = 2) in vec2 uv;
      out vec2 vuv;
      void main(){ 
        vuv = uv;
        gl_Position = vec4(position, 1.0); 
      }"""

    val frag = s"""
      #version 330 core
      in vec2 vuv;
      out vec4 fragColor;
      uniform sampler2D tex;
      void main() {
        float dx = 1.0/$w;
        float dy = 1.0/$h;

        vec4 color = texture(tex, vuv);
        vec4 L = texture(tex, vuv + vec2(0,-dy))
          +  texture(tex, vuv + vec2(-dx,0)) 
          -  4.0 * texture(tex,  vuv )
          +  texture(tex, vuv + vec2(dx,0)) 
          +  texture(tex, vuv + vec2(0,dy));

        color += L * 0.1 * 1.55;
        fragColor = vec4(color);
      }"""

    val vertTex = """
      #version 330 core
      layout(location = 0) in vec3 position;
      layout(location = 2) in vec2 uv;
      out vec2 vuv;
      void main(){ 
        vuv = uv;
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragTex = """
      #version 330 core
      in vec2 vuv;
      out vec4 fragColor;
      uniform sampler2D tex;
      void main() {
        vec4 color = texture(tex, vuv);
        fragColor = vec4(color);
      }"""
    
    fluidShader = new ShaderProgram().create(vert, frag)
    texShader = new ShaderProgram().create(vertTex, fragTex)
    
    quad = new Mesh()
    quad.resize(4, hasTexcoords=true)
    quad.vertices.put(Array(-1.0f,-1.0f,0.0f,  1.0f,-1.0f,0.0f,  1.0f,1.0f,0.0f, -1.0f,1.0f,0.0f))
    quad.texcoords.put(Array(0f,0f,  1.0f,0f,  1.0f,1.0f, 0f,1.0f))
    quad.resizeIndices(6)
    quad.indices.put(Array(0,1,2,2,3,0))
    quad.update()

    // initialize fbo texture image
    for(y <- 0 until h; x <- 0 until w; c <- 0 until 4){
      val px = x - w/2
      val py = y - h/2
      if( px*px + py*py < 100*100)
        image.floatBuffer.put(y*(w*4)+(x*4)+c, 1.0f)
        // image.buffer.put(y*w+x, 255.toByte)
    }

    // create 2 textures, to swap and feedback into the fbo
    // tex0 will be input sampler first, tex1 will be rendered to first
    fbo = new FrameBuffer().create()
    tex0 = new Texture().create()
    tex1 = new Texture().create()
    tex0.bind(0)
    tex0.update(image)
    tex1.bind(0)
    tex1.update(image)
  }

  graphics.onUpdate = (dt:Double) => {
    timer += dt
    if(timer > 0.5) timer = 0.0    
  }

  graphics.onDraw = (g:Graphics) => {
    import g.gl._

    fbo.begin()
    fbo.attachTexture(tex1)
    
    fluidShader.bind()
    glViewport(0,0,w,h)
    tex0.bind(0)
    fluidShader.uniform("tex", 0)
    quad.draw()

    fbo.end()


    texShader.bind()
    glViewport(0,0,graphics.w,graphics.h)
    tex1.bind(0)
    texShader.uniform("tex", 0)
    // glClear(GL_COLOR_BUFFER_BIT)
    quad.draw()

    // swap textures
    val tmp = tex0
    tex0 = tex1
    tex1 = tmp 
  }

}