package seer
package live

import actor._

import java.io.File
import scala.io.Source

// import reflect.runtime.universe._
// import reflect.runtime.currentMirror
// import tools.reflect.ToolBox

// import com.github.dmytromitin.eval._
import com.eed3si9n.eval._

import org.apache.pekko.actor._
import org.apache.pekko.event.Logging
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout

import concurrent.Await
import concurrent.duration._

import collection.mutable.ListBuffer

/**
  * ScriptLoaderActor companion object
  */
object ScriptLoaderActor {
  case class Path(path:String, reloadOnChange:Boolean)
  case class Code(code:String)
  case object Load
  case object Reload
  case object Unload
  case object Status

  def props = propsEval
  def propsEval = Props(new ScriptLoaderActor(new EvalScriptLoader()))
  // def propsToolbox = Props(new ScriptLoaderActor(new ToolboxScriptLoader()))
}

/**
 * ScriptLoaderActor, each responsible for compiling and running
 * a script file or chunk of code
 */
class ScriptLoaderActor(val loader:ScriptLoader) extends Actor with ActorLogging {
  import ScriptLoaderActor._

  def receive = {
    case Path(path, reloadOnChange) =>
      log.info(s"path $path")
      if(reloadOnChange) FileMonitor(path){ (f) => self ! Reload }
      loader.setPath(path)

    case Code(code) => 
      loader.setCode(code)
    
    case Load | "load" =>
      log.info("loading..");
      loader.reload()
    
    case Reload | "reload" => 
      log.info("reloading..");
      loader.reload()
    
    case Unload | "unload" => loader.unload()
    case Status => 
      // if(loader.errors.isEmpty) loader.checkErrors()
      // sender ! loader.errors

    // case _ => log.warning("Received unknown message: {}", x)
  }
}

/**
  * ScriptLoader trait 
  */
trait ScriptLoader {

  var gid = 0

  var code=""
  var path:Option[String] = None
  var result:Any = null              // result of evaluating code
  var errors:Seq[(Int,String)] = Seq()  // seq of lineNumber -> message tuples

  def setPath(s:String) = path = Some(s)
  def setCode(s:String) = code = s

  def getCode() = {
    if(path.isDefined) code = Source.fromFile(new File(path.get)).mkString
    val importString = ScriptManager.imports.flatMap( (i) => s"import $i\n").mkString
    importString + code
  }

  // compile / recompile and evaluate script
  def reload() = {
    try{
      // notify running script about to reload
      result match {
        // case s:SeerScript => s.preUnload()
        case a:ActorRef => a ! "preunload" //org.apache.pekko.actor.PoisonPill
        case _ => ()
      }

      errors = Seq()
      unload()
      val ret = Eval[Any](getCode())//eval[AnyRef]()
      ret match{
        // case s:Script =>
          // result = ret
          // s.load()
        // case s:SeerScript =>
        //   result = ret
        //   s.load()
        case a:ActorRef =>
          result = ret
          a ! "load"
        case l:List[ActorRef] =>
          result = ret
          l.foreach{ case a => a ! "load" }
        case c:Class[_] if c.getSuperclass == classOf[SeerActor] =>
          val r = ".*\\$(.*)\\$.".r
          val r(simple) = c.getName
          println(c)
          println(c.getName)
          // val id = s"live.$simple.${util.Random.int()}"
          val id = s"live.$simple.$gid"
          gid += 1
          println(s"loading $id")
          val a = System().actorOf( Props(Class.forName(c.getName)), id) //SeerActor.props(c), id )
          result = a
          a ! SeerActor.Name(id)
          a ! "load"
        case x => println(s"Unrecognized return value from script: $x")
      }

    } catch { case e:Exception => 
      println("Exception in script: " + e.getMessage)
      e.printStackTrace()
      val frame = e.getStackTrace.find{ e => e.getMethodName.contains("load") }.get
      errors = Seq((frame.getLineNumber, "RuntimeError: " + e.toString))
    }
  }

  def unload() = {
    result match {
      // case s:Script => 
        // s.unload()
      // case s:SeerScript =>
      //   s.unload()
      case a:ActorRef =>
        a ! "unload"
        a ! org.apache.pekko.actor.PoisonPill
      case l:List[ActorRef] =>
        l.foreach{ case a =>
          a ! "unload"
          a ! org.apache.pekko.actor.PoisonPill
        }
      case null => ()
      case x => println(s"Unrecognized return value from ScriptLoader: $x")
    }
  }

  def checkErrors() = {}
  def eval[T]():T
}


// Eval Script Loader scala3
class EvalScriptLoader extends ScriptLoader {

  def eval[T]() : T = {
    val source = getCode()
    // Eval[T](source)
    Eval[T](source)

  }
  
  // override def checkErrors() = {
  // }

}


