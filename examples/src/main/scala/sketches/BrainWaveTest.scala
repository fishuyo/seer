// package seer 
// package aforest

// import graphics._
// import math._
// import osc._

// import de.sciss.osc._
// import org.lwjgl.glfw.GLFW._

// import me.walkerknapp.devolay.Devolay
// import me.walkerknapp.devolay.DevolayFrameFourCCType
// import me.walkerknapp.devolay.DevolaySender
// import me.walkerknapp.devolay.DevolayVideoFrame


// object BrainWaveTest extends SeerApp {

//   var meshShader:ShaderProgram = _
//   var compositeShader:ShaderProgram = _
//   var texShader:ShaderProgram = _

//   var circle:Mesh = _
//   var ring:Mesh = _
//   var quad:Mesh = _
//   var particles:Mesh = _

//   var fbo:FrameBuffer = _
//   var tex0:Texture = _
//   var tex1:Texture = _

//   val noise = new Noise()
//   var (w,h) = (640,480)
//   val channels = 4
//   val bytes = 1
//   var image = Image(w,h,channels,bytes)

//   val sender = new DevolaySender("ParticleTest");

//   // Create a video frame
//   val videoFrame = new DevolayVideoFrame();
//   videoFrame.setResolution(w, h);
//   videoFrame.setFourCCType(DevolayFrameFourCCType.BGRX);
//   videoFrame.setData(image.buffer);
//   videoFrame.setFrameRate(30, 1);

//   var timer = 0.0
//   var animR = 0.01f
//   var blend = Vec2(0.3f,0.995f)

//   var (alpha, beta, gamma, delta, theta) = (0f,0f,0f,0f,0f)

//   val handler:OSC.OSCHandler = {
//     case ( Message("/muse/elements/alpha_absolute", value:Float), addr) => 
//       alpha = value
//     case ( Message("/muse/elements/beta_absolute", value:Float), addr) => 
//       beta = value
//     case ( Message("/muse/elements/delta_absolute", value:Float), addr) => 
//       delta = value
//     case ( Message("/muse/elements/theta_absolute", value:Float), addr) => 
//       theta = value
//     case ( Message("/muse/elements/gamma_absolute", value:Float), addr) => 
//       gamma = value
//     case msg => //println(msg)
//   }
//   val recv = new OSCRecv()
//   recv.bind(handler)
//   recv.listen(8000)

//   Gravity.set(0f,0f,0f)
//   var numAttractors = 6
//   var attractors = (0 until numAttractors).map{ case _ => 
//     val a = new Attractor
//     a.radius = 0.05f
//     a.strength = 0.55f + Random.float()*0.01f
//     a
//   }
//   var repulsors = (0 until numAttractors).map{ case _ => 
//     val a = new Attractor
//     a.radius = 0.005f
//     a.maxRadius = 0.1f
//     a.strength = 0.05f + Random.float()*0.01f
//     a
//   }

//   val numParticles = 100000
//   val emitter = new ParticleEmitter(numParticles) {
//     override def animate(dt:Float) = {
//       attractors.foreach{ case a => a(this.particles.toSeq) }
//       repulsors.foreach{ case a => a(this.particles.toSeq) }
//       super.animate(dt)
//     }
//   }

//   val (nx,ny,nz) = (200,20,20)
//   val field = VecField3(Vec3(nx,ny,nz),Vec3(0.2f,0.1f,0.1f))
//   var updateField = false
//   emitter.ttl = 0f
//   emitter.damping = 100f
//   // emitter.field = Some(field)
//   emitter.fieldAsForce = true
//   randomizeField()

//   // (0 until 30000).foreach{ case _ => emitter += Particle(Random.vec3()) }
//   // (0 until 100000).foreach{ case _ => emitter += Particle(Random.vec3()*Vec3(10f,0f,0f) + Vec3(0f,-1f,0f)) }
//   // emitter.particles.foreach{ case p => p.mass = 1f + Random.float()*5f }

