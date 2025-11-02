package seer 
package examplesjs

import graphics._
import math._

import scala.scalajs.js.annotation._

@JSExportTopLevel("ReactionDiffusion")
object ReactionDiffusion extends SeerApp {

  var timer = 0.0
  var fluidShader:ShaderProgram = _
  var texShader:ShaderProgram = _
  var quad:Mesh = _

  var fbo:FrameBuffer = _
  var tex0:Texture = _
  var tex1:Texture = _

  var (w,h) = (20,20)
  val channels = 4
  val bytes = 4
  var image:Image = _

  var mouse = Vec2()

  graphics.onCreate = () => {
    val gl = Graphics().gl
    import gl._ 

    val vert = """
      #version 300 es
      layout(location = 0) in vec3 position;
      layout(location = 2) in vec2 uv;
      out vec2 vuv;
      void main(){ 
        vuv = uv;
        gl_Position = vec4(position, 1.0); 
      }"""

    val frag = s"""
      #version 300 es
      precision highp float; 
      in vec2 vuv;
      out vec4 fragColor;
      uniform vec2 size;
      uniform vec2 brush;
      uniform sampler2D tex;
      void main() {
        float dx = 1.0/size.x;
        float dy = 1.0/size.y;
        float dt = 0.5;

        // vec2 alpha = vec2(da/(dx*dx), db/(dy*dy));
        vec2 alpha = vec2(0.2097, 0.105);


        vec2 V = texture(tex, vuv).rg;
        vec4 L = texture(tex, vuv + vec2(0,-dy))
          +  texture(tex, vuv + vec2(-dx,0)) 
          -  4.0 * texture(tex,  vuv )
          +  texture(tex, vuv + vec2(dx,0)) 
          +  texture(tex, vuv + vec2(0,dy));

        V += L.rg * alpha * dt;


        // float F = 0.034; //mitosis
        // float K = 0.063;
        float F = 0.025; //pulse
        float K = 0.06;
        // float F = 0.014; //waves
        // float K = 0.045;
        // float F = 0.026; //brains
        // float K = 0.055;
        // float F = 0.082; //worms
        // float K = 0.061;
        // float F = 0.082; //worm channels
        // float K = 0.059;
        
        // grey scott
        float ABB = V.r*V.g*V.g;
        float rA = -ABB + F*(1.0 - V.r);
        float rB = ABB - (F+K)*V.g;

        vec2 R = vec2(rA,rB) * dt;

        // output diffusion + reaction
        vec2 RD = V + R;


        if(brush.x > 0.0){
          vec2 brsh = brush;
          //brsh.y = 1.0 - brsh.y;
          vec2 diff = (vuv - brsh)/vec2(dx,dy);
          float dist = dot(diff, diff);
          if(dist < 100.0){
              RD.r = 0.0;
              RD.g = 0.9;
          }
        }
        
        fragColor = vec4(RD, 0.0, 1.0);
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
      uniform sampler2D tex;
      void main() {
        vec4 value = texture(tex, vuv);
        float v = value.g;
        float a = 0.0;
        vec3 col = vec3(0.0,0.0,0.0);

        vec4 color1 = vec4(0.0,0.0,0.0,0.0);
        vec4 color2 = vec4(1.0,1.0,1.0,0.3);
        vec4 color3 = vec4(0.0,1.0,1.0,0.35);
        vec4 color4 = vec4(0.0,0.0,1.0,0.5);
        vec4 color5 = vec4(0.0,0.0,0.0,0.6);

        if(v <= color1.a){
          col = color1.rgb;
        } else if(v <= color2.a){
          a = (v - color1.a)/(color2.a - color1.a);
          col = mix(color1.rgb, color2.rgb, a);
        } else if(v <= color3.a){
          a = (v - color2.a)/(color3.a - color2.a);
          col = mix(color2.rgb, color3.rgb, a);
        } else if(v <= color4.a){
          a = (v - color3.a)/(color4.a - color3.a);
          col = mix(color3.rgb, color4.rgb, a);
        } else if(v <= color5.a){
          a = (v - color4.a)/(color5.a - color4.a);
          col = mix(color4.rgb, color5.rgb, a);
        } else {
          col = color5.rgb;
        }

        fragColor = vec4(col, 1.0);
      }"""
    
    fluidShader = new ShaderProgram().create(vert.stripLeading, frag.stripLeading)
    texShader = new ShaderProgram().create(vertTex.stripLeading, fragTex.stripLeading)
    
    quad = new Mesh()
    quad.resize(4, hasTexcoords=true)
    quad.vertices.put(Array(-1.0f,-1.0f,0.0f,  1.0f,-1.0f,0.0f,  1.0f,1.0f,0.0f, -1.0f,1.0f,0.0f))
    quad.texcoords.put(Array(0f,0f,  1.0f,0f,  1.0f,1.0f, 0f,1.0f))
    quad.resizeIndices(6)
    quad.indices.put(Array(0,1,2,2,3,0))
    quad.update()

    // initialize fbo texture image
    w = graphics.w / 2; h = graphics.h / 2
    image = Image(w,h,channels,bytes)
    for(y <- 0 until h; x <- 0 until w; c <- 0 until 4){
      val px = x - w/2
      val py = y - h/2
      if( px*px + py*py < 100*100)
        image.floatBuffer.put(y*(w*4)+(x*4)+c, 0.0f*c)
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


    graphics.onMouseEvent = (e) => {
      mouse = Vec2(e.state.x, e.state.y)
    }

    // graphics.onResize = (width,height) => {
    //   w = width; h = height
    //   image = Image(w,h,channels,bytes)
    //   tex0.bind(0)
    //   tex0.update(image)
    //   tex1.bind(0)
    //   tex1.update(image)
    // }

  }

  graphics.onUpdate = (dt:Double) => {
    timer += dt
    if(timer > 0.5) timer = 0.0    
  }

  graphics.onDraw = (g:Graphics) => {
    import g.gl._

    fbo.begin()
    
    for(i <- 0 until 5){
      fbo.attachTexture(tex1)
      fluidShader.bind()
      glViewport(0,0,w,h)
      tex0.bind(0)
      fluidShader.uniform("tex", 0)
      fluidShader.uniform("size", Vec2(w,h))
      fluidShader.uniform("brush", mouse)

      quad.draw()

      // swap textures
      val tmp = tex0
      tex0 = tex1
      tex1 = tmp 
    }

    fbo.end()


    texShader.bind()
    glViewport(0,0,graphics.w,graphics.h)
    tex1.bind(0)
    texShader.uniform("tex", 0)
    // glClear(GL_COLOR_BUFFER_BIT)
    quad.draw()

    // // swap textures
    // val tmp = tex0
    // tex0 = tex1
    // tex1 = tmp 
  }

}