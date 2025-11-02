package seer.event

import seer.event._

/**
 * Comprehensive examples demonstrating the power and expressivity of the Seer Event System
 * 
 * This file showcases various patterns and capabilities inspired by HumanInput.js
 * and demonstrates how to compose complex event interactions across multiple modules.
 */
object EventSystemExamples {
  
  def basicEventHandling(): Unit = {
    println("=== Basic Event Handling ===")
    
    // Simple event subscription
    Events.subscribe(classOf[WindowEvent]) { event =>
      println(s"Window event: ${event.action}")
    }
    
    Events.subscribe(classOf[KeyboardEvent]) { event =>
      println(s"Key ${event.key} was ${event.action}")
    }
    
    // Emit some test events
    Events.emit(Event.window("resize", Some(800), Some(600)))
    Events.emit(Event.keyboard("a", "press"))
    Events.emit(Event.keyboard("ctrl", "down"))
  }
  
  def fluentEventComposition(): Unit = {
    println("\n=== Fluent Event Composition ===")
    
    // Mouse and keyboard combinations
    Events.on(Events.Mouse.leftClick)
      .and(Events.Keyboard.keyHeld("ctrl"))
      .emit(Event.custom("ctrl_click", Map("action" -> "select")))
    
    // Event sequences
    Events.on(Events.Keyboard.keyDown("a"))
      .then(Events.Keyboard.keyDown("b"))
      .then(Events.Keyboard.keyDown("c"))
      .emit(Event.custom("abc_sequence", Map("pattern" -> "keyboard_shortcut")))
    
    // Timing-based events
    Events.on(Events.Mouse.leftDown)
      .within(1000) // Within 1 second
      .emit(Event.custom("quick_click", Map("duration" -> "short")))
    
    // Listen to composed events
    Events.subscribe(classOf[CustomEvent]) { event =>
      println(s"Composed event: ${event.name} - ${event.data}")
    }
  }
  
  def advancedPatterns(): Unit = {
    println("\n=== Advanced Event Patterns ===")
    
    // Double-click detection
    Events.on(EventPatterns.doubleClick())
      .emit(Event.custom("double_click", Map("action" -> "open")))
    
    // Long press detection
    Events.on(EventPatterns.longPress())
      .emit(Event.custom("long_press", Map("action" -> "context_menu")))
    
    // Key combinations
    Events.on(EventPatterns.keyCombo("ctrl", "c"))
      .emit(Event.custom("copy", Map("action" -> "copy_to_clipboard")))
    
    Events.on(EventPatterns.keyCombo("ctrl", "v"))
      .emit(Event.custom("paste", Map("action" -> "paste_from_clipboard")))
    
    // Key sequences
    Events.on(EventPatterns.keySequence("g", "g"))
      .emit(Event.custom("vim_gg", Map("action" -> "go_to_top")))
    
    // Trackpad gestures
    Events.on(EventPatterns.pinchZoom)
      .emit(Event.custom("zoom", Map("gesture" -> "pinch")))
    
    Events.on(EventPatterns.swipeGesture("left"))
      .emit(Event.custom("swipe_left", Map("action" -> "previous_page")))
  }
  
  def parameterSystem(): Unit = {
    println("\n=== Parameter System ===")
    
    // Create reactive parameters
    val volume = Parameter[Float]("audio/volume", 0.5f)
    val brightness = Parameter[Float]("display/brightness", 0.8f)
    val speed = Parameter[Int]("animation/speed", 100)
    
    // Listen to parameter changes
    volume.onChange { newValue =>
      println(s"Volume changed to: $newValue")
    }
    
    brightness.onChange { newValue =>
      println(s"Brightness changed to: $newValue")
    }
    
    // Bind parameters to events
    Events.on(Events.Keyboard.keyDown("+"))
      .foreach { _ =>
        volume() = (volume() + 0.1f).min(1.0f)
      }
    
    Events.on(Events.Keyboard.keyDown("-"))
      .foreach { _ =>
        volume() = (volume() - 0.1f).max(0.0f)
      }
    
    // Simulate parameter changes
    volume() = 0.7f
    brightness() = 0.9f
    speed() = 150
  }
  
  def timelineSystem(): Unit = {
    println("\n=== Timeline System ===")
    
    // Create a timeline with events
    val animationTimeline = Timeline()
      .addEvent(0, Event.custom("animation_start", Map("type" -> "fade_in")))
      .addEvent(500, Event.custom("animation_mid", Map("type" -> "scale_up")))
      .addEvent(1000, Event.custom("animation_end", Map("type" -> "fade_out")))
    
    // Schedule timeline execution
    Scheduler.now(animationTimeline)
    
    // Create a looping timeline
    val heartbeatTimeline = Timeline()
      .addEvent(0, Event.custom("heartbeat", Map("beat" -> 1)))
      .addEvent(500, Event.custom("heartbeat", Map("beat" -> 2)))
      .addEvent(1000, Event.custom("heartbeat", Map("beat" -> 3)))
    
    Scheduler.loop(heartbeatTimeline)
    
    // Schedule delayed actions
    Scheduler.schedule(2000) {
      println("This runs after 2 seconds")
    }
    
    // Listen to timeline events
    Events.subscribe(classOf[CustomEvent]) { event =>
      if (event.name == "heartbeat") {
        println(s"Heartbeat: ${event.data("beat")}")
      }
    }
  }
  
