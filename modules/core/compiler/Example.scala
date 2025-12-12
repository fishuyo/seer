// package seer.compiler

// import java.nio.file.Paths
// import org.apache.pekko.actor._
// import scala.concurrent.ExecutionContext

// /**
//  * Example live classes
//  * These would be in separate files in a real project
//  */
// object ExampleLiveClasses {
//   // Example 1: Simple behavior class
//   @live class Renderer {
//     def render(frame: Int) = println(s"rendering frame $frame")
//     def setColor(color: String) = println(s"setting color to $color")
//   }
  
//   // Example 2: Actor class
//   @live class AudioProcessor extends Actor {
//     def receive = {
//       case "play" => println("playing audio")
//       case "stop" => println("stopping audio")
//       case "volume" => println("adjusting volume")
//       case msg => println(s"unknown audio message: $msg")
//     }
//   }
  
//   // Example 3: Class that references other live classes using working macros
//   @live class GameLoop {
//     def update() = {
//       val renderer = LiveRefMacros.liveDynamic("Renderer")
//       val audio = LiveRefMacros.liveActor("AudioProcessor")
      
//       // Use reflection to call methods
//       val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
//       renderMethod.invoke(renderer, 42.asInstanceOf[AnyRef])
      
//       val setColorMethod = renderer.getClass.getMethod("setColor", classOf[String])
//       setColorMethod.invoke(renderer, "red".asInstanceOf[AnyRef])
      
//       audio ! "play"            // Actor message
//     }
    
//     def stop() = {
//       val audio = LiveRefMacros.liveActor("AudioProcessor")
//       audio ! "stop"
//     }
//   }
  
//   // Example 4: Class using manual live references (extends LiveRefSupport)
//   @live class GameLoopManual extends LiveRefSupport {
//     private var _renderer: Any = _
//     private var _audio: ActorRef = _
    
//     def renderer: Any = {
//       if (_renderer == null) {
//         _renderer = getLiveDynamic("Renderer")
//       }
//       _renderer
//     }
    
//     def audio: ActorRef = {
//       if (_audio == null) {
//         _audio = getLiveActorRef("AudioProcessor")
//       }
//       _audio
//     }
    
//     def update() = {
//       // Use reflection to call methods
//       val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
//       renderMethod.invoke(renderer, 42.asInstanceOf[AnyRef])
      
//       val setColorMethod = renderer.getClass.getMethod("setColor", classOf[String])
//       setColorMethod.invoke(renderer, "red".asInstanceOf[AnyRef])
      
//       audio ! "play"
//     }
    
//     def stop() = {
//       audio ! "stop"
//     }
//   }
  
//   // Example 5: Class using the new Scala 3 @liveref annotation
//   @live class GameLoopScala3 {
//     @liveref var renderer: Any = _
//     @liveref var audio: ActorRef = _
    
//     def update() = {
//       // Use reflection to call methods on the dynamic object
//       val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
//       renderMethod.invoke(renderer, 42.asInstanceOf[AnyRef])
      
//       val setColorMethod = renderer.getClass.getMethod("setColor", classOf[String])
//       setColorMethod.invoke(renderer, "red".asInstanceOf[AnyRef])
      
//       audio ! "play"            // Actor message
//     }
    
//     def stop() = {
//       audio ! "stop"
//     }
//   }
// }

// /**
//  * Example usage of the live compiler
//  */
// object Example {
//   def main(args: Array[String]): Unit = {
//     // Set up the live compiler
//     val sourceRoot = Paths.get("src/main/scala")  // Adjust path as needed
//     val compiler = LiveCompiler(sourceRoot)
    
//     // Set the compiler in LiveRefs for @liveref annotations
//     LiveRefs.setCompiler(compiler)
    
//     // Wait a moment for compilation to complete
//     Thread.sleep(1000)
    
//     // Test dynamic references
//     println("Testing dynamic references:")
//     compiler.getDynamic("Renderer").foreach { renderer =>
//       val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
//       renderMethod.invoke(renderer, 1.asInstanceOf[AnyRef])
      
//       val setColorMethod = renderer.getClass.getMethod("setColor", classOf[String])
//       setColorMethod.invoke(renderer, "blue".asInstanceOf[AnyRef])
//     }
    
//     // Test actor references
//     println("\nTesting actor references:")
//     compiler.getActorRef("AudioProcessor").foreach { audio =>
//       audio ! "play"
//       audio ! "volume"
//       audio ! "stop"
//     }
    
