package seer.window.glfw

import org.lwjgl.glfw.GLFW._

/**
 * Trait for providing backend-specific GLFW context hints.
 * Implementations provide hints for OpenGL, Vulkan, or other backends.
 */
trait ContextHintProvider {
  /**
   * Apply backend-specific window hints before window creation.
   * This is called by GLFWManager before creating a window.
   */
  def applyContextHints(): Unit
}
