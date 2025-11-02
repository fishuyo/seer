// package seer.compiler

// import scala.reflect.ClassTag

// /**
//  * Base trait for all runtime-compilable scripts
//  */
// trait Script {
//   def id: String
//   def version: String
// }

// /**
//  * A component that can be attached to entities
//  */
// trait Component extends Script {
//   def attach(entity: Entity): Unit
//   def detach(entity: Entity): Unit
// }

// /**
//  * An entity that can have multiple components
//  */
// trait Entity {
//   def id: String
//   def components: Set[Component]
//   def addComponent(component: Component): Unit
//   def removeComponent(componentId: String): Unit
//   def getComponent[T <: Component](componentId: String)(implicit ct: ClassTag[T]): Option[T]
// }

// /**
//  * Result of a compilation attempt
//  */
// sealed trait CompilationResult
// case class CompilationSuccess[T <: Script](script: T) extends CompilationResult
// case class CompilationError(errors: List[String]) extends CompilationResult

// /**
//  * Registry for managing scripts
//  */
// trait ScriptRegistry {
//   def register[T <: Script](script: T): Unit
//   def get[T <: Script](id: String)(implicit ct: ClassTag[T]): Option[T]
//   def update[T <: Script](id: String, newScript: T): Unit
//   def remove(id: String): Unit
//   def list: Map[String, Script]
// }

// /**
//  * Simple in-memory implementation of ScriptRegistry
//  */
// class InMemoryScriptRegistry extends ScriptRegistry {
//   private val scripts = collection.mutable.HashMap[String, Script]()
  
//   def register[T <: Script](script: T): Unit = {
//     scripts(script.id) = script
//   }
  
//   def get[T <: Script](id: String)(implicit ct: ClassTag[T]): Option[T] = {
//     scripts.get(id).flatMap { script =>
//       if (ct.runtimeClass.isInstance(script)) Some(script.asInstanceOf[T])
//       else None
//     }
//   }
  
//   def update[T <: Script](id: String, newScript: T): Unit = {
//     if (scripts.contains(id)) {
//       scripts(id) = newScript
//     } else {
//       throw new IllegalArgumentException(s"No script found with id: $id")
//     }
//   }
  
//   def remove(id: String): Unit = {
//     scripts.remove(id)
//   }
  
//   def list: Map[String, Script] = scripts.toMap
// }

// /**
//  * Simple implementation of Entity
//  */
// class SimpleEntity(
//   override val id: String,
//   private val components: collection.mutable.Set[Component] = collection.mutable.Set.empty
// ) extends Entity {
  
//   override def addComponent(component: Component): Unit = {
//     components.add(component)
//     component.attach(this)
//   }
  
//   override def removeComponent(componentId: String): Unit = {
//     components.find(_.id == componentId).foreach { component =>
//       component.detach(this)
//       components.remove(component)
//     }
//   }
  
//   override def getComponent[T <: Component](componentId: String)(implicit ct: ClassTag[T]): Option[T] = {
//     components.find(_.id == componentId).flatMap { comp =>
//       if (ct.runtimeClass.isInstance(comp)) Some(comp.asInstanceOf[T])
//       else None
//     }
//   }
// }