
package seer
package graphics 


class RenderBuffer {
  lazy val gl = Graphics().gl
  import gl._

  var id = 0
  var format = GL_DEPTH_COMPONENT16
  var (w,h) = (0,0)

  def create() = {
    id = glGenRenderbuffer()
    this
  }
  def destroy() = {
    glDeleteRenderbuffer(id)
    id = 0
  }

  def resize(_w:Int, _h:Int) = {
    bind()
    glRenderbufferStorage(GL_RENDERBUFFER, format, _w, _h)
    w = _w; h = _h
    unbind()
  }

  def bind() = glBindRenderbuffer(GL_RENDERBUFFER, id)
  def unbind() = glBindRenderbuffer(GL_RENDERBUFFER, 0)
}


class FrameBuffer {
  lazy val gl = Graphics().gl
  import gl._

  var id = 0

  def create() = {
    id = glGenFramebuffer()
    this
  }
  def destroy() = {
    glDeleteFramebuffer(id)
    id = 0
  }

  def bind() = glBindFramebuffer(GL_FRAMEBUFFER, id)
  def unbind() = glBindFramebuffer(GL_FRAMEBUFFER, 0)

  def begin() = bind()
  def end() = unbind()

  def attachRBO(rbo:RenderBuffer, attachment:Int = GL_DEPTH_ATTACHMENT) = {
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, rbo.id)
  }
  def detachRBO(attachment:Int = GL_DEPTH_ATTACHMENT) = {
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, 0)
  }

  def attachTexture(tex:Texture, attachment:Int = GL_COLOR_ATTACHMENT0, level:Int = 0) = {
    glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, tex.id, level)
  }
  def detachTexture(attachment:Int = GL_COLOR_ATTACHMENT0, level:Int = 0) = {
    glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, 0, level)
  }
}