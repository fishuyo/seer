// package seer 
// package examples

// import graphics._
// import math._
// import actor._

// import scala.io.Source

// object Particles extends SeerApp {

//   var timer = 0.0
//   var particleShader:ShaderProgram = _
//   var drawShader:ShaderProgram = _
//   var particleMesh:Mesh = _
//   var quad:Mesh = _

//   var fbo:FrameBuffer = _
//   var tex0:Texture = _
//   var tex1:Texture = _

//   val n = 100
//   val channels = 4
//   val bytes = 4
//   var image = Image(n,n,channels,bytes)

//   var mouse = Vec2()

//   val path = "src/main/scala/fluids/shaders"

//   graphics.onCreate = () => {
//     val gl = Graphics().gl
//     import gl._ 

//     particleShader = new LiveShader(s"$path/particles.vert", s"$path/particles.frag")
//     drawShader = new LiveShader(s"$path/draw.vert", s"$path/draw.frag")
    
//     quad = Mesh.quad()

//     particleMesh = new Mesh()
//     particleMesh.primitive = GL_POINTS
//     particleMesh.resize(n*n)
//     for(i <- 0 until n; j <- 0 until n)
//       particleMesh.vertices.put(Array(i.toFloat/n,j.toFloat/n,0.0f))
//     particleMesh.update()

//     // initialize fbo texture image
//     for(y <- 0 until n; x <- 0 until n){
//         image.floatBuffer.put(y*(n*4)+(x*4)+0, Random.float(-0.1f,0.1f)())
//         image.floatBuffer.put(y*(n*4)+(x*4)+1, Random.float(-0.1f,0.1f)())
//         image.floatBuffer.put(y*(n*4)+(x*4)+2, Random.float(-1f,1f)())
//         image.floatBuffer.put(y*(n*4)+(x*4)+3, Random.float(-1f,1f)())
//     }

//     // create 2 textures, to swap and feedback into the fbo
//     // tex0 will be input sampler first, tex1 will be rendered to first
//     fbo = new FrameBuffer().create()
//     tex0 = new Texture().create()
//     tex1 = new Texture().create()
//     tex0.bind(0)
//     tex0.update(image)
//     tex1.bind(0)
//     tex1.update(image)


//     Window().onMouseEvent = (e) => {
//       mouse = Vec2(e.state.x, e.state.y)
//     }

//   }

//   graphics.onUpdate = (dt:Double) => {
//     timer += dt
//     if(timer > 0.5) timer = 0.0    
//   }

//   graphics.onDraw = (g:Graphics) => {
//     import g.gl._

//     fbo.begin()
    
//     // for(i <- 0 until 1){
//       fbo.attachTexture(tex1)
//       particleShader.bind()
//       glViewport(0,0,n,n)
//       tex0.bind(0)
//       particleShader.uniform("particlesPosDir", 0)
//       // particleShader.uniform("dx", 1.0/n)
//       // particleShader.uniform("screenSize", Vec2(graphics.w, graphics.h))
//       // particleShader.uniform("brush", mouse)

//       quad.draw()

//       // swap textures
//       // val tmp = tex0
//       // tex0 = tex1
//       // tex1 = tmp 
//     // }

//     fbo.end()


//     drawShader.bind()
//     glViewport(0,0,graphics.w,graphics.h)
//     tex1.bind(0)
//     drawShader.uniform("particlesPosDir", 0)
//     // glClear(GL_COLOR_BUFFER_BIT)
//     particleMesh.draw()

//     // swap textures
//     val tmp = tex0
//     tex0 = tex1
//     tex1 = tmp 
//   }

// }


// class LiveShader(val vpath:String, val fpath:String) extends ShaderProgram {

//   var vcode = Source.fromFile(vpath).mkString
//   var fcode = Source.fromFile(fpath).mkString
//   this.setCode(vcode, fcode)

//   FileMonitor(vpath){ (f) => 
//     vcode = Source.fromFile(f.pathAsString).mkString
//     this.setCode(vcode, fcode)
//   }
//   FileMonitor(fpath){ (f) => 
//     fcode = Source.fromFile(f.pathAsString).mkString
//     this.setCode(vcode, fcode)
//   }


// }