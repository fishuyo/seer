package seer.tests.graphics

import seer._
import seer.graphics._

/**
 * WebGL Test: Clear Color API
 * 
 * Verifies clearColor() and clear() work correctly in WebGL.
 */
object WebGLClearColorTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("WebGL Clear Color Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    val graphics = new WebGLGraphicsModule(
      WebGLConfig(version = 2, alpha = true, antialias = true)
    )
    runtime.addModule(graphics)
    
    var colorIndex = 0
    
    // Test colors
    val testColors = Array(
      Color.red,
      Color.green,
      Color.blue,
      Color.white,
      Color.black
    )
    
    val colorNames = Array("Red", "Green", "Blue", "White", "Black")
    
    runtime.subscribe[WindowCreated] { event =>
      println(s"✓ Window created: ${event.width}x${event.height}")
    }
    
    runtime.subscribe[KeyPressed] { event =>
      if (event.key == Key.ESCAPE) {
        println("\nExiting test...")
        runtime.stop()
      } else if (event.key == Key.SPACE) {
        colorIndex = (colorIndex + 1) % testColors.length
        println(s"Switched to color: ${colorNames(colorIndex)}")
      }
    }
    
    // Create window
    val window = graphics.createWindow(
      WindowConfig(
        title = "WebGL Clear Color Test - Press SPACE to cycle colors, ESC to exit",
        width = 800,
        height = 600
      )
    )
    
    var lastColorChangeTime = System.currentTimeMillis()
    val colorChangeInterval = 2000L // 2 seconds
    
    graphics.onUpdate = { dt =>
      // Auto-cycle colors every 2 seconds
      val now = System.currentTimeMillis()
      if (now - lastColorChangeTime > colorChangeInterval) {
        colorIndex = (colorIndex + 1) % testColors.length
        println(s"Color changed to: ${colorNames(colorIndex)}")
        lastColorChangeTime = now
      }
    }
    
    graphics.onDraw = { g =>
      val currentColor = testColors(colorIndex)
      g.clear(currentColor)
    }
    
    println("\n" + "=" * 60)
    println("Test Instructions:")
    println("  - Window should display solid colors")
    println("  - Colors should auto-cycle every 2 seconds")
    println("  - Press SPACE to manually cycle colors")
    println("  - Press ESC to exit")
    println("=" * 60)
    
    // Run the test
    runtime.run()
    
    println("\nTest completed.")
  }
}
