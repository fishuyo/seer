package seer.graphics
package webgl

import java.nio.Buffer
import java.nio.FloatBuffer 
import java.nio.IntBuffer

import org.scalajs.dom.raw._
// import WebGLRenderingContext._
// import typings.std.{WebGLProgram, WebGLTexture, WebGLBuffer, WebGLShader, WebGLUniformLocation, WebGLRenderingContext}

import collection.mutable.HashMap

import scalajs.js.JSConverters._
import scalajs.js.typedarray._
import scalajs.js.typedarray.TypedArrayBufferOps._

object TextureUtil {
  val textures = HashMap[Int,WebGLTexture]()
  implicit def int2texture(id:Int) = textures(id) 
  
  def apply(id:Int) = textures(id)
  def nextId():Int = {
    for(i <- 1 to textures.size) 
      if(!textures.contains(i)) return i
    return textures.size + 1
  }

  def createTexture()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    textures += id -> gl.createTexture()
    id 
  }

  def deleteTexture(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteTexture(id)
    textures -= id
  }
}

object BufferUtil {
  val buffers = HashMap[Int,WebGLBuffer]()
  implicit def int2buffer(id:Int) = buffers(id) 
  
  def apply(id:Int) = buffers(id)
  def nextId():Int = {
    for(i <- 1 to buffers.size) 
      if(!buffers.contains(i)) return i
    return buffers.size + 1
  }

  def createBuffer()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    buffers += id -> gl.createBuffer()
    id 
  }

  def deleteBuffer(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteBuffer(id)
    buffers -= id
  }
}

object ShaderUtil {
  val shaders = HashMap[Int,WebGLShader]()
  implicit def int2shader(id:Int) = shaders(id) 
  
  def apply(id:Int) = shaders(id)
  def nextId():Int = {
    for(i <- 1 to shaders.size) 
      if(!shaders.contains(i)) return i
    return shaders.size + 1
  }

  def createShader(typ:Int)(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    shaders += id -> gl.createShader(typ)
    id 
  }

  def deleteShader(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteShader(id)
    shaders -= id
  }
}

object ProgramUtil {
  val programs = HashMap[Int,WebGLProgram]()
  implicit def int2program(id:Int) = programs(id) 
  
  def apply(id:Int) = programs(id)
  def nextId():Int = {
    for(i <- 1 to programs.size) 
      if(!programs.contains(i)) return i
    return programs.size + 1
  }

  def createProgram()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    programs += id -> gl.createProgram()
    id 
  }

  def deleteProgram(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteProgram(id)
    programs -= id
  }
}

object UniformLocationUtil {
  val uniforms = HashMap[Int,WebGLUniformLocation]()
  implicit def int2uniform(id:Int) = uniforms(id) 
  
  def apply(id:Int) = uniforms(id)
  def nextId():Int = {
    for(i <- 1 to uniforms.size) 
      if(!uniforms.contains(i)) return i
    return uniforms.size + 1
  }

  def getUniformLocation(program:WebGLProgram, name:String)(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    uniforms += id -> gl.getUniformLocation(program,name)
    id 
  }

  // def deleteUniformLocation(id:Int)(implicit gl:WebGLRenderingContext) = {
  //   gl.deleteUniformLocation(id)
  //   uniforms -= id
  // }
}

object RenderbufferUtil {
  val buffers = HashMap[Int,WebGLRenderbuffer]()
  implicit def int2renderbuffer(id:Int) = buffers(id) 
  
  buffers(0) = null

  def apply(id:Int) = buffers(id)
  def nextId():Int = {
    for(i <- 1 to buffers.size) 
      if(!buffers.contains(i)) return i
    return buffers.size + 1
  }

  def createRenderbuffer()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    buffers += id -> gl.createRenderbuffer()
    id 
  }

  def deleteRenderbuffer(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteRenderbuffer(id)
    buffers -= id
  }
}

