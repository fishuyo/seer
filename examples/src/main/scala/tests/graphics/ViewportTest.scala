package seer.tests.graphics

import seer._
import seer.graphics._

/**
 * Test: Viewport API
 * 
 * Verifies:
 * - setViewport() correctly sets the viewport
 * - Viewport affects rendering area
 * - Multiple viewport changes work
 * - Viewport matches window size
 */
object ViewportTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("Viewport Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    val graphics = new LwjglOpenGLGraphicsModule(
      OpenGLConfig(version = (3, 3), vsync = true)
    )
    runtime.addModule(graphics)
    
    runtime.subscribe[WindowCreated] { event =>
      println(s"✓ Window created: ${event.width}x${event.height}")
    }
    
    runtime.subscribe[WindowResized] { event =>
      println(s"✓ Window resized: ${event.width}x${event.height}")
    }
    
    runtime.subscribe[KeyPressed] { event =>
      if (event.key == Key.ESCAPE) {
        println("\nExiting test...")
        runtime.stop()
      }
    }
    
    // Create window
    val window = graphics.createWindow(
      WindowConfig(
        title = "Viewport Test - Resize window to test viewport, ESC to exit",
        width = 800,
        height = 600,
        resizable = true
      )
    )
    
    var viewportMode = 0 // 0 = full, 1 = half, 2 = quarter, 3 = custom
    
    runtime.subscribe[KeyPressed] { event =>
      if (event.key.name == "V" || event.key.name == "v") {
        viewportMode = (viewportMode + 1) % 4
        println(s"Viewport mode changed to: $viewportMode")
      }
    }
    
    graphics.onUpdate = { dt =>
      // Update logic
    }
    
    graphics.onDraw = { g =>
      val (width, height) = (window.width, window.height)
      
      viewportMode match {
        case 0 => // Full viewport
          g.setViewport(0, 0, width, height)
          g.clearColor(1.0f, 0.0f, 0.0f, 1.0f) // Red
          g.clear(Color.red)
          
        case 1 => // Half viewport (top-left)
          g.setViewport(0, height / 2, width / 2, height / 2)
          g.clearColor(0.0f, 1.0f, 0.0f, 1.0f) // Green
          g.clear(Color.green)
          // Clear rest with black
          g.setViewport(0, 0, width, height)
          g.clearColor(0.0f, 0.0f, 0.0f, 1.0f)
          g.clear(Color.black)
          // Draw green quadrant
          g.setViewport(0, height / 2, width / 2, height / 2)
          g.clearColor(0.0f, 1.0f, 0.0f, 1.0f)
          g.clear(Color.green)
          
        case 2 => // Quarter viewport (center)
          val qw = width / 4
          val qh = height / 4
          g.setViewport(qw, qh, qw * 2, qh * 2)
          g.clearColor(0.0f, 0.0f, 1.0f, 1.0f) // Blue
          g.clear(Color.blue)
          // Clear rest with black
          g.setViewport(0, 0, width, height)
          g.clearColor(0.0f, 0.0f, 0.0f, 1.0f)
          g.clear(Color.black)
          // Draw blue quadrant
          g.setViewport(qw, qh, qw * 2, qh * 2)
          g.clearColor(0.0f, 0.0f, 1.0f, 1.0f)
          g.clear(Color.blue)
          
        case 3 => // Custom: 4 quadrants with different colors
          val hw = width / 2
          val hh = height / 2
          
          // Top-left: Red
          g.setViewport(0, hh, hw, hh)
          g.clearColor(1.0f, 0.0f, 0.0f, 1.0f)
          g.clear(Color.red)
          
          // Top-right: Green
          g.setViewport(hw, hh, hw, hh)
          g.clearColor(0.0f, 1.0f, 0.0f, 1.0f)
          g.clear(Color.green)
          
          // Bottom-left: Blue
          g.setViewport(0, 0, hw, hh)
          g.clearColor(0.0f, 0.0f, 1.0f, 1.0f)
          g.clear(Color.blue)
          
          // Bottom-right: White
          g.setViewport(hw, 0, hw, hh)
          g.clearColor(1.0f, 1.0f, 1.0f, 1.0f)
          g.clear(Color.white)
      }
    }
    
    println("\n" + "=" * 60)
    println("Test Instructions:")
    println("  - Window should display colored areas based on viewport")
    println("  - Press V to cycle through viewport modes:")
    println("    0: Full viewport (red)")
    println("    1: Half viewport - top-left quadrant (green)")
    println("    2: Quarter viewport - center (blue)")
    println("    3: Four quadrants (red/green/blue/white)")
    println("  - Resize window to verify viewport adapts")
    println("  - Press ESC to exit")
    println("=" * 60)
    
    // Run the test
    runtime.run()
    
    println("\nTest completed.")
  }
}
