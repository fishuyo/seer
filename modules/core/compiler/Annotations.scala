// package seer.compiler

// import scala.annotation.StaticAnnotation
// import org.apache.pekko.actor.ActorRef

// /**
//  * Annotation to mark a class as live-compilable
//  * Usage: @live class MyClass { ... }
//  */
// class live extends StaticAnnotation

// /**
//  * Annotation to mark a field as a live reference
//  * Usage: @liveref var myComponent: Any = _
//  * Usage: @liveref var myActor: ActorRef = _
//  */
// class liveref extends StaticAnnotation

// /**
//  * Helper object for accessing live references
//  * This provides a simpler API than using the registry directly
//  */
// object LiveRefs {
//   private var compiler: Option[LiveCompiler] = None
  
//   def setCompiler(c: LiveCompiler): Unit = {
//     compiler = Some(c)
//   }
  
//   def getDynamic(className: String): Option[Any] = {
//     compiler.flatMap(_.getDynamic(className))
//   }
  
//   def getActorRef(className: String): Option[ActorRef] = {
//     compiler.flatMap(_.getActorRef(className))
//   }
  
//   def list: Map[String, LiveClass] = {
//     compiler.map(_.registry.list).getOrElse(Map.empty)
//   }
// }

// /**
//  * Example of how @liveref would be used
//  * Note: This is just for documentation - the actual implementation
//  * would require macro processing to expand the annotations
//  */
// object LiveRefExample {
//   // This is what the user would write:
//   /*
//   @live class MyBehavior {
//     def update() = println("behavior updated")
//   }
  
//   @live class MyActor extends Actor {
//     def receive = {
//       case "update" => println("actor updated")
//     }
//   }
  
//   @live class AnotherBehavior {
//     @liveref var myBehavior: Any = _
//     @liveref var myActor: ActorRef = _
    
//     def doSomething() = {
//       // Use reflection to call methods on myBehavior
//       val updateMethod = myBehavior.getClass.getMethod("update")
//       updateMethod.invoke(myBehavior)
//       myActor ! "update"           // Actor message
//     }
//   }
//   */
  
//   // This is what it would expand to (approximately):
//   /*
//   @live class AnotherBehavior {
//     private var _myBehavior: Any = _
//     private var _myActor: ActorRef = _
    
//     def myBehavior: Any = {
//       if (_myBehavior == null) {
//         _myBehavior = LiveRefs.getDynamic("MyBehavior").get
//       }
//       _myBehavior
//     }
    
//     def myBehavior_=(value: Any): Unit = _myBehavior = value
    
//     def myActor: ActorRef = {
//       if (_myActor == null) {
//         _myActor = LiveRefs.getActorRef("MyActor").get
//       }
//       _myActor
//     }
    
//     def myActor_=(value: ActorRef): Unit = _myActor = value
    
//     def doSomething() = {
//       val updateMethod = myBehavior.getClass.getMethod("update")
//       updateMethod.invoke(myBehavior)
//       myActor ! "update"
//     }
//   }
//   */
// } 