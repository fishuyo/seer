# Seer Cross-Platform Runtime Module Architecture

## Overview

This document outlines the architecture for a unified, cross-platform runtime module system that works across:
- **JVM** (Scala on JVM)
- **Native** (Scala Native - future)
- **Browser** (Scala.js)

## Core Principles

1. **Platform Abstraction**: Core APIs are platform-agnostic, with platform-specific implementations
2. **Module Lifecycle**: Consistent lifecycle management across all modules
3. **Event-Driven**: Unified event system for module lifecycle, window, input, etc.
4. **Type Safety**: Leverage Scala's type system for compile-time safety
5. **Composability**: Modules can be composed to build complex applications

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         Application Layer               │
│    (User Code / Examples)              │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│         Unified API Layer               │
│  (Graphics, Audio, Events, Runtime)    │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│      Platform Abstraction Layer         │
│    (Module Trait, Event System)        │
└─────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
┌───────────────┐      ┌───────────────┐
│  JVM Backend  │      │   JS Backend   │
│  (LWJGL, etc) │      │ (WebGL, etc)   │
└───────────────┘      └───────────────┘
```

## Module System

### Module Trait

```scala
trait Module {
  def id: String
  def name: String
  
  // Lifecycle
  def create(): Unit
  def destroy(): Unit
  def start(): Unit
  def stop(): Unit
  def update(dt: Double): Unit
  
  // Events
  def onEvent(event: ModuleEvent): Unit
  
  // State
  def isCreated: Boolean
  def isRunning: Boolean
}
```

### Module Events

```scala
sealed trait ModuleEvent
case class ModuleCreated(module: Module) extends ModuleEvent
case class ModuleDestroyed(module: Module) extends ModuleEvent
case class ModuleStarted(module: Module) extends ModuleEvent
case class ModuleStopped(module: Module) extends ModuleEvent
```

## Graphics Module Architecture

### Unified Graphics API

```scala
trait Graphics {
  // Context Management
  def createContext(config: GraphicsConfig): GraphicsContext
  def destroyContext(context: GraphicsContext): Unit
  
  // Window Management
  def createWindow(config: WindowConfig): Window
  def destroyWindow(window: Window): Unit
  
  // Rendering
  def clear(color: Color): Unit
  def draw(mesh: Mesh, shader: ShaderProgram): Unit
  
  // State
  def getCurrentContext(): Option[GraphicsContext]
  def getWindows(): Seq[Window]
}
```

### Graphics Backends

Instead of a single `GraphicsModule` with backend selection, we use **separate module classes** for each backend:

1. **JVM (LWJGL)**
   - `LwjglOpenGLGraphicsModule(config: OpenGLConfig)` - OpenGL via LWJGL
   - `LwjglVulkanGraphicsModule(config: VulkanConfig)` - Vulkan via LWJGL-Vulkan
   
2. **Browser**
   - `WebGLGraphicsModule(config: WebGLConfig)` - WebGL 2.0
   - `WebGPUGraphicsModule(config: WebGPUConfig)` - WebGPU (future)
   
3. **Native** (future)
   - `NativeOpenGLGraphicsModule(config: OpenGLConfig)` - OpenGL via native bindings
   - `NativeVulkanGraphicsModule(config: VulkanConfig)` - Vulkan via native bindings

### Graphics Configuration Types

Each backend has its own configuration type:

```scala
// OpenGL configuration
case class OpenGLConfig(
  version: (Int, Int) = (3, 3),
  profile: OpenGLProfile = OpenGLCoreProfile,
  vsync: Boolean = true,
  msaa: Int = 0,
  // ... other OpenGL-specific options
)

// Vulkan configuration
case class VulkanConfig(
  apiVersion: (Int, Int, Int) = (1, 0, 0),
  validationLayers: Boolean = false,
  // ... other Vulkan-specific options
)

// WebGL configuration
case class WebGLConfig(
  version: Int = 2, // WebGL 1.0 or 2.0
  alpha: Boolean = true,
  antialias: Boolean = true,
  // ... other WebGL-specific options
)
```

**Benefits of this approach:**
- **Type Safety**: Compile-time guarantee of which backend you're using
- **Clearer API**: `LwjglOpenGLGraphicsModule` is more explicit than `GraphicsModule(backend=OpenGL)`
- **Backend-Specific Options**: Each config can have options specific to that backend
- **Better Separation**: Each backend module is independent
- **Platform-Specific**: Some backends only available on certain platforms

## Audio Module Architecture

### Unified Audio API

```scala
trait Audio {
  // Context Management
  def createContext(config: AudioConfig): AudioContext
  def destroyContext(context: AudioContext): Unit
  
  // Playback
  def play(source: AudioSource): Unit
  def stop(source: AudioSource): Unit
  
