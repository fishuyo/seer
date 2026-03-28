# Event Binding System Plan: HumanInput-Inspired Flexible Event Composition

## Overview

This document outlines a plan for implementing a flexible, composable event binding system in Seer, inspired by HumanInput.js. The goal is to enable rich, heterogeneous event combinations with temporal awareness, leveraging Scala's metaprogramming capabilities and a domain-specific language (DSL) for event composition.

## Key Inspiration: HumanInput.js Features

HumanInput.js provides:
1. **Unified API** across keyboard, mouse, touch, gamepad, etc.
2. **Pattern Composition** - e.g., `"ctrl+c"`, `"shift+click"`, `"cmd+alt+delete"`
3. **Temporal Patterns** - sequences, timing windows, gestures
4. **Event Filtering** - throttle, debounce, filter by conditions
5. **Gesture Recognition** - swipe, pinch, long-press, double-click
6. **Flexible Binding** - bind to strings, patterns, or programmatic combinations

## Current Seer Event System

### Strengths
- ✅ Type-safe event system with sealed traits
- ✅ Timestamp support on all events
- ✅ Event bus with subscription model
- ✅ Cross-platform (JVM + JS)
- ✅ Multiple event types: Input, Window, Module lifecycle

### Limitations
- ❌ No pattern composition (e.g., "ctrl+c")
- ❌ No temporal patterns (sequences, timing windows)
- ❌ No gesture recognition
- ❌ No event filtering/transformation DSL
- ❌ Manual event combination logic required

## Proposed Architecture

### 1. Event Pattern DSL

A fluent DSL for composing event patterns:

```scala
// Basic patterns
val ctrlC = Key("c") + Modifier.Control
val shiftClick = Mouse.LeftClick + Modifier.Shift
val doubleClick = Mouse.LeftClick * 2 within 500.ms

// Sequences
val konami = Key("up") then Key("up") then Key("down") then Key("down") 
  then Key("left") then Key("right") then Key("left") then Key("right")
  then Key("b") then Key("a")

// Temporal patterns
val quickPress = Key("space") within 200.ms
val longPress = Mouse.LeftDown for 1000.ms
val rapidFire = Key("x") repeated 3.times within 500.ms

// Combinations
val ctrlShiftClick = Mouse.LeftClick + Modifier.Control + Modifier.Shift
val anyArrow = Key("ArrowUp") | Key("ArrowDown") | Key("ArrowLeft") | Key("ArrowRight")

// Gestures
val swipeLeft = Mouse.Drag from (x1, y1) to (x2, y2) where (x2 < x1 - 100)
val pinchZoom = Touch.Pinch(scale > 1.1)
```

### 2. Event Binding API

```scala
// Simple bindings
runtime.bind(ctrlC) { event =>
  println("Copy!")
}

runtime.bind("ctrl+c") { event =>
  println("Copy!")
}

// Pattern bindings with context
runtime.bind(shiftClick) { event =>
  val mouseEvent = event.asInstanceOf[MousePressed]
  selectAt(mouseEvent.x, mouseEvent.y)
}

// Sequence bindings
runtime.bind(konami) { events =>
  println("Konami code activated!")
  activateCheatMode()
}

// Temporal bindings
runtime.bind(longPress) { event =>
  showContextMenu(event.x, event.y)
}
```

### 3. Event Stream Processing

```scala
// Filtering
runtime.events[MouseMoved]
  .throttle(100.ms)
  .filter(_.x > 100)
  .subscribe { event =>
    updateCursor(event.x, event.y)
  }

// Transformation
runtime.events[KeyPressed]
  .map(_.key.name)
  .debounce(300.ms)
  .subscribe { keyName =>
    handleKeyInput(keyName)
  }

// Windowing
runtime.events[InputEvent]
  .window(1000.ms)
  .subscribe { events =>
    analyzeInputPattern(events)
  }
```

### 4. Gesture Recognition

```scala
// Built-in gestures
runtime.bind(Gesture.DoubleClick) { event =>
  openFile(event.target)
}

runtime.bind(Gesture.Swipe(Direction.Left)) { event =>
  goToNextPage()
}

runtime.bind(Gesture.PinchZoom) { event =>
  zoom(event.scale)
}

// Custom gestures
val customGesture = Gesture.define {
  Mouse.LeftDown then
  Mouse.Drag(distance > 50) then
  Mouse.LeftUp
}
```

### 5. Metaprogramming Integration

Leverage Scala 3 metaprogramming for compile-time optimization:

```scala
// Macro-based pattern compilation
@eventPattern("ctrl+c")
def handleCopy(): Unit = {
  copyToClipboard()
}

// Inline pattern matching
inline def bindPattern(inline pattern: String)(inline handler: => Unit): Unit = {
  ${ compilePattern('pattern, 'handler) }
}

// Type-safe pattern construction
trait EventPattern[T <: Event] {
  def matches(event: Event): Option[T]
  def compile(): CompiledPattern
}
```

