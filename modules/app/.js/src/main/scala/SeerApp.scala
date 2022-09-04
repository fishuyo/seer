
package seer

import scalajs.js.annotation._

import graphics._
// import audio._

class SeerApp {

  val runtime = new Runtime
  val graphics = new WebglGraphicsModule()
	// val audio = new PortAudioModule()

	runtime.useModules(graphics :: List())

  def parseArgs(args:Array[String]) = {}

  @JSExport
  def main(args: Array[String]): Unit = {
    parseArgs(args)

    runtime.run()
  }
}