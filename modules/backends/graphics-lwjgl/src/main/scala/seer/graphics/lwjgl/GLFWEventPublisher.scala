package seer.graphics.lwjgl

import seer._
import seer.window.glfw.GLFWEventHandler
import seer.{WindowEvent, InputEvent}

/**
 * GLFW event handler that publishes events to the Seer runtime.
 */
class GLFWEventPublisher(runtime: SeerRuntime) extends GLFWEventHandler {
  
  override def handleWindowEvent(event: WindowEvent): Unit = {
    runtime.publish(event)
  }
  
  override def handleInputEvent(event: InputEvent): Unit = {
    runtime.publish(event)
  }
}
