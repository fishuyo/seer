package seer.tests.graphics

import seer._
import seer.graphics._

/**
 * Test: Event System
 * 
 * Verifies:
 * - Window events are published correctly
 * - Keyboard events are published correctly
 * - Mouse events are published correctly
 * - Event data is correct
 * - Multiple subscribers work
 */
object EventTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("Event System Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    val graphics = new LwjglOpenGLGraphicsModule(
      OpenGLConfig(version = (3, 3), vsync = true)
    )
    runtime.addModule(graphics)
    
    var windowEventCount = 0
    var keyEventCount = 0
    var mouseEventCount = 0
    
    // Test window events
    println("\n[Test 1] Testing window events...")
    runtime.subscribe[WindowCreated] { event =>
      windowEventCount += 1
      println(s"  ✓ WindowCreated: ${event.width}x${event.height} (id: ${event.windowId})")
      assert(event.width > 0, "Window width should be positive")
      assert(event.height > 0, "Window height should be positive")
    }
    
    runtime.subscribe[WindowResized] { event =>
      windowEventCount += 1
      println(s"  ✓ WindowResized: ${event.width}x${event.height}")
    }
    
    runtime.subscribe[WindowClosed] { event =>
      windowEventCount += 1
      println(s"  ✓ WindowClosed: ${event.windowId}")
    }
    
    // Test keyboard events
    println("\n[Test 2] Testing keyboard events...")
    runtime.subscribe[KeyPressed] { event =>
      keyEventCount += 1
      println(s"  ✓ KeyPressed: ${event.key.name} (code: ${event.key.code})")
      println(s"    Modifiers: shift=${event.mods.shift}, ctrl=${event.mods.control}, alt=${event.mods.alt}")
    }
    
    runtime.subscribe[KeyReleased] { event =>
      keyEventCount += 1
      println(s"  ✓ KeyReleased: ${event.key.name}")
    }
    
    runtime.subscribe[KeyRepeated] { event =>
      keyEventCount += 1
      println(s"  ✓ KeyRepeated: ${event.key.name}")
    }
    
    // Test mouse events
    println("\n[Test 3] Testing mouse events...")
    runtime.subscribe[MouseMoved] { event =>
      mouseEventCount += 1
      if (mouseEventCount % 60 == 0) { // Print every 60th event to avoid spam
        println(s"  ✓ MouseMoved: (${event.x.formatted("%.2f")}, ${event.y.formatted("%.2f")})")
      }
    }
    
    runtime.subscribe[MousePressed] { event =>
      mouseEventCount += 1
      println(s"  ✓ MousePressed: button=${event.button} at (${event.x.formatted("%.2f")}, ${event.y.formatted("%.2f")})")
    }
    
    runtime.subscribe[MouseReleased] { event =>
      mouseEventCount += 1
      println(s"  ✓ MouseReleased: button=${event.button} at (${event.x.formatted("%.2f")}, ${event.y.formatted("%.2f")})")
    }
    
    runtime.subscribe[MouseScrolled] { event =>
      mouseEventCount += 1
      println(s"  ✓ MouseScrolled: (${event.scrollX.formatted("%.2f")}, ${event.scrollY.formatted("%.2f")})")
    }
    
    // Test module lifecycle events
    println("\n[Test 4] Testing module lifecycle events...")
    runtime.subscribe[ModuleCreated] { event =>
      if (event.module != null) {
        println(s"  ✓ ModuleCreated: ${event.module.name}")
      }
    }
    
    runtime.subscribe[ModuleStarted] { event =>
      if (event.module != null) {
        println(s"  ✓ ModuleStarted: ${event.module.name}")
      }
    }
    
    // Create window
    val window = graphics.createWindow(
      WindowConfig(
        title = "Event Test - Interact with window, press ESC to exit",
        width = 800,
        height = 600,
        resizable = true
      )
    )
    
    runtime.subscribe[KeyPressed] { event =>
      if (event.key == Key.ESCAPE) {
        println("\n" + "=" * 60)
        println("Event Statistics:")
        println(s"  Window events: $windowEventCount")
        println(s"  Keyboard events: $keyEventCount")
        println(s"  Mouse events: $mouseEventCount")
        println("=" * 60)
        println("\nExiting test...")
        runtime.stop()
      }
    }
    
    graphics.onDraw = { g =>
      g.clear(Color(0.2f, 0.2f, 0.2f, 1.0f))
    }
    
    println("\n" + "=" * 60)
    println("Test Instructions:")
    println("  - Move mouse around the window")
    println("  - Click mouse buttons")
    println("  - Scroll mouse wheel")
    println("  - Press keyboard keys")
    println("  - Resize the window")
    println("  - Press ESC to exit and see event statistics")
    println("=" * 60)
    
    // Run the test
    runtime.run()
    
    println("\nTest completed.")
  }
}
