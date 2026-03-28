# API Test Examples

This directory contains functional tests that verify the Seer API design and implementation across all platforms and backends.

## Purpose

These tests serve dual purposes:
1. **API Verification**: Ensure the API works correctly
2. **Documentation**: Show how to use the API

## Running Tests

### JVM Platform

```bash
# Run a specific test
sbt "examples/runMain seer.tests.graphics.WindowCreationTest"
sbt "examples/runMain seer.tests.graphics.ClearColorTest"
sbt "examples/runMain seer.tests.graphics.ViewportTest"
sbt "examples/runMain seer.tests.graphics.EventTest"

sbt "examples/runMain seer.tests.audio.ContextCreationTest"
sbt "examples/runMain seer.tests.audio.CallbackTest"
```

### Browser Platform (Future)

```bash
# When WebGL backend is updated
sbt "examplesjs/fastOptJS"
# Then open examplesjs/examplesjs.html in browser
```

## Test Organization

```
tests/
├── graphics/              # Graphics API tests
│   ├── WindowCreationTest.scala    # Window creation
│   ├── ClearColorTest.scala        # clearColor() API
│   ├── ViewportTest.scala          # setViewport() API
│   └── EventTest.scala             # Event system
├── audio/                 # Audio API tests
│   ├── ContextCreationTest.scala   # Audio context
│   └── CallbackTest.scala          # Audio callback
└── integration/           # Cross-module tests (future)
    └── GraphicsAndAudioTest.scala
```

## Test Coverage

### Graphics API

- ✅ Window Creation
- ✅ Clear Color
- ✅ Viewport
- ✅ Events (Window, Keyboard, Mouse)
- ⏳ Multi-Window (future)
- ⏳ Context Management (future)

### Audio API

- ✅ Context Creation
- ✅ Callback System
- ⏳ Generators (future)
- ⏳ Playback Control (future)

### Integration

- ⏳ Graphics + Audio (future)
- ⏳ Event Flow (future)

## Platform Coverage

### JVM
- ✅ LWJGL OpenGL (`LwjglOpenGLGraphicsModule`)
- ✅ PortAudio (`PortAudioModule`)

### Browser (Future)
- ⏳ WebGL (`WebGLGraphicsModule`)
- ⏳ Web Audio (`WebAudioModule`)

### Native (Future)
- ⏳ Native OpenGL/Vulkan
- ⏳ Native PortAudio

## Writing New Tests

1. Create test file in appropriate directory (`graphics/`, `audio/`, etc.)
2. Follow naming convention: `*Test.scala`
3. Use the test template structure:
   ```scala
   object MyTest {
     def main(args: Array[String]): Unit = {
       // Setup
       val runtime = new SeerRuntime()
       val module = new SomeModule(config)
       runtime.addModule(module)
       
       // Test operations
       // ...
       
       // Verify results
       // ...
       
       // Run
       runtime.run()
     }
   }
   ```
4. Print clear test results
5. Handle cleanup properly

## Success Criteria

A test passes if:
- ✅ No exceptions thrown
- ✅ Expected behavior occurs
- ✅ Resources properly cleaned up
- ✅ Works on target platform/backend

## Notes

- Tests are **functional tests**, not unit tests
- Visual/audio verification may be required
- Some tests need manual interaction
- Tests serve as API documentation
