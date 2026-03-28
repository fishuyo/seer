package seer.audio

import com.github.rjeschke.jpa._
import seer._
import collection.mutable.ArrayBuffer

/**
 * PortAudio Module implementing the unified AudioModule trait.
 * Provides audio I/O via PortAudio on the JVM platform.
 */
class PortAudioModule(val config: AudioConfig = AudioConfig()) extends PaCallback with AudioModule with AudioIO {
  
  override def id: String = "portaudio"
  override def name: String = "PortAudio Module"
  
  private var _isCreated = false
  private var _isRunning = false
  private var context: Option[AudioContext] = None
  private val sources = ArrayBuffer[AudioSource]()
  private var _gain = 1.0f
  
  // Runtime reference for event publishing
  private var runtime: Option[SeerRuntime] = None
  
  def setRuntime(rt: SeerRuntime): Unit = {
    runtime = Some(rt)
  }
  
  private def publish(event: Event): Unit = {
    runtime.foreach(_.publish(event))
  }
  
  override def isCreated: Boolean = _isCreated
  override def isRunning: Boolean = _isRunning
  
  // PortAudio callback implementation
  def paCallback(paIn: PaBuffer, paOut: PaBuffer, nframes: Int): Unit = {
    // Read input
    if (config.inputs > 0) {
      paIn.getFloatBuffer.get(in(0))
    }
    
    // Zero output buffers
    zeroOutputs()
    
    // Call user callback
    onAudioIO(this)
    
    // Process audio sources
    sources.foreach { source =>
      reset()
      source.audioIO(this)
    }
    
    // Apply gain
    if (_gain != 1.0f) {
      for (c <- 0 until config.outputs) {
        for (i <- 0 until config.bufferSize) {
          out(c)(i) *= _gain
        }
      }
    }
    
    // Copy output buffers to interleaved
    val output = interleave()
    
    // Write samples to audio device
    paOut.getFloatBuffer.put(output)
  }
  
  override def create(): Unit = {
    if (_isCreated) return
    
    println(s"Initializing $name..")
    
    try {
      JPA.initialize()
    } catch {
      case e: Exception =>
        println(s"Failed to initialize PortAudio: $e")
        throw e
    }
    
    JPA.setCallback(this)
    JPA.openDefaultStream(
      config.inputs,
      config.outputs,
      PaSampleFormat.paFloat32,
      config.sampleRate,
      config.bufferSize
    )
    
    // Create audio context
    context = Some(new PortAudioContext(config))
    
    Audio() = this
    _isCreated = true
    
    publish(ModuleCreated(this))
  }
  
  override def start(): Unit = {
    if (!_isCreated) create()
    if (_isRunning) return
    
    JPA.startStream()
    _isRunning = true
    
    publish(ModuleStarted(this))
  }
  
  override def stop(): Unit = {
    if (!_isRunning) return
    
    JPA.stopStream()
    _isRunning = false
    
    publish(ModuleStopped(this))
  }
  
  override def destroy(): Unit = {
    if (!_isCreated) return
    
    if (_isRunning) {
      stop()
    }
    
    JPA.stopStream()
    context = None
    
    _isCreated = false
    publish(ModuleDestroyed(this))
  }
  
  override def update(dt: Double): Unit = {
    // Audio processing happens in the callback thread
  }
  
  override def createContext(): AudioContext = {
    if (!_isCreated) create()
    context.getOrElse {
      val ctx = new PortAudioContext(config)
      context = Some(ctx)
      ctx
    }
  }
  
  override def destroyContext(ctx: AudioContext): Unit = {
    if (context.contains(ctx)) {
      context = None
    }
  }
  
  override def getCurrentContext(): Option[AudioContext] = context
  
  override def play(source: AudioSource): Unit = {
    if (!sources.contains(source)) {
      sources += source
    }
  }
  
  override def stop(source: AudioSource): Unit = {
    sources -= source
  }
  
  override def pause(source: AudioSource): Unit = {
    // PortAudio doesn't have per-source pause, so we just remove it
    sources -= source
  }
  
  override def resume(source: AudioSource): Unit = {
    play(source)
  }
  
  override def isPlaying: Boolean = _isRunning && sources.nonEmpty
  
  override def gain: Float = _gain
  
  override def setGain(gain: Float): Unit = {
    _gain = gain.max(0.0f).min(1.0f)
  }
  
  override def audioIO: AudioIO = this
}

// PortAudio Context implementation
class PortAudioContext(val config: AudioConfig) extends AudioContext {
  override def id: String = s"portaudio-context-${System.identityHashCode(this)}"
  override def isValid: Boolean = true
  override def sampleRate: Int = config.sampleRate
  override def bufferSize: Int = config.bufferSize
}
