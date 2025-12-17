# Implementation Plan: Cross-Platform Graphics & Audio

## Current Status

✅ **Completed:**
1. Enhanced Module trait with lifecycle and events
2. Unified Event system (Module, Window, Input events)
3. Enhanced Runtime system with event bus
4. Graphics API interface definitions
5. Audio API interface definitions

## Next Steps

### Phase 1: Graphics Module Implementation

#### 1.1 Update LWJGL Backend - OpenGL
**File:** `modules/backends/graphics-lwjgl/src/main/scala/LwjglOpenGLGraphicsModule.scala`

**Tasks:**
- [ ] Create `LwjglOpenGLGraphicsModule(config: OpenGLConfig)` class
- [ ] Implement `GraphicsModule` trait
- [ ] Implement `Window` trait for GLFWWindow
- [ ] Integrate event publishing (WindowCreated, WindowResized, KeyPressed, etc.)
- [ ] Support OpenGLConfig for OpenGL context creation

**Key Changes:**
```scala
class LwjglOpenGLGraphicsModule(val config: OpenGLConfig) extends GraphicsModule {
  override def id: String = "lwjgl-opengl"
  override def name: String = "LWJGL OpenGL Graphics Module"
  
  private var context: Option[GraphicsContext] = None
  private val windows = collection.mutable.ArrayBuffer[Window]()
  
  override def create(): Unit = {
    // Initialize GLFW with OpenGL hints
    // Create OpenGL context
    // Publish ModuleCreated event
  }
  
  override def createWindow(windowConfig: WindowConfig): Window = {
    val window = new GLFWWindowImpl(windowConfig, config)
    windows += window
    publish(WindowCreated(window.id, window.width, window.height))
    window
  }
  
  // ... implement other methods
}
```

#### 1.2 Create LWJGL Vulkan Backend
**File:** `modules/backends/graphics-lwjgl/src/main/scala/LwjglVulkanGraphicsModule.scala` (new)

**Tasks:**
- [ ] Create `LwjglVulkanGraphicsModule(config: VulkanConfig)` class
- [ ] Implement `GraphicsModule` trait
- [ ] Implement Vulkan-specific window and context creation
- [ ] Integrate event publishing
- [ ] Support VulkanConfig for Vulkan instance creation

#### 1.3 Update WebGL Backend
**File:** `modules/backends/graphics-webgl/src/main/scala/WebGLGraphicsModule.scala`

**Tasks:**
- [ ] Rename/refactor to `WebGLGraphicsModule(config: WebGLConfig)`
- [ ] Implement `GraphicsModule` trait
- [ ] Implement `Window` trait for Canvas element
- [ ] Integrate event publishing
- [ ] Support WebGLConfig for WebGL context creation

#### 1.4 Create WebGPU Backend (Future)
**File:** `modules/backends/graphics-webgpu/` (new)

**Tasks:**
- [ ] Create `WebGPUGraphicsModule(config: WebGPUConfig)` class
- [ ] Implement `GraphicsModule` trait
- [ ] Use WebGPU API when available
- [ ] Integrate event publishing

#### 1.5 Create Native Graphics Backends (Future)
**File:** `modules/backends/graphics-native/` (new)

**Tasks:**
- [ ] Set up Scala Native project
- [ ] Create `NativeOpenGLGraphicsModule(config: OpenGLConfig)`
- [ ] Create `NativeVulkanGraphicsModule(config: VulkanConfig)`
- [ ] Create OpenGL/Vulkan bindings or use existing library
- [ ] Implement windowing (GLFW or native)
- [ ] Implement GraphicsModule trait for each backend

### Phase 2: Audio Module Implementation

#### 2.1 Update PortAudio Backend
**File:** `modules/backends/audio-portaudio/src/main/scala/PortAudioModule.scala`

**Tasks:**
- [ ] Implement `AudioModule` trait (from AudioAPI.scala)
- [ ] Integrate event publishing
- [ ] Support AudioConfig
- [ ] Improve callback system

**Key Changes:**
```scala
class PortAudioModule(val config: AudioConfig) extends AudioModule {
  private var context: Option[AudioContext] = None
  
  override def create(): Unit = {
    // Initialize PortAudio
    // Create context
    // Publish ModuleCreated event
  }
  
  override def setCallback(callback: AudioCallback): Unit = {
    onAudioIO = callback
  }
  
  // ... implement other methods
}
```

