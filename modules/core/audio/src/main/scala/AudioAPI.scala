package seer.audio

import seer.{Event, Module}

/**
 * Unified Audio API that works across all platforms.
 * This trait defines the interface that all audio backends must implement.
 */

// Audio Configuration
case class AudioConfig(
  sampleRate: Int = 44100,
  bufferSize: Int = 512,
  inputs: Int = 0,
  outputs: Int = 2,
  channels: Int = 2 // stereo by default
)

// Audio Context (represents an audio device/stream)
trait AudioContext {
  def id: String
  def config: AudioConfig
  def isValid: Boolean
  def sampleRate: Int
  def bufferSize: Int
}

// Audio Source (something that can produce audio)
trait AudioSource {
  def audioIO(io: AudioIO): Unit
}

// Audio Callback type
type AudioCallback = AudioIO => Unit

// Audio Module interface - base trait for all audio modules
trait AudioModule extends Module {
  
  // Context Management
  def createContext(): AudioContext
  def destroyContext(context: AudioContext): Unit
  def getCurrentContext(): Option[AudioContext]
  
  // Configuration
  def config: AudioConfig
  
  // Playback Control
  def play(source: AudioSource): Unit
  def stop(source: AudioSource): Unit
  def pause(source: AudioSource): Unit
  def resume(source: AudioSource): Unit
  
  // Callback
  var onAudioIO: AudioCallback = _ => {}
  
  // Access to low-level audio interface
  def audioIO: AudioIO
  
  // State
  def isPlaying: Boolean
  def gain: Float
  def setGain(gain: Float): Unit
}

// Example backend module interfaces (these would be implemented in backend modules)
//
// For JVM:
//   - PortAudioModule(config: AudioConfig) extends AudioModule
//   - JackAudioModule(config: AudioConfig) extends AudioModule
//
// For Browser:
//   - WebAudioModule(config: AudioConfig) extends AudioModule
//
// For Native (future):
//   - NativePortAudioModule(config: AudioConfig) extends AudioModule
