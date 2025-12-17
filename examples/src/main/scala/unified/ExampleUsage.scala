package seer.unified

import seer._
import seer.graphics._
import seer.audio._

/**
 * Example demonstrating the unified cross-platform API.
 * 
 * This example shows:
 * - Creating a runtime with graphics and audio modules
 * - Subscribing to events
 * - Setting up callbacks
 * - Cross-platform usage patterns
 * 
 * Note: This is a conceptual example. Actual backend implementations
 * need to be updated to support the new GraphicsModule and AudioModule traits.
 */
object ExampleUsage {

  def main(args: Array[String]): Unit = {
    
    // Create runtime
    val runtime = new SeerRuntime()
    
    // Create graphics module - explicit backend selection
    val openGLConfig = OpenGLConfig(
      version = (3, 3),
      vsync = true,
      msaa = 4,
      profile = OpenGLCoreProfile
    )
    
    // Note: This would be implemented as:
    // val graphics = new LwjglOpenGLGraphicsModule(openGLConfig)
    // For Vulkan: val vulkanGraphics = new LwjglVulkanGraphicsModule(VulkanConfig(...))
    // For now, this is a conceptual example
    // val graphics: GraphicsModule = ???
    
    // Create audio module with configuration
    val audioConfig = AudioConfig(
      sampleRate = 44100,
      bufferSize = 512,
      inputs = 0,
      outputs = 2
    )
    
    // Note: This would be implemented as:
    // val audio = new PortAudioModule(audioConfig)
    // For now, this is a conceptual example
    // val audio: AudioModule = ???
    
    // Add modules to runtime
    // runtime.addModule(graphics)
    // runtime.addModule(audio)
    
    // Subscribe to window events
    runtime.subscribe[WindowCreated] { event =>
      println(s"Window created: ${event.width}x${event.height}")
    }
    
    runtime.subscribe[WindowResized] { event =>
      println(s"Window resized: ${event.width}x${event.height}")
    }
    
    // Subscribe to input events
    runtime.subscribe[KeyPressed] { event =>
      println(s"Key pressed: ${event.key.name}")
      if (event.key == Key.ESCAPE) {
        println("Escape pressed, stopping runtime...")
        runtime.stop()
      }
    }
    
    runtime.subscribe[MouseMoved] { event =>
      // Handle mouse movement
      // println(s"Mouse moved: (${event.x}, ${event.y})")
    }
    
    // Subscribe to module lifecycle events
    runtime.subscribe[ModuleStarted] { event =>
      println(s"Module started: ${event.module.name}")
    }
    
    // Setup graphics callbacks
    // graphics.onDraw = { g =>
    //   g.clear(Color.blue)
    //   // Draw meshes, shaders, etc.
    // }
    
    // graphics.onUpdate = { dt =>
    //   // Update game state, animations, etc.
    // }
    
    // Setup audio callbacks
    // val oscillator = new Oscillator(440f) // 440 Hz sine wave
    // audio.onAudioIO = { io =>
    //   while(io()) {
    //     val sample = oscillator()
    //     io.setOutput(0)(sample)
    //     io.setOutput(1)(sample)
    //   }
    // }
    
    // Create a window
    // val window = graphics.createWindow(
    //   WindowConfig(
    //     title = "Seer Example",
    //     width = 800,
    //     height = 600,
    //     resizable = true
    //   )
    // )
    
    // Run the runtime
    // runtime.run()
    
    println("Example usage file created. Backend implementations needed.")
  }
}
