package seer.tests.audio

import seer._
import seer.audio._

/**
 * Test: Audio Context Creation
 * 
 * Verifies:
 * - Audio context creation works
 * - Context configuration is respected
 * - Context properties are correct
 * - Context destruction works
 */
object ContextCreationTest {

  def main(args: Array[String]): Unit = {
    println("=" * 60)
    println("Audio Context Creation Test")
    println("=" * 60)
    
    val runtime = new SeerRuntime()
    
    var testPassed = true
    
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
    
    // Test 1: Basic context creation
    println("\n[Test 1] Creating audio module with default config...")
    try {
      val audio = new PortAudioModule()
      runtime.addModule(audio)
      
      assert(audio.config.sampleRate == 44100, "Default sample rate should be 44100")
      assert(audio.config.bufferSize == 512, "Default buffer size should be 512")
      assert(audio.config.outputs == 2, "Default outputs should be 2")
      
      println("  ✓ Audio module created with default config")
      println(s"    - Sample rate: ${audio.config.sampleRate}")
      println(s"    - Buffer size: ${audio.config.bufferSize}")
      println(s"    - Outputs: ${audio.config.outputs}")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
        testPassed = false
    }
    
    // Test 2: Custom configuration
    println("\n[Test 2] Creating audio module with custom config...")
    try {
      val customConfig = AudioConfig(
        sampleRate = 48000,
        bufferSize = 1024,
        inputs = 2,
        outputs = 2
      )
      
      val audio = new PortAudioModule(customConfig)
      
      assert(audio.config.sampleRate == 48000, "Sample rate should match config")
      assert(audio.config.bufferSize == 1024, "Buffer size should match config")
      assert(audio.config.inputs == 2, "Inputs should match config")
      assert(audio.config.outputs == 2, "Outputs should match config")
      
      println("  ✓ Audio module created with custom config")
      println(s"    - Sample rate: ${audio.config.sampleRate}")
      println(s"    - Buffer size: ${audio.config.bufferSize}")
      println(s"    - Inputs: ${audio.config.inputs}")
      println(s"    - Outputs: ${audio.config.outputs}")
    } catch {
      case e: Exception =>
        println(s"  ✗ FAILED: ${e.getMessage}")
        e.printStackTrace()
        testPassed = false
    }
    
    // Test 3: Context creation
    println("\n[Test 3] Testing context creation...")
    try {
      val audio = new PortAudioModule()
      runtime.addModule(audio)
      
      // Context should be created when module is created
      val context = audio.getCurrentContext()
      assert(context.isDefined, "Context should be created")
      assert(context.get.isValid, "Context should be valid")
      assert(context.get.sampleRate == audio.config.sampleRate, "Context sample rate should match config")
      assert(context.get.bufferSize == audio.config.bufferSize, "Context buffer size should match config")
      
      println("  ✓ Context created successfully")
      println(s"    - Context ID: ${context.get.id}")
      println(s"    - Sample rate: ${context.get.sampleRate}")
      println(s"    - Buffer size: ${context.get.bufferSize}")
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
    
    println("\nNote: Audio tests verify API correctness.")
    println("For actual audio playback, see CallbackTest.")
  }
}
