package seer.compiler

import java.io.File
import java.nio.file.{Paths, Path}
import scala.io.Source
import com.eed3si9n.eval._
import better.files._
import io.methvin.better.files._
import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag
import scala.concurrent.ExecutionContext

/**
 * Service for compiling and managing components
 */
class CompilerService(
  val registry: ComponentRegistry = new InMemoryComponentRegistry,
  val backingDir: Path = Paths.get("/tmp/seer-components")
)(implicit ec: ExecutionContext = ExecutionContext.global) {
  private val eval = Eval(
    backingDir = backingDir,
    mkReporter = () => EvalReporter.store
  )

  /**
   * Compile a component from source code
   * @param source The source code to compile
   * @param id The component ID
   * @param version The component version
   * @return CompilationResult indicating success or failure
   */
  def compileComponent[T <: Component](
    source: String,
    id: String,
    version: String
  )(implicit ct: ClassTag[T]): CompilationResult = {
    try {
      // First try to compile and instantiate the component
      println(ct.runtimeClass.getName)
      val result = eval.eval(source, Some(ct.runtimeClass.getName))
      val component = result.getValue(this.getClass.getClassLoader).asInstanceOf[T]
      // val component = Eval[T](source)
      // Verify it's a Component and has the correct ID/version
      if (component.id != id) {
        CompilationError(List(s"Component ID mismatch: expected $id but got ${component.id}"))
      } else {
        // Register the component
        registry.register(component)
        CompilationSuccess(component)
      }
    } catch {
      case e: ClassCastException =>
        CompilationError(List(s"Type mismatch: ${e.getMessage}"))
      case e: Exception => 
        CompilationError(List(s"Compilation failed: ${e.getMessage}"))
    }
  }

  /**
   * Compile a component from a file
   */
  def compileFromFile[T <: Component](
    file: File,
    id: String,
    version: String
  )(implicit ct: ClassTag[T]): CompilationResult = {
    Try(Source.fromFile(file).mkString) match {
      case Success(source) => compileComponent[T](source, id, version)
      case Failure(e) => CompilationError(List(s"Failed to read file: ${e.getMessage}"))
    }
  }

  /**
   * Watch a directory for changes and recompile components
   */
  def watchDirectory[T <: Component](
    dir: File,
    id: String,
    version: String
  )(onChange: CompilationResult => Unit)(implicit ct: ClassTag[T]): Unit = {
    val betterDir = dir.toScala // Convert java.io.File to better.files.File
    val watcher = new RecursiveFileMonitor(betterDir) {
      override def onModify(file: better.files.File, count: Int) = {
        if (file.extension == Some(".scala")) {
          val result = compileFromFile[T](file.toJava, id, version)
          onChange(result)
        }
      }
    }
    watcher.start()
  }
}

/**
 * Companion object providing a default compiler service instance
 */
object CompilerService {
  private implicit val defaultEc: ExecutionContext = ExecutionContext.global
  private val defaultService = new CompilerService()
  
  def apply(): CompilerService = defaultService
  
  def apply(registry: ComponentRegistry): CompilerService = 
    new CompilerService(registry = registry)
    
  def apply(backingDir: Path): CompilerService =
    new CompilerService(backingDir = backingDir)
    
  def apply(registry: ComponentRegistry, backingDir: Path): CompilerService =
    new CompilerService(registry = registry, backingDir = backingDir)
    
  def apply(ec: ExecutionContext): CompilerService =
    new CompilerService()(ec)
    
  def apply(registry: ComponentRegistry, ec: ExecutionContext): CompilerService =
    new CompilerService(registry = registry)(ec)
    
  def apply(backingDir: Path, ec: ExecutionContext): CompilerService =
    new CompilerService(backingDir = backingDir)(ec)
    
  def apply(registry: ComponentRegistry, backingDir: Path, ec: ExecutionContext): CompilerService =
    new CompilerService(registry = registry, backingDir = backingDir)(ec)
}

