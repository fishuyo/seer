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
import org.apache.pekko.actor._

/**
 * Scanner for finding @live annotations in source files
 */
trait LiveCodeScanner {
  def scanSourceTree(root: Path): Set[LiveClassInfo]
  def watchSourceTree(root: Path)(onChange: LiveClassInfo => Unit): Unit
}

case class LiveClassInfo(
  className: String,
  packageName: String,
  filePath: Path,
  sourceCode: String,
  isActor: Boolean,
  lastModified: Long
)

/**
 * Scanner implementation that looks for @live annotations
 */
class AnnotationBasedScanner extends LiveCodeScanner {
  
  def scanSourceTree(root: Path): Set[LiveClassInfo] = {
    val rootFile = root.toFile
    if (!rootFile.exists() || !rootFile.isDirectory) {
      return Set.empty
    }
    
    val scalaFiles = findScalaFiles(rootFile)
    scalaFiles.flatMap(parseLiveClasses).toSet
  }
  
  def watchSourceTree(root: Path)(onChange: LiveClassInfo => Unit): Unit = {
    val rootFile = root.toFile
    if (!rootFile.exists() || !rootFile.isDirectory) {
      return
    }
    
    val betterRoot = rootFile.toScala
    val watcher = new RecursiveFileMonitor(betterRoot) {
      override def onModify(file: better.files.File, count: Int) = {
        if (file.extension == Some(".scala")) {
          parseLiveClasses(file.toJava).foreach(onChange)
        }
      }
    }
    watcher.start()
  }
  
  private def findScalaFiles(dir: File): List[File] = {
    if (!dir.isDirectory) return List.empty
    
    val files = dir.listFiles().toList
    val scalaFiles = files.filter(_.getName.endsWith(".scala"))
    val subdirFiles = files.filter(_.isDirectory).flatMap(findScalaFiles)
    scalaFiles ++ subdirFiles
  }
  
  private def parseLiveClasses(file: File): List[LiveClassInfo] = {
    Try(Source.fromFile(file).mkString) match {
      case Success(source) => extractLiveClasses(source, file.toPath)
      case Failure(_) => List.empty
    }
  }
  
  private def extractLiveClasses(source: String, filePath: Path): List[LiveClassInfo] = {
    // Simple regex-based extraction - in a real implementation you'd use a proper parser
    val classPattern = """@live\s+class\s+(\w+)(?:\s+extends\s+(\w+))?""".r
    val packagePattern = """package\s+([\w.]+)""".r
    
    val packageName = packagePattern.findFirstMatchIn(source).map(_.group(1)).getOrElse("")
    val lastModified = filePath.toFile.lastModified()
    
    classPattern.findAllMatchIn(source).map { m =>
      val className = m.group(1)
      val extendsClass = Option(m.group(2))
      val isActor = extendsClass.exists(_ == "Actor")
      
      LiveClassInfo(
        className = className,
        packageName = packageName,
        filePath = filePath,
        sourceCode = source,
        isActor = isActor,
        lastModified = lastModified
      )
    }.toList
  }
}

/**
 * Main live compiler service
 */
class LiveCompiler(
  val registry: LiveRegistry = new InMemoryLiveRegistry,
  val scanner: LiveCodeScanner = new AnnotationBasedScanner,
  val backingDir: Path = Paths.get("/tmp/seer-live-classes")
)(implicit ec: ExecutionContext = ExecutionContext.global) {
  
  private val eval = Eval(
    backingDir = backingDir,
    mkReporter = () => EvalReporter.store
  )
  
  /**
   * Initialize the live compiler by scanning the source tree
   */
  def initialize(sourceRoot: Path): Unit = {
    println(s"Initializing live compiler for: $sourceRoot")
    
    // Scan for all @live classes
    val liveClasses = scanner.scanSourceTree(sourceRoot)
    println(s"Found ${liveClasses.size} live classes")
    
    // Compile and register them
    liveClasses.foreach { liveClassInfo =>
      compileLiveClass(liveClassInfo) match {
        case LiveCompilationSuccess(liveClass) =>
          registry.register(liveClass)
          println(s"Registered live class: ${liveClass.className}")
        case LiveCompilationError(errors) =>
          println(s"Failed to compile ${liveClassInfo.className}: ${errors.mkString(", ")}")
      }
    }
    
    // Start watching for changes
    scanner.watchSourceTree(sourceRoot) { liveClassInfo =>
      handleLiveClassChange(liveClassInfo)
    }
  }
  
  /**
   * Compile a live class from source
   */
  def compileLiveClass(liveClassInfo: LiveClassInfo): LiveCompilationResult = {
    try {
      // Compile the class
      val compiledClass = eval.eval[Class[_]](liveClassInfo.sourceCode)
      
      // Create LiveClass
      val liveClass = LiveClass(
        className = liveClassInfo.className,
        compiledClass = compiledClass,
        sourceCode = liveClassInfo.sourceCode,
        filePath = liveClassInfo.filePath,
        lastModified = liveClassInfo.lastModified,
        isActor = liveClassInfo.isActor
      )
      
      LiveCompilationSuccess(liveClass)
    } catch {
      case e: Exception => 
        LiveCompilationError(List(s"Compilation failed: ${e.getMessage}"))
    }
  }
  
  /**
   * Handle changes to live class files
   */
  def handleLiveClassChange(liveClassInfo: LiveClassInfo): Unit = {
    println(s"Live class changed: ${liveClassInfo.className}")
    
    compileLiveClass(liveClassInfo) match {
      case LiveCompilationSuccess(liveClass) =>
        registry.update(liveClass)
        println(s"Updated live class: ${liveClass.className}")
      case LiveCompilationError(errors) =>
        println(s"Failed to update ${liveClassInfo.className}: ${errors.mkString(", ")}")
    }
  }
  
  /**
   * Get a dynamic reference to a live class
   */
  def getDynamic(className: String): Option[Dynamic] = {
    registry.getDynamic(className)
  }
  
  /**
   * Get an actor reference to a live class
   */
  def getActorRef(className: String): Option[ActorRef] = {
    registry.getActorRef(className)
  }
}

/**
 * Companion object providing default instances
 */
object LiveCompiler {
  private implicit val defaultEc: ExecutionContext = ExecutionContext.global
  private val defaultCompiler = new LiveCompiler()
  
  def apply(): LiveCompiler = defaultCompiler
  
  def apply(registry: LiveRegistry): LiveCompiler = 
    new LiveCompiler(registry = registry)
    
  def apply(sourceRoot: Path): LiveCompiler = {
    val compiler = new LiveCompiler()
    compiler.initialize(sourceRoot)
    compiler
  }
    
  def apply(registry: LiveRegistry, sourceRoot: Path): LiveCompiler = {
    val compiler = new LiveCompiler(registry = registry)
    compiler.initialize(sourceRoot)
    compiler
  }
} 