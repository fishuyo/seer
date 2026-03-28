# Testing Plan: API Verification Examples

## Overview

We're creating a comprehensive test example hierarchy to verify API design and implementation across all platforms and backends. These are **functional tests** that demonstrate and verify API correctness, not unit tests.

## Test Organization

```
examples/
├── tests/                          # API verification tests
│   ├── graphics/                   # Graphics API tests
│   │   ├── WindowCreationTest.scala
│   │   ├── ClearColorTest.scala
│   │   ├── ViewportTest.scala
│   │   ├── EventTest.scala
│   │   └── MultiWindowTest.scala
│   ├── audio/                      # Audio API tests
│   │   ├── ContextCreationTest.scala
│   │   ├── CallbackTest.scala
│   │   ├── GeneratorTest.scala
│   │   └── PlaybackTest.scala
│   └── integration/                # Cross-module tests
│       ├── GraphicsAndAudioTest.scala
│       └── EventFlowTest.scala
├── tutorials/                       # Pedagogical examples (future)
│   └── ...
└── worlds/                       # Creative/artistic examples (future)
    └── ...
```

## Test Categories

### 1. Graphics API Tests

#### Window Creation Tests

- **Purpose**: Verify window creation works correctly
- **Tests**:
  - Basic window creation
  - Window with custom config
  - Window size and title
  - Multiple windows
  - Window destruction

#### Basic Graphics API Tests

- **Purpose**: Verify core graphics functions
- **Tests**:
  - `clearColor()` - Set clear color
  - `clear()` - Clear buffers
  - `setViewport()` - Set viewport
  - Context creation and management

#### Event Tests

- **Purpose**: Verify event system works
- **Tests**:
  - Window events (Created, Resized, Closed)
  - Keyboard events (Pressed, Released, Repeated)
  - Mouse events (Moved, Pressed, Released, Scrolled)

#### Multi-Window Tests

- **Purpose**: Verify multiple windows work correctly
- **Tests**:
  - Create multiple windows
  - Independent rendering per window
  - Independent event handling

### 2. Audio API Tests

#### Context Creation Tests

- **Purpose**: Verify audio context creation
- **Tests**:
  - Basic context creation
  - Context with custom config
  - Context destruction

#### Callback Tests

- **Purpose**: Verify audio callback system
- **Tests**:
  - Basic callback execution
  - Sample generation
  - Buffer handling

#### Generator Tests

- **Purpose**: Verify audio generators work
- **Tests**:
  - Oscillator generators
  - Envelope generators
  - Filter generators

#### Playback Tests

- **Purpose**: Verify audio playback
- **Tests**:
  - Play/stop/pause/resume
  - Multiple sources
  - Gain control

### 3. Integration Tests

#### Graphics + Audio

- **Purpose**: Verify modules work together
- **Tests**:
  - Both modules in same runtime
  - Independent operation
  - Shared event bus

#### Event Flow

- **Purpose**: Verify event propagation
- **Tests**:
  - Events from graphics module
  - Events from audio module
  - Cross-module event handling

## Platform Coverage

### JVM Platform

- ✅ LWJGL OpenGL (`LwjglOpenGLGraphicsModule`)
- ⏳ LWJGL Vulkan (`LwjglVulkanGraphicsModule`) - future
- ✅ PortAudio (`PortAudioModule`)
- ⏳ JACK (`JackAudioModule`) - future

### Browser Platform (Scala.js)

- ⏳ WebGL (`WebGLGraphicsModule`) - needs update
- ⏳ WebGPU (`WebGPUGraphicsModule`) - future
- ⏳ Web Audio (`WebAudioModule`) - future

### Native Platform

- ⏳ Native OpenGL (`NativeOpenGLGraphicsModule`) - future
- ⏳ Native Vulkan (`NativeVulkanGraphicsModule`) - future
- ⏳ Native PortAudio (`NativePortAudioModule`) - future

## Test Structure

Each test should:

1. **Setup**: Create runtime and modules
2. **Execute**: Perform test operations
3. **Verify**: Check results (print/log, visual verification)
4. **Cleanup**: Properly destroy resources

## Example Test Template

```scala
package seer.tests.graphics

import seer._
import seer.graphics._

object WindowCreationTest {
  def main(args: Array[String]): Unit = {
    println("=== Window Creation Test ===")
    
    val runtime = new SeerRuntime()
    val graphics = new LwjglOpenGLGraphicsModule(OpenGLConfig())
    runtime.addModule(graphics)
    
    // Test 1: Basic window creation
    println("Test 1: Creating basic window...")
    val window1 = graphics.createWindow(WindowConfig(title = "Test Window 1"))
    assert(window1 != null, "Window should be created")
    assert(window1.width > 0, "Window should have width")
    assert(window1.height > 0, "Window should have height")
    println("✓ Basic window created successfully")
    
    // Test 2: Window with custom config
    println("Test 2: Creating window with custom config...")
    val window2 = graphics.createWindow(
      WindowConfig(
        title = "Custom Window",
        width = 1024,
        height = 768,
        resizable = false
      )
    )
    assert(window2.width == 1024, "Window should have correct width")
    assert(window2.height == 768, "Window should have correct height")
    println("✓ Custom window created successfully")
    
    // Test 3: Multiple windows
    println("Test 3: Creating multiple windows...")
    val windows = (1 to 3).map { i =>
      graphics.createWindow(WindowConfig(title = s"Window $i"))
    }
    assert(graphics.getWindows().length == 5, "Should have 5 windows total")
    println("✓ Multiple windows created successfully")
    
    println("\nAll tests passed! Press ESC to exit.")
    
    runtime.subscribe[KeyPressed] { event =>
      if (event.key == Key.ESCAPE) runtime.stop()
    }
    
    graphics.onDraw = { g => g.clear(Color.black) }
    
    runtime.run()
  }
}
```

## Test Execution Strategy

### Manual Testing

- Run tests individually
- Visual verification
- Check console output
- Verify no crashes/errors

### Automated Testing (Future)

- Headless rendering tests
- Screenshot comparison
- Audio analysis
- Performance benchmarks

## Priority Order

1. **High Priority** (Core functionality):
   - Window creation test
   - ClearColor test
   - Basic event test

2. **Medium Priority** (Extended functionality):
   - Multi-window test
   - Audio callback test
   - Integration test

3. **Low Priority** (Advanced features):
   - Generator tests
   - Performance tests
   - Stress tests

## Success Criteria

A test passes if:

- ✅ No exceptions thrown
- ✅ Expected behavior occurs
- ✅ Resources properly cleaned up
- ✅ Works on target platform/backend

## Notes

- Tests are **examples** that verify API correctness
- They serve dual purpose: testing + documentation
- Visual/audio verification may be required
- Some tests may need manual interaction (keyboard/mouse)
