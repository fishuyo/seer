
package seer
package graphics

import math.Vec2
import math.Vec3

class ShaderProgram {
  lazy val gl = Graphics().gl
  import gl._

  var id:Option[Int] = None
  var vid:Option[Int] = None
  var fid:Option[Int] = None

  var vertCode = ""
  var fragCode = ""
  var needsUpdate = false
  
  def init():Unit = {
    if(id.isDefined) return
    id = Some(glCreateProgram())
    vid = Some(glCreateShader(GL_VERTEX_SHADER))
    fid = Some(glCreateShader(GL_FRAGMENT_SHADER))
  }

  def setCode(vert:String, frag:String) = {
    vertCode = vert
    fragCode = frag
    needsUpdate = true
  }

  // def loadSource(vert:String, frag:String) = {
  //   vid.foreach(glShaderSource(_, vert))
  //   fid.foreach(glShaderSource(_, frag))
  // }

  def compile() = {
    vid.foreach{ case i =>
      glShaderSource(i, vertCode) 
      glCompileShader(i)
      println(glGetShaderInfoLog(i))
    }
    fid.foreach{ case i => 
      glShaderSource(i, fragCode) 
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
    setCode(v,f)
    init()
    compile()
    link()
    needsUpdate = false
    this
  }

  def update():Unit = {
    if(!needsUpdate) return
    init()
    compile()
    link()
    needsUpdate = false
  }

  def bind() = {
    update()
    id.foreach(glUseProgram(_))
  }

  def unbind() = glUseProgram(0)


  def uniform(name:String, value:Any, dim:Int=1):Unit = {
    val loc = glGetUniformLocation(id.get, name)
    if(loc == -1 ){
      println(s"Error: Uniform location not found: $name")
      return
    }
    // println(s"uniform: $name $value $dim")

    (dim, value) match {
      case (_, v:Int) => glUniform1i(loc, v)
      case (_, v:Float) => glUniform1f(loc, v)
      case (_, v:Double) => glUniform1f(loc, v.toFloat)
      case (_, v:Vec2) => glUniform2f(loc, v.x, v.y)
      case (_, v:Vec3) => glUniform3f(loc, v.x, v.y, v.z)
      case (1, a:Array[Float]) => glUniform1fv(loc, a.length, a, 0)
      case (2, a:Array[Float]) => glUniform2fv(loc, a.length/2, a, 0)
      case (3, a:Array[Float]) => glUniform3fv(loc, a.length/3, a, 0)
      case (4, a:Array[Float]) => glUniform4fv(loc, a.length/4, a, 0)
      case (1, a:Array[Int]) => glUniform1iv(loc, a.length, a, 0)
      case (2, a:Array[Int]) => glUniform2iv(loc, a.length/2, a, 0)
      case (3, a:Array[Int]) => glUniform3iv(loc, a.length/3, a, 0)
      case (3, a:Array[Int]) => glUniform4iv(loc, a.length/4, a, 0)


      case _ => println(s"Error: uniform type not implemented for $name dim: $dim value: $value")
    }

  }
}