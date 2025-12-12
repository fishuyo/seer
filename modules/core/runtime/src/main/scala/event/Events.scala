// package seer.event

// import scala.collection.mutable
// import scala.concurrent.{ExecutionContext, Future}
// import scala.util.{Try, Success, Failure}
// import java.util.concurrent.atomic.AtomicLong

// /**
//  * Unified Event System for Seer
//  * 
//  * Provides a powerful, composable event API inspired by HumanInput.js that allows:
//  * - Event composition and sequences
//  * - Cross-module event binding
//  * - Timing and history tracking
//  * - Fluent API for complex event patterns
//  */
// object Events {
  
//   // Global event bus instance
//   private val eventBus = new EventBus
//   private val eventHistory = new EventHistory(maxHistorySize = 1000)
//   private val eventIdGenerator = new AtomicLong(0)
  
//   // Main API entry points
//   def on(eventSource: EventSource): EventListenerBuilder = new EventListenerBuilder(eventSource)
//   def emit(event: Event): Unit = eventBus.publish(event)
//   def subscribe[T <: Event](eventType: Class[T])(handler: T => Unit): Unit = eventBus.subscribe(eventType)(handler)
//   def unsubscribe[T <: Event](eventType: Class[T])(handler: T => Unit): Unit = eventBus.unsubscribe(eventType)(handler)
  
//   // Event history and timing utilities
//   def history: Seq[Event] = eventHistory.getAll
//   def history(duration: Long): Seq[Event] = eventHistory.getSince(System.currentTimeMillis() - duration)
//   def lastEvent[T <: Event](eventType: Class[T]): Option[T] = eventHistory.getLast(eventType)
  
//   // Fluent API for event composition
//   def sequence(events: EventSource*): SequenceBuilder = new SequenceBuilder(events.toSeq)
//   def parallel(events: EventSource*): ParallelBuilder = new ParallelBuilder(events.toSeq)
//   def within(duration: Long): TimingBuilder = new TimingBuilder(duration)
  
//   // Event source factories
//   object Mouse {
//     def leftClick: EventSource = MouseEventSource("left", "click")
//     def rightClick: EventSource = MouseEventSource("right", "click")
//     def middleClick: EventSource = MouseEventSource("middle", "click")
//     def leftDown: EventSource = MouseEventSource("left", "down")
//     def leftUp: EventSource = MouseEventSource("left", "up")
//     def rightDown: EventSource = MouseEventSource("right", "down")
//     def rightUp: EventSource = MouseEventSource("right", "up")
//     def move: EventSource = MouseEventSource("move", "move")
//     def drag: EventSource = MouseEventSource("drag", "drag")
//     def scroll(direction: String): EventSource = MouseEventSource("scroll", direction)
//   }
  
//   object Keyboard {
//     def key(key: String): EventSource = KeyboardEventSource(key, "press")
//     def keyDown(key: String): EventSource = KeyboardEventSource(key, "down")
//     def keyUp(key: String): EventSource = KeyboardEventSource(key, "up")
//     def keyHeld(key: String): EventSource = KeyboardEventSource(key, "held")
//     def modifier(mod: String): EventSource = KeyboardEventSource(mod, "modifier")
//     def ctrl: EventSource = modifier("ctrl")
//     def alt: EventSource = modifier("alt")
//     def shift: EventSource = modifier("shift")
//     def meta: EventSource = modifier("meta")
//   }
  
//   object Window {
//     def resize: EventSource = WindowEventSource("resize")
//     def close: EventSource = WindowEventSource("close")
//     def focus: EventSource = WindowEventSource("focus")
//     def blur: EventSource = WindowEventSource("blur")
//     def move: EventSource = WindowEventSource("move")
//   }
  
//   object Trackpad {
//     def pinch: EventSource = TrackpadEventSource("pinch")
//     def rotate: EventSource = TrackpadEventSource("rotate")
//     def swipe(direction: String): EventSource = TrackpadEventSource(s"swipe_$direction")
//     def tap: EventSource = TrackpadEventSource("tap")
//     def twoFingerTap: EventSource = TrackpadEventSource("two_finger_tap")
//   }
  
//   // Custom event sources
//   def custom(name: String, data: Map[String, Any] = Map.empty): EventSource = CustomEventSource(name, data)
  
//   // Internal event bus implementation
//   private class EventBus {
//     private val subscribers = mutable.Map.empty[Class[_ <: Event], mutable.Set[Event => Unit]]
    
//     def subscribe[T <: Event](eventType: Class[T])(handler: T => Unit): Unit = {
//       val handlers = subscribers.getOrElseUpdate(eventType, mutable.Set.empty)
//       handlers += (handler.asInstanceOf[Event => Unit])
//     }
    
//     def unsubscribe[T <: Event](eventType: Class[T])(handler: T => Unit): Unit = {
//       subscribers.get(eventType).foreach { handlers =>
//         handlers -= handler.asInstanceOf[Event => Unit]
//       }
//     }
    
//     def publish(event: Event): Unit = {
//       // Add to history
//       eventHistory.add(event)
      
//       // Notify subscribers
//       subscribers.get(event.getClass).foreach { handlers =>
//         handlers.foreach { handler =>
//           Try(handler(event)) match {
//             case Success(_) => // Successfully handled
//             case Failure(e) => println(s"Error handling event $event: $e")
//           }
//         }
//       }
//     }
//   }
  
//   // Event history tracking
//   private class EventHistory(maxHistorySize: Int) {
//     private val events = mutable.ArrayBuffer.empty[Event]
//     private val lock = new Object
    
//     def add(event: Event): Unit = lock.synchronized {
//       events += event
//       if (events.length > maxHistorySize) {
//         events.remove(0)
//       }
//     }
    
//     def getAll: Seq[Event] = lock.synchronized(events.toSeq)
    
//     def getSince(timestamp: Long): Seq[Event] = lock.synchronized {
//       events.filter(_.timestamp >= timestamp).toSeq
//     }
    
//     def getLast[T <: Event](eventType: Class[T]): Option[T] = lock.synchronized {
//       events.reverse.find(_.getClass == eventType).map(_.asInstanceOf[T])
//     }
//   }
// }

// // Event hierarchy
// sealed trait Event {
//   val id: Long = System.currentTimeMillis() // Simple ID generation
//   val timestamp: Long = System.currentTimeMillis()
//   val source: String
// }

// case class WindowEvent(action: String, width: Option[Int] = None, height: Option[Int] = None, override val source: String = "window") extends Event
// case class KeyboardEvent(key: String, action: String, modifiers: Set[String] = Set.empty, override val source: String = "keyboard") extends Event
// case class MouseEvent(button: String, action: String, x: Int = 0, y: Int = 0, deltaX: Int = 0, deltaY: Int = 0, override val source: String = "mouse") extends Event
// case class TrackpadEvent(gesture: String, details: Map[String, Any] = Map.empty, override val source: String = "trackpad") extends Event
// case class CustomEvent(name: String, data: Map[String, Any] = Map.empty, override val source: String = "custom") extends Event

// // Event sources for composition
// sealed trait EventSource {
//   def matches(event: Event): Boolean
//   def description: String
// }

// case class MouseEventSource(button: String, action: String) extends EventSource {
//   def matches(event: Event): Boolean = event match {
//     case MouseEvent(b, a, _, _, _, _, _) => b == button && a == action
//     case _ => false
//   }
//   def description: String = s"Mouse.$button.$action"
// }

// case class KeyboardEventSource(key: String, action: String) extends EventSource {
//   def matches(event: Event): Boolean = event match {
//     case KeyboardEvent(k, a, _, _) => k == key && a == action
//     case _ => false
//   }
//   def description: String = s"Keyboard.$key.$action"
// }

// case class WindowEventSource(action: String) extends EventSource {
//   def matches(event: Event): Boolean = event match {
//     case WindowEvent(a, _, _, _) => a == action
//     case _ => false
//   }
//   def description: String = s"Window.$action"
// }

// case class TrackpadEventSource(gesture: String) extends EventSource {
//   def matches(event: Event): Boolean = event match {
//     case TrackpadEvent(g, _, _) => g == gesture
//     case _ => false
//   }
//   def description: String = s"Trackpad.$gesture"
// }

// case class CustomEventSource(name: String, data: Map[String, Any] = Map.empty) extends EventSource {
//   def matches(event: Event): Boolean = event match {
//     case CustomEvent(n, d, _) => n == name && (data.isEmpty || data == d)
//     case _ => false
//   }
//   def description: String = s"Custom.$name"
// }

// // Builder classes for fluent API
// class EventListenerBuilder(eventSource: EventSource) {
//   def and(other: EventSource): CompositeEventSource = CompositeEventSource(Seq(eventSource, other))
//   def or(other: EventSource): CompositeEventSource = CompositeEventSource(Seq(eventSource, other), mode = "or")
//   def then(other: EventSource): SequenceEventSource = SequenceEventSource(Seq(eventSource, other))
//   def within(duration: Long): TimingEventSource = TimingEventSource(eventSource, duration)
  
//   def emit(event: Event): Unit = {
//     Events.subscribe(classOf[Event]) { e =>
//       if (eventSource.matches(e)) {
//         Events.emit(event)
//       }
//     }
//   }
  
//   def foreach(handler: Event => Unit): Unit = {
//     Events.subscribe(classOf[Event]) { e =>
//       if (eventSource.matches(e)) {
//         handler(e)
//       }
//     }
//   }
// }

// case class CompositeEventSource(sources: Seq[EventSource], mode: String = "and") extends EventSource {
//   def matches(event: Event): Boolean = mode match {
//     case "and" => sources.forall(_.matches(event))
//     case "or" => sources.exists(_.matches(event))
//     case _ => false
//   }
//   def description: String = s"Composite(${sources.map(_.description).mkString(s" $mode ")})"
// }

// case class SequenceEventSource(sources: Seq[EventSource]) extends EventSource {
//   private val eventHistory = mutable.Queue.empty[Event]
//   private val maxHistorySize = 100
  
//   def matches(event: Event): Boolean = {
//     eventHistory += event
//     if (eventHistory.length > maxHistorySize) {
//       eventHistory.dequeue()
//     }
    
//     // Check if we have a matching sequence in recent history
//     val recentEvents = eventHistory.toSeq
//     if (recentEvents.length >= sources.length) {
//       val recentSlice = recentEvents.takeRight(sources.length)
//       recentSlice.zip(sources).forall { case (e, source) => source.matches(e) }
//     } else false
//   }
  
//   def description: String = s"Sequence(${sources.map(_.description).mkString(" -> ")})"
// }

// case class TimingEventSource(source: EventSource, duration: Long) extends EventSource {
//   private var lastMatchTime: Long = 0
  
//   def matches(event: Event): Boolean = {
//     val now = System.currentTimeMillis()
//     if (source.matches(event)) {
//       lastMatchTime = now
//       true
//     } else {
//       now - lastMatchTime <= duration
//     }
//   }
  
//   def description: String = s"${source.description}.within(${duration}ms)"
// }

// class SequenceBuilder(sources: Seq[EventSource]) {
//   def emit(event: Event): Unit = {
//     val sequenceSource = SequenceEventSource(sources)
//     Events.subscribe(classOf[Event]) { e =>
//       if (sequenceSource.matches(e)) {
//         Events.emit(event)
//       }
//     }
//   }
  
//   def foreach(handler: Event => Unit): Unit = {
//     val sequenceSource = SequenceEventSource(sources)
//     Events.subscribe(classOf[Event]) { e =>
//       if (sequenceSource.matches(e)) {
//         handler(e)
//       }
//     }
//   }
// }

// class ParallelBuilder(sources: Seq[EventSource]) {
//   def emit(event: Event): Unit = {
//     val parallelSource = CompositeEventSource(sources, "or")
//     Events.subscribe(classOf[Event]) { e =>
//       if (parallelSource.matches(e)) {
//         Events.emit(event)
//       }
//     }
//   }
  
//   def foreach(handler: Event => Unit): Unit = {
//     val parallelSource = CompositeEventSource(sources, "or")
//     Events.subscribe(classOf[Event]) { e =>
//       if (parallelSource.matches(e)) {
//         handler(e)
//       }
//     }
//   }
// }

// class TimingBuilder(duration: Long) {
//   def of(source: EventSource): TimingEventSource = TimingEventSource(source, duration)
// }





// // api brainstorm usage...
// // Events encompassses an Event hub/amanager/matrix/mixer that we use to bind callbacks from various modules to a single api
// // one potential goal is to provide an slick high level api for listening to event sequences or... something like HumanInput.js library?
// //
// // specification
// // - must be able to listen to events from various sources ie we bind callbacks to event api ```
// // window.onResize = (width,height) => {
// //   println(s"window resized to $width x $height")
// //   Events.emit(Event("windowResized", width, height))
// // }
// //```scala

// // - .. ie Events.on(Mouse.leftClick).and(Keyboard.keyHeld('a')).emit(Event("myevent"))
// // - internally track timing and history for some duration to enable listening to complex event sequences or compositions 
// // - provide a way to listen to events from a source that is not a callback, ie Events.on(Event("windowResized")).foreach{...}

// // Events.foreach { 
//   // case Window.Resize()
// // }
// // Events.collelct(10).map {
//   // case Seq() => ??
// //}
