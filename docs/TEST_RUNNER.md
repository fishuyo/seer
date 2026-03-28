# Test Runner Guide

## Running Tests

### JVM Platform Tests

#### Prerequisites
- SBT installed
- Java/JVM installed
- LWJGL native libraries (should be included)

#### Graphics Tests

```bash
# Window Creation Test
sbt "examples/runMain seer.tests.graphics.WindowCreationTest"

# Clear Color Test
sbt "examples/runMain seer.tests.graphics.ClearColorTest"

# Viewport Test
sbt "examples/runMain seer.tests.graphics.ViewportTest"

# Event Test
sbt "examples/runMain seer.tests.graphics.EventTest"
```

#### Audio Tests

```bash
# Context Creation Test
sbt "examples/runMain seer.tests.audio.ContextCreationTest"

# Callback Test (plays audio)
sbt "examples/runMain seer.tests.audio.CallbackTest"
```

### Browser Platform Tests (Scala.js)

#### Prerequisites
- SBT installed
- Scala.js plugin configured
- Web browser

#### Build and Run

```bash
# Compile to JavaScript
sbt "examplesjs/fastOptJS"

# Open test.html in browser
open examplesjs/test.html
# Or
firefox examplesjs/test.html
# Or
chrome examplesjs/test.html
```

#### Available WebGL Tests

1. **WebGLWindowCreationTest** - Tests window creation
2. **WebGLClearColorTest** - Tests clearColor API

To run a specific test, edit `examplesjs/src/main/scala/Main.scala`:

```scala
object Main {
  def main(args: Array[String]): Unit = {
    WebGLWindowCreationTest.main(args)
    // or
    // WebGLClearColorTest.main(args)
  }
}
```

Then rebuild:
```bash
sbt "examplesjs/fastOptJS"
```

## Test Verification Checklist

### Graphics Tests

#### Window Creation Test
- [ ] Window appears on screen
- [ ] Window has correct size
- [ ] Window title is correct
- [ ] Multiple windows can be created (JVM only)
- [ ] Console shows "✓ ALL TESTS PASSED"

#### Clear Color Test
- [ ] Window displays solid colors
- [ ] Colors cycle automatically every 2 seconds
- [ ] SPACE key manually cycles colors
- [ ] ESC key exits
- [ ] Console shows color changes

#### Viewport Test
- [ ] Window displays colored areas
- [ ] V key cycles viewport modes
- [ ] Viewport affects rendering area correctly
- [ ] Window resize adapts viewport

#### Event Test
- [ ] Mouse movement prints to console
- [ ] Mouse clicks detected
- [ ] Keyboard presses detected
- [ ] Window resize events detected
- [ ] Event statistics printed on exit

### Audio Tests

#### Context Creation Test
- [ ] Console shows "✓ ALL TESTS PASSED"
- [ ] Audio module created successfully
- [ ] Context properties are correct

#### Callback Test
- [ ] 440 Hz tone plays (A4 note)
- [ ] Console shows callback statistics
- [ ] No audio glitches or dropouts
- [ ] Enter key stops audio cleanly

## Cross-Platform Verification

### Test Matrix

| Test | JVM (LWJGL) | Browser (WebGL) | Status |
|------|-------------|-----------------|--------|
| Window Creation | ✅ | ✅ | Working |
| Clear Color | ✅ | ✅ | Working |
| Viewport | ✅ | ⏳ | Pending |
| Events | ✅ | ⏳ | Pending |
| Audio Context | ✅ | ⏳ | Pending |
| Audio Callback | ✅ | ⏳ | Pending |

### Platform-Specific Notes

#### JVM (LWJGL)
- Full window management support
- Multiple windows supported
- Native keyboard/mouse input
- PortAudio for audio

#### Browser (WebGL)
- Single canvas = single window
- Browser keyboard/mouse events
- Web Audio API for audio (future)
- Fullscreen API available

## Troubleshooting

### JVM Tests

**Issue**: LWJGL native library not found
- **Solution**: Ensure native libraries are in `lib/` directory
- Check `DYLD_LIBRARY_PATH` on macOS

**Issue**: Window doesn't appear
- **Solution**: Check console for errors
- Verify OpenGL context creation

### Browser Tests

**Issue**: Canvas not found
- **Solution**: Ensure `glcanvas` element exists in HTML
- Check browser console for errors

**Issue**: WebGL context creation fails
- **Solution**: Check browser WebGL support
- Try WebGL 1.0 instead of 2.0

**Issue**: JavaScript errors
- **Solution**: Check Scala.js compilation
- Verify all dependencies are included

## Continuous Testing

### Quick Test Suite

Run all JVM tests:
```bash
sbt "examples/runMain seer.tests.graphics.WindowCreationTest" && \
sbt "examples/runMain seer.tests.graphics.ClearColorTest" && \
sbt "examples/runMain seer.tests.audio.ContextCreationTest"
```

### Automated Testing (Future)

- Headless rendering tests
- Screenshot comparison
- Audio analysis
- Performance benchmarks

## Next Steps

1. ✅ Create test examples
2. ✅ Run tests on JVM
3. ⏳ Run tests on browser
4. ⏳ Add more comprehensive tests
5. ⏳ Automated test suite
