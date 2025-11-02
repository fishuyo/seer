// package seer 
// package examples

// import graphics._
// import math._

// import org.lwjgl.glfw.GLFW._


// object MeditationFloor extends SeerApp {

//   var timer = 0.0
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

//   var (w,h) = (640,480)
//   val channels = 4
//   val bytes = 4
//   var image = Image(w,h,channels,bytes)


//   val imageLeaves = Image.load("/Users/fishuyo/Downloads/walk/PXL_20230920_214115268.jpg").get
//   val imageLeaves2 = Image.load("/Users/fishuyo/Downloads/walk/PXL_20230920_221306546.jpg").get
//   val imageTrees = Image.load("/Users/fishuyo/Downloads/walk/PXL_20230920_214228149.jpg").get
//   val imageBark = Image.load("/Users/fishuyo/Downloads/walk/PXL_20230920_214313772.jpg").get
//   val imageBark2 = Image.load("/Users/fishuyo/Downloads/walk/PXL_20230920_221356328.jpg").get
//   val imageBark3 = Image.load("/Users/fishuyo/Downloads/walk/PXL_20230920_214336997.jpg").get
//   val imageScales = Image.load("/Users/fishuyo/Downloads/walk/scales.jpg").get
//   val imagePeaks = Image.load("/Users/fishuyo/Downloads/walk/peaks.jpg").get
//   val imageMoss = Image.load("/Users/fishuyo/Downloads/walk/moss.jpg").get
//   val imageLeafStrip = Image.load("/Users/fishuyo/Downloads/walk/leafStrip.jpg").get
//   val imageTrunks = Image.load("/Users/fishuyo/Downloads/walk/trunks.jpg").get
//   val imageP1 = Image.load("/Users/fishuyo/Downloads/walk/1.png").get
//   val imageP2 = Image.load("/Users/fishuyo/Downloads/walk/2.png").get
//   val imageP3 = Image.load("/Users/fishuyo/Downloads/walk/3.png").get
//   val imageP4 = Image.load("/Users/fishuyo/Downloads/walk/4.png").get
//   var texBG:Texture = _
//   var texBG2:Texture = _
//   var texBG3:Texture = _
//   var texNoise:Texture = _

//   val (nw,nh) = (300,200)
//   val noise = new Noise()
//   val noiseImage = Image(nw,nh,3,4)



//   var animR = 0.01f

//   var blend = Vec2(0.3f,0.995f)


//   Gravity.set(0f,0f,0f)
//   var numAttractors = 6
//   var attractors = (0 until numAttractors).map{ case _ => 
//     val a = new Attractor
//     a.radius = 0.05f
//     a.strength = 0.75f + Random.float()*0.01f
//     a
//   }
//   var repulsors = (0 until numAttractors).map{ case _ => 
//     val a = new Attractor
//     a.radius = 0.005f
//     a.maxRadius = 0.1f
//     a.strength = 0.05f + Random.float()*0.01f
//     a
//   }

//   val numParticles = 300000
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
//       // uniform sampler2D tex2;
//       // uniform sampler2D tex3;
//       // uniform sampler2D tex4;
//       // uniform sampler2D texNoise;
//       uniform vec2 blend;
//       void main() {
//         vec4 color0 = texture(tex0, vuv) * blend.x;
//         vec4 color1 = texture(tex1, vuv) * blend.y;
//         vec4 purp = vec4(0.25,0.0,0.35,1.0);
//         vec4 blue = vec4(0.0,0.1,0.15,1.0);
//         // vec4 color2 = mix(purp, blue, texture(tex2, vuv).r ) * 0.01;
//         // vec4 noise = texture(texNoise, vuv);
//         // vec4 color2 = texture(tex2, vuv + vec2(noise.g,noise.b)*0.1) * noise.r * 0.002;
//         // vec4 color3 = texture(tex3, vuv + vec2(noise.b,noise.r)*0.1) * noise.g * 0.002;
//         // vec4 color4 = texture(tex4, vuv + vec2(noise.r,noise.g)*0.1) * noise.b * 0.002;
//         // fragColor = color0 + color1 + color2 + color3 + color4;
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
//       uniform sampler2D tex2;
//       uniform sampler2D tex3;
//       uniform sampler2D tex4;
//       uniform sampler2D texNoise;

