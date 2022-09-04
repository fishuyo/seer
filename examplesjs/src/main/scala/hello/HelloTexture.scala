package seer 
package examplesjs

import graphics._
import math._

import scala.scalajs.js.annotation._

@JSExportTopLevel("HelloTexture")
object HelloTexture extends SeerApp {

	var timer = 0.0
  var shader:ShaderProgram = _
  var mesh:Mesh = _
  var texture:Texture = _

  val noise = new Noise()
  noise.frequency = 0.025f
  // noise.noiseType = Noise.NoiseType.SimplexFractal
  // noise.noiseType = Noise.NoiseType.Perlin
  // noise.noiseType = Noise.NoiseType.PerlinFractal
  noise.noiseType = Noise.NoiseType.ValueNoise
  // noise.noiseType = Noise.NoiseType.Cellular
  // noise.noiseType = Noise.NoiseType.WhiteNoise

  val (w,h) = (600,512)
  val channels = 1
  val bytes = 1
  val image = Image(w,h,channels,bytes)

  graphics.onInit = () => {
    val gl = Graphics().gl
    import gl._ 

    val vertText = """
      #version 300 es
      layout(location = 0) in vec3 position;
      layout(location = 2) in vec2 uv;
      out vec2 vuv;
      void main(){ 
        vuv = uv;
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragText = """
      #version 300 es
      precision highp float; 
      in vec2 vuv;
      out vec4 fragColor;
      uniform sampler2D tex0;
      void main() {
        vec4 color = texture(tex0, vuv);
        fragColor = color.rrra;
      }"""

    shader = new ShaderProgram().create(vertText.stripLeading, fragText.stripLeading)
    
    mesh = new Mesh()
    mesh.resize(4, hasTexcoords=true)
    mesh.vertices.put(Array(-1.0f,-1.0f,0.0f,  1.0f,-1.0f,0.0f,  1.0f,1.0f,0.0f, -1.0f,1.0f,0.0f))
    mesh.texcoords.put(Array(0f,0f,  1.0f,0f,  1.0f,1.0f, 0f,1.0f))
    mesh.resizeIndices(6)
    mesh.indices.put(Array(0,1,2,2,3,0))
    mesh.update()

    texture = new Texture()
    texture.create()
  }

	graphics.onUpdate = (dt:Double) => {
		timer += 2*dt
    texture.bind(0)
    image.buffer.rewind()
    // bytes match {
    //   case 4 => image.floatBuffer.put( (0 until w*h*channels).map{ case _ => Random.float()}.toArray )
    //   // case 2 => image.shortBuffer.put( (0 until w*h*channels).map{ case _ => (Random.float()*65535).toShort}.toArray ) // Broken Shorts must use integer format webgl XXX
    //   case _ => image.buffer.put( (0 until w*h*channels).map{ case _ => (Random.float()*255).toByte}.toArray )
    // }
    bytes match {
      case 4 => 
        val seq = for( y <- 0 until h; x <- 0 until w; c <- 0 until channels) yield {
          0.5f * noise(x, y, 10*timer.toFloat) + 0.5f
        }
        image.floatBuffer.put(seq.toArray)
        // image.floatBuffer.put( (0 until w*h*channels).map{ case _ => Random.float()}.toArray )
        // image.floatBuffer.put( (0 until w*h*channels).map{ case _ => 1f}.toArray )
      case 2 =>
        for( y <- 0 until h; x <- 0 until w; c <- 0 until channels){
          val value = 0.5f * noise(x, y, 10*timer.toFloat) + 0.5f
          image.shortBuffer.put((value*65535).toShort)
        }
        // image.shortBuffer.put( (0 until w*h*channels).map{ case _ => (Random.float()*65535).toShort}.toArray )
      case _ =>
        for( y <- 0 until h; x <- 0 until w; c <- 0 until channels){
          val value = 0.5f * noise(x, y, 10*timer.toFloat) + 0.5f
          image.buffer.put((value*255).toByte)
        }
        // image.buffer.put( (0 until w*h*channels).map{ case _ => (Random.float()*255).toByte}.toArray )
    }
    texture.update(image)

	}

	graphics.onDraw = (g:Graphics) => {
		import g.gl._

    shader.bind()
    shader.uniform("tex0", 0)
    texture.bind(0)
    mesh.draw()
	}

}
