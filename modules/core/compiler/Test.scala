// package seer.compiler

// import java.nio.file.Paths
// import org.apache.pekko.actor._

// /**
//  * Simple test to verify the macro implementation
//  */
// object Test {
//   def main(args: Array[String]): Unit = {
//     println("Testing live compiler with macros...")
    
//     // Test the macros directly
//     println("\nTesting macros directly:")
//     try {
//       // These will fail because no compiler is set up yet, but they should compile
//       val dynamicTest = LiveRefMacros.liveDynamic("TestClass")
//       val actorTest = LiveRefMacros.liveActor("TestActor")
//       println("Macros compiled successfully!")
//     } catch {
//       case e: Exception =>
//         println(s"Macro test failed (expected): ${e.getMessage}")
//     }
    
//     // Test the LiveRefSupport trait
//     println("\nTesting LiveRefSupport trait:")
//     val support = new LiveRefSupport {}
//     try {
//       val dynamic = support.getLiveDynamic("TestClass")
//       println("LiveRefSupport compiled successfully!")
//     } catch {
//       case e: Exception =>
//         println(s"LiveRefSupport test failed (expected): ${e.getMessage}")
//     }
    
//     println("\nAll tests completed!")
//   }
// }

// /**
//  * Test class that uses the macros
//  */
// @live class TestClass {
//   def test() = {
//     val renderer = LiveRefMacros.liveDynamic("Renderer")
//     val audio = LiveRefMacros.liveActor("AudioProcessor")
    
//     // Use reflection to call methods
//     val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
//     renderMethod.invoke(renderer, 1.asInstanceOf[AnyRef])
    
//     audio ! "test"
//   }
// } 