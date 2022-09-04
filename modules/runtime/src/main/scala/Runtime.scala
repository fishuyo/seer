

package seer

import collection.mutable.ArrayBuffer


class Runtime {

  val modules = ArrayBuffer[Module]()

  var onInit = () => {}
  var onStart = () => {}
  var onCleanup = () => {}

  def useModule(mod:Module) = modules += mod
  def useModules(mods:Seq[Module]) = modules ++= mods

  def run(): Unit = {
    println("Initializing modules..")
    modules.foreach(_.init())
    onInit()

    println("Starting modules..")
    modules.foreach(_.start())
    onStart()
    
    println("Cleaning up modules..")
    modules.foreach(_.cleanup())
    onCleanup()
  }
}
