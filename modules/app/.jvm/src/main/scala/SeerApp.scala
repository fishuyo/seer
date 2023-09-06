
package seer

import graphics._
import audio._

class SeerApp {

  val runtime = new Runtime
  val graphics = new LwjglGraphicsModule()
  val audio = new PortAudioModule()

  runtime.useModules(audio :: graphics :: List())

  def parseArgs(as:Array[String]) = {}

  def main(args: Array[String]): Unit = {
    parseArgs(args)

    runtime.run()
  }
}