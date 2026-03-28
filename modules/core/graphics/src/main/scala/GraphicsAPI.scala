package seer.graphics

import seer.{Event, Module, ModuleEvent}

/**
 * Unified Graphics API that works across all platforms.
 * This trait defines the interface that all graphics backends must implement.
 * 
 * Instead of a single GraphicsModule with backend selection, we use separate
 * module classes for each backend (e.g., LwjglOpenGLGraphicsModule, 
 * LwjglVulkanGraphicsModule, WebGLGraphicsModule, etc.) for better type safety
 * and clearer separation of concerns.
 */

// Base configuration shared across all backends
trait GraphicsConfig {
  def vsync: Boolean
  def msaa: Int // multisample anti-aliasing samples
  def depthBits: Int
  def stencilBits: Int
}

// OpenGL-specific configuration
case class OpenGLConfig(
  version: (Int, Int) = (3, 3), // major, minor
  profile: OpenGLProfile = OpenGLCoreProfile,
  vsync: Boolean = true,
  msaa: Int = 0,
  depthBits: Int = 24,
  stencilBits: Int = 8,
  debug: Boolean = false
) extends GraphicsConfig

sealed trait OpenGLProfile
case object OpenGLCoreProfile extends OpenGLProfile
case object OpenGLCompatibilityProfile extends OpenGLProfile
case object OpenGLESProfile extends OpenGLProfile

// Vulkan-specific configuration
case class VulkanConfig(
  apiVersion: (Int, Int, Int) = (1, 0, 0), // major, minor, patch
  vsync: Boolean = true,
  msaa: Int = 0,
  depthBits: Int = 24,
  stencilBits: Int = 8,
  validationLayers: Boolean = false,
  requiredExtensions: Seq[String] = Seq.empty
) extends GraphicsConfig

// WebGL-specific configuration
case class WebGLConfig(
  version: Int = 2, // WebGL 1.0 or 2.0
  vsync: Boolean = true,
  msaa: Int = 0,
  depthBits: Int = 24,
  stencilBits: Int = 8,
  alpha: Boolean = true,
  antialias: Boolean = true,
  premultipliedAlpha: Boolean = false,
  preserveDrawingBuffer: Boolean = false
) extends GraphicsConfig

// WebGPU-specific configuration
case class WebGPUConfig(
  powerPreference: WebGPUPowerPreference = WebGPUPowerPreferenceDefault,
  vsync: Boolean = true,
  msaa: Int = 0,
  depthBits: Int = 24,
  stencilBits: Int = 8
) extends GraphicsConfig

sealed trait WebGPUPowerPreference
case object WebGPUPowerPreferenceDefault extends WebGPUPowerPreference
case object WebGPUPowerPreferenceLowPower extends WebGPUPowerPreference
case object WebGPUPowerPreferenceHighPerformance extends WebGPUPowerPreference

// Window Configuration
case class WindowConfig(
  title: String = "Seer Window",
  width: Int = 800,
  height: Int = 600,
  resizable: Boolean = true,
  fullscreen: Boolean = false,
  decorated: Boolean = true
)

// Color
case class Color(r: Float, g: Float, b: Float, a: Float = 1.0f) {
  def toArray: Array[Float] = Array(r, g, b, a)
}

object Color {
  val black = Color(0f, 0f, 0f)
  val white = Color(1f, 1f, 1f)
  val red = Color(1f, 0f, 0f)
  val green = Color(0f, 1f, 0f)
  val blue = Color(0f, 0f, 1f)
  val transparent = Color(0f, 0f, 0f, 0f)
}

// Graphics Context (represents an OpenGL/Vulkan/WebGL context)
trait GraphicsContext {
  def id: String
  def isValid: Boolean
}

// Window interface is now in Window.scala for better visibility

// Graphics Module interface - base trait for all graphics modules
trait GraphicsModule extends Module {
  
  // Context Management
  def createContext(): GraphicsContext
  def destroyContext(context: GraphicsContext): Unit
  def getCurrentContext(): Option[GraphicsContext]
  
  // Window Management
  def createWindow(config: WindowConfig): Window
  def destroyWindow(window: Window): Unit
  def getWindows(): Seq[Window]
  def getWindow(id: String): Option[Window]
  
  // Rendering
  def clear(color: Color): Unit
  def clearColor(r: Float, g: Float, b: Float, a: Float): Unit
  def clearDepth(depth: Double): Unit
  
  // Viewport
  def setViewport(x: Int, y: Int, width: Int, height: Int): Unit
  
  // Callbacks
  var onDraw: Graphics => Unit = _ => {}
  var onUpdate: Double => Unit = _ => {}
  
  // Access to low-level graphics interface (for advanced usage)
  def graphics: Graphics
}

// Example backend module interfaces (these would be implemented in backend modules)
// 
// For JVM/LWJGL:
//   - LwjglOpenGLGraphicsModule(config: OpenGLConfig) extends GraphicsModule
//   - LwjglVulkanGraphicsModule(config: VulkanConfig) extends GraphicsModule
//
// For Browser:
//   - WebGLGraphicsModule(config: WebGLConfig) extends GraphicsModule
//   - WebGPUGraphicsModule(config: WebGPUConfig) extends GraphicsModule
//
// For Native (future):
//   - NativeOpenGLGraphicsModule(config: OpenGLConfig) extends GraphicsModule
//   - NativeVulkanGraphicsModule(config: VulkanConfig) extends GraphicsModule