  def eventFilteringAndTransformation(): Unit = {
    println("\n=== Event Filtering and Transformation ===")
    
    // Throttle mouse move events
    val mouseThrottler = Event.throttle[MouseEvent](100) // Max once per 100ms
    
    Events.subscribe(classOf[MouseEvent]) { event =>
      mouseThrottler(event).foreach { throttledEvent =>
        println(s"Throttled mouse move: (${throttledEvent.x}, ${throttledEvent.y})")
      }
    }
    
    // Debounce window resize events
    val resizeDebouncer = Event.debounce[WindowEvent](300) // Wait 300ms after last resize
    
    Events.subscribe(classOf[WindowEvent]) { event =>
      resizeDebouncer(event) // This won't emit immediately
    }
    
    // Filter specific events
    val keyFilter = Event.filter[KeyboardEvent](_.key == "space")
    
    Events.subscribe(classOf[KeyboardEvent]) { event =>
      keyFilter(event).foreach { spaceEvent =>
        println("Space key was pressed!")
      }
    }
    
    // Transform events
    val mouseTransformer = Event.map[MouseEvent, String] { mouseEvent =>
      s"Mouse ${mouseEvent.button} ${mouseEvent.action} at (${mouseEvent.x}, ${mouseEvent.y})"
    }
    
    Events.subscribe(classOf[MouseEvent]) { event =>
      mouseTransformer(event).foreach { transformed =>
        println(s"Transformed: $transformed")
      }
    }
  }
  
  def crossModuleIntegration(): Unit = {
    println("\n=== Cross-Module Integration ===")
    
    // Window events affecting graphics
    Events.on(Events.Window.resize)
      .emit(Event.custom("graphics/resize", Map("module" -> "graphics")))
    
    // Audio events affecting visuals
    Events.on(Event.custom("audio/beat", Map("frequency" -> 440)))
      .emit(Event.custom("visuals/pulse", Map("intensity" -> 1.0)))
    
    // Input events affecting multiple systems
    Events.on(Events.Mouse.leftClick)
      .emit(Event.custom("ui/click", Map("module" -> "ui")))
    
    Events.on(Events.Mouse.leftClick)
      .emit(Event.custom("physics/click", Map("module" -> "physics")))
    
    // Custom module events
    Events.on(Event.custom("osc/message", Map("address" -> "/volume", "value" -> 0.8)))
      .emit(Event.custom("audio/volume_change", Map("value" -> 0.8)))
    
    // Listen to cross-module events
    Events.subscribe(classOf[CustomEvent]) { event =>
      if (event.name.startsWith("graphics/") || event.name.startsWith("audio/")) {
        println(s"Cross-module event: ${event.name} -> ${event.data}")
      }
    }
  }
  
  def debuggingAndIntrospection(): Unit = {
    println("\n=== Debugging and Introspection ===")
    
    // Enable event logging
    EventDebug.logAll()
    
    // Log specific event types
    EventDebug.logType(classOf[KeyboardEvent])
    
    // Generate some events for statistics
    for (i <- 1 to 10) {
      Events.emit(Event.keyboard(s"key$i", "press"))
      Events.emit(Event.mouse("left", "click", i * 10, i * 10))
    }
    
    // Show event statistics
    EventDebug.stats()
    
    // Access event history
    val recentEvents = Events.history(5000) // Last 5 seconds
    println(s"Recent events: ${recentEvents.length}")
    
    val lastKeyboardEvent = Events.lastEvent(classOf[KeyboardEvent])
    lastKeyboardEvent.foreach { event =>
      println(s"Last keyboard event: ${event.key} ${event.action}")
    }
  }
  
  def complexCompositionExample(): Unit = {
    println("\n=== Complex Composition Example ===")
    
    // Multi-step gesture: Ctrl+Click, then drag, then release
    val complexGesture = Events.sequence(
      Events.Keyboard.ctrl.and(Events.Mouse.leftDown),
      Events.Mouse.drag,
      Events.Mouse.leftUp
    )
    
    complexGesture.emit(Event.custom("complex_gesture", Map(
      "type" -> "ctrl_drag",
      "action" -> "move_selection"
    )))
    
    // Parallel event detection: Any of these keys pressed
    val anyArrowKey = Events.parallel(
      Events.Keyboard.keyDown("ArrowUp"),
      Events.Keyboard.keyDown("ArrowDown"),
      Events.Keyboard.keyDown("ArrowLeft"),
      Events.Keyboard.keyDown("ArrowRight")
    )
    
    anyArrowKey.emit(Event.custom("arrow_key", Map("action" -> "navigate")))
    
    // Time-based composition: Two events within a time window
    val quickDoubleAction = Events.within(500).of(
      Events.Mouse.leftClick.and(Events.Keyboard.keyDown("shift"))
    )
    
    quickDoubleAction.emit(Event.custom("quick_double_action", Map(
      "type" -> "shift_click",
      "action" -> "multi_select"
    )))
    
    // Listen to complex events
    Events.subscribe(classOf[CustomEvent]) { event =>
      if (event.name == "complex_gesture") {
        println("Complex gesture detected!")
      } else if (event.name == "arrow_key") {
        println("Arrow key navigation!")
      } else if (event.name == "quick_double_action") {
        println("Quick double action!")
      }
    }
  }
  
  def main(args: Array[String]): Unit = {
    println("Seer Event System Examples")
    println("=" * 50)
    
    basicEventHandling()
    fluentEventComposition()
    advancedPatterns()
    parameterSystem()
    timelineSystem()
    eventFilteringAndTransformation()
    crossModuleIntegration()
    debuggingAndIntrospection()
    complexCompositionExample()
    
    println("\n" + "=" * 50)
    println("All examples completed!")
  }
}


