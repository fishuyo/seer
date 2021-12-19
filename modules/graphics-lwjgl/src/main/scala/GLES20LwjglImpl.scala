package seer.graphics
package lwjgl

import java.nio.Buffer
import java.nio.FloatBuffer 
import java.nio.IntBuffer

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL41


class GLES20LwjglImpl extends GLES20 {

  def glActiveTexture (texture:Int):Unit = GL13.glActiveTexture(texture)

  def glBindTexture (target:Int, texture:Int):Unit = GL11.glBindTexture(target, texture)

  def glBlendFunc (sfactor:Int, dfactor:Int):Unit = GL11.glBlendFunc(sfactor, dfactor)

  def glClear (mask:Int):Unit = GL11.glClear(mask)

  def glClearColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = GL11.glClearColor(red, green, blue, alpha)

  def glClearDepthf (depth:Float):Unit = GL41.glClearDepthf(depth)

  def glClearStencil (s:Int):Unit = GL11.glClearStencil(s)

  def glColorMask (red:Boolean, green:Boolean, blue:Boolean, alpha:Boolean):Unit = GL11.glColorMask(red, green, blue, alpha)

  def glCompressedTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, imageSize:Int, data:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glCompressedTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, imageSize:Int, data:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glCopyTexImage2D (target:Int, level:Int, internalformat:Int, x:Int, y:Int, width:Int, height:Int, border:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glCopyTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, x:Int, y:Int, width:Int, height:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glCullFace (mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDeleteTextures (n:Int, textures:IntBuffer):Unit = GL11.glDeleteTextures(textures)
  
  def glDeleteTexture (texture:Int):Unit = GL11.glDeleteTextures(texture)

  def glDepthFunc (func:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDepthMask (flag:Boolean):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDepthRangef (zNear:Float, zFar:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDisable (cap:Int):Unit = GL11.glDisable(cap)

  def glDrawArrays (mode:Int, first:Int, count:Int):Unit = GL11.glDrawArrays(mode, first, count)

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Buffer):Unit = GL11C.glDrawElements(mode, count, `type`, 0)

  def glEnable (cap:Int):Unit = GL11.glEnable(cap)

  def glFinish ():Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glFlush ():Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glFrontFace (mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGenTextures (n:Int, textures:IntBuffer):Unit = GL11.glGenTextures(textures)
  
  def glGenTexture ():Int = GL11.glGenTextures()

  def glGetError ():Int = GL11.glGetError()

  def glGetIntegerv (pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetString (name:Int):String = ""

  def glHint (target:Int, mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glLineWidth (width:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glPixelStorei (pname:Int, param:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glPolygonOffset (factor:Float, units:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glReadPixels (x:Int, y:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}//glReadPixels(x,y,width, height, format, `type`, pixels)

  def glScissor (x:Int, y:Int, width:Int, height:Int):Unit = glScissor(x,y,width,height)

  def glStencilFunc (func:Int, ref:Int, mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glStencilMask (mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glStencilOp (fail:Int, zfail:Int, zpass:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glTexImage2D (target:Int, level:Int, internalformat:Int, width:Int, height:Int, border:Int, format:Int, `type`:Int, pixels:Buffer):Unit = GL11.glTexImage2D(target, level, internalformat, width, height, border, format, `type`, pixels.asInstanceOf[java.nio.ByteBuffer])

  def glTexParameterf (target:Int, pname:Int, param:Float):Unit = GL11.glTexParameterf(target,pname,param)

  def glTexSubImage2D (target:Int, level:Int, xoffset:Int, yoffset:Int, width:Int, height:Int, format:Int, `type`:Int, pixels:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glViewport (x:Int, y:Int, width:Int, height:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glAttachShader (program:Int, shader:Int):Unit = GL20.glAttachShader(program, shader)

  def glBindAttribLocation (program:Int, index:Int, name:String):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glBindBuffer (target:Int, buffer:Int):Unit = GL15.glBindBuffer(target, buffer)

  def glBindFramebuffer (target:Int, framebuffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glBindRenderbuffer (target:Int, renderbuffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glBlendColor (red:Float, green:Float, blue:Float, alpha:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glBlendEquation (mode:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glBlendEquationSeparate (modeRGB:Int, modeAlpha:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glBlendFuncSeparate (srcRGB:Int, dstRGB:Int, srcAlpha:Int, dstAlpha:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glBufferData (target:Int, size:Int, data:Buffer, usage:Int):Unit = {
    data match {
      case b:FloatBuffer => GL15.glBufferData(target, b, usage); //println("float")
      case _ => println("nope")
    }
  }

  def glBufferSubData (target:Int, offset:Int, size:Int, data:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glCheckFramebufferStatus (target:Int):Int = 0

  def glCompileShader (shader:Int):Unit = GL20.glCompileShader(shader)

  def glCreateProgram ():Int = GL20.glCreateProgram()

  def glCreateShader (`type`:Int):Int = GL20.glCreateShader(`type`)

  def glDeleteBuffer (buffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDeleteBuffers (n:Int, buffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDeleteFramebuffer (framebuffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glDeleteFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDeleteProgram (program:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDeleteRenderbuffer (renderbuffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glDeleteRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDeleteShader (shader:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDetachShader (program:Int, shader:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glDisableVertexAttribArray (index:Int):Unit = GL20.glDisableVertexAttribArray(index)

  def glDrawElements (mode:Int, count:Int, `type`:Int, indices:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glEnableVertexAttribArray (index:Int):Unit = GL20.glEnableVertexAttribArray(index)

  def glFramebufferRenderbuffer (target:Int, attachment:Int, renderbuffertarget:Int, renderbuffer:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glFramebufferTexture2D (target:Int, attachment:Int, textarget:Int, texture:Int, level:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGenBuffer ():Int = GL15.glGenBuffers()

  def glGenBuffers (n:Int, buffers:IntBuffer):Unit = GL15.glGenBuffers(buffers)

  def glGenerateMipmap (target:Int):Unit = GL30.glGenerateMipmap(target)

  def glGenFramebuffer ():Int = 0

  def glGenFramebuffers (n:Int, framebuffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGenRenderbuffer ():Int = 0

  def glGenRenderbuffers (n:Int, renderbuffers:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  // deviates
  def glGetActiveAttrib (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  // deviates
  def glGetActiveUniform (program:Int, index:Int, size:IntBuffer, `type`:Buffer):String = ""

  def glGetAttachedShaders (program:Int, maxcount:Int, count:Buffer, shaders:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetAttribLocation (program:Int, name:String):Int = GL20.glGetAttribLocation(program, name)

  def glGetBooleanv (pname:Int, params:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetBufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetFloatv (pname:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetFramebufferAttachmentParameteriv (target:Int, attachment:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetProgramiv (program:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  // deviates
  def glGetProgramInfoLog (program:Int):String = GL20.glGetProgramInfoLog(program)

  def glGetRenderbufferParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetShaderiv (shader:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  // deviates
  def glGetShaderInfoLog (shader:Int):String = GL20.glGetShaderInfoLog(shader)

  def glGetShaderPrecisionFormat (shadertype:Int, precisiontype:Int, range:IntBuffer, precision:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetUniformfv (program:Int, location:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetUniformiv (program:Int, location:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetUniformLocation (program:Int, name:String):Int = GL20.glGetUniformLocation(program, name)

  def glGetVertexAttribfv (index:Int, pname:Int, params:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetVertexAttribiv (index:Int, pname:Int, params:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glGetVertexAttribPointerv (index:Int, pname:Int, pointer:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glIsBuffer (buffer:Int):Boolean = false

  def glIsEnabled (cap:Int):Boolean = false

  def glIsFramebuffer (framebuffer:Int):Boolean = false

  def glIsProgram (program:Int):Boolean = false

  def glIsRenderbuffer (renderbuffer:Int):Boolean = false

  def glIsShader (shader:Int):Boolean = false

  def glIsTexture (texture:Int):Boolean = false

  def glLinkProgram (program:Int):Unit = GL20.glLinkProgram(program)

  def glReleaseShaderCompiler ():Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glRenderbufferStorage (target:Int, internalformat:Int, width:Int, height:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glSampleCoverage (value:Float, invert:Boolean):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glShaderBinary (n:Int, shaders:IntBuffer, binaryformat:Int, binary:Buffer, length:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  // Deviates
  def glShaderSource (shader:Int, string:String):Unit = GL20.glShaderSource(shader, string)

  def glStencilFuncSeparate (face:Int, func:Int, ref:Int, mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glStencilMaskSeparate (face:Int, mask:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glStencilOpSeparate (face:Int, fail:Int, zfail:Int, zpass:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glTexParameterfv (target:Int, pname:Int, params:FloatBuffer):Unit = glTexParameterfv(target,pname,params)

  def glTexParameteri (target:Int, pname:Int, param:Int):Unit = glTexParameteri(target,pname,param)

  def glTexParameteriv (target:Int, pname:Int, params:IntBuffer):Unit = glTexParameteriv(target,pname,params)

  def glUniform1f (location:Int, x:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform1fv (location:Int, count:Int, v:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniform1fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform1i (location:Int, x:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform1iv (location:Int, count:Int, v:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniform1iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform2f (location:Int, x:Float, y:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform2fv (location:Int, count:Int, v:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniform2fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform2i (location:Int, x:Int, y:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform2iv (location:Int, count:Int, v:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniform2iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform3f (location:Int, x:Float, y:Float, z:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform3fv (location:Int, count:Int, v:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniform3fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform3i (location:Int, x:Int, y:Int, z:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform3iv (location:Int, count:Int, v:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniform3iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform4f (location:Int, x:Float, y:Float, z:Float, w:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform4fv (location:Int, count:Int, v:FloatBuffer):Unit = GL20.glUniform4fv(location, v)
  
  def glUniform4fv (location:Int, count:Int, v:Array[Float], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform4i (location:Int, x:Int, y:Int, z:Int, w:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniform4iv (location:Int, count:Int, v:IntBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniform4iv (location:Int, count:Int, v:Array[Int], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniformMatrix2fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniformMatrix3fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}
  
  def glUniformMatrix4fv (location:Int, count:Int, transpose:Boolean, value:Array[Float], offset:Int):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glUseProgram (program:Int):Unit = GL20.glUseProgram(program)

  def glValidateProgram (program:Int):Unit = GL20.glValidateProgram(program)

  def glVertexAttrib1f (indx:Int, x:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttrib1fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttrib2f (indx:Int, x:Float, y:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttrib2fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttrib3f (indx:Int, x:Float, y:Float, z:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttrib3fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttrib4f (indx:Int, x:Float, y:Float, z:Float, w:Float):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttrib4fv (indx:Int, values:FloatBuffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  /**
   * In OpenGl core profiles (3.1+), passing a pointer to client memory is not valid.
   * In 3.0 and later, use the other version of this function instead, pass a zero-based
   * offset which references the buffer currently bound to GL_ARRAY_BUFFER.
   */
  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Buffer):Unit = {println("Err: Not implemented " + Thread.currentThread.getStackTrace()(1).getMethodName)}

  def glVertexAttribPointer (indx:Int, size:Int, `type`:Int, normalized:Boolean, stride:Int, ptr:Int):Unit = GL20.glVertexAttribPointer(indx, size, `type`, normalized, stride, ptr)

}