//   def randomizeField() = {
//     for( z<-(0 until nz); y<-(0 until ny); x<-(0 until nx)){
//       if(emitter.fieldAsForce) field.update(x,y,z, Random.vec3() + Vec3(0f,0.4f,0))
//       else field.update(x,y,z, Random.vec3()*0.01f)
//     }
//   }

//   graphics.onCreate = () => {
//     val gl = Graphics().gl
//     import gl._ 


//     val meshVert = """
//       #version 330 core
//       layout(location = 0) in vec3 position;
//       uniform vec2 size;
//       uniform float scale;
//       uniform vec2 translate;
//       void main(){ 
//         vec2 pos = position.xy * vec2(size.y/size.x, 1.0) * scale;
//         vec2 tr = translate * vec2(size.y/size.x, 1.0);
//         gl_Position = vec4(pos + tr, 0.0, 1.0); 
//       }"""

//     val meshFrag = """
//       #version 330 core
//       uniform vec3 color;
//       out vec4 fragColor;

//       void main() {
//         fragColor = vec4(color, 1.);
//       }"""
    
//     val compositeVert = """
//       #version 330 core
//       layout(location = 0) in vec3 position;
//       layout(location = 2) in vec2 uv;
//       out vec2 vuv;
//       void main(){ 
//         vuv = uv;
//         gl_Position = vec4(position, 1.0); 
//       }"""

//     val compositeFrag = s"""
//       #version 330 core
//       in vec2 vuv;
//       out vec4 fragColor;
//       uniform sampler2D tex0;
//       uniform sampler2D tex1;
//       uniform vec2 blend;
//       void main() {
//         vec4 color0 = texture(tex0, vuv) * blend.x;
//         vec4 color1 = texture(tex1, vuv) * blend.y;
//         fragColor = color0 + color1;
//       }"""

//     val texVert = """
//       #version 330 core
//       layout(location = 0) in vec3 position;
//       layout(location = 2) in vec2 uv;
//       out vec2 vuv;
//       void main(){ 
//         vuv = uv;
//         gl_Position = vec4(position, 1.0); 
//       }"""

//     val texFrag = """
//       #version 330 core
//       in vec2 vuv;
//       out vec4 fragColor;
//       uniform sampler2D tex;

//       void main() {
//         vec4 color = texture(tex, vuv);
//         fragColor = vec4(color);
//       }"""
    
//     meshShader = new ShaderProgram().create(meshVert, meshFrag)
//     compositeShader = new ShaderProgram().create(compositeVert, compositeFrag)
//     texShader = new ShaderProgram().create(texVert, texFrag)
    
//     circle = Mesh.circle(60, 1f, filled=true)
//     ring = Mesh.circle(60, 1f)
//     quad = Mesh.quad()
    
//     particles = new Mesh()
//     particles.primitive = GL_POINTS
//     particles.resize(numParticles)

//     fbo = new FrameBuffer().create()
//     tex0 = new Texture().create()
//     tex0.bind().update(image)
//     tex1 = new Texture().create()
//     tex1.bind().update(image)

//     Window().onFramebufferResize = (width,height) => {
//       w = width; h = height
//       image = Image(w,h,channels,bytes)
//       tex0.bind().update(image)
//       tex1.bind().update(image)

//       // update ndi frame size
//       videoFrame.setResolution(w, h);
//       videoFrame.setData(image.buffer);
//     }

//     // Window().setPos(0,500)
//     // Window().setSize(2056,206)

//     Window().onKeyEvent = this.onKeyEvent

//   }

//   var onKeyEvent:(KeyEvent)=>Unit = (event:KeyEvent) => {
//     Window().defaultKeyHandler(event)
//     if(event.action == GLFW_PRESS){
//       println(event.key)
//       event.key match {
//         case "r" => emitter.particles.clear() 
//         case "v" => blend.set(0f,0f)
//         case "b" => blend.set(0.3f,0.995f)
//         case _ => 
//       }
//     }
//   }

