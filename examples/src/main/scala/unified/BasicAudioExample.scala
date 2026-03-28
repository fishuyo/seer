package seer.unified

import seer._
import seer.audio._
import seer.audio.gen._

/**
 * Basic example demonstrating the unified Audio API.
 * 
 * This example shows:
 * - Creating a runtime with audio module
 * - Subscribing to events
 * - Setting up audio callback with generators
 * - Playing a simple tone
 */
object BasicAudioExample {

  def main(args: Array[String]): Unit = {
    
    // Create runtime
    val runtime = new SeerRuntime()
    
    // Create audio module with configuration
    val audioConfig = AudioConfig(
      sampleRate = 44100,
      bufferSize = 512,
      inputs = 0,
      outputs = 2
    )
    
    val audio = new PortAudioModule(audioConfig)
    
    // Add module to runtime
    runtime.addModule(audio)
    
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
    
    // Create a simple oscillator (440 Hz sine wave)
    val frequency = 440.0f // A4 note
    val sampleRate = audioConfig.sampleRate.toFloat
    var phase = 0.0f
    
    // Simple sine wave generator
    def generateSineWave(): Float = {
      phase += frequency / sampleRate
      if (phase >= 1.0f) phase -= 1.0f
      math.sin(phase * 2 * math.Pi).toFloat * 0.3f // 30% amplitude
    }
    
    // Setup audio callback
    audio.onAudioIO = { io =>
      while (io()) {
        // Generate sample
        val sample = generateSineWave()
        
        // Output to both channels (stereo)
        io.setOutput(0)(sample)
        io.setOutput(1)(sample)
      }
    }
    
    println("Starting audio...")
    println("Press Enter to stop")
    
    // Run the runtime
    runtime.run()
    
    // Wait for user input
    scala.io.StdIn.readLine()
    
    println("Stopping runtime...")
    runtime.stop()
    
    println("Runtime stopped.")
  }
}
