
package seer.runtime

import seer._
import seer.graphics._
import seer.audio._

object Webserver {

  val runtime = new SeerRuntime()

  val graphics = new GraphicsModule()
  val audio = new PortAudioModule()

  runtime.useModules(audio :: graphics :: List())

  def parseArgs(as:Array[String]) = {}

  def main(args: Array[String]): Unit = {
    parseArgs(args)

    runtime.run()
  }
} 