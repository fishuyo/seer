package seer.compiler

import scala.annotation.StaticAnnotation
import scala.quoted.*
import org.apache.pekko.actor.ActorRef

/**
 * Scala 3 macro annotation to expand @liveref fields into registry lookups
 * Usage: @liveref var myComponent: Any = _
 */
class liveref extends StaticAnnotation {
  inline def apply(annottees: Any*): Any = ${ LiveRefMacro.impl }
}

object LiveRefMacro {
  def impl(using Quotes)(annottees: Expr[Any]*): Expr[Any] = {
    import quotes.reflect.*
    
    // Extract the annotated field
    val inputs = annottees.map(_.asTerm).toList
    
    inputs match {
      case List(field: ValDef) =>
        // Extract field information
        val fieldName = field.name
        val fieldType = field.tpt.tpe
        val className = getEnclosingClassName(using quotes)
        
        // Generate the expanded code
        generateExpandedCode(using quotes)(className, fieldName, fieldType)
        
      case _ =>
        report.error("@liveref can only be applied to var fields")
        annottees.head
    }
  }
  
  private def getEnclosingClassName(using Quotes): String = {
    import quotes.reflect.*
    
    var current = Symbol.spliceOwner
    while (current != null && !current.isClassDef) {
      current = current.owner
    }
    
    if (current == null) {
      report.error("@liveref must be used inside a class")
      "Unknown"
    } else {
      current.name
    }
  }
  
  private def generateExpandedCode(using Quotes)(className: String, fieldName: String, fieldType: TypeRepr): Expr[Any] = {
    import quotes.reflect.*
    
    // Determine the registry method based on field type
    val registryMethod = fieldType.asType match {
      case '[ActorRef] => "getActorRef"
      case '[Any] => "getDynamic"
      case _ => 
        report.error(s"@liveref field must be of type Any or ActorRef, got: ${fieldType.show}")
        "getDynamic"
    }
    
    // Generate the expanded code
    val privateFieldName = s"_$fieldName"
    val privateField = ValDef(
      Symbol.newVal(Symbol.spliceOwner, privateFieldName, fieldType, Flags.Private, Symbol.noSymbol),
      Some(Literal(UnitConstant()))
    ).asInstanceOf[Statement]
    
    val getter = DefDef(
      Symbol.newMethod(Symbol.spliceOwner, fieldName, MethodType(Nil)(_ => Nil, _ => fieldType)),
      params => Some {
        If(
          Select(Ident(Symbol.requiredVal(privateFieldName)), "==").appliedTo(Literal(NullConstant())),
          Assign(
            Ident(Symbol.requiredVal(privateFieldName)),
            Select(
              Select(Ident(Symbol.requiredModule("seer.compiler.LiveRefs")), registryMethod),
              "apply"
            ).appliedTo(Literal(StringConstant(className))).appliedTo(Select(Ident(Symbol.requiredModule("scala.Option")), "get"))
          ),
          Literal(UnitConstant())
        )
        Select(Ident(Symbol.requiredVal(privateFieldName)), fieldName)
      }
    ).asInstanceOf[Statement]
    
    val setter = DefDef(
      Symbol.newMethod(Symbol.spliceOwner, s"${fieldName}_=", MethodType(List("value"))(t => List(t.head), _ => TypeRepr.of[Unit])),
      params => Some {
        Assign(Ident(Symbol.requiredVal(privateFieldName)), params.head.head)
      }
    ).asInstanceOf[Statement]
    
    Block(List(privateField, getter, setter), Literal(UnitConstant())).asExpr
  }
}

/**
 * Alternative approach: Use a regular annotation with runtime processing
 * This is more reliable than macros for now
 */
class liverefRuntime extends StaticAnnotation

/**
 * Runtime processor for @liverefRuntime annotations
 * This processes the annotations at runtime instead of compile time
 */
object LiveRefProcessor {
  def processClass[T](obj: T): T = {
    // Use reflection to find @liverefRuntime fields and replace them
    val clazz = obj.getClass
    
    // This is a simplified implementation
    // In practice, you'd use reflection to:
    // 1. Find all fields with @liverefRuntime annotation
    // 2. Replace them with proxy objects that do registry lookups
    // 3. Handle both Dynamic and ActorRef types
    
    obj
  }
}

/**
 * Manual implementation of live references
 * This provides the same functionality as the macro would, but manually
 */
trait LiveRefSupport {
  protected def getLiveDynamic(className: String): Any = {
    LiveRefs.getDynamic(className).getOrElse {
      throw new RuntimeException(s"Live class not found: $className")
    }
  }
  
  protected def getLiveActorRef(className: String): ActorRef = {
    LiveRefs.getActorRef(className).getOrElse {
      throw new RuntimeException(s"Live actor not found: $className")
    }
  }
}

/**
 * Example of how to use manual live references
 */
object ManualLiveRefExample {
  @live class GameLoop extends LiveRefSupport {
    // Manual implementation of what @liveref would generate
    private var _renderer: Any = _
    private var _audio: ActorRef = _
    
    def renderer: Any = {
      if (_renderer == null) {
        _renderer = getLiveDynamic("Renderer")
      }
      _renderer
    }
    
    def audio: ActorRef = {
      if (_audio == null) {
        _audio = getLiveActorRef("AudioProcessor")
      }
      _audio
    }
    
    def update() = {
      // Use reflection to call methods on the dynamic object
      val rendererObj = renderer
      val renderMethod = rendererObj.getClass.getMethod("render", classOf[Int])
      renderMethod.invoke(rendererObj, 42.asInstanceOf[AnyRef])
      
      val setColorMethod = rendererObj.getClass.getMethod("setColor", classOf[String])
      setColorMethod.invoke(rendererObj, "red".asInstanceOf[AnyRef])
      
      audio ! "play"
    }
    
    def stop() = {
      audio ! "stop"
    }
  }
}

/**
 * Working macro implementation using a different approach
 * Instead of macro annotations, we'll use regular macros
 */
object LiveRefMacros {
  /**
   * Macro to create a dynamic reference
   * Usage: val myComponent = liveDynamic("MyComponent")
   */
  inline def liveDynamic(className: String): Any = ${ liveDynamicImpl('className) }
  
  def liveDynamicImpl(className: Expr[String])(using Quotes): Expr[Any] = {
    '{ LiveRefs.getDynamic($className).get }
  }
  
  /**
   * Macro to create an actor reference
   * Usage: val myActor = liveActor("MyActor")
   */
  inline def liveActor(className: String): ActorRef = ${ liveActorImpl('className) }
  
  def liveActorImpl(className: Expr[String])(using Quotes): Expr[ActorRef] = {
    '{ LiveRefs.getActorRef($className).get }
  }
}

/**
 * Example using the working macros
 */
object WorkingMacroExample {
  @live class GameLoop {
    // Using the working macros instead of @liveref
    def update() = {
      val renderer = LiveRefMacros.liveDynamic("Renderer")
      val audio = LiveRefMacros.liveActor("AudioProcessor")
      
      // Use reflection to call methods
      val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
      renderMethod.invoke(renderer, 42.asInstanceOf[AnyRef])
      
      val setColorMethod = renderer.getClass.getMethod("setColor", classOf[String])
      setColorMethod.invoke(renderer, "red".asInstanceOf[AnyRef])
      
      audio ! "play"
    }
  }
}

/**
 * Example using the new Scala 3 @liveref annotation
 */
object Scala3LiveRefExample {
  @live class GameLoop {
    @liveref var renderer: Any = _
    @liveref var audio: ActorRef = _
    
    def update() = {
      // Use reflection to call methods on the dynamic object
      val renderMethod = renderer.getClass.getMethod("render", classOf[Int])
      renderMethod.invoke(renderer, 42.asInstanceOf[AnyRef])
      
      val setColorMethod = renderer.getClass.getMethod("setColor", classOf[String])
      setColorMethod.invoke(renderer, "red".asInstanceOf[AnyRef])
      
      audio ! "play"            // Actor message
    }
    
    def stop() = {
      audio ! "stop"
    }
  }
} 