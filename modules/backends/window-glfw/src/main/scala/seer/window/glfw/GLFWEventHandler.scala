package seer.window.glfw

import seer.{WindowEvent, InputEvent}

/**
 * Handler interface for GLFW window and input events.
 * Implementations should publish events to the runtime system.
 */
trait GLFWEventHandler {
  /**
   * Handle a window event (resize, close, focus, etc.)
   */
  def handleWindowEvent(event: WindowEvent): Unit
  
  /**
   * Handle an input event (keyboard, mouse, etc.)
   */
  def handleInputEvent(event: InputEvent): Unit
}
