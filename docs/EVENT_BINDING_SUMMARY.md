# Event Binding System: Summary & Key Ideas

## Overview

This document summarizes the plan for implementing a HumanInput-inspired flexible event binding system in Seer, leveraging Scala's metaprogramming capabilities and a DSL for rich event composition.

## Key Concepts from HumanInput

HumanInput.js excels at:
1. **Unified API** - Single interface for keyboard, mouse, touch, gamepad
2. **Pattern Composition** - `"ctrl+c"`, `"shift+click"`, complex combinations
3. **Temporal Patterns** - Sequences, timing windows, gestures over time
4. **Flexible Binding** - String-based or programmatic patterns
5. **Event Filtering** - Throttle, debounce, filter, transform

## Core Innovation: Pattern-Based Event Matching

Instead of subscribing to individual event types, we bind to **patterns** that can match:
- Single events with predicates
- Logical combinations (AND/OR)
- Temporal sequences (ordered events)
- Time-constrained patterns (within window, after delay, held duration)
- Gestures (swipe, pinch, double-click)

## Architecture Highlights

### 1. Pattern Hierarchy

```
EventPattern[T]
├── SinglePattern[T]        - Single event type + predicate
├── AndPattern[T1, T2, R]   - Both patterns match
├── OrPattern[T]            - Any pattern matches
├── SequencePattern[T]       - Ordered sequence of events
└── TemporalPattern[T]      - Time-constrained matching
```

### 2. Event History

Sliding window of recent events (default 5 seconds) used for:
- Pattern matching across time
- Sequence detection
- Temporal queries
- Gesture recognition

### 3. Pattern Matcher

Efficient matching engine that:
- Compiles patterns at registration time
- Processes events through active patterns
- Maintains state for sequences
- Handles timeouts and cleanup

### 4. DSL Design

Three ways to create patterns:

**String-based** (convenient):
```scala
runtime.bind("ctrl+c") { _ => copy() }
```

**Operator-based** (composition):
```scala
runtime.bind(Key("c") + Modifier.Control) { _ => copy() }
```

**Fluent API** (complex patterns):
```scala
runtime.bind(
  Key("up") then Key("up") then Key("down") then Key("down")
  then Key("left") then Key("right") then Key("left") then Key("right")
  then Key("b") then Key("a") within 5000.ms
) { _ => activateCheatMode() }
```

## Metaprogramming Opportunities

### 1. Compile-Time Pattern Compilation

Use Scala 3 macros to compile patterns at compile-time:

```scala
@eventPattern("ctrl+c")
def handleCopy(): Unit = {
  copyToClipboard()
}
```

### 2. Type-Safe Pattern Construction

Leverage Scala's type system for:
- Type inference in pattern composition
- Compile-time validation
- Type-safe event extraction

### 3. Inline Optimization

Use `inline` for zero-cost abstractions:

```scala
inline def bindPattern(inline pattern: String)(inline handler: => Unit): Unit = {
  ${ compilePattern('pattern, 'handler) }
}
```

## Key Design Decisions

### Pattern Representation
- **Hybrid**: AST for construction, compiled functions for matching
- Allows flexible construction + efficient execution

### Event History
- **Pattern-Specific Buffers**: Each pattern maintains its own buffer
- Optimized for specific patterns, configurable window sizes

### Matching Strategy
- **Compiled Patterns**: Patterns compiled to optimized matchers
- Fallback to state machines for dynamic patterns

### DSL Support
- **All Three**: String parsing, operators, and fluent API
- Maximum flexibility for different use cases

## Implementation Phases

### Phase 1: Core Pattern System ✅ (Planned)
- Event pattern types
- Pattern matcher
- Basic bindings

### Phase 2: Temporal Patterns
- Timestamp tracking
- Temporal operators (within, after, for)
- Sequence matching

### Phase 3: Gesture Recognition
- Built-in gestures
- Custom gesture DSL
- Gesture state machine

### Phase 4: Stream Processing
- Stream operators (filter, map, throttle, debounce)
- Reactive streams
- Performance optimization

### Phase 5: DSL & Metaprogramming
- DSL syntax refinement
- Macro-based compilation
- Type system integration

## Example Use Cases

### Keyboard Shortcuts
```scala
runtime.bind("ctrl+c") { _ => copy() }
runtime.bind("ctrl+v") { _ => paste() }
runtime.bind(Key("z") + Control + Shift) { _ => redo() }
```

### Mouse Gestures
```scala
runtime.bind(Mouse.LeftClick * 2 within 500.ms) { _ => openFile() }
runtime.bind(Mouse.RightDown for 1000.ms) { _ => showContextMenu() }
```

### Input Sequences
```scala
runtime.bind(konamiCode) { _ => activateCheatMode() }
runtime.bind(Key("space") repeated 3.times within 500.ms) { _ => specialMove() }
```

### Stream Processing
```scala
runtime.events[MouseMoved].throttle(16.ms).subscribe { e => updateCursor(e.x, e.y) }
runtime.events[WindowResized].debounce(300.ms).subscribe { e => recalculateLayout() }
```

## Benefits

1. **Expressiveness**: Rich patterns in concise syntax
2. **Type Safety**: Compile-time validation, type inference
3. **Performance**: Compiled patterns, efficient matching
4. **Flexibility**: String, operator, or fluent API
5. **Composability**: Patterns can be combined arbitrarily
6. **Temporal Awareness**: Built-in support for time-based patterns
7. **Cross-Platform**: Works on JVM and JS

## Integration Points

- **SeerRuntime**: Extends existing runtime with `bind` API
- **Event Bus**: Uses same event bus, adds pattern matching layer
- **Backward Compatible**: Existing `subscribe[T]` API continues to work
- **Type System**: Leverages Scala's type system throughout

## Next Steps

1. **Review & Refine**: Review design documents, gather feedback
2. **Prototype**: Build minimal viable pattern system
3. **Test**: Validate with real use cases
4. **Iterate**: Refine based on usage patterns
5. **Document**: Create user-facing documentation

## Questions to Consider

1. **Pattern Complexity**: How complex should patterns be? (e.g., nested sequences)
2. **Performance Trade-offs**: Memory vs. speed for event history
3. **Error Handling**: How to handle pattern matching errors?
4. **Debugging**: How to debug pattern matching? (visualization tools?)
5. **Accessibility**: How to support assistive technologies?
6. **Platform Differences**: How to handle platform-specific events?

## References

- HumanInput.js: Flexible event binding library
- Scala 3 Metaprogramming: Macros, inline, type-level programming
- Reactive Streams: Event stream processing patterns
- State Machines: Pattern matching algorithms

## Conclusion

This system will enable Seer users to express complex event interactions in a concise, type-safe, and performant way. The combination of DSL, metaprogramming, and temporal awareness provides a powerful foundation for building interactive applications.
