package seer 
package examples

import runtime.SeerApp
import audio._

object HelloAudio extends SeerApp {

	val audio = new PortAudioRuntimeModule()

	useModules(audio :: List())

	audio.onAudioIO = (io:AudioIO) => {
    io.reset()
    while( io() ){
      val s = io.input(0)
      io.sumOutput(0)(s)
      io.sumOutput(1)(s)
    }
	}


  onInit = () => {}
  onStart = () => { while(true){Thread.sleep(100)} }

}