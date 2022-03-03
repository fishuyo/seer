
package seer
package runtime 

import collection.mutable.ArrayBuffer
import scalajs.js.annotation._

class SeerApp extends SeerObject {

  val modules = ArrayBuffer[RuntimeModule]()

  var onInit = () => {}
  var onStart = () => {}
  var onCleanup = () => {}
  var onDraw = () => {}
  var onUpdate = () => {}

  def init() = {}
  def draw() = {}
  def update() = {}

  def useModule(mod:RuntimeModule) = modules += mod
  def useModules(mods:Seq[RuntimeModule]) = modules ++= mods

  def parseArgs(as:Array[String]) = {}

  @JSExport
  def main(args: Array[String]): Unit = {
    parseArgs(args)

    modules.foreach(_.init())
    onInit()

    modules.foreach(_.start())
    onStart()
    
    modules.foreach(_.cleanup())
    onCleanup()
  }
}