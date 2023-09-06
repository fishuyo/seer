
package seer
package live

// import seer.graphics._
// import audio._
// import io._
// import util._ 
// import time._
import actor._
// import flow._
// import openni._

import akka.actor._
import akka.event.Logging

import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent.duration._

object SeerActor {
  def props(c:Class[_]) = Props(c)

  case class Name(n:String)
}





class SeerActor extends Actor { //with ActorLogging with Animatable with AudioSource {
  import SeerActor._

  implicit var name:String = "default"
  var active = true

  implicit val system:ActorSystem = System()
  implicit val materializer:ActorMaterializer = ActorMaterializer()

  // implicit def source2io[T,M](src:Source[T,M]) = IOSource(src)
  // implicit val kill = KillSwitches.shared("script")

  // implicit def d2f(d:Double) = d.toFloat

  // val Print = Sink.foreach(println(_:Any))


  // var scene:Scene = Scene //graphics.Scene()
  // var camera:NavCamera = Camera
  // var shader = Shader("basic")
  // var node = RenderNode()
  // var xFadeTime = 1f
  // updateRenderer()

  // val Out = AudioScene()
  // var audioXFadeTime = 1f
  // Out += this

  def receive = {
    case b:Boolean => active = b
    case "load" => load()
    case "unload" => unload()
    case "preunload" => preunload()
    case Name(n) => name = n
  }

  // def shader(name:String){
  //   val s = Shader(name)
  //   Renderer().shader = s
  //   s.begin
  // }

  def load() = {
    // Run.animate {
      // val c =  RenderGraph.compositor
      // node.>>(1)(c)
      // scene += this
      // RenderGraph += node
      // Schedule.over(xFadeTime seconds){ case t => c.xfade(t) }
    // }
    // Out.gain = Ramp(0f,1f,(44100 * audioXFadeTime).toInt )
    // Audio().push(Out)
  }
  def unload() = {
    // Run.animate {
      // val c =  RenderGraph.compositor
      // node.>>(0)(c)
      // Schedule.after(xFadeTime seconds){ RenderGraph -= node; scene -= this }
      // RenderGraph -= node
      // scene -= this 
    // }
    // Out.gain = Ramp(Out.gain.value, 0f, (44100 * audioXFadeTime).toInt)
    // time.Schedule.after(audioXFadeTime.seconds){ 
      // Out.sources.clear 
      // Audio().sources -= Out
    // }
    // if(_keyboard.isDefined) _keyboard.get.remove
    // if(_mouse.isDefined) _mouse.get.remove
    // if(_schedule.isDefined) _schedule.get.clear
    // if(_openni.isDefined) _openni.get.remove

    // shader.stopMonitor() ??
    // kill.shutdown
  }

  def preunload() = {}

  // var _keyboard:Option[Keyboard] = None
  // var _mouse:Option[Mouse] = None
  // var _schedule:Option[Schedule] = None

  // def Keyboard = _keyboard.getOrElse({_keyboard = Some(io.Keyboard()); _keyboard.get })
  // def Mouse = _mouse.getOrElse({_mouse = Some(io.Mouse()); _mouse.get })
  // def Schedule = _schedule.getOrElse({_schedule = Some(time.Schedule()); _schedule.get })

  // def Scene(name:String) = {
    // scene = Scene(name)
    // s
  // }
  // var _openni:Option[OpenNIListener] = None
  // def OpenNI = _openni.getOrElse({_openni = Some(new OpenNIListener()); _openni.get })

  // def updateRenderer(){
  //   node.renderer.scene = scene
  //   node.renderer.camera = camera 
  //   node.renderer.shader = shader 
  //   node.renderer.active = active
  // }

}

// class SeerActorNode extends RenderNode with Actor with ActorLogging with Animatable with AudioSource {

//   override def animate(dt:Float) = super[RenderNode].animate(dt)

//   def receive = {
//     case "load" => 
//       RenderGraph.addNode(this)
//       renderer.scene.push(this)
//       renderer.camera = Camera

//       // Scene.push(this)
//       Audio().push(this)
//     case "unload" =>
//       RenderGraph.removeNode(this)

//       // Scene.remove(this)
//       Audio().sources -= this
//   }
// }