//     // Test classes that use live references
//     println("\nTesting GameLoop with macros:")
//     compiler.getDynamic("GameLoop").foreach { gameLoop =>
//       val updateMethod = gameLoop.getClass.getMethod("update")
//       updateMethod.invoke(gameLoop)
      
//       val stopMethod = gameLoop.getClass.getMethod("stop")
//       stopMethod.invoke(gameLoop)
//     }
    
//     println("\nTesting GameLoopManual with manual references:")
//     compiler.getDynamic("GameLoopManual").foreach { gameLoop =>
//       val updateMethod = gameLoop.getClass.getMethod("update")
//       updateMethod.invoke(gameLoop)
      
//       val stopMethod = gameLoop.getClass.getMethod("stop")
//       stopMethod.invoke(gameLoop)
//     }
    
//     println("\nTesting GameLoopScala3 with @liveref annotation:")
//     compiler.getDynamic("GameLoopScala3").foreach { gameLoop =>
//       val updateMethod = gameLoop.getClass.getMethod("update")
//       updateMethod.invoke(gameLoop)
      
//       val stopMethod = gameLoop.getClass.getMethod("stop")
//       stopMethod.invoke(gameLoop)
//     }
    
//     // List all live classes
//     println("\nAll live classes:")
//     compiler.registry.list.foreach { case (name, liveClass) =>
//       println(s"  $name (actor: ${liveClass.isActor})")
//     }
    
//     // Keep the system running to test hot-swapping
//     println("\nSystem running. Edit live class files to test hot-swapping...")
//     println("Press Ctrl+C to exit")
    
//     // Keep the main thread alive
//     while (true) {
//       Thread.sleep(1000)
//     }
//   }
// }

// /**
//  * Manual usage example (without @liveref macro)
//  */
// object ManualExample {
//   def main(args: Array[String]): Unit = {
//     val sourceRoot = Paths.get("src/main/scala")
//     val compiler = LiveCompiler(sourceRoot)
    
//     // Wait for compilation
//     Thread.sleep(1000)
    
//     // Manual dynamic access
//     val renderer = compiler.getDynamic("Renderer")
//     renderer.foreach { r =>
//       val renderMethod = r.getClass.getMethod("render", classOf[Int])
//       renderMethod.invoke(r, 100.asInstanceOf[AnyRef])
//     }
    
//     // Manual actor access
//     val audio = compiler.getActorRef("AudioProcessor")
//     audio.foreach { a =>
//       a ! "play"
//     }
//   }
// }

// /**
//  * Example showing different ways to use live references
//  */
// object LiveRefUsageExamples {
//   def main(args: Array[String]): Unit = {
//     val sourceRoot = Paths.get("src/main/scala")
//     val compiler = LiveCompiler(sourceRoot)
//     LiveRefs.setCompiler(compiler)
    
//     Thread.sleep(1000)
    
//     println("Different ways to use live references:")
    
//     // Method 1: Direct compiler access
//     println("\n1. Direct compiler access:")
//     compiler.getDynamic("Renderer").foreach { renderer =>
//       val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
//       renderMethod.invoke(renderer, 1.asInstanceOf[AnyRef])
//     }
    
//     // Method 2: Using LiveRefs helper
//     println("\n2. Using LiveRefs helper:")
//     LiveRefs.getDynamic("Renderer").foreach { renderer =>
//       val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
//       renderMethod.invoke(renderer, 2.asInstanceOf[AnyRef])
//     }
    
//     // Method 3: Using macros (in live classes)
//     println("\n3. Using macros (tested via GameLoop):")
//     compiler.getDynamic("GameLoop").foreach { gameLoop =>
//       val updateMethod = gameLoop.getClass.getMethod("update")
//       updateMethod.invoke(gameLoop)
//     }
    
//     // Method 4: Manual implementation with LiveRefSupport
//     println("\n4. Manual implementation:")
//     compiler.getDynamic("GameLoopManual").foreach { gameLoop =>
//       val updateMethod = gameLoop.getClass.getMethod("update")
//       updateMethod.invoke(gameLoop)
//     }
    
//     // Method 5: Scala 3 @liveref annotation
//     println("\n5. Scala 3 @liveref annotation:")
//     compiler.getDynamic("GameLoopScala3").foreach { gameLoop =>
//       val updateMethod = gameLoop.getClass.getMethod("update")
//       updateMethod.invoke(gameLoop)
//     }
//   }
// } 