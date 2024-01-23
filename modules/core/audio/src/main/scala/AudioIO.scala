
package seer.audio

import collection.mutable.ListBuffer


object Audio {
  var io:Option[AudioIO] = None
  def apply():AudioIO = io.get
  def update(_io:AudioIO) = io = Some(_io)

  // lazy val out = new Gen {
  //   def apply() = 0f
  // } 
}


case class AudioConfig(bufferSize:Int, sampleRate:Int, inputs:Int, outputs:Int)

trait AudioIO {

  var config = AudioConfig(512, 44100, 1, 2)
  var gain = 1f
  var index = -1

  // val sources = new ListBuffer[AudioSource]

  val in = Array.ofDim[Float](config.inputs, config.bufferSize)
  val out = Array.ofDim[Float](config.outputs, config.bufferSize)
  val outInterleaved = new Array[Float](config.outputs * config.bufferSize)
  // val ioBuffer = new AudioIOBuffer(config.inputs, config.outputs, config.bufferSize, in, out)
  
  var onAudioIO = (io:AudioIO) => {}

  def init()={}

  def apply():Boolean = {
    index += 1
    index < config.bufferSize
  }
  def reset() = index = -1

  def input(c:Int=0) = in(c)(index)
  def output(c:Int) = out(c)(index)
  def setOutput(c:Int)(v:Float) = out(c)(index) = v
  def sumOutput(c:Int)(v:Float) = out(c)(index) += v

  // def *=(gain:Gen) = {
  //   for(s <- 0 until config.bufferSize){
  //     val g = gain()
  //     out(0)(s) *= g
  //     out(1)(s) *= g
  //   }
  // }

  // def multiply(c:Int, gain:Float) = {
  //   for(s <- 0 until config.bufferSize){
  //     out(c)(s) *= gain
  //   }
  // }

  // def +=(buffer:AudioIOBuffer) = {
  //   for(s <- 0 until bufferSize){
  //     outputSamples(0)(s) += buffer.outputSamples(0)(s)
  //     outputSamples(1)(s) += buffer.outputSamples(1)(s)
  //   }
  // }

  def zeroOutputs() = {
    for( c <- (0 until config.outputs))
      for( i <- (0 until config.bufferSize)) out(c)(i) = 0f
    out
  }

  def interleave() = {
    for(i <- 0 until config.bufferSize){
      for(c <- 0 until config.outputs){
        outInterleaved(config.outputs * i + c) = out(c)(i) //* gain
      }
    }
    outInterleaved
  }




}






