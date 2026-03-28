# Event Binding System: Technical Design

## Core Abstractions

### 1. Event Pattern Hierarchy

```scala
package seer.events

import seer._

/**
 * Base trait for all event patterns.
 * Patterns can match single events, combinations, sequences, or temporal patterns.
 */
sealed trait EventPattern[+T <: Event] {
  /** Check if this pattern matches given event history */
  def matches(history: EventHistory): Option[T]
  
  /** Compile pattern to optimized matcher */
  def compile(): CompiledPattern[T]
  
  /** Human-readable pattern description */
  def description: String
}

/**
 * Matches a single event type with optional predicate
 */
case class SinglePattern[T <: Event](
  eventType: Class[T],
  predicate: T => Boolean = (_: T) => true
) extends EventPattern[T] {
  def matches(history: EventHistory): Option[T] = {
    history.recent.find { e =>
      eventType.isInstance(e) && predicate(e.asInstanceOf[T])
    }.map(_.asInstanceOf[T])
  }
  
  def compile(): CompiledPattern[T] = CompiledSinglePattern(this)
  
  def description: String = eventType.getSimpleName
}

/**
 * Logical AND - both patterns must match
 */
case class AndPattern[T1 <: Event, T2 <: Event, R <: Event](
  left: EventPattern[T1],
  right: EventPattern[T2],
  combiner: (T1, T2) => R
) extends EventPattern[R] {
  def matches(history: EventHistory): Option[R] = {
    for {
      l <- left.matches(history)
      r <- right.matches(history)
    } yield combiner(l, r)
  }
  
  def compile(): CompiledPattern[R] = CompiledAndPattern(this)
  
  def description: String = s"(${left.description} AND ${right.description})"
}

/**
 * Logical OR - either pattern matches
 */
case class OrPattern[T <: Event](
  patterns: Seq[EventPattern[T]]
) extends EventPattern[T] {
  def matches(history: EventHistory): Option[T] = {
    patterns.flatMap(_.matches(history)).headOption
  }
  
  def compile(): CompiledPattern[T] = CompiledOrPattern(this)
  
  def description: String = patterns.map(_.description).mkString("(", " OR ", ")")
}

/**
 * Sequence pattern - events must occur in order
 */
case class SequencePattern[T <: Event](
  patterns: Seq[EventPattern[_ <: Event]],
  timeout: Long = 5000, // milliseconds
  combiner: Seq[Event] => T
) extends EventPattern[T] {
  def matches(history: EventHistory): Option[T] = {
    // Implementation would use state machine
    // to track partial matches
    None // Placeholder
  }
  
  def compile(): CompiledPattern[T] = CompiledSequencePattern(this)
  
  def description: String = patterns.map(_.description).mkString(" then ")
}

/**
 * Temporal pattern - event must occur within time window
 */
case class TemporalPattern[T <: Event](
  pattern: EventPattern[T],
  window: Long, // milliseconds
  operator: TemporalOperator
) extends EventPattern[T] {
  sealed trait TemporalOperator
  case object Within extends TemporalOperator // within window
  case object After extends TemporalOperator  // after delay
  case object For extends TemporalOperator    // held for duration
  
  def matches(history: EventHistory): Option[T] = {
    val now = Timestamp.now()
    val windowStart = now - window
    
    operator match {
      case Within =>
        history.inWindow(windowStart, now).flatMap(pattern.matches)
      case After =>
        // Check if pattern matches after delay
        None // Placeholder
      case For =>
        // Check if event held for duration
        None // Placeholder
    }
  }
  
  def compile(): CompiledPattern[T] = CompiledTemporalPattern(this)
  
  def description: String = s"${pattern.description} ${operator} ${window}ms"
}
```

### 2. Event History

```scala
package seer.events

import seer._
import collection.mutable

/**
 * Maintains a sliding window of recent events for pattern matching
 */
class EventHistory(maxAge: Long = 5000) { // 5 seconds default
  private val events = mutable.ArrayBuffer[Event]()
  private var lastCleanup = Timestamp.now()
  
  /** Add event to history */
  def add(event: Event): Unit = {
    events += event
    cleanup()
  }
  
  /** Get all recent events */
  def recent: Seq[Event] = {
    cleanup()
    events.toSeq
  }
  
  /** Get events in time window */
  def inWindow(start: Long, end: Long): EventHistory = {
    val filtered = new EventHistory(maxAge)
    events.filter { e =>
      e.timestamp >= start && e.timestamp <= end
    }.foreach(filtered.add)
    filtered
  }
  
  /** Get events of specific type */
  def ofType[T <: Event](implicit ct: scala.reflect.ClassTag[T]): Seq[T] = {
    events.collect {
      case e if ct.runtimeClass.isInstance(e) => e.asInstanceOf[T]
    }
  }
  
  /** Clean up old events */
  private def cleanup(): Unit = {
    val now = Timestamp.now()
    if (now - lastCleanup > 1000) { // Cleanup every second
      val cutoff = now - maxAge
      events.filterInPlace(_.timestamp >= cutoff)
      lastCleanup = now
    }
  }
}
```

### 3. Pattern Matcher

```scala
package seer.events

import seer._

/**
 * Efficient pattern matcher using compiled patterns
 */
class PatternMatcher {
  private val activePatterns = mutable.ArrayBuffer[CompiledPattern[_]]()
  private val history = new EventHistory()
  
  /** Register a pattern */
  def register[T <: Event](pattern: EventPattern[T]): CompiledPattern[T] = {
    val compiled = pattern.compile()
    activePatterns += compiled
    compiled.asInstanceOf[CompiledPattern[T]]
  }
  
  /** Process new event */
  def process(event: Event): Seq[Event] = {
    history.add(event)
    
    // Check all active patterns
    activePatterns.flatMap { pattern =>
      pattern.matches(history).toSeq
    }
  }
  
  /** Unregister pattern */
  def unregister(pattern: CompiledPattern[_]): Unit = {
    activePatterns -= pattern
  }
}

/**
 * Compiled pattern for efficient matching
 */
trait CompiledPattern[T <: Event] {
  def matches(history: EventHistory): Option[T]
  def description: String
}

case class CompiledSinglePattern[T <: Event](pattern: SinglePattern[T])
  extends CompiledPattern[T] {
  def matches(history: EventHistory): Option[T] = pattern.matches(history)
  def description: String = pattern.description
}

case class CompiledAndPattern[T <: Event](pattern: AndPattern[_, _, T])
  extends CompiledPattern[T] {
  def matches(history: EventHistory): Option[T] = pattern.matches(history)
  def description: String = pattern.description
}

case class CompiledOrPattern[T <: Event](pattern: OrPattern[T])
  extends CompiledPattern[T] {
  def matches(history: EventHistory): Option[T] = pattern.matches(history)
  def description: String = pattern.description
}

case class CompiledSequencePattern[T <: Event](pattern: SequencePattern[T])
  extends CompiledPattern[T] {
  private val stateMachine = new SequenceStateMachine(pattern)
  
  def matches(history: EventHistory): Option[T] = {
    stateMachine.process(history.recent)
  }
  
  def description: String = pattern.description
}

case class CompiledTemporalPattern[T <: Event](pattern: TemporalPattern[T])
  extends CompiledPattern[T] {
  def matches(history: EventHistory): Option[T] = pattern.matches(history)
  def description: String = pattern.description
}
```

### 4. DSL for Pattern Construction

```scala
package seer.events

import seer._

/**
 * DSL for constructing event patterns
 */
object EventPatternDSL {
  
  // Implicit conversions for fluent API
  implicit class KeyPatternBuilder(key: Key) {
    def + (mod: KeyModifiers): EventPattern[KeyPressed] = {
      SinglePattern(
        classOf[KeyPressed],
        (e: KeyPressed) => e.key == key && matchesModifiers(e.mods, mod)
      )
    }
    
    private def matchesModifiers(actual: KeyModifiers, required: KeyModifiers): Boolean = {
      (!required.shift || actual.shift) &&
      (!required.control || actual.control) &&
      (!required.alt || actual.alt) &&
      (!required.superKey || actual.superKey)
    }
  }
  
  implicit class PatternOps[T <: Event](pattern: EventPattern[T]) {
    def + (other: EventPattern[_ <: Event]): EventPattern[T] = {
      AndPattern(pattern, other, (a: T, b: Event) => a)
    }
    
    def | (other: EventPattern[T]): EventPattern[T] = {
      OrPattern(Seq(pattern, other))
    }
    
    def then (next: EventPattern[_ <: Event]): SequenceBuilder[T] = {
      SequenceBuilder(Seq(pattern, next))
    }
    
    def within(duration: Long): TemporalPattern[T] = {
      TemporalPattern(pattern, duration, TemporalPattern.Within)
    }
    
    def after(duration: Long): TemporalPattern[T] = {
      TemporalPattern(pattern, duration, TemporalPattern.After)
    }
    
    def forDuration(duration: Long): TemporalPattern[T] = {
      TemporalPattern(pattern, duration, TemporalPattern.For)
    }
  }
  
  case class SequenceBuilder[T <: Event](patterns: Seq[EventPattern[_ <: Event]]) {
    def then(next: EventPattern[_ <: Event]): SequenceBuilder[T] = {
      SequenceBuilder(patterns :+ next)
    }
    
    def within(timeout: Long): SequencePattern[T] = {
      SequencePattern(
        patterns,
        timeout,
        (events: Seq[Event]) => events.last.asInstanceOf[T]
      )
    }
  }
  
  // Time units
  implicit class LongTimeOps(ms: Long) {
    def ms: Long = ms
    def seconds: Long = ms * 1000
    def minutes: Long = ms * 60000
  }
  
  // Mouse patterns
  object Mouse {
    val LeftClick = SinglePattern(
      classOf[MousePressed],
      (e: MousePressed) => e.button == MouseLeft
    )
    
    val RightClick = SinglePattern(
      classOf[MousePressed],
      (e: MousePressed) => e.button == MouseRight
    )
    
    val Drag = SinglePattern(
      classOf[MouseMoved],
      (_: MouseMoved) => true // Would check if button is held
    )
  }
  
  // Modifier helpers
  object Modifier {
    val Control = KeyModifiers(control = true)
    val Shift = KeyModifiers(shift = true)
    val Alt = KeyModifiers(alt = true)
    val Super = KeyModifiers(superKey = true)
  }
}
```

### 5. Event Binding API

```scala
package seer.events

import seer._

/**
 * Event binding manager integrated with runtime
 */
class EventBindingManager(runtime: SeerRuntime) {
  private val matcher = new PatternMatcher()
  private val bindings = mutable.HashMap[CompiledPattern[_], Event => Unit]()
  
  // Subscribe to all events for pattern matching
  runtime.subscribe[Event] { event =>
    val matches = matcher.process(event)
    matches.foreach { matchedEvent =>
      bindings.get(matchedEvent.getClass).foreach(_(matchedEvent))
    }
  }
  
  /**
   * Bind a pattern to a handler
   */
  def bind[T <: Event](pattern: EventPattern[T])(handler: T => Unit): Subscription = {
    val compiled = matcher.register(pattern)
    bindings(compiled) = handler.asInstanceOf[Event => Unit]
    
    new Subscription {
      def unsubscribe(): Unit = {
        matcher.unregister(compiled)
        bindings -= compiled
      }
    }
  }
  
  /**
   * Bind from string pattern (e.g., "ctrl+c")
   */
  def bind(pattern: String)(handler: Event => Unit): Subscription = {
    val parsed = PatternParser.parse(pattern)
    bind(parsed)(handler)
  }
}

/**
 * Pattern parser for string-based patterns
 */
object PatternParser {
  def parse(pattern: String): EventPattern[Event] = {
    // Parse patterns like:
    // "ctrl+c" -> Key("c") + Modifier.Control
    // "shift+click" -> Mouse.LeftClick + Modifier.Shift
    // "ctrl+shift+z" -> Key("z") + Control + Shift
    
    val parts = pattern.split("\\+")
    // Implementation would parse and construct pattern
    // This is a simplified version
    SinglePattern(classOf[Event], (_: Event) => true)
  }
}

/**
 * Extension methods for runtime
 */
implicit class RuntimeEventBinding(runtime: SeerRuntime) {
  private lazy val bindingManager = new EventBindingManager(runtime)
  
  def bind[T <: Event](pattern: EventPattern[T])(handler: T => Unit): Subscription = {
    bindingManager.bind(pattern)(handler)
  }
  
  def bind(pattern: String)(handler: Event => Unit): Subscription = {
    bindingManager.bind(pattern)(handler)
  }
}
```

### 6. Sequence State Machine

```scala
package seer.events

import seer._

/**
 * State machine for matching event sequences
 */
class SequenceStateMachine[T <: Event](pattern: SequencePattern[T]) {
  private var currentState = 0
  private var lastMatchTime = 0L
  
  def process(events: Seq[Event]): Option[T] = {
    val now = Timestamp.now()
    
    // Check timeout
    if (lastMatchTime > 0 && now - lastMatchTime > pattern.timeout) {
      reset()
      return None
    }
    
    // Process events in order
    for (event <- events) {
      if (currentState < pattern.patterns.length) {
        val currentPattern = pattern.patterns(currentState)
        currentPattern.matches(new EventHistory().tap(_.add(event))) match {
          case Some(_) =>
            currentState += 1
            lastMatchTime = event.timestamp
            
            // Check if sequence complete
            if (currentState >= pattern.patterns.length) {
              val result = pattern.combiner(events.take(currentState))
              reset()
              return Some(result)
            }
          case None =>
            // No match, but don't reset (might be partial match)
        }
      }
    }
    
    None
  }
  
  private def reset(): Unit = {
    currentState = 0
    lastMatchTime = 0L
  }
}
```

## Usage Examples

### Example 1: Keyboard Shortcuts

```scala
import seer._
import seer.events._
import EventPatternDSL._

val runtime = new SeerRuntime()
// ... setup modules ...

// Simple shortcut
runtime.bind(Key("c") + Modifier.Control) { event =>
  copyToClipboard()
}

// Complex shortcut
runtime.bind(Key("z") + Modifier.Control + Modifier.Shift) { event =>
  redo()
}

// String-based binding
runtime.bind("ctrl+v") { event =>
  pasteFromClipboard()
}
```

### Example 2: Mouse Gestures

```scala
// Double click
runtime.bind(Mouse.LeftClick then Mouse.LeftClick within 500.ms) { events =>
  openFile(events.head.target)
}

// Long press
runtime.bind(Mouse.LeftClick forDuration 1000.ms) { event =>
  showContextMenu(event.x, event.y)
}
```

### Example 3: Konami Code

```scala
val konami = 
  Key("up") then Key("up") then Key("down") then Key("down")
  then Key("left") then Key("right") then Key("left") then Key("right")
  then Key("b") then Key("a") within 5000.ms

runtime.bind(konami) { events =>
  activateCheatMode()
}
```

### Example 4: Stream Processing

```scala
// Throttled mouse movement
runtime.events[MouseMoved]
  .throttle(16.ms)
  .subscribe { event =>
    updateCursor(event.x, event.y)
  }

// Debounced resize
runtime.events[WindowResized]
  .debounce(300.ms)
  .subscribe { event =>
    recalculateLayout(event.width, event.height)
  }
```

## Integration with Existing System

The new event binding system integrates seamlessly with the existing `SeerRuntime`:

1. **Backward Compatible**: Existing `subscribe[T]` API continues to work
2. **Additive**: New `bind` API adds pattern matching capabilities
3. **Shared Event Bus**: Uses same event bus, adds pattern matching layer
4. **Type Safe**: Maintains type safety throughout

## Performance Optimizations

1. **Pattern Compilation**: Patterns compiled at registration time
2. **Early Filtering**: Filter events by type before pattern matching
3. **Lazy Evaluation**: Only process events when patterns active
4. **Memory Management**: Bounded event history, automatic cleanup
5. **Parallel Matching**: Match multiple patterns concurrently

## Next Steps

1. Implement core `EventPattern` hierarchy
2. Build `EventHistory` with sliding window
3. Create `PatternMatcher` with basic matching
4. Add DSL for pattern construction
5. Integrate with `SeerRuntime`
6. Add string-based pattern parsing
7. Implement temporal patterns
8. Add gesture recognition
9. Build stream processing operators
