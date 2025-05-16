

package seer

import collection.mutable.ArrayBuffer


class SeerRuntime {

  val modules = ArrayBuffer[Module]()

  var onCreate = () => {}
  var onDestroy = () => {}
  var onStart = () => {}

  def +=(mod:Module) = modules += mod
  def ++=(mods:Seq[Module]) = modules ++= mods 

  def useModule(mod:Module) = modules += mod
  def useModules(mods:Seq[Module]) = modules ++= mods

  def run(): Unit = {
    println("Initializing modules..")
    modules.foreach(_.create())
    onCreate()

    println("Starting modules..")
    modules.foreach(_.start())
    onStart()
    
    println("Cleaning up modules..")
    modules.foreach(_.destroy())
    onDestroy()
  }
}
