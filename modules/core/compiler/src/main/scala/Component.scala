package seer.compiler

import scala.reflect.ClassTag

/**
 * Base trait for all components that can be compiled and hot-swapped
 */
trait Component {
  def id: String
  def version: String
}

/**
 * Result of a compilation attempt
 */
sealed trait CompilationResult
case class CompilationSuccess[T <: Component](component: T) extends CompilationResult
case class CompilationError(errors: List[String]) extends CompilationResult

/**
 * Registry for managing components
 */
trait ComponentRegistry {
  def register[T <: Component](component: T): Unit
  def get[T <: Component](id: String)(implicit ct: ClassTag[T]): Option[T]
  def update[T <: Component](id: String, newComponent: T): Unit
  def remove(id: String): Unit
  def list: Map[String, Component]
}

/**
 * Simple in-memory implementation of ComponentRegistry
 */
class InMemoryComponentRegistry extends ComponentRegistry {
  private val components = collection.mutable.HashMap[String, Component]()
  
  def register[T <: Component](component: T): Unit = {
    components(component.id) = component
  }
  
  def get[T <: Component](id: String)(implicit ct: ClassTag[T]): Option[T] = {
    components.get(id).flatMap { comp =>
      if (ct.runtimeClass.isInstance(comp)) Some(comp.asInstanceOf[T])
      else None
    }
  }
  
  def update[T <: Component](id: String, newComponent: T): Unit = {
    if (components.contains(id)) {
      components(id) = newComponent
    } else {
      throw new IllegalArgumentException(s"No component found with id: $id")
    }
  }
  
  def remove(id: String): Unit = {
    components.remove(id)
  }
  
  def list: Map[String, Component] = components.toMap
} 