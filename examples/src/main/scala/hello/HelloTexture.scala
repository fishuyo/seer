package seer 
package examples

import graphics._
import math._


object HelloTexture extends SeerApp {

	var timer = 0.0
  var shader:ShaderProgram = _
  var mesh:Mesh = _
  var texture:Texture = _

  val noise = new Noise()
  // noise.noiseType = Noise.NoiseType.Cellular
  // noise.noiseType = Noise.NoiseType.WhiteNoise

  val (w,h) = (512,512)
  val channels = 1
  val bytes = 4
  val image = Image(w,h,channels,bytes)

  graphics.onInit = () => {
    val gl = Graphics().gl
    import gl._ 

    val vertText = """
      #version 330 core
      layout(location = 0) in vec3 position;
      layout(location = 2) in vec2 uv;
      out vec2 vuv;
      void main(){ 
        vuv = uv;
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragText = """
      #version 330 core
      in vec2 vuv;
      out vec4 fragColor;
      uniform sampler2D tex0;
      void main() {
        vec4 color = texture(tex0, vuv);
        fragColor = vec4(color);
      }"""

    shader = new ShaderProgram().create(vertText, fragText)
    
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
		timer += dt
    texture.bind(0)
    image.buffer.rewind()

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
