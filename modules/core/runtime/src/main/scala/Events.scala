package seer

/**
 * Unified event system for Seer runtime modules.
 * Events are used for module lifecycle, window management, and input handling.
 */

sealed trait Event {
  def timestamp: Long
}

// Module Lifecycle Events
sealed trait ModuleEvent extends Event {
  def module: Module
}

case class ModuleCreated(module: Module, timestamp: Long = Timestamp.now()) extends ModuleEvent
case class ModuleDestroyed(module: Module, timestamp: Long = Timestamp.now()) extends ModuleEvent
case class ModuleStarted(module: Module, timestamp: Long = Timestamp.now()) extends ModuleEvent
case class ModuleStopped(module: Module, timestamp: Long = Timestamp.now()) extends ModuleEvent

// Window Events
sealed trait WindowEvent extends Event {
  def windowId: String
}

case class WindowCreated(windowId: String, width: Int, height: Int, timestamp: Long = Timestamp.now()) extends WindowEvent
case class WindowDestroyed(windowId: String, timestamp: Long = Timestamp.now()) extends WindowEvent
case class WindowResized(windowId: String, width: Int, height: Int, timestamp: Long = Timestamp.now()) extends WindowEvent
case class WindowClosed(windowId: String, timestamp: Long = Timestamp.now()) extends WindowEvent
case class WindowFocused(windowId: String, timestamp: Long = Timestamp.now()) extends WindowEvent
case class WindowUnfocused(windowId: String, timestamp: Long = Timestamp.now()) extends WindowEvent

// Key Modifiers
case class KeyModifiers(
  shift: Boolean = false,
  control: Boolean = false,
  alt: Boolean = false,
  superKey: Boolean = false,
  capsLock: Boolean = false,
  numLock: Boolean = false
)

// Key representation (platform-agnostic)
case class Key(
  name: String,      // e.g., "A", "Space", "Escape"
  code: Int,          // platform-specific key code
  scanCode: Int = 0   // platform-specific scan code
)

object Key {
  // Common keys
  val SPACE = Key("Space", 32)
  val ESCAPE = Key("Escape", 27)
  val ENTER = Key("Enter", 13)
  val TAB = Key("Tab", 9)
  val BACKSPACE = Key("Backspace", 8)
  val DELETE = Key("Delete", 127)
  val UP = Key("ArrowUp", 38)
  val DOWN = Key("ArrowDown", 40)
  val LEFT = Key("ArrowLeft", 37)
  val RIGHT = Key("ArrowRight", 39)
  
  // Function keys
  val F1 = Key("F1", 112)
  val F2 = Key("F2", 113)
  val F3 = Key("F3", 114)
  val F4 = Key("F4", 115)
  val F5 = Key("F5", 116)
  val F6 = Key("F6", 117)
  val F7 = Key("F7", 118)
  val F8 = Key("F8", 119)
  val F9 = Key("F9", 120)
  val F10 = Key("F10", 121)
  val F11 = Key("F11", 122)
  val F12 = Key("F12", 123)
}

// Mouse Button
sealed trait MouseButton
case object MouseLeft extends MouseButton
case object MouseRight extends MouseButton
case object MouseMiddle extends MouseButton
case object MouseButton4 extends MouseButton
case object MouseButton5 extends MouseButton

// Input Events
sealed trait InputEvent extends Event {
  def windowId: String
}

case class KeyPressed(
  windowId: String,
  key: Key,
  mods: KeyModifiers,
  timestamp: Long = Timestamp.now()
) extends InputEvent

case class KeyReleased(
  windowId: String,
  key: Key,
  mods: KeyModifiers,
  timestamp: Long = Timestamp.now()
) extends InputEvent

case class KeyRepeated(
  windowId: String,
  key: Key,
  mods: KeyModifiers,
  timestamp: Long = Timestamp.now()
) extends InputEvent

case class MouseMoved(
  windowId: String,
  x: Double,
  y: Double,
  dx: Double,
  dy: Double,
  timestamp: Long = Timestamp.now()
) extends InputEvent

case class MousePressed(
  windowId: String,
  button: MouseButton,
  x: Double,
  y: Double,
  mods: KeyModifiers,
  timestamp: Long = Timestamp.now()
) extends InputEvent

case class MouseReleased(
  windowId: String,
  button: MouseButton,
  x: Double,
  y: Double,
  mods: KeyModifiers,
  timestamp: Long = Timestamp.now()
) extends InputEvent

case class MouseScrolled(
  windowId: String,
  x: Double,
  y: Double,
  scrollX: Double,
  scrollY: Double,
  timestamp: Long = Timestamp.now()
) extends InputEvent

// Event Subscription
trait Subscription {
  def unsubscribe(): Unit
}

// Event Bus
trait EventBus {
  def subscribe[T <: Event](handler: T => Unit): Subscription
  def publish(event: Event): Unit
  def unsubscribe(subscription: Subscription): Unit
}

class SimpleEventBus extends EventBus {
  private val handlers = collection.mutable.HashMap[Class[_], collection.mutable.ArrayBuffer[Event => Unit]]()
  private var subscriptionIdCounter = 0
  
  case class SimpleSubscription(id: Int, eventType: Class[_], handler: Event => Unit) extends Subscription {
    def unsubscribe(): Unit = {
      handlers.get(eventType).foreach(_.filterInPlace(_ ne handler))
    }
  }
  
  def subscribe[T <: Event](handler: T => Unit): Subscription = {
    // Use a wrapper to capture the type
    val wrapper: Event => Unit = { event =>
      if (event.isInstanceOf[T]) {
        handler(event.asInstanceOf[T])
      }
    }
    
    // Store handlers by the actual event class
    // We'll match on the event's class when publishing
    val eventType = classOf[Event] // Store all handlers under Event, filter by type at runtime
    handlers.getOrElseUpdate(eventType, collection.mutable.ArrayBuffer()) += wrapper
    subscriptionIdCounter += 1
    SimpleSubscription(subscriptionIdCounter, eventType, wrapper)
  }
  
  def publish(event: Event): Unit = {
    // Call all handlers that match the event type
    handlers.get(classOf[Event]).foreach { handlers =>
      handlers.foreach(_(event))
    }
  }
  
  def unsubscribe(subscription: Subscription): Unit = {
    subscription.unsubscribe()
  }
}
