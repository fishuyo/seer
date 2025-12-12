// package seer.compiler

// import java.nio.file.Path
// import scala.reflect.ClassTag
// import org.apache.pekko.actor._

// /**
//  * Represents a live-compiled class
//  */
// case class LiveClass(
//   className: String,        // e.g., "blackjajqeiupq.MyBehavior"
//   compiledClass: Class[_],  // The actual compiled class
//   sourceCode: String,
//   filePath: Path,
//   lastModified: Long,
//   isActor: Boolean = false  // Whether this class extends Actor
// )

// /**
//  * Result of a live compilation attempt
//  */
// sealed trait LiveCompilationResult
// case class LiveCompilationSuccess(liveClass: LiveClass) extends LiveCompilationResult
// case class LiveCompilationError(errors: List[String]) extends LiveCompilationResult

// /**
//  * Registry for managing live classes
//  */
// trait LiveRegistry {
//   def register(liveClass: LiveClass): Unit
//   def get(className: String): Option[LiveClass]
//   def update(liveClass: LiveClass): Unit
//   def remove(className: String): Unit
//   def list: Map[String, LiveClass]
  
//   // Dynamic and ActorRef access
//   def getDynamic(className: String): Option[Any]
//   def getActorRef(className: String): Option[ActorRef]
//   def instantiate(className: String): Option[Any]
// }

// /**
//  * Simple in-memory implementation of LiveRegistry
//  */
// class InMemoryLiveRegistry extends LiveRegistry {
//   private val classes = collection.mutable.HashMap[String, LiveClass]()
//   private val actorRefs = collection.mutable.HashMap[String, ActorRef]()
  
//   def register(liveClass: LiveClass): Unit = {
//     classes(liveClass.className) = liveClass
//   }
  
//   def get(className: String): Option[LiveClass] = {
//     classes.get(className)
//   }
  
//   def update(liveClass: LiveClass): Unit = {
//     classes(liveClass.className) = liveClass
    
//     // If it's an actor, kill the old one and create new one
//     if (liveClass.isActor) {
//       actorRefs.get(liveClass.className).foreach { oldRef =>
//         oldRef ! PoisonPill
//       }
//       actorRefs.remove(liveClass.className)
//     }
//   }
  
//   def remove(className: String): Unit = {
//     // Kill actor if it exists
//     actorRefs.get(className).foreach { ref =>
//       ref ! PoisonPill
//     }
//     actorRefs.remove(className)
//     classes.remove(className)
//   }
  
//   def list: Map[String, LiveClass] = classes.toMap
  
//   def getDynamic(className: String): Option[Any] = {
//     classes.get(className).map { liveClass =>
//       val instance = instantiate(liveClass)
//       instance
//     }
//   }
  
//   def getActorRef(className: String): Option[ActorRef] = {
//     // Check if we already have an actor ref
//     actorRefs.get(className).orElse {
//       classes.get(className).flatMap { liveClass =>
//         if (liveClass.isActor) {
//           // Create new actor instance
//           val actorRef = System().actorOf(Props(liveClass.compiledClass), className)
//           actorRefs(className) = actorRef
//           Some(actorRef)
//         } else {
//           None
//         }
//       }
//     }
//   }
  
//   def instantiate(className: String): Option[Any] = {
//     classes.get(className).map(instantiate)
//   }
  
//   private def instantiate(liveClass: LiveClass): Any = {
//     liveClass.compiledClass.getDeclaredConstructor().newInstance()
//   }
// } 