  // Callback
  def setCallback(callback: AudioCallback): Unit
  
  // State
  def getCurrentContext(): Option[AudioContext]
}
```

### Audio Backends

1. **JVM**
   - PortAudio
   - JACK
   
2. **Browser**
   - Web Audio API
   
3. **Native** (future)
   - PortAudio native bindings

## Event System

### Event Hierarchy

```scala
sealed trait Event

// Module Lifecycle Events
sealed trait ModuleEvent extends Event
case class ModuleCreated(module: Module) extends ModuleEvent
case class ModuleDestroyed(module: Module) extends ModuleEvent
case class ModuleStarted(module: Module) extends ModuleEvent
case class ModuleStopped(module: Module) extends ModuleEvent

// Window Events
sealed trait WindowEvent extends Event
case class WindowCreated(window: Window) extends WindowEvent
case class WindowDestroyed(window: Window) extends WindowEvent
case class WindowResized(window: Window, width: Int, height: Int) extends WindowEvent
case class WindowClosed(window: Window) extends WindowEvent

// Input Events
sealed trait InputEvent extends Event
case class KeyPressed(key: Key, mods: KeyModifiers) extends InputEvent
case class KeyReleased(key: Key, mods: KeyModifiers) extends InputEvent
case class KeyRepeated(key: Key, mods: KeyModifiers) extends InputEvent
case class MouseMoved(x: Double, y: Double, dx: Double, dy: Double) extends InputEvent
case class MousePressed(button: MouseButton, x: Double, y: Double, mods: KeyModifiers) extends InputEvent
case class MouseReleased(button: MouseButton, x: Double, y: Double, mods: KeyModifiers) extends InputEvent
case class MouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double) extends InputEvent
```

### Event Bus

```scala
trait EventBus {
  def subscribe[T <: Event](handler: T => Unit): Subscription
  def publish(event: Event): Unit
  def unsubscribe(subscription: Subscription): Unit
}
```

## Runtime System

### SeerRuntime

```scala
class SeerRuntime {
  private val modules = mutable.ArrayBuffer[Module]()
  private val eventBus = new EventBus()
  
  def addModule(module: Module): Unit
  def removeModule(module: Module): Unit
  def getModule[T <: Module](id: String): Option[T]
  
  def subscribe[T <: Event](handler: T => Unit): Subscription
  
  def run(): Unit
  def stop(): Unit
}
```

## Implementation Strategy

### Phase 1: Core Infrastructure
1. ✅ Enhanced Module trait with events
2. ✅ Event system (Module, Window, Input events)
3. ✅ Runtime system with event bus

### Phase 2: Graphics API
1. ✅ Unified Graphics trait
2. ✅ Window management API
3. ✅ Mesh/Shader API
4. ✅ Backend implementations:
   - LWJGL (OpenGL + Vulkan)
   - WebGL
   - WebGPU (browser)

### Phase 3: Audio API
1. ✅ Unified Audio trait
2. ✅ Audio callback system
3. ✅ Generator system
4. ✅ Backend implementations:
   - PortAudio (JVM)
   - Web Audio API (browser)

### Phase 4: Native Support
1. Native graphics backend
2. Native audio backend
3. Cross-compilation setup

## Platform-Specific Considerations

### JVM
- Use LWJGL for graphics (OpenGL/Vulkan)
- Use PortAudio/JACK for audio
- Full access to native libraries

### Browser (Scala.js)
- WebGL 2.0 for graphics
- WebGPU for modern graphics (when available)
- Web Audio API for audio
- Limited to browser APIs

### Native (Future)
- Direct native bindings
- Smaller binary size
- Better performance
- Platform-specific optimizations

## Example Usage

```scala
val runtime = new SeerRuntime()

// Create graphics module - explicit backend selection
val graphics = new LwjglOpenGLGraphicsModule(
  OpenGLConfig(version = (3, 3), vsync = true)
)

// Or use Vulkan backend
val vulkanGraphics = new LwjglVulkanGraphicsModule(
  VulkanConfig(apiVersion = (1, 0, 0), validationLayers = true)
)

// Create audio module
val audio = new PortAudioModule(
  AudioConfig(sampleRate = 44100, bufferSize = 512)
)

// Add modules
runtime.addModule(graphics)
runtime.addModule(audio)

// Subscribe to events
runtime.subscribe[WindowResized] { event =>
  println(s"Window resized: ${event.width}x${event.height}")
}

runtime.subscribe[KeyPressed] { event =>
  if (event.key == Key.ESCAPE) runtime.stop()
}

// Setup callbacks
graphics.onDraw { g =>
  g.clear(Color.black)
  mesh.draw(shader)
}

audio.setCallback { io =>
  val sample = oscillator()
  io.setOutput(0)(sample)
  io.setOutput(1)(sample)
}

// Run
runtime.run()
```
