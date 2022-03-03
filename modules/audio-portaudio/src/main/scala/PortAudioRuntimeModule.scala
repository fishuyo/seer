
package seer.audio

import seer.runtime._ 

import com.github.rjeschke.jpa._


class PortAudioRuntimeModule extends PaCallback with RuntimeModule with AudioIO { 

	// var onAudioIO = (io:AudioIO) => {}

  // var callback = new PaCallback {
  	def paCallback(paIn:PaBuffer, paOut:PaBuffer, nframes:Int) = {

        paIn.getFloatBuffer.get(in(0))

        // zero output buffers
        zeroOutputs()
        
        onAudioIO(this)
        // call audio callbacks
        // ioBuffer.reset
        // sources.foreach{ case s=> s.audioIO( ioBuffer ); ioBuffer.reset } //in,out,channelsOut,bufferSize) )

        // if playThru set add input to output
        // if( playThru ) AudioPass.audioIO( ioBuffer ) //in,out,channelsOut,bufferSize)

        // copy output buffers to interleaved
        val output = interleave()

        // write samples to audio device
        paOut.getFloatBuffer.put(output)

        // if(recording){
          // if recordThru set and not already done add input to output
          // if( recordThru && !playThru ) AudioPass.audioIO( ioBuffer ) //in,out,channelsOut,bufferSize)

          // outFile.write(out,0,bufferSize)
        // }

  	}
  // }

  override def init() = {
    println("Initializing PortAudio RuntimeModule..")

    try{ JPA.initialize() }
    catch { case e:Exception => println(e) }
    
  	JPA.setCallback( this )
  	JPA.openDefaultStream(config.inputs, config.outputs, PaSampleFormat.paFloat32, config.sampleRate, config.bufferSize)
    Audio() = this
  }


  override def start() = JPA.startStream 
  override def startBlocking() = {}


  override def cleanup() = JPA.stopStream 
  


  


}