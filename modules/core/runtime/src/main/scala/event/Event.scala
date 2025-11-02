package seer.event

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.util.{Try, Success, Failure}

/**
 * Event utilities and extensions for the Seer event system
 * 
 * This module provides additional functionality for working with events,
 * including parameter binding, timeline management, and advanced composition patterns.
 */

object EventUtils {
  
  // Event creation helpers
  def window(action: String, width: Option[Int] = None, height: Option[Int] = None): WindowEvent = 
    WindowEvent(action, width, height)
  
  def keyboard(key: String, action: String, modifiers: Set[String] = Set.empty): KeyboardEvent = 
    KeyboardEvent(key, action, modifiers)
  
  def mouse(button: String, action: String, x: Int = 0, y: Int = 0, deltaX: Int = 0, deltaY: Int = 0): MouseEvent = 
    MouseEvent(button, action, x, y, deltaX, deltaY)
  
  def trackpad(gesture: String, details: Map[String, Any] = Map.empty): TrackpadEvent = 
    TrackpadEvent(gesture, details)
  
  def custom(name: String, data: Map[String, Any] = Map.empty): CustomEvent = 
    CustomEvent(name, data)
  
  // Event filtering and transformation utilities
  def filter[T <: Event](predicate: T => Boolean): EventFilter[T] = new EventFilter(predicate)
  
  def map[T <: Event, U](transform: T => U): EventMapper[T, U] = new EventMapper(transform)
  
  def throttle[T <: Event](duration: Long): EventThrottler[T] = new EventThrottler(duration)
  
  def debounce[T <: Event](duration: Long): EventDebouncer[T] = new EventDebouncer(duration)
}

// Event processing utilities
class EventFilter[T <: Event](predicate: T => Boolean) {
  def apply(event: Event): Option[T] = event match {
    case e: T if predicate(e) => Some(e)
    case _ => None
  }
}

class EventMapper[T <: Event, U](transform: T => U) {
  def apply(event: Event): Option[U] = event match {
    case e: T => Some(transform(e))
    case _ => None
  }
}

class EventThrottler[T <: Event](duration: Long) {
  private var lastEmitted: Long = 0
  
  def apply(event: Event): Option[T] = {
    val now = System.currentTimeMillis()
    if (now - lastEmitted >= duration) {
      lastEmitted = now
      event match {
        case e: T => Some(e)
        case _ => None
      }
    } else None
  }
}

class EventDebouncer[T <: Event](duration: Long) {
  private var lastEvent: Option[T] = None
  private var lastTime: Long = 0
  
  def apply(event: Event): Option[T] = {
    val now = System.currentTimeMillis()
    event match {
      case e: T => 
        lastEvent = Some(e)
        lastTime = now
        None // Don't emit immediately
      case _ => None
    }
  }
  
  def flush(): Option[T] = {
    val now = System.currentTimeMillis()
    if (lastEvent.isDefined && now - lastTime >= duration) {
      val result = lastEvent
      lastEvent = None
      result
    } else None
  }
}

// Parameter system for reactive programming
class Parameter[T](name: String, initialValue: T) {
  private var _value: T = initialValue
  private val changeListeners = mutable.Set.empty[T => Unit]
  
  def apply(): T = _value
  
  def update(newValue: T): Unit = {
    if (_value != newValue) {
      val oldValue = _value
      _value = newValue
      
      // Emit parameter change event
      Events.emit(CustomEvent(s"parameter_change", Map(
        "name" -> name,
        "oldValue" -> oldValue,
        "newValue" -> newValue
      )))
      
      // Notify listeners
      changeListeners.foreach(_(newValue))
    }
  }
  
  def onChange(handler: T => Unit): Unit = {
    changeListeners += handler
  }
  
  def removeChangeListener(handler: T => Unit): Unit = {
    changeListeners -= handler
  }
}

object Parameter {
  def apply[T](name: String, initialValue: T): Parameter[T] = new Parameter(name, initialValue)
}

// Timeline system for temporal event sequences
class Timeline {
  private val events = mutable.ArrayBuffer.empty[(Long, Event)]
  private var _isPlaying = false
  private var startTime: Long = 0
  private var currentTime: Long = 0
  
  def isPlaying: Boolean = _isPlaying
  
  def addEvent(timeOffset: Long, event: Event): Timeline = {
    events += ((timeOffset, event))
    this
  }
  
