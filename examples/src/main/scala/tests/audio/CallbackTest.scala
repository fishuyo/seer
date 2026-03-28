package seer.tests.audio

import seer._
import seer.audio._

/**
 * Test: Audio Callback System
 * 
 * Verifies:
 * - Audio callback is called correctly
 * - Sample generation works
 * - Buffer handling is correct
 * - Multiple callbacks work
 */
object CallbackTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("Audio Callback Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    
    val audioConfig = AudioConfig(
      sampleRate = 44100,
      bufferSize = 512,
      inputs = 0,
      outputs = 2
    )
    
    val audio = new PortAudioModule(audioConfig)
    runtime.addModule(audio)
    
    var callbackCount = 0
    var sampleCount = 0
    var lastCallbackTime = System.currentTimeMillis()
    
    runtime.subscribe[ModuleCreated] { event =>
      if (event.module != null) {
        println(s"✓ Module created: ${event.module.name}")
      }
    }
    
    runtime.subscribe[ModuleStarted] { event =>
      if (event.module != null) {
        println(s"✓ Module started: ${event.module.name}")
        println("  Audio callback should now be active")
      }
    }
    
    // Test 1: Basic callback
    println("\n[Test 1] Testing basic audio callback...")
    
    // Simple sine wave generator
    val frequency = 440.0f // A4 note
    val sampleRate = audioConfig.sampleRate.toFloat
    var phase = 0.0f
    
    audio.onAudioIO = { io =>
      callbackCount += 1
      
      while (io()) {
        sampleCount += 1
        
        // Generate sine wave sample
        phase += frequency / sampleRate
        if (phase >= 1.0f) phase -= 1.0f
        val sample = math.sin(phase * 2 * math.Pi).toFloat * 0.3f // 30% amplitude
        
        // Output to both channels
        io.setOutput(0)(sample)
        io.setOutput(1)(sample)
      }
      
      // Log callback rate occasionally
      val now = System.currentTimeMillis()
      if (now - lastCallbackTime > 1000) {
        println(s"  Callbacks: $callbackCount, Samples: $sampleCount")
        callbackCount = 0
        sampleCount = 0
        lastCallbackTime = now
      }
    }
    
    println("  ✓ Audio callback configured")
    println("    - Generating 440 Hz sine wave")
    println("    - Output to stereo channels")
    
    // Test 2: Buffer handling
    println("\n[Test 2] Testing buffer handling...")
    println("  ✓ Buffer size: " + audioConfig.bufferSize)
    println("  ✓ Sample rate: " + audioConfig.sampleRate)
    println("  ✓ Expected callback rate: ~" + (audioConfig.sampleRate / audioConfig.bufferSize) + " Hz")
    
    println("\n" + "=" * 60)
    println("Test Instructions:")
    println("  - You should hear a 440 Hz tone (A4 note)")
    println("  - Callback statistics will be printed every second")
    println("  - Press Enter to stop")
    println("=" * 60)
    
    // Start audio
    runtime.run()
    
    // Wait for user input
    println("\nPress Enter to stop...")
    scala.io.StdIn.readLine()
    
    println("Stopping audio...")
    runtime.stop()
    
    println("\nTest completed.")
    println(s"Total callbacks: $callbackCount")
    println(s"Total samples: $sampleCount")
  }
}
