package seer
package graphics

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer

object Texture {}


class Texture {
  lazy val gl = Graphics().gl
  import gl._

  var id = 0
  var (w,h) = (0,0)
  var target = GL_TEXTURE_2D
  var internal = GL_RGBA
  var format = GL_RGBA
  var _type = GL_UNSIGNED_BYTE

  var filterMin = GL_NEAREST
  var filterMag = GL_NEAREST
  var wrapS = GL_REPEAT
  var wrapT = GL_REPEAT
  var wrapR = GL_REPEAT
  var genMipmap = false

  var parametersChanged = true


  def create() = {
    id = glGenTexture()
    this
  }

  def destroy() = {
    if (id != 0) {
      glDeleteTexture(id)
      id = 0;
    }
  }

  def bind(i:Int) = {
    glActiveTexture(GL_TEXTURE0+i)
    // glEnable(target) // XXX neccessary?
    glBindTexture(target, id)
    updateParameters()
  }

  def updateParameters() = {
    if(parametersChanged){
      glTexParameteri(target, GL_TEXTURE_MAG_FILTER, filterMag)
      glTexParameteri(target, GL_TEXTURE_MIN_FILTER, filterMin)
      glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS)
      glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT)
      glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR)
      // glTexParameteri(target, GL_GENERATE_MIPMAP, if(genMipmap) 1 else 0)

      // if (filterMin != GL_LINEAR && filterMin != GL_NEAREST) {
      //   glTexParameteri(target, GL_GENERATE_MIPMAP, GL_TRUE); // automatic mipmap
      // }
      parametersChanged = false
    }
  }

  def update(buffer:Buffer): Unit = {
    buffer.rewind()
    glTexImage2D(target,0,internal,w,h,0,format,_type,buffer)
    if(genMipmap) glGenerateMipmap(target)
  }

  def update(image:Image): Unit = {
    w = image.w 
    h = image.h

    (image.bytesPerChannel, image.channels) match {
      case (1,1) => _type = GL_UNSIGNED_BYTE; format = GL_RED; internal = GL_R8
      case (1,2) => _type = GL_UNSIGNED_BYTE; format = GL_RG; internal = GL_RG8
      case (1,3) => _type = GL_UNSIGNED_BYTE; format = GL_RGB; internal = GL_RGB8
      case (1,4) => _type = GL_UNSIGNED_BYTE; format = GL_RGBA; internal = GL_RGBA8
      
      case (2,1) => _type = GL_SHORT; format = GL_RED; internal = GL_RED
      case (2,2) => _type = GL_SHORT; format = GL_RG; internal = GL_RG
      case (2,3) => _type = GL_SHORT; format = GL_RGB; internal = GL_RGB
      case (2,4) => _type = GL_SHORT; format = GL_RGBA; internal = GL_RGBA

      case (4,1) => _type = GL_FLOAT; format = GL_RED; internal = GL_R32F
      case (4,2) => _type = GL_FLOAT; format = GL_RG; internal = GL_RG32F
      case (4,3) => _type = GL_FLOAT; format = GL_RGB; internal = GL_RGB32F
      case (4,4) => _type = GL_FLOAT; format = GL_RGBA; internal = GL_RGBA32F
      
      case _ => ()
    }
    update(image.buffer)
  }
}








