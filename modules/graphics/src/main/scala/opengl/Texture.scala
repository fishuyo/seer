package seer
package graphics

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer

object Texture {}


class Texture {
  lazy val gl = Graphics().gl

  var id = 0
  var (w,h) = (0,0)
  var target = gl.GL_TEXTURE_2D
  var internal = gl.GL_RGBA
  var format = gl.GL_RGBA
  var _type = gl.GL_UNSIGNED_BYTE

  var filterMin = gl.GL_NEAREST
  var filterMag = gl.GL_NEAREST
  var wrapS = gl.GL_REPEAT
  var wrapT = gl.GL_REPEAT
  var wrapR = gl.GL_REPEAT
  var genMipmap = false

  var parametersChanged = true


  def create() = {
    id = gl.glGenTexture()
    this
  }

  def destroy() = {
    if (id != 0) {
      gl.glDeleteTexture(id)
      id = 0;
    }
  }

  def bind(i:Int) = {
    gl.glActiveTexture(gl.GL_TEXTURE0+i)
    gl.glBindTexture(target, id)
    updateParameters()
  }
  def unbind(i:Int) = {
    gl.glActiveTexture(gl.GL_TEXTURE0+i)
    gl.glBindTexture(target, 0)
    updateParameters()
  }

  def updateParameters() = {
    if(parametersChanged){
      gl.glTexParameteri(target, gl.GL_TEXTURE_MAG_FILTER, filterMag)
      gl.glTexParameteri(target, gl.GL_TEXTURE_MIN_FILTER, filterMin)
      gl.glTexParameteri(target, gl.GL_TEXTURE_WRAP_S, wrapS)
      gl.glTexParameteri(target, gl.GL_TEXTURE_WRAP_T, wrapT)
      gl.glTexParameteri(target, gl.GL_TEXTURE_WRAP_R, wrapR)
      // gl.glTexParameteri(target, gl.GL_GENERATE_MIPMAP, if(genMipmap) 1 else 0)

      // if (filterMin != gl.GL_LINEAR && filterMin != gl.GL_NEAREST) {
      //   gl.glTexParameteri(target, gl.GL_GENERATE_MIPMAP, gl.GL_TRUE); // automatic mipmap
      // }
      parametersChanged = false
    }
  }

  def update(buffer:Buffer): Unit = {
    buffer.rewind()
    gl.glTexImage2D(target,0,internal,w,h,0,format,_type,buffer)
    if(genMipmap) gl.glGenerateMipmap(target)
  }

  def updateFormat(image:Image): Unit = {
    w = image.w 
    h = image.h

    (image.bytesPerChannel, image.channels) match {
      case (1,1) => _type = gl.GL_UNSIGNED_BYTE; format = gl.GL_RED; internal = gl.GL_R8
      case (1,2) => _type = gl.GL_UNSIGNED_BYTE; format = gl.GL_RG; internal = gl.GL_RG8
      case (1,3) => _type = gl.GL_UNSIGNED_BYTE; format = gl.GL_RGB; internal = gl.GL_RGB8
      case (1,4) => _type = gl.GL_UNSIGNED_BYTE; format = gl.GL_RGBA; internal = gl.GL_RGBA8
      
      case (2,1) => _type = gl.GL_SHORT; format = gl.GL_RED; internal = gl.GL_RED
      case (2,2) => _type = gl.GL_SHORT; format = gl.GL_RG; internal = gl.GL_RG
      case (2,3) => _type = gl.GL_SHORT; format = gl.GL_RGB; internal = gl.GL_RGB
      case (2,4) => _type = gl.GL_SHORT; format = gl.GL_RGBA; internal = gl.GL_RGBA

      case (4,1) => _type = gl.GL_FLOAT; format = gl.GL_RED; internal = gl.GL_R32F
      case (4,2) => _type = gl.GL_FLOAT; format = gl.GL_RG; internal = gl.GL_RG32F
      case (4,3) => _type = gl.GL_FLOAT; format = gl.GL_RGB; internal = gl.GL_RGB32F
      case (4,4) => _type = gl.GL_FLOAT; format = gl.GL_RGBA; internal = gl.GL_RGBA32F
      
      case _ => ()
    }
  }

  def update(image:Image): Unit = {
    updateFormat(image)
    update(image.buffer)
  }
}








