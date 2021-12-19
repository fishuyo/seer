
package seer.graphics 

import java.nio.FloatBuffer
import java.nio.IntBuffer

object VAO {
  def apply() = new VAO()
  def create() = new VAO().create()
}

class VAO {
  val gl = Graphics().gl
  import gl._

  var id = 0
  
  def create() = {
    val a = Array(0)
    glGenVertexArrays(1, a)
    id = a(0)
    this
  }

  def destroy() = {
    glDeleteVertexArrays(1, Array(id), 0)
    id = 0
  }

  def bind() = glBindVertexArray(id)
  def unbind() = glBindVertexArray(0)
  
  def enableAttribute(index:Int) = glEnableVertexAttribArray(index)
  def disableAttribute(index:Int) = glDisableVertexAttribArray(index)
  
  def attributePointer(buffer:Buffer, index:Int, size:Int, _type:Int=GL_FLOAT, normalized:Boolean=false, stride:Int=0, offset:Int=0) = {
    buffer.bind()
    glVertexAttribPointer(index, size, _type, normalized, stride, offset)
  }
}



class Mesh {
  lazy val gl = Graphics().gl
  import gl._

  case class Attribute(index:Int, size:Int, buffer:Buffer=Buffer())

  val vao = VAO()
  val positionAttribute = Attribute(0, 3)
  val colorAttribute = Attribute(1, 4)
  val texcoordAttribute = Attribute(2, 2)
  val normalAttribute = Attribute(3, 3)
  val indexBuffer = Buffer()

  var primitive = GL_TRIANGLES
  var vertices:FloatBuffer = BufferUtils.newFloatBuffer(0)
  var colors:FloatBuffer = BufferUtils.newFloatBuffer(0)
  var texcoords:FloatBuffer = BufferUtils.newFloatBuffer(0)
  var normals:FloatBuffer = BufferUtils.newFloatBuffer(0)
  var indices:IntBuffer = BufferUtils.newIntBuffer(0)


  def update() = {
    // create VAO if not already
    if(vao.id == 0){
      // println("Mesh: creating VAO")
      vao.create()
    }
    vao.bind()

    // update all attributes
    updateAttribute(vertices, positionAttribute)
    // updateAttribute(colors, positionAttribute)
    // updateAttribute(texcoords, texcoordAttribute)
    // updateAttribute(normals, normalAttribute)

    // update indices if there are any
    if(indices.capacity() > 0){
      if(indexBuffer.id == 0){
        indexBuffer.create()
        indexBuffer._type = GL_ELEMENT_ARRAY_BUFFER
      }
      indexBuffer.bind()
      indexBuffer.data(indices.capacity(), indices)
    }

  }

  def updateAttribute(data:FloatBuffer, attribute:Attribute) = {

    // enable attributes with data
    if(data.capacity() > 0) vao.enableAttribute(attribute.index)
    else vao.disableAttribute(attribute.index)

    // if attribute buffer not created, create and set vao pointer
    if(attribute.buffer.id == 0){
      // println("Mesh: creating attribute buffer")
      attribute.buffer.create()
      vao.attributePointer(attribute.buffer, attribute.index, attribute.size)
    }

    // send attribute data
    attribute.buffer.bind()
    attribute.buffer.data(data.capacity(), data) // check size parameter XXX

  }

  def draw() = {
    vao.bind()
    if(indices.capacity() > 0){
      indexBuffer.bind()
      gl.glDrawElements(primitive, indices.capacity(), GL_UNSIGNED_INT, indices)
    } else {
      val count = vertices.capacity() / 3 
      gl.glDrawArrays(primitive, 0, count)
    }
  }
}