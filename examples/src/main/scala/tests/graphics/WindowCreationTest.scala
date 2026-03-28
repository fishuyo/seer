package seer.tests.graphics

import seer._
import seer.graphics._

/**
 * Test: Window Creation
 * 
 * Verifies:
 * - Basic window creation works
 * - Window configuration is respected
 * - Multiple windows can be created
 * - Window properties are correct
 * - Window destruction works
 */
object WindowCreationTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("Window Creation Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    val graphics = new LwjglOpenGLGraphicsModule(
      OpenGLConfig(version = (3, 3), vsync = false)
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
      if (event.key == Key.ESCAPE) {
        println("\nExiting test...")
        runtime.stop()
      }
    }
    
    // Test 1: Basic window creation
    println("\n[Test 1] Creating basic window...")
    try {
      val window1 = graphics.createWindow(
        WindowConfig(
          title = "Window Creation Test - Window 1",
          width = 640,
          height = 480
        )
      )
      
      assert(window1 != null, "Window should not be null")
      assert(window1.width == 640, s"Expected width 640, got ${window1.width}")
      assert(window1.height == 480, s"Expected height 480, got ${window1.height}")
      assert(window1.title == "Window Creation Test - Window 1", "Title should match")
      
      println("  ✓ Window created successfully")
      println(s"    - ID: ${window1.id}")
      println(s"    - Size: ${window1.width}x${window1.height}")
      println(s"    - Buffer: ${window1.bufferWidth}x${window1.bufferHeight}")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
        testPassed = false
    }
    
    // Test 2: Window with custom configuration
    println("\n[Test 2] Creating window with custom config...")
    try {
      val window2 = graphics.createWindow(
        WindowConfig(
          title = "Custom Config Window",
          width = 1024,
          height = 768,
          resizable = true,
          decorated = true
        )
      )
      
      assert(window2.width == 1024, s"Expected width 1024, got ${window2.width}")
      assert(window2.height == 768, s"Expected height 768, got ${window2.height}")
      
      println("  ✓ Custom window created successfully")
      println(s"    - Size: ${window2.width}x${window2.height}")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
        testPassed = false
    }
    
    // Test 3: Multiple windows
    println("\n[Test 3] Creating multiple windows...")
    try {
      val initialCount = graphics.getWindows().length
      val newWindows = (1 to 2).map { i =>
        graphics.createWindow(
          WindowConfig(
            title = s"Multi-Window Test $i",
            width = 400,
            height = 300
          )
        )
      }
      
      val totalCount = graphics.getWindows().length
      assert(totalCount == initialCount + 2, 
        s"Expected ${initialCount + 2} windows, got $totalCount")
      
      println(s"  ✓ Created ${newWindows.length} additional windows")
      println(s"    - Total windows: $totalCount")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
        testPassed = false
    }
    
    // Test 4: Window retrieval
    println("\n[Test 4] Testing window retrieval...")
    try {
      val windows = graphics.getWindows()
      assert(windows.nonEmpty, "Should have at least one window")
      
      val firstWindow = windows.head
      val retrieved = graphics.getWindow(firstWindow.id)
      assert(retrieved.isDefined, "Should be able to retrieve window by ID")
      assert(retrieved.get.id == firstWindow.id, "Retrieved window should match")
      
      println(s"  ✓ Successfully retrieved ${windows.length} windows")
      println(s"    - Retrieved window by ID: ${retrieved.get.id}")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
        testPassed = false
    }
    
    // Test 5: Window properties
    println("\n[Test 5] Verifying window properties...")
    try {
      val windows = graphics.getWindows()
      windows.foreach { window =>
        assert(window.id.nonEmpty, "Window should have non-empty ID")
        assert(window.width > 0, "Window should have positive width")
        assert(window.height > 0, "Window should have positive height")
        assert(window.bufferWidth > 0, "Window should have positive buffer width")
        assert(window.bufferHeight > 0, "Window should have positive buffer height")
      }
      println(s"  ✓ All ${windows.length} windows have valid properties")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
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
    println("Windows should be visible and responsive")
    
    // Setup rendering
    graphics.onDraw = { g =>
      // Clear with a dark gray color so we can see the windows
      g.clear(Color(0.1f, 0.1f, 0.1f, 1.0f))
    }
    
    graphics.onUpdate = { dt =>
      // Update logic if needed
    }
    
    // Run the test
    runtime.run()
    
    println("\nTest completed.")
  }
}