object FramebufferUtil {
  val buffers = HashMap[Int,WebGLFramebuffer]()
  implicit def int2framebuffer(id:Int) = buffers(id) 
  buffers(0) = null
  
  def apply(id:Int) = buffers(id)
  def nextId():Int = {
    for(i <- 1 to buffers.size) 
      if(!buffers.contains(i)) return i
    return buffers.size + 1
  }

  def createFramebuffer()(implicit gl:WebGLRenderingContext):Int = { 
    val id = nextId()
    buffers += id -> gl.createFramebuffer()
    id 
  }

  def deleteFramebuffer(id:Int)(implicit gl:WebGLRenderingContext) = {
    gl.deleteFramebuffer(id)
    buffers -= id
  }
}

class GLES20WebGLImpl(gl:WebGLRenderingContext) extends GLES20 {
  import TextureUtil._
  import BufferUtil._
  import ShaderUtil._
  import ProgramUtil._
  import UniformLocationUtil._
  import RenderbufferUtil._
  import FramebufferUtil._

  implicit val g = gl

  def glActiveTexture (texture:Int):Unit = gl.activeTexture(texture)

  def glBindTexture (target:Int, texture:Int):Unit = gl.bindTexture(target, texture)

  def glBlendFunc (sfactor:Int, dfactor:Int):Unit = gl.blendFunc(sfactor, dfactor)

  def glClear (mask:Int):Unit = gl.clear(mask)

  def glClearColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = gl.clearColor(red,green,blue,alpha)

  def glClearDepthf (depth:Float):Unit = gl.clearDepth(depth) 

  def glClearStencil (s:Int):Unit = gl.clearStencil(s)

  def glColorMask (red:Boolean, green:Boolean, blue:Boolean, alpha:Boolean):Unit = gl.colorMask(red,green,blue,alpha)

