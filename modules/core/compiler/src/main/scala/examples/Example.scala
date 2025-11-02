// package seer.compiler

// import java.io.File
// import scala.concurrent.ExecutionContext

// /**
//  * Example component that adds movement behavior to entities
//  */
// class MovementComponent(
//   override val id: String,
//   override val version: String,
//   var speed: Double = 1.0
// ) extends Component {
//   private var entity: Option[Entity] = None
  
//   def attach(e: Entity): Unit = {
//     entity = Some(e)
//     println(s"Movement component attached to entity ${e.id}")
//   }
  
//   def detach(e: Entity): Unit = {
//     if (entity.contains(e)) {
//       entity = None
//       println(s"Movement component detached from entity ${e.id}")
//     }
//   }
  
//   def move(x: Double, y: Double): Unit = {
//     entity.foreach { e =>
//       println(s"Moving entity ${e.id} by ($x, $y) at speed $speed")
//     }
//   }
// }

// /**
//  * Example component that adds rendering behavior to entities
//  */
// class RenderComponent(
//   override val id: String,
//   override val version: String,
//   var visible: Boolean = true
// ) extends Component {
//   private var entity: Option[Entity] = None
  
//   def attach(e: Entity): Unit = {
//     entity = Some(e)
//     println(s"Render component attached to entity ${e.id}")
//   }
  
//   def detach(e: Entity): Unit = {
//     if (entity.contains(e)) {
//       entity = None
//       println(s"Render component detached from entity ${e.id}")
//     }
//   }
  
//   def render(): Unit = {
//     if (visible) {
//       entity.foreach { e =>
//         println(s"Rendering entity ${e.id}")
//       }
//     }
//   }
// }

// /**
//  * Example usage of the script/component/entity system
//  */
// object Example {
//   def main(args: Array[String]): Unit = {
//     // Create a registry
//     val registry = new InMemoryScriptRegistry()
    
//     // Create some components
//     val movement = new MovementComponent("movement", "1.0", speed = 2.0)
//     val render = new RenderComponent("render", "1.0", visible = true)
    
//     // Register components
//     registry.register(movement)
//     registry.register(render)
    
//     // Create an entity
//     val entity = new SimpleEntity("player-1")
    
//     // Add components to entity
//     entity.addComponent(movement)
//     entity.addComponent(render)
    
//     // Use components
//     entity.getComponent[MovementComponent]("movement").foreach { movement =>
//       movement.move(10, 20)
//     }
    
//     entity.getComponent[RenderComponent]("render").foreach { render =>
//       render.render()
//     }
    
//     // Remove a component
//     entity.removeComponent("movement")
    
//     // Try to use removed component
//     entity.getComponent[MovementComponent]("movement") match {
//       case Some(_) => println("Movement component still exists!")
//       case None => println("Movement component was removed successfully")
//     }
//   }
// } 