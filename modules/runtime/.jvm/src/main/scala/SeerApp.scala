
package seer
package runtime 

import collection.mutable.ArrayBuffer

class SeerApp extends SeerObject {

  val modules = ArrayBuffer[RuntimeModule]()

  var onInit = () => {}
  var onDraw = () => {}
  var onUpdate = () => {}

  def init() = {}
  def draw() = {}
  def update() = {}

  def useModule(mod:RuntimeModule) = modules += mod
  def useModules(mods:Seq[RuntimeModule]) = modules ++= mods

  def parseArgs(as:Array[String]) = {}

  def main(args: Array[String]): Unit = {
    parseArgs(args)

    modules.foreach(_.init())
    onInit()
    modules.foreach(_.start())
    modules.foreach(_.cleanup())
  }
}