  def glCompressedTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, imageSize:Int, data:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glCompressedTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, imageSize:Int, data:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glCopyTexImage2D (target:Int, level:Int, internalformat:Int, x:Int, y:Int, width:Int, height:Int, border:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glCopyTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, x:Int, y:Int, width:Int, height:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glCullFace (mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDeleteTextures (n:Int, textures:IntBuffer):Unit = {
    for(i <- 0 until n)
      glDeleteTexture(textures.get(i))
  }
  
  def glDeleteTexture(texture:Int):Unit = TextureUtil.deleteTexture(texture)
  

  def glDepthFunc (func:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDepthMask (flag:Boolean):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDepthRangef (zNear:Float, zFar:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDisable (cap:Int):Unit = gl.disable(cap)

  def glDrawArrays (mode:Int, first:Int, count:Int):Unit = gl.drawArrays(mode, first, count)

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Buffer):Unit = gl.drawElements(mode, count, `type`, 0)

  def glEnable (cap:Int):Unit = gl.enable(cap)

  def glFinish ():Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glFlush ():Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glFrontFace (mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGenTextures (n:Int, textures:IntBuffer):Unit = {
    for(i <- 0 until n){
      val id = glGenTexture()
      textures.put(i, id)
    }
  }
  
  def glGenTexture ():Int = TextureUtil.createTexture()
  

  def glGetError ():Int = 0

  def glGetIntegerv (pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetString (name:Int):String = ""

  def glHint (target:Int, mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glLineWidth (width:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glPixelStorei (pname:Int, param:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glPolygonOffset (factor:Float, units:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glReadPixels (x:Int, y:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = gl.readPixels(x,y,width, height, format, `type`, pixels.typedArray())

  def glScissor (x:Int, y:Int, width:Int, height:Int):Unit = gl.scissor(x,y,width,height)

  def glStencilFunc (func:Int, ref:Int, mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glStencilMask (mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glStencilOp (fail:Int, zfail:Int, zpass:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, format:Int, `type`:Int, pixels:Buffer):Unit = {
    `type` match {
      case GL_UNSIGNED_BYTE => gl.texImage2D(target, level, internalformat, width, height, border, format, `type`, new Uint8Array(pixels.arrayBuffer()))
      case GL_FLOAT => gl.texImage2D(target, level, internalformat, width, height, border, format, `type`, new Float32Array(pixels.arrayBuffer()))
      case GL_UNSIGNED_SHORT => gl.texImage2D(target, level, internalformat, width, height, border, format, `type`, new Uint16Array(pixels.arrayBuffer()))
      case GL_SHORT => gl.texImage2D(target, level, internalformat, width, height, border, format, `type`, new Int16Array(pixels.arrayBuffer()))
      case GL_UNSIGNED_INT => gl.texImage2D(target, level, internalformat, width, height, border, format, `type`, new Uint32Array(pixels.arrayBuffer()))
      case _ => println("ERR: Not implemented glTexImage2D type")
    }
    // gl.texImage2D(target, level, internalformat, width, height, border, format, `type`, pixels.typedArray())
  }

  def glTexParameterf (target:Int, pname:Int, param:Float):Unit = gl.texParameterf(target,pname,param)

  def glTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glViewport (x:Int, y:Int, width:Int, height:Int):Unit = gl.viewport(x, y, width, height)

  def glAttachShader (program:Int, shader:Int):Unit = gl.attachShader(program, shader)

  def glBindAttribLocation (program:Int, index:Int, name:String):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glBindBuffer (target:Int, buffer:Int):Unit = gl.bindBuffer(target, buffer)

  def glBindFramebuffer (target:Int, framebuffer:Int):Unit = gl.bindFramebuffer(target, framebuffer)

  def glBindRenderbuffer (target:Int, renderbuffer:Int):Unit = gl.bindRenderbuffer(target, renderbuffer)

  def glBlendColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glBlendEquation (mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glBlendEquationSeparate (modeRGB:Int, modeAlpha:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glBlendFuncSeparate (srcRGB:Int, dstRGB:Int, srcAlpha:Int, dstAlpha:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  // def glBufferData (target:Int, data: typedarray.ArrayBuffer, usage:Int):Unit = gl.bufferData(target, data, usage)
  def glBufferData (target:Int, size:Int, data:Buffer, usage:Int):Unit = gl.bufferData(target, data.arrayBuffer(), usage)

  def glBufferSubData (target:Int, offset:Int, size:Int, data:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glCheckFramebufferStatus (target:Int):Int = 0

  def glCompileShader (shader:Int):Unit = gl.compileShader(shader)

  def glCreateProgram ():Int = ProgramUtil.createProgram()

  def glCreateShader (`type`:Int):Int = ShaderUtil.createShader(`type`)

  def glDeleteBuffer (buffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDeleteBuffers (n:Int, buffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDeleteFramebuffer (framebuffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}
  
  def glDeleteFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDeleteProgram (program:Int):Unit = ProgramUtil.deleteProgram(program)

  def glDeleteRenderbuffer (renderbuffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}
  
  def glDeleteRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glDeleteShader (shader:Int):Unit = ShaderUtil.deleteShader(shader)

  def glDetachShader (program:Int, shader:Int):Unit = gl.detachShader(program, shader)

  def glDisableVertexAttribArray (index:Int):Unit = gl.disableVertexAttribArray(index)

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glEnableVertexAttribArray (index:Int):Unit = gl.enableVertexAttribArray(index)

  def glFramebufferRenderbuffer (target:Int, attachment:Int, renderbuffertarget:Int, renderbuffer:Int):Unit = gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)

  def glFramebufferTexture2D (target:Int, attachment:Int, textarget:Int, texture:Int, level:Int):Unit = gl.framebufferTexture2D(target, attachment, textarget, texture, level)
  
  def glGenBuffer ():Int = BufferUtil.createBuffer()

  def glGenBuffers (n:Int, buffers:IntBuffer):Unit = {
    for(i <- 0 until n){
      val id = glGenBuffer()
      buffers.put(i, id)
    }
  }

  def glGenerateMipmap (target:Int):Unit = gl.generateMipmap(target)

  def glGenFramebuffer ():Int = FramebufferUtil.createFramebuffer()

  def glGenFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {
    for(i <- 0 until n){
      val id = glGenFramebuffer()
      framebuffers.put(i, id)
    }
  }

  def glGenRenderbuffer ():Int = RenderbufferUtil.createRenderbuffer()

  def glGenRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {
    for(i <- 0 until n){
      val id = glGenRenderbuffer()
      renderbuffers.put(i, id)
    }
  }

  // deviates
  def glGetActiveAttrib (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  // deviates
  def glGetActiveUniform (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  def glGetAttachedShaders (program:Int, maxcount:Int, count:Buffer, shaders:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetAttribLocation (program:Int, name:String):Int = gl.getAttribLocation(program, name)

  def glGetBooleanv (pname:Int, params:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetBufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetFloatv (pname:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetFramebufferAttachmentParameteriv (target:Int, attachment:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetProgramiv (program:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  // deviates
  def glGetProgramInfoLog (program:Int):String = gl.getProgramInfoLog(program)

  def glGetRenderbufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetShaderiv (shader:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  // deviates
  def glGetShaderInfoLog (shader:Int):String = gl.getShaderInfoLog(shader)

  def glGetShaderPrecisionFormat (shadertype:Int, precisiontype:Int, range:IntBuffer, precision:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetUniformfv (program:Int, location:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetUniformiv (program:Int, location:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetUniformLocation (program:Int, name:String):Int = UniformLocationUtil.getUniformLocation(program, name)

  def glGetVertexAttribfv (index:Int, pname:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetVertexAttribiv (index:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glGetVertexAttribPointerv (index:Int, pname:Int, pointer:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glIsBuffer (buffer:Int):Boolean = false

  def glIsEnabled (cap:Int):Boolean = false

  def glIsFramebuffer (framebuffer:Int):Boolean = false

  def glIsProgram (program:Int):Boolean = false

  def glIsRenderbuffer (renderbuffer:Int):Boolean = false

  def glIsShader (shader:Int):Boolean = false

  def glIsTexture (texture:Int):Boolean = false

  def glLinkProgram (program:Int):Unit = gl.linkProgram(program)

  def glReleaseShaderCompiler ():Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glRenderbufferStorage (target:Int, internalformat:Int, width:Int, height:Int):Unit = gl.renderbufferStorage(target, internalformat, width, height)

  def glSampleCoverage (value:Float, invert:Boolean):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glShaderBinary (n:Int, shaders:IntBuffer, binaryformat:Int, binary:Buffer, length:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  // Deviates
  def glShaderSource (shader:Int, string:String):Unit = gl.shaderSource(shader, string)

  def glStencilFuncSeparate (face:Int, func:Int, ref:Int, mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glStencilMaskSeparate (face:Int, mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glStencilOpSeparate (face:Int, fail:Int, zfail:Int, zpass:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glTexParameteri (target:Int, pname:Int, param:Int):Unit = gl.texParameteri(target,pname,param)

  def glTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glUniform1f (location:Int, x:Float):Unit = gl.uniform1f(location, x)

  def glUniform1fv (location:Int, count:Int, v:FloatBuffer):Unit = gl.uniform1fv(location, new Float32Array(v.arrayBuffer()))
  
  def glUniform1fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = gl.uniform1fv(location, v.map(_.toDouble).toJSArray)

  def glUniform1i (location:Int, x:Int):Unit = gl.uniform1i(location, x)

  def glUniform1iv (location:Int, count:Int, v:IntBuffer):Unit = gl.uniform1iv(location, new Int32Array(v.arrayBuffer()))
  
  def glUniform1iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = gl.uniform1iv(location, v.toJSArray)

  def glUniform2f (location:Int, x:Float, y:Float):Unit = gl.uniform2f(location, x, y)

  def glUniform2fv (location:Int, count:Int, v:FloatBuffer):Unit = gl.uniform2fv(location, new Float32Array(v.arrayBuffer()))
  
  def glUniform2fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = gl.uniform2fv(location, v.map(_.toDouble).toJSArray)

  def glUniform2i (location:Int, x:Int, y:Int):Unit = gl.uniform2i(location, x, y)

  def glUniform2iv (location:Int, count:Int, v:IntBuffer):Unit = gl.uniform2iv(location, new Int32Array(v.arrayBuffer()))
  
  def glUniform2iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = gl.uniform2iv(location, v.toJSArray)

  def glUniform3f (location:Int, x:Float, y:Float, z:Float):Unit = gl.uniform3f(location, x, y, z)

  def glUniform3fv (location:Int, count:Int, v:FloatBuffer):Unit = gl.uniform3fv(location, new Float32Array(v.arrayBuffer()))
  
  def glUniform3fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = gl.uniform3fv(location, v.map(_.toDouble).toJSArray)

  def glUniform3i (location:Int, x:Int, y:Int, z:Int):Unit = gl.uniform3i(location, x, y, z)

  def glUniform3iv (location:Int, count:Int, v:IntBuffer):Unit = gl.uniform3iv(location, new Int32Array(v.arrayBuffer()))
  
  def glUniform3iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = gl.uniform3iv(location, v.toJSArray)

  def glUniform4f (location:Int, x:Float, y:Float, z:Float, w:Float):Unit = gl.uniform4f(location, x, y, z, w)

  def glUniform4fv (location:Int, count:Int, v:FloatBuffer):Unit = gl.uniform4fv(location, new Float32Array(v.arrayBuffer()))
  
  def glUniform4fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = gl.uniform4fv(location, v.map(_.toDouble).toJSArray)

  def glUniform4i (location:Int, x:Int, y:Int, z:Int, w:Int):Unit = gl.uniform4i(location, x, y, z, w)

  def glUniform4iv (location:Int, count:Int, v:IntBuffer):Unit = gl.uniform4iv(location, new Int32Array(v.arrayBuffer()))
  
  def glUniform4iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = gl.uniform4iv(location, v.toJSArray)

  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = gl.uniformMatrix2fv(location, transpose, new Float32Array(value.arrayBuffer()))
  
  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = gl.uniformMatrix2fv(location, transpose, value.map(_.toDouble).toJSArray)

  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = gl.uniformMatrix3fv(location, transpose, new Float32Array(value.arrayBuffer()))
  
  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = gl.uniformMatrix3fv(location, transpose, value.map(_.toDouble).toJSArray)

  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = gl.uniformMatrix4fv(location, transpose, new Float32Array(value.arrayBuffer()))
  
  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = gl.uniformMatrix4fv(location, transpose, value.map(_.toDouble).toJSArray)

  def glUseProgram (program:Int):Unit = gl.useProgram(program)

  def glValidateProgram (program:Int):Unit = gl.validateProgram(program)

  def glVertexAttrib1f (indx:Int, x:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttrib1fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttrib2f (indx:Int, x:Float, y:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttrib2fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttrib3f (indx:Int, x:Float, y:Float, z:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttrib3fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttrib4f (indx:Int, x:Float, y:Float, z:Float, w:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttrib4fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  /**
   * In OpenGl core profiles (3.1+), passing a pointer to client memory is not valid.
   * In 3.0 and later, use the other version of this function instead, pass a zero-based
   * offset which references the buffer currently bound to GL_ARRAY_BUFFER.
   */
  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(0).getMethodName)}

  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Int):Unit = gl.vertexAttribPointer(indx,size,`type`,normalized,stride,ptr)

}

