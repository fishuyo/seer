
package seer.graphics 

object Buffer {
  def apply() = new Buffer()
  def create() = new Buffer().create
}

class Buffer {
  lazy val gl = Graphics().gl
  import gl._

  var id = 0
  var _type = GL_ARRAY_BUFFER // GL_ELEMENT_ARRAY_BUFFER
  var usage = GL_DYNAMIC_DRAW // GL_STATIC_DRAW
  var size = 0

  def create() = {
    id = glGenBuffer()
    this 
  }

  def destroy() = { 
    glDeleteBuffer(id)
    id = 0
  }

  def bind() = glBindBuffer(_type, id)
  def unbind() = glBindBuffer(_type, 0)

  def data(len:Int, src:java.nio.Buffer) = {
    glBufferData(_type, len, src, usage)
    size = len
  }

  def subdata(offset:Int, len:Int, src:java.nio.Buffer) = glBufferSubData(_type, offset, len, src)
  
}