//   graphics.onUpdate = (dt:Double) => {
//     timer += dt
//     // val duration = 3.0
//     // if(timer > duration) timer = 0.0
//     // animR = Ease.expoOut((timer/duration).toFloat)  

//     val blend0 = 0.3f
//     val blend1 = 1.0f - (1.0f - (alpha+delta)/2)*0.15f
//     // println(s"$blend0 $blend1")
//     blend.set(blend0, blend1)

//     emitter.animate(dt.toFloat)  
//     val pos = emitter.particles.map(_.position).flatMap{ case v => Array(v.x,v.y,v.z)}.toArray
//     particles.vertices.put(pos)
//     particles.update()

//     // tex0.bind(0)
//     // image.buffer.rewind()
//     // for( y <- 0 until h; x <- 0 until w; c <- 0 until channels){
//     //   val value = 0.5f * noise(x, y, 10*timer.toFloat) + 0.5f
//     //   image.buffer.put((value*255).toByte)
//     // }

//     // // send image
//     // sender.sendVideoFrameAsync(videoFrame);

//     // tex0.update(image)
//   }

//   graphics.onDraw = (g:Graphics) => {
//     import g.gl._

//     // draw mesh scene to texture
//     fbo.begin()
//     fbo.attachTexture(tex0)
    
//     meshShader.bind()
//     meshShader.uniform("size", Vec2(w,h))
//     glViewport(0,0,w,h)
//     glClear(GL_COLOR_BUFFER_BIT)
//     meshShader.uniform("color", Vec3(0.15f))
//     meshShader.uniform("translate", Vec2(0,0))
//     meshShader.uniform("scale", 1f)
//     particles.draw()

//     glLineWidth(5.0)
//     val ringR = 0.2f
//     meshShader.uniform("color", Vec3(1f))
//     meshShader.uniform("translate", Vec2(0,0))
//     meshShader.uniform("scale", ringR)
//     ring.draw()

//     (0 until numAttractors).foreach{ case i =>
//       val x = scala.math.cos(i.toFloat/numAttractors*2*Pi)
//       val y = scala.math.sin(i.toFloat/numAttractors*2*Pi)
//       val p3 = Vec3(x,y,0)
//       val p2 = Vec2(x,y)
//       val wx = -10f + (i.toFloat + 0.5)*(20f/(numAttractors))
//       attractors(i).position = p3*1.5f
//       repulsors(i).position = p3*ringR
//       (0 until 5).foreach{ case _ => emitter += Particle(p3*ringR + Random.vec3()*0.005f) }
//       // val ps = (0 until 5).map{ case _ => Particle(Vec3(wx, -1f, 0f) + Random.vec3()*Vec3(0.05f,0f,0f)) }
//       // ps.foreach { case p => p.mass = 1f + Random.float()*3f; emitter += p }

      
//       meshShader.uniform("translate", p2*ringR)
//       meshShader.uniform("scale",animR*0.05f+0.0f)
//       ring.draw()
//       meshShader.uniform("scale", 0.01f)
//       circle.draw()  
//     }

//     fbo.end()

//     // feedback through composite shader
//     fbo.begin()
//     fbo.attachTexture(tex1)
    
//     compositeShader.bind()
//     glViewport(0,0,w,h)
//     tex0.bind(0)
//     tex1.bind(1)
//     compositeShader.uniform("tex0", 0)
//     compositeShader.uniform("tex1", 1)
//     compositeShader.uniform("blend", blend)
//     quad.draw()

//     fbo.end()

//     // send image
//     // fbo.readBytes(image.buffer)
//     // sender.sendVideoFrameAsync(videoFrame);


//     // draw composite to screen
//     texShader.bind()
//     glViewport(0,0,w,h) //graphics.w,graphics.h)
//     tex1.bind(0)
//     texShader.uniform("tex", 0)
//     // // glClear(GL_COLOR_BUFFER_BIT)
//     quad.draw()

//     // // swap textures
//     // val tmp = tex0
//     // tex0 = tex1
//     // tex1 = tmp 
//   }

// }