package seer.tests.graphics

import seer._
import seer.graphics._

/**
 * WebGL Test: Window Creation
 * 
 * Verifies WebGL backend window creation in the browser.
 * Run this by compiling to JS and opening in a browser.
 */
object WebGLWindowCreationTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("WebGL Window Creation Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    val graphics = new WebGLGraphicsModule(
      WebGLConfig(version = 2, alpha = true, antialias = true)
    )
    runtime.addModule(graphics)
    
    var testPassed = true
    
    runtime.subscribe[ModuleCreated] { event =>
      if (event.module != null) {
        println(s"✓ Module created: ${event.module.name}")
      }
    }
    
    runtime.subscribe[WindowCreated] { event =>
      println(s"✓ Window created: ${event.width}x${event.height} (id: ${event.windowId})")
    }
    
    runtime.subscribe[KeyPressed] { event =>
      println(s"Key pressed: ${event.key.name}")
      if (event.key == Key.ESCAPE) {
        println("\nExiting test...")
        runtime.stop()
      }
    }
    
    // Test 1: Basic window creation
    println("\n[Test 1] Creating WebGL window...")
    try {
      val window = graphics.createWindow(
        WindowConfig(
          title = "WebGL Window Creation Test",
          width = 800,
          height = 600
        )
      )
      
      assert(window != null, "Window should not be null")
      assert(window.width > 0, "Window should have width")
      assert(window.height > 0, "Window should have height")
      
      println("  ✓ Window created successfully")
      println(s"    - ID: ${window.id}")
      println(s"    - Size: ${window.width}x${window.height}")
      println(s"    - Buffer: ${window.bufferWidth}x${window.bufferHeight}")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
        testPassed = false
    }
    
    // Test 2: Window properties
    println("\n[Test 2] Verifying window properties...")
    try {
      val windows = graphics.getWindows()
      assert(windows.nonEmpty, "Should have at least one window")
      
      windows.foreach { window =>
        assert(window.id.nonEmpty, "Window should have non-empty ID")
        assert(window.width > 0, "Window should have positive width")
        assert(window.height > 0, "Window should have positive height")
      }
      
      println(s"  ✓ All ${windows.length} windows have valid properties")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        testPassed = false
    }
    
    // Summary
    println("\n" + "=" * 60)
    if (testPassed) {
      println("✓ ALL TESTS PASSED")
    } else {
      println("✗ SOME TESTS FAILED")
    }
    println("=" * 60)
    println("\nPress ESC to exit")
    
    // Setup rendering
    graphics.onDraw = { g =>
      g.clear(Color(0.1f, 0.1f, 0.3f, 1.0f)) // Dark blue
    }
    
    graphics.onUpdate = { dt =>
      // Update logic
    }
    
    // Run the test
    runtime.run()
    
    println("\nTest completed.")
  }
}