#### 2.2 Create Web Audio Backend
**File:** `modules/backends/audio-webaudio/` (new)

**Tasks:**
- [ ] Create Scala.js project
- [ ] Implement AudioModule trait
- [ ] Use Web Audio API
- [ ] Support AudioConfig

#### 2.3 Create Native Audio Backend (Future)
**File:** `modules/backends/audio-native/` (new)

**Tasks:**
- [ ] Set up Scala Native project
- [ ] Create PortAudio bindings
- [ ] Implement AudioModule trait

### Phase 3: Example Applications

#### 3.1 Basic Graphics Example
**File:** `examples/src/main/scala/unified/BasicGraphics.scala`

```scala
val runtime = new SeerRuntime()

// Explicit backend selection - type-safe!
val graphics = new LwjglOpenGLGraphicsModule(
  OpenGLConfig(version = (3, 3), vsync = true)
)

// Or use Vulkan
val vulkanGraphics = new LwjglVulkanGraphicsModule(
  VulkanConfig(apiVersion = (1, 0, 0), validationLayers = true)
)

runtime.addModule(graphics)

runtime.subscribe[WindowCreated] { event =>
  println(s"Window created: ${event.width}x${event.height}")
}

graphics.onDraw { g =>
  g.clear(Color.blue)
  // Draw mesh with shader
}

runtime.run()
```

#### 3.2 Basic Audio Example
**File:** `examples/src/main/scala/unified/BasicAudio.scala`

```scala
val runtime = new SeerRuntime()

val audio = new PortAudioModule(
  AudioConfig(sampleRate = 44100, bufferSize = 512)
)

runtime.addModule(audio)

val oscillator = new Oscillator(440f) // 440 Hz sine wave

audio.onAudioIO = { io =>
  while(io()) {
    val sample = oscillator()
    io.setOutput(0)(sample)
    io.setOutput(1)(sample)
  }
}

runtime.run()
```

#### 3.3 Combined Graphics + Audio Example
**File:** `examples/src/main/scala/unified/GraphicsAndAudio.scala`

Demonstrates:
- Graphics module with window
- Audio module with callback
- Event handling
- Cross-module communication

### Phase 4: Build System Updates

#### 4.1 Update build.sbt
- [ ] Add Native platform support (when ready)
- [ ] Ensure cross-platform dependencies work
- [ ] Add WebGPU dependencies for browser

#### 4.2 Documentation
- [ ] API documentation
- [ ] Migration guide from old API
- [ ] Platform-specific notes

## Migration Strategy

### For Existing Code

1. **Graphics Modules:**
   - Old: `new GraphicsModule()` with callbacks
   - New: `new LwjglOpenGLGraphicsModule(OpenGLConfig(...))` - explicit backend selection
   - Or: `new LwjglVulkanGraphicsModule(VulkanConfig(...))` for Vulkan

2. **Audio Modules:**
   - Old: `new PortAudioModule()` with onAudioIO callback
   - New: `new PortAudioModule(config)` implementing AudioModule trait

3. **Runtime:**
   - Old: `runtime.useModule(module)`
   - New: `runtime.addModule(module)` (backward compatible)

4. **Events:**
   - Old: Module-specific callbacks
   - New: `runtime.subscribe[EventType] { event => ... }`

## Testing Strategy

1. **Unit Tests:**
   - Module lifecycle
   - Event system
   - API interfaces

2. **Integration Tests:**
   - Graphics + Audio together
   - Event flow
   - Cross-platform compatibility

3. **Example Tests:**
   - All examples should compile and run
   - Verify on JVM and JS platforms

## Platform-Specific Notes

### JVM
- Use LWJGL for graphics (OpenGL/Vulkan)
- Use PortAudio/JACK for audio
- Full native library access

### Browser (Scala.js)
- WebGL 2.0 for graphics
- WebGPU when available
- Web Audio API for audio
- Limited to browser APIs

### Native (Future)
- Direct native bindings
- Smaller binaries
- Better performance
- Platform-specific optimizations

## Timeline Estimate

- **Phase 1 (Graphics):** 2-3 days
- **Phase 2 (Audio):** 1-2 days
- **Phase 3 (Examples):** 1 day
- **Phase 4 (Build/Docs):** 1 day

**Total:** ~1 week for core implementation
