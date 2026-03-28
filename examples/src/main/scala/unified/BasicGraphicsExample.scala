package seer.unified

import seer._
import seer.graphics._
import seer.audio._

/**
 * Basic example demonstrating the unified Graphics API.
 * 
 * This example shows:
 * - Creating a runtime with graphics module
 * - Subscribing to events
 * - Setting up callbacks
 * - Creating a window and drawing
 */
object BasicGraphicsExample {

  def main(args: Array[String]): Unit = {
    
    // Create runtime
    val runtime = new SeerRuntime()
    
    // Create graphics module with OpenGL configuration
    val openGLConfig = OpenGLConfig(
      version = (3, 3),
      profile = OpenGLCoreProfile,
      vsync = true,
      msaa = 4
    )
    
    val graphics = new LwjglOpenGLGraphicsModule(openGLConfig)
    
    // Add module to runtime
    runtime.addModule(graphics)
    
    // Subscribe to window events
    runtime.subscribe[WindowCreated] { event =>
      println(s"✓ Window created: ${event.width}x${event.height}")
    }
    
    runtime.subscribe[WindowResized] { event =>
      println(s"Window resized: ${event.width}x${event.height}")
    }
    
    // Subscribe to input events
    runtime.subscribe[KeyPressed] { event =>
      println(s"Key pressed: ${event.key.name} (code: ${event.key.code})")
      if (event.key == Key.ESCAPE) {
        println("Escape pressed, stopping runtime...")
        runtime.stop()
      }
    }
    
    runtime.subscribe[MouseMoved] { event =>
      // Uncomment to see mouse movement
      // println(s"Mouse: (${event.x.formatted("%.2f")}, ${event.y.formatted("%.2f")})")
    }
    
    runtime.subscribe[MousePressed] { event =>
      println(s"Mouse button ${event.button} pressed at (${event.x.formatted("%.2f")}, ${event.y.formatted("%.2f")})")
    }
    
    // Subscribe to module lifecycle events
    runtime.subscribe[ModuleCreated] { event =>
      if (event.module != null) {
        println(s"✓ Module created: ${event.module.name}")
      }
    }
    
    runtime.subscribe[ModuleStarted] { event =>
      if (event.module != null) {
        println(s"✓ Module started: ${event.module.name}")
      }
    }
    
    // Setup graphics callbacks
    var frameCount = 0
    var lastFpsTime = System.currentTimeMillis()
    
    graphics.onUpdate = { dt =>
      frameCount += 1
      val now = System.currentTimeMillis()
      if (now - lastFpsTime > 1000) {
        println(s"FPS: $frameCount")
        frameCount = 0
        lastFpsTime = now
      }
    }
    
    graphics.onDraw = { g =>
      // Clear with a blue color
      val time = System.currentTimeMillis() / 1000.0
      val r = (math.sin(time) * 0.5 + 0.5).toFloat
      val b = (math.cos(time) * 0.5 + 0.5).toFloat
      g.clear(Color(r, 0.2f, b, 1.0f))
      
      // In a real application, you would draw meshes, shaders, etc. here
    }
    
    // Create a window
    val window = graphics.createWindow(
      WindowConfig(
        title = "Seer Unified Graphics Example",
        width = 800,
        height = 600,
        resizable = true
      )
    )
    
    println("Starting runtime...")
    println("Press ESC to exit")
    
    // Run the runtime (this will block until runtime.stop() is called)
    runtime.run()
    
    println("Runtime stopped.")
  }
}
