package seer.window.glfw

import org.lwjgl.glfw.GLFW._
import seer.graphics.OpenGLConfig

/**
 * OpenGL-specific context hint provider.
 * Applies GLFW window hints for OpenGL context creation.
 */
class OpenGLContextHintProvider(config: OpenGLConfig) extends ContextHintProvider {
  
  override def applyContextHints(): Unit = {
    // OpenGL version
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, config.version._1)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, config.version._2)
    
    // Profile
    config.profile match {
      case seer.graphics.OpenGLCoreProfile =>
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE) // Required on macOS
      case seer.graphics.OpenGLCompatibilityProfile =>
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE)
      case seer.graphics.OpenGLESProfile =>
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API)
    }
    
    // Debug context
    if (config.debug) {
      glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
    }
    
    // MSAA
    if (config.msaa > 0) {
      glfwWindowHint(GLFW_SAMPLES, config.msaa)
    }
  }
}
