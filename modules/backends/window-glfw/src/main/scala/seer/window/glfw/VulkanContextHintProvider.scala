package seer.window.glfw

import org.lwjgl.glfw.GLFW._
import seer.graphics.VulkanConfig

/**
 * Vulkan-specific context hint provider.
 * Applies GLFW window hints for Vulkan surface creation.
 * Note: Vulkan doesn't use OpenGL-style context hints, but we still need
 * to configure the window appropriately for Vulkan.
 */
class VulkanContextHintProvider(config: VulkanConfig) extends ContextHintProvider {
  
  override def applyContextHints(): Unit = {
    // Vulkan doesn't create a rendering context like OpenGL
    // Instead, we set GLFW_CLIENT_API to GLFW_NO_API
    glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)
    
    // Note: Other Vulkan-specific configuration (validation layers, extensions, etc.)
    // is handled during Vulkan instance and device creation, not through GLFW hints.
  }
}