## Implementation Phases

### Phase 1: Core Pattern System

**Goal**: Basic pattern composition and matching

1. **Event Pattern Types**
   - `SinglePattern[T]` - matches single event type
   - `CombinedPattern` - AND/OR combinations
   - `SequencePattern` - ordered event sequences
   - `TemporalPattern` - time-based constraints

2. **Pattern Matcher**
   - State machine for pattern matching
   - Efficient event stream processing
   - Pattern compilation/optimization

3. **Basic Bindings**
   - String-based pattern parsing
   - Programmatic pattern construction
   - Handler registration

**Files to create:**
- `modules/core/runtime/src/main/scala/EventPattern.scala`
- `modules/core/runtime/src/main/scala/PatternMatcher.scala`
- `modules/core/runtime/src/main/scala/EventBinding.scala`

### Phase 2: Temporal Patterns

**Goal**: Time-aware event composition

1. **Timestamp Tracking**
   - Event history buffer
   - Temporal queries
   - Time window management

2. **Temporal Operators**
   - `within(duration)` - events within time window
   - `after(duration)` - event after delay
   - `for(duration)` - event held for duration
   - `repeated(n)` - event repeated n times

3. **Sequence Matching**
   - Ordered event sequences
   - Timeout handling
   - Partial match tracking

**Files to create:**
- `modules/core/runtime/src/main/scala/TemporalPattern.scala`
- `modules/core/runtime/src/main/scala/EventHistory.scala`
- `modules/core/runtime/src/main/scala/SequenceMatcher.scala`

### Phase 3: Gesture Recognition

**Goal**: High-level gesture detection

1. **Gesture Types**
   - Click gestures (single, double, long-press)
   - Drag gestures (swipe, pan)
   - Multi-touch gestures (pinch, rotate)
   - Keyboard gestures (chords, sequences)

2. **Gesture Engine**
   - Gesture state machine
   - Velocity/acceleration calculation
   - Gesture classification

3. **Custom Gestures**
   - Gesture DSL
   - User-defined gesture patterns
   - Gesture composition

**Files to create:**
- `modules/core/runtime/src/main/scala/Gesture.scala`
- `modules/core/runtime/src/main/scala/GestureRecognizer.scala`
- `modules/core/runtime/src/main/scala/GesturePattern.scala`

### Phase 4: Stream Processing

**Goal**: Event stream transformations

1. **Stream Operators**
   - `filter`, `map`, `flatMap`
   - `throttle`, `debounce`
   - `window`, `buffer`
   - `merge`, `zip`

2. **Reactive Streams**
   - Backpressure handling
   - Stream composition
   - Error handling

3. **Performance**
   - Efficient stream processing
   - Lazy evaluation
   - Memory management

**Files to create:**
- `modules/core/runtime/src/main/scala/EventStream.scala`
- `modules/core/runtime/src/main/scala/StreamOperators.scala`

### Phase 5: DSL & Metaprogramming

**Goal**: Expressive DSL with compile-time optimization

1. **DSL Syntax**
   - Infix operators for composition
   - String-based pattern parsing
   - Fluent API design

2. **Macros**
   - Pattern compilation at compile-time
   - Type-safe pattern construction
   - Performance optimization

3. **Type System Integration**
   - Type-safe event extraction
   - Pattern type inference
   - Compile-time validation

**Files to create:**
- `modules/core/runtime/src/main/scala/EventDSL.scala`
- `modules/core/runtime/src/main/scala/PatternMacros.scala`

## Design Decisions

### 1. Pattern Representation

**Option A: AST-based**
```scala
sealed trait PatternAST
case class SingleEvent[T](eventType: Class[T]) extends PatternAST
case class AndPattern(left: PatternAST, right: PatternAST) extends PatternAST
case class OrPattern(left: PatternAST, right: PatternAST) extends PatternAST
case class SequencePattern(patterns: List[PatternAST]) extends PatternAST
```

**Option B: Function-based**
```scala
trait EventPattern[T] {
  def matches(history: EventHistory): Option[T]
  def compile(): CompiledPattern
}
```

**Recommendation**: Hybrid approach - AST for construction, compiled functions for matching.

### 2. Event History Management

**Option A: Full History**
- Keep all events in memory
- Unlimited query capabilities
- Memory intensive

**Option B: Sliding Window**
- Keep only recent events (e.g., last 5 seconds)
- Memory efficient
- Limited query window

**Option C: Pattern-Specific Buffers**
- Each pattern maintains its own event buffer
- Optimized for specific patterns
- Complex memory management

**Recommendation**: Option C with configurable window sizes per pattern.

### 3. Pattern Matching Strategy

**Option A: State Machine**
- Each pattern = state machine
- Efficient for sequences
- Complex for combinations

**Option B: Rule Engine**
- Pattern = rules
- Flexible matching
- Slower performance

**Option C: Compiled Patterns**
- Patterns compiled to optimized matchers
- Best performance
- Complex compilation

**Recommendation**: Option C with fallback to Option A for dynamic patterns.

### 4. DSL Design

**Option A: Method Chaining**
```scala
Key("c") + Modifier.Control bind { ... }
```

**Option B: Infix Operators**
```scala
Key("c") + Control bind { ... }
```

**Option C: String Parsing**
```scala
"ctrl+c" bind { ... }
```

**Recommendation**: Support all three - string parsing for convenience, operators for composition, chaining for complex patterns.

## Example Usage Scenarios

### Scenario 1: Keyboard Shortcuts

```scala
// Simple shortcuts
runtime.bind("ctrl+c") { _ => copy() }
runtime.bind("ctrl+v") { _ => paste() }
runtime.bind("ctrl+z") { _ => undo() }
runtime.bind("ctrl+shift+z") { _ => redo() }

// Complex shortcuts
runtime.bind(Key("s") + Control + Shift) { _ => saveAs() }
runtime.bind(Key("f") + Control + Alt) { _ => findAdvanced() }
```

### Scenario 2: Mouse Gestures

```scala
// Click patterns
runtime.bind(Mouse.LeftClick * 2 within 500.ms) { event =>
  openFile(event.target)
}

runtime.bind(Mouse.RightDown for 1000.ms) { event =>
  showContextMenu(event.x, event.y)
}

// Drag patterns
runtime.bind(Mouse.Drag(distance > 100, direction = Direction.Left)) { event =>
  goToNextPage()
}
```

### Scenario 3: Input Sequences

```scala
// Konami code
runtime.bind(
  Key("up") then Key("up") then Key("down") then Key("down")
  then Key("left") then Key("right") then Key("left") then Key("right")
  then Key("b") then Key("a")
) { _ =>
  activateCheatMode()
}

// Rapid key presses
runtime.bind(Key("space") repeated 3.times within 500.ms) { _ =>
  activateSpecialMove()
}
```

### Scenario 4: Cross-Module Events

```scala
// Audio + Graphics
runtime.bind(
  AudioEvent.BeatDetected(frequency > 440) 
  and WindowEvent.Focused
) { _ =>
  startVisualization()
}

// OSC + Input
runtime.bind(
  OSCEvent.Message("/volume") 
  and Key("v") + Control
) { event =>
  adjustVolume(event.value)
}
```

### Scenario 5: Stream Processing

```scala
// Throttled mouse movement
runtime.events[MouseMoved]
  .throttle(16.ms) // ~60fps
  .subscribe { event =>
    updateCursor(event.x, event.y)
  }

// Debounced window resize
runtime.events[WindowResized]
  .debounce(300.ms)
  .subscribe { event =>
    recalculateLayout(event.width, event.height)
  }

// Input pattern analysis
runtime.events[InputEvent]
  .window(1000.ms)
  .map(_.timestamp)
  .subscribe { timestamps =>
    val rate = timestamps.length / 1.0
    if (rate > 10) println("High input rate detected!")
  }
```

## Performance Considerations

1. **Pattern Compilation**: Compile patterns at registration time, not at runtime
2. **Event Filtering**: Filter events early in the pipeline
3. **Memory Management**: Use bounded buffers, clear old events
4. **Lazy Evaluation**: Only process events when patterns are active
5. **Parallel Matching**: Match multiple patterns in parallel when possible

## Cross-Platform Considerations

1. **Scala.js Compatibility**: Avoid reflection, use type classes
2. **JVM Optimization**: Leverage JVM-specific optimizations
3. **Native Future**: Design for Scala Native compatibility
4. **Platform-Specific Events**: Abstract platform differences

## Testing Strategy

1. **Unit Tests**: Pattern matching logic, temporal operators
2. **Integration Tests**: End-to-end event binding scenarios
3. **Performance Tests**: Pattern matching performance, memory usage
4. **Cross-Platform Tests**: Verify behavior on JVM and JS

## Future Enhancements

1. **Machine Learning**: Learn patterns from user behavior
2. **Accessibility**: Support for assistive technologies
3. **Remote Events**: Network-based event binding
4. **Event Recording**: Record and replay event sequences
5. **Visual Pattern Builder**: GUI for creating event patterns

## Conclusion

This plan provides a roadmap for implementing a flexible, HumanInput-inspired event binding system in Seer. The phased approach allows incremental development while maintaining backward compatibility with the existing event system. The combination of DSL, metaprogramming, and stream processing will enable rich, expressive event composition while maintaining type safety and performance.
