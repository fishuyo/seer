package seer.tests.graphics

import seer._
import seer.graphics._

/**
 * Test: Clear Color API
 * 
 * Verifies:
 * - clearColor() sets the clear color correctly
 * - clear() clears the buffers with the set color
 * - Multiple clear operations work
 * - Color values are correctly applied
 */
object ClearColorTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("Clear Color Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    val graphics = new LwjglOpenGLGraphicsModule(
      OpenGLConfig(version = (3, 3), vsync = true)
    )
    runtime.addModule(graphics)
    
    var testPassed = true
    var frameCount = 0
    var colorIndex = 0
    
    // Test colors: Red, Green, Blue, White, Black
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
        // Cycle through colors on spacebar
        colorIndex = (colorIndex + 1) % testColors.length
        println(s"Switched to color: ${colorNames(colorIndex)}")
      }
    }
    
    // Test 1: Basic clearColor call
    println("\n[Test 1] Testing clearColor() API...")
    try {
      // This will be tested visually and through the render loop
      println("  ✓ clearColor() API available")
      println("    - Will verify visually in render loop")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        testPassed = false
    }
    
    // Test 2: clear() with Color object
    println("\n[Test 2] Testing clear() with Color object...")
    try {
      // This will be tested visually
      println("  ✓ clear() API available")
      println("    - Will verify visually in render loop")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        testPassed = false
    }
    
    // Test 3: Color cycling
    println("\n[Test 3] Testing color cycling...")
    println("  - Colors will cycle every 2 seconds")
    println("  - Press SPACE to manually cycle colors")
    println("  - Colors: Red → Green → Blue → White → Black")
    
    // Create window
    val window = graphics.createWindow(
      WindowConfig(
        title = "Clear Color Test - Press SPACE to cycle colors, ESC to exit",
        width = 800,
        height = 600
      )
    )
    
    var lastColorChangeTime = System.currentTimeMillis()
    val colorChangeInterval = 2000L // 2 seconds
    
    graphics.onUpdate = { dt =>
      frameCount += 1
      
      // Auto-cycle colors every 2 seconds
      val now = System.currentTimeMillis()
      if (now - lastColorChangeTime > colorChangeInterval) {
        colorIndex = (colorIndex + 1) % testColors.length
        println(s"Color changed to: ${colorNames(colorIndex)}")
        lastColorChangeTime = now
      }
    }
    
    graphics.onDraw = { g =>
      // Test clearColor with individual components
      val currentColor = testColors(colorIndex)
      g.clearColor(currentColor.r, currentColor.g, currentColor.b, currentColor.a)
      
      // Test clear() - this should use the color set by clearColor
      g.clear(currentColor)
      
      // Also test that we can call clear with a Color object directly
      // (this internally calls clearColor and then clears)
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
    println(s"Total frames rendered: $frameCount")
  }
}
