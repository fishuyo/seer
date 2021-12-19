
package seer
package graphics

class ShaderProgram {
  lazy val gl = Graphics().gl
  import gl._

  var id:Option[Int] = None
  var vid:Option[Int] = None
  var fid:Option[Int] = None
  
  def init() = {
    id = Some(glCreateProgram())
    vid = Some(glCreateShader(GL_VERTEX_SHADER))
    fid = Some(glCreateShader(GL_FRAGMENT_SHADER))
  }

  def loadSource(vert:String, frag:String) = {
    vid.foreach(glShaderSource(_, vert))
    fid.foreach(glShaderSource(_, frag))
  }

  def compile() = {
    vid.foreach{ case i => 
      glCompileShader(i)
      println(glGetShaderInfoLog(i))
    }
    fid.foreach{ case i => 
      glCompileShader(i)
      println(glGetShaderInfoLog(i))
    }
  }

  def link() = {
    id.foreach { case i =>
      glAttachShader(i, vid.get)
      glAttachShader(i, fid.get)
      glLinkProgram(i)
      println(glGetProgramInfoLog(i))
      // glValidateProgram(i)
      // println(glGetProgramInfoLog(i))
    }
  }

  def create(v:String,f:String) = {
    init()
    loadSource(v,f)
    compile()
    link()
    this
  }

  def bind() = id.foreach(glUseProgram(_))
  def unbind() = glUseProgram(0)


  def uniform(name:String, value:Any, dim:Int=4) = {
    val loc = glGetUniformLocation(id.get, name)
    if(loc != -1 ){
      (dim, value) match {
        case (4, a:Array[Float]) =>
          val tmp = BufferUtils.newFloatBuffer(4) // TODO cache tmp buffers and reuse?
          tmp.put(a)
          tmp.rewind()
          glUniform4fv(loc, a.length/4, tmp)

        case _ => println("Error: uniform type not implemented")
      }
    }
      

  }
}