//       void main() {
//         vec4 color = texture(tex, vuv);
//         vec4 noise = texture(texNoise, vuv);
//         vec4 color2 = texture(tex2, vuv + vec2(noise.g,noise.b)*0.15) * noise.r * 0.2;
//         vec4 color3 = texture(tex3, vuv + vec2(noise.b,noise.r)*0.15) * noise.g * 0.2;
//         vec4 color4 = texture(tex4, vuv + vec2(noise.r,noise.g)*0.15) * noise.b * 0.2;
//         fragColor = color + color2 + color3 + color4;
//         // fragColor = vec4(color);
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
//     texBG = new Texture().create()
//     texBG2 = new Texture().create()
//     texBG3 = new Texture().create()
//     texNoise = new Texture().create()
//     texBG.bind().update(imageLeaves)
//     // texBG.bind().update(imageBark3)
//     // texBG.bind().update(imageBark)
//     // texBG2.bind().update(imageLeaves2)
//     texBG2.bind().update(imageBark3)
//     // texBG2.bind().update(imageLeafStrip)
//     // texBG3.bind().update(imageBark2)
//     // texBG3.bind().update(imageMoss)
//     texBG3.bind().update(imageTrees)

//     Window().onFramebufferResize = (width,height) => {
//       w = width; h = height
//       image = Image(w,h,channels,bytes)
//       tex0.bind().update(image)
//       tex1.bind().update(image)
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

//     emitter.animate(dt.toFloat)  
//     val pos = emitter.particles.map(_.position).flatMap{ case v => Array(v.x,v.y,v.z)}.toArray
//     particles.vertices.put(pos)
//     particles.update()

//     // noise tex update
//     texNoise.bind(0)
//     noiseImage.buffer.rewind()

//     bytes match {
//       case 4 => 
//         val seq = for( y <- 0 until nh; x <- 0 until nw; c <- 0 until 3) yield {
//           0.5f * noise(x, y, 2*timer.toFloat + c*30) + 0.5f
//         }
//         noiseImage.floatBuffer.put(seq.toArray)
//         // noiseImage.floatBuffer.put( (0 until w*h*channels).map{ case _ => Random.float()}.toArray )
//         // noiseImage.floatBuffer.put( (0 until w*h*channels).map{ case _ => 1f}.toArray )
//       case 2 =>
//         for( y <- 0 until h; x <- 0 until w; c <- 0 until channels){
//           val value = 0.5f * noise(x, y, 10*timer.toFloat) + 0.5f
//           noiseImage.shortBuffer.put((value*65535).toShort)
//         }
//         // noiseImage.shortBuffer.put( (0 until w*h*channels).map{ case _ => (Random.float()*65535).toShort}.toArray )
//       case _ =>
//         for( y <- 0 until h; x <- 0 until w; c <- 0 until channels){
//           val value = 0.5f * noise(x, y, 10*timer.toFloat) + 0.5f
//           noiseImage.buffer.put((value*255).toByte)
//         }
//         // noiseImage.buffer.put( (0 until w*h*channels).map{ case _ => (Random.float()*255).toByte}.toArray )
//     }
//     texNoise.update(noiseImage)
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
//     texBG.bind(2)
//     texBG2.bind(3)
//     texBG3.bind(4)
//     texNoise.bind(5)
//     compositeShader.uniform("tex0", 0)
//     compositeShader.uniform("tex1", 1)
//     // compositeShader.uniform("tex2", 2)
//     // compositeShader.uniform("tex3", 3)
//     // compositeShader.uniform("tex4", 4)
//     // compositeShader.uniform("texNoise", 5)
//     compositeShader.uniform("blend", blend)
//     quad.draw()

//     fbo.end()

//     // draw composite to screen
//     texShader.bind()
//     glViewport(0,0,w,h) //graphics.w,graphics.h)
//     tex1.bind(0)
//     texShader.uniform("tex", 0)
//     texShader.uniform("tex2", 2)
//     texShader.uniform("tex3", 3)
//     texShader.uniform("tex4", 4)
//     texShader.uniform("texNoise", 5)
//     // // glClear(GL_COLOR_BUFFER_BIT)
//     quad.draw()

//     // // swap textures
//     // val tmp = tex0
//     // tex0 = tex1
//     // tex1 = tmp 
//   }

// }