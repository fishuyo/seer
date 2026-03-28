package seer.compiler

import java.io.File
import java.nio.file.Paths

import com.eed3si9n.eval._

/**
 * Example component that can be compiled and hot-swapped
 */
class ExampleComponent(
  // override val id: String,
  // override val version: String,
  var state: Int = 0
) extends Component {
  def id: String = "example-1"
  def version: String = "1.0"

  def increment(): Int = {
    state += 1
    state
  }
  
  def getState: Int = state
}

/**
 * Example usage of the compiler service
 */
object ExampleComponent {
  def main(args: Array[String]): Unit = {
    // Create a compiler service
    val compiler = CompilerService()
    
    // Example source code for a component
    val source = """ 
      val component = new seer.compiler.ExampleComponent(
        // id = "example-1",
        // version = "1.0",
        state = 42
      )
      
      component.increment()
      component
    """

    // val eval = Eval(
    //   backingDir = Paths.get("/tmp/classes"),
    //   mkReporter = () => EvalReporter.store
    // )

    val test = """
      var i = 42
      i += 1
      println(s"hello world: $i")
      i
    """

    // val result = eval.evalInfer(test)
    // val result = seer.compiler.Compiler.eval[Int](test)
    // println(result)
    
    // Compile and register the component
    compiler.compileComponent[ExampleComponent](source, "example-1", "1.0") match {
      case CompilationSuccess(component: ExampleComponent) =>
        println(s"Component compiled successfully!")
        println(s"Initial state: ${component.getState}")
        println(s"Incremented: ${component.increment()}")
        
      case CompilationSuccess(_) =>
        println("Component compiled but is not an ExampleComponent")
        
      case CompilationError(errors) =>
        println("Compilation failed:")
        errors.foreach(println)
    }
    
    // // Example of watching a directory for changes
    // val componentDir = new File("components")
    // if (componentDir.exists()) {
    //   compiler.watchDirectory[ExampleComponent](
    //     dir = componentDir,
    //     id = "example-1",
    //     version = "1.0"
    //   ) { result =>
    //     result match {
    //       case CompilationSuccess(component: ExampleComponent) =>
    //         println(s"Component updated! New state: ${component.getState}")
    //       case CompilationSuccess(_) =>
    //         println("Component updated but is not an ExampleComponent")
    //       case CompilationError(errors) =>
    //         println("Update failed:")
    //         errors.foreach(println)
    //     }
    //   }
    // }
  }
} 
