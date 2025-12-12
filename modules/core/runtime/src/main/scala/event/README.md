# Seer Event System

A powerful, composable event system for Scala inspired by HumanInput.js, designed to handle complex event interactions across multiple modules including window events, keyboard and mouse inputs, trackpad gestures, and custom events.

## Features

- **Unified Event Hierarchy**: Type-safe event system with sealed traits
- **Event Composition**: Chain, sequence, and combine events with fluent API
- **Timing & History**: Built-in event history tracking and timing-based patterns
- **Cross-Module Integration**: Seamless integration across different system modules
- **Parameter System**: Reactive parameters that emit change events
- **Timeline System**: Temporal event sequences and scheduling
- **Advanced Patterns**: Gesture recognition, key combinations, and complex interactions

## Quick Start

```scala
import seer.event._

// Basic event handling
Events.subscribe(classOf[KeyboardEvent]) { event =>
  println(s"Key ${event.key} was ${event.action}")
}

// Event composition
Events.on(Events.Mouse.leftClick)
  .and(Events.Keyboard.keyHeld("ctrl"))
  .emit(Event.custom("ctrl_click", Map("action" -> "select")))

// Event sequences
Events.on(Events.Keyboard.keyDown("a"))
  .`then`(Events.Keyboard.keyDown("b"))
  .`then`(Events.Keyboard.keyDown("c"))
  .emit(Event.custom("abc_sequence"))

// Timing-based events
Events.on(Events.Mouse.leftDown)
  .within(1000) // Within 1 second
  .emit(Event.custom("quick_click"))
```

## Event Sources

### Mouse Events
```scala
Events.Mouse.leftClick
Events.Mouse.rightClick
Events.Mouse.leftDown
Events.Mouse.leftUp
Events.Mouse.move
Events.Mouse.drag
Events.Mouse.scroll("up")
```

### Keyboard Events
```scala
Events.Keyboard.key("a")
Events.Keyboard.keyDown("space")
Events.Keyboard.keyUp("enter")
Events.Keyboard.keyHeld("shift")
Events.Keyboard.ctrl
Events.Keyboard.alt
Events.Keyboard.shift
Events.Keyboard.meta
```

### Window Events
```scala
Events.Window.resize
Events.Window.close
Events.Window.focus
Events.Window.blur
Events.Window.move
```

### Trackpad Events
```scala
Events.Trackpad.pinch
Events.Trackpad.rotate
Events.Trackpad.swipe("left")
Events.Trackpad.tap
Events.Trackpad.twoFingerTap
```

## Event Composition

### Logical Operators
```scala
// AND composition
Events.on(Events.Mouse.leftClick).and(Events.Keyboard.ctrl)

// OR composition
Events.on(Events.Keyboard.keyDown("a")).or(Events.Keyboard.keyDown("A"))

// Sequence composition
Events.on(Events.Keyboard.keyDown("a")).`then`(Events.Keyboard.keyDown("b"))
```

### Timing
```scala
// Events within time window
Events.on(Events.Mouse.leftClick).within(500)

// Using timing builder
Events.within(1000).of(Events.Keyboard.keyDown("space"))
```

### Advanced Patterns
```scala
// Double-click detection
Events.on(EventPatterns.doubleClick())

// Long press detection
Events.on(EventPatterns.longPress())

// Key combinations
Events.on(EventPatterns.keyCombo("ctrl", "c"))

// Key sequences
Events.on(EventPatterns.keySequence("g", "g"))
```

## Parameter System

```scala
// Create reactive parameters
val volume = Parameter[Float]("audio/volume", 0.5f)
val brightness = Parameter[Float]("display/brightness", 0.8f)

// Listen to parameter changes
volume.onChange { newValue =>
  println(s"Volume changed to: $newValue")
}

// Bind parameters to events
Events.on(Events.Keyboard.keyDown("+"))
  .foreach { _ =>
    volume() = (volume() + 0.1f).min(1.0f)
  }
```

## Timeline System

```scala
// Create a timeline with events
val animationTimeline = Timeline()
  .addEvent(0, Event.custom("animation_start"))
  .addEvent(500, Event.custom("animation_mid"))
  .addEvent(1000, Event.custom("animation_end"))

// Schedule timeline execution
Scheduler.now(animationTimeline)

// Schedule delayed actions
Scheduler.schedule(2000) {
  println("This runs after 2 seconds")
}
```

## Event Filtering and Transformation

```scala
// Throttle events
val mouseThrottler = EventUtils.throttle[MouseEvent](100)
Events.subscribe(classOf[MouseEvent]) { event =>
  mouseThrottler(event).foreach { throttledEvent =>
    println(s"Throttled: ${throttledEvent.x}, ${throttledEvent.y}")
  }
}

// Debounce events
val resizeDebouncer = EventUtils.debounce[WindowEvent](300)

// Filter events
val keyFilter = EventUtils.filter[KeyboardEvent](_.key == "space")

// Transform events
val mouseTransformer = EventUtils.map[MouseEvent, String] { mouseEvent =>
  s"Mouse ${mouseEvent.button} at (${mouseEvent.x}, ${mouseEvent.y})"
}
```

## Cross-Module Integration

```scala
// Window events affecting graphics
Events.on(Events.Window.resize)
  .emit(Event.custom("graphics/resize", Map("module" -> "graphics")))

// Audio events affecting visuals
Events.on(Event.custom("audio/beat", Map("frequency" -> 440)))
  .emit(Event.custom("visuals/pulse", Map("intensity" -> 1.0)))

// Custom module events
Events.on(Event.custom("osc/message", Map("address" -> "/volume")))
  .emit(Event.custom("audio/volume_change"))
```

## Debugging and Introspection

```scala
// Enable event logging
EventDebug.logAll()

// Log specific event types
EventDebug.logType(classOf[KeyboardEvent])

// Show event statistics
EventDebug.stats()

// Access event history
val recentEvents = Events.history(5000) // Last 5 seconds
val lastKeyboardEvent = Events.lastEvent(classOf[KeyboardEvent])
```

## Architecture

The event system is built around several key components:

1. **Event Hierarchy**: Sealed trait `Event` with specific implementations for different event types
2. **Event Sources**: Composable event matchers that can be combined using logical operators
3. **Event Bus**: Centralized publish-subscribe mechanism with history tracking
4. **Builder Pattern**: Fluent API for event composition and handling
5. **Parameter System**: Reactive parameters that emit change events
6. **Timeline System**: Temporal event sequences and scheduling

## Examples

See `EventSystemExamples.scala` for comprehensive usage examples demonstrating all features of the event system.

## Integration with Seer Modules

The event system is designed to integrate seamlessly with other Seer modules:

- **Graphics**: Window events, mouse interactions, keyboard shortcuts
- **Audio**: Parameter changes, OSC messages, MIDI events
- **Runtime**: Custom events, system state changes
- **Extensions**: Multi-touch, video, and other specialized inputs

This unified approach allows for powerful cross-module event composition and interaction patterns that would be difficult to achieve with traditional callback-based systems.



