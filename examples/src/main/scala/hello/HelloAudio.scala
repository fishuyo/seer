package seer 
package examples

import audio._

object HelloAudio extends App {

  val runtime = new SeerRuntime()
  val audio = new PortAudioModule()

  runtime.useModules(audio :: List())

  audio.onAudioIO = (io:AudioIO) => {
    io.reset()
    while( io() ){
      val s = io.input(0)
      io.sumOutput(0)(s)
      io.sumOutput(1)(s)
    }
  }

  runtime.onCreate = () => {}
  runtime.onStart = () => { while(true){Thread.sleep(100)} }

  runtime.run()

}