  def play(): Timeline = {
    _isPlaying = true
    startTime = System.currentTimeMillis()
    this
  }
  
  def pause(): Timeline = {
    _isPlaying = false
    this
  }
  
  def stop(): Timeline = {
    _isPlaying = false
    currentTime = 0
    this
  }
  
  def seek(time: Long): Timeline = {
    currentTime = time
    this
  }
  
  def update(): Unit = {
    if (_isPlaying) {
      val now = System.currentTimeMillis()
      val elapsed = now - startTime
      
      events.foreach { case (timeOffset, event) =>
        if (elapsed >= timeOffset && currentTime < timeOffset) {
          Events.emit(event)
        }
      }
      
      currentTime = elapsed
    }
  }
  
  def duration: Long = events.map(_._1).maxOption.getOrElse(0L)
}

object Timeline {
  def apply(): Timeline = new Timeline()
}

// Scheduler for managing timelines and temporal events
object Scheduler {
  private val timelines = mutable.Set.empty[Timeline]
  private var isRunning = false
  
  def now(timeline: Timeline): Unit = {
    timeline.play()
    timelines += timeline
  }
  
  def loop(timeline: Timeline): Unit = {
    timeline.play()
    timelines += timeline
    // Note: In a real implementation, you'd want to handle looping logic
  }
  
  def schedule(delay: Long)(action: => Unit): Unit = {
    val timeline = Timeline()
    timeline.addEvent(delay, CustomEvent("scheduled_action", Map("action" -> "delayed")))
    now(timeline)
  }
  
  def update(): Unit = {
    timelines.foreach(_.update())
    timelines.filter(t => t.duration > 0 && !t.isPlaying).foreach(timelines -= _)
  }
  
  def start(): Unit = {
    isRunning = true
    // In a real implementation, you'd start a background thread here
  }
  
  def stop(): Unit = {
    isRunning = false
    timelines.foreach(_.stop())
    timelines.clear()
  }
}

// Advanced event composition patterns
object EventPatterns {
  
  // Gesture recognition patterns
  def doubleClick(button: String = "left", maxInterval: Long = 500): EventSource = {
    val clickSource = Events.Mouse.leftClick
    val doubleClickSource = SequenceEventSource(Seq(clickSource, clickSource))
    TimingEventSource(doubleClickSource, maxInterval)
  }
  
  def longPress(button: String = "left", minDuration: Long = 1000): EventSource = {
    val pressSource = Events.Mouse.leftDown
    val releaseSource = Events.Mouse.leftUp
    val longPressSource = SequenceEventSource(Seq(pressSource, releaseSource))
    TimingEventSource(longPressSource, minDuration)
  }
  
  def keyCombo(keys: String*): EventSource = {
    val keySources = keys.map(Events.Keyboard.keyDown)
    CompositeEventSource(keySources, "and")
  }
  
  def keySequence(keys: String*): EventSource = {
    val keySources = keys.map(Events.Keyboard.keyDown)
    SequenceEventSource(keySources)
  }
  
  // Multi-touch patterns
  def pinchZoom: EventSource = Events.Trackpad.pinch
  def rotateGesture: EventSource = Events.Trackpad.rotate
  def swipeGesture(direction: String): EventSource = Events.Trackpad.swipe(direction)
  
  // Window interaction patterns
  def windowResize: EventSource = Events.Window.resize
  def windowClose: EventSource = Events.Window.close
  def windowFocus: EventSource = Events.Window.focus
}

// Event debugging and introspection
object EventDebug {
  def logAll(): Unit = {
    Events.subscribe(classOf[Event]) { event =>
      println(s"[${event.timestamp}] ${event.getClass.getSimpleName}: $event")
    }
  }
  
  def logType[T <: Event](eventType: Class[T]): Unit = {
    Events.subscribe(eventType) { event =>
      println(s"[${event.timestamp}] $event")
    }
  }
  
  def stats(): Unit = {
    val history = Events.history
    val byType = history.groupBy(_.getClass.getSimpleName)
    
    println("Event Statistics:")
    byType.foreach { case (typeName, events) =>
      println(s"  $typeName: ${events.length} events")
    }
    
    if (history.nonEmpty) {
      val timeSpan = history.last.timestamp - history.head.timestamp
      println(s"  Time span: ${timeSpan}ms")
      println(s"  Events per second: ${history.length * 1000.0 / timeSpan}")
    }
  }
}