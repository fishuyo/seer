package seer

import collection.mutable.{ArrayBuffer, HashMap}
import scala.reflect.ClassTag

/**
 * SeerRuntime manages modules and provides event-driven architecture.
 * Modules can be added/removed at runtime, and events are published through an event bus.
 */
class SeerRuntime {

  private val modules = ArrayBuffer[Module]()
  private val modulesById = HashMap[String, Module]()
  private val eventBus = new SimpleEventBus()
  
  private var running = false
  private var lastUpdateTime = 0.0

  // Legacy callbacks (for backward compatibility)
  var onCreate = () => {}
  var onDestroy = () => {}
  var onStart = () => {}

  /** Add a module to the runtime */
  def addModule(module: Module): Unit = {
    if (!modulesById.contains(module.id)) {
      modules += module
      modulesById(module.id) = module
      
      // Set runtime reference on modules for event publishing
      // Use a type-safe approach that works cross-platform
      if (hasSetRuntime(module)) {
        setRuntimeOnModule(module, this)
      }
    }
  }
  
  // Helper to check if module has setRuntime method
  private def hasSetRuntime(module: Module): Boolean = {
    try {
      module.getClass.getMethods.exists(_.getName == "setRuntime")
    } catch {
      case _: Exception => false // Reflection not available (e.g., in Scala.js)
    }
  }
  
  // Helper to call setRuntime
  private def setRuntimeOnModule(module: Module, runtime: SeerRuntime): Unit = {
    try {
      val method = module.getClass.getMethod("setRuntime", classOf[SeerRuntime])
      method.invoke(module, runtime)
    } catch {
      case _: Exception => // Method doesn't exist or reflection not available, that's okay
    }
  }

  /** Remove a module from the runtime */
  def removeModule(module: Module): Unit = {
    if (modulesById.contains(module.id)) {
      modules -= module
      modulesById -= module.id
      if (module.isRunning) {
        module.stop()
      }
      if (module.isCreated) {
        module.destroy()
      }
    }
  }

  /** Get a module by ID */
  def getModule[T <: Module](id: String)(implicit ct: ClassTag[T]): Option[T] = {
    modulesById.get(id).flatMap { m =>
      if (ct.runtimeClass.isInstance(m)) Some(m.asInstanceOf[T])
      else None
    }
  }

  /** Get all modules */
  def getModules(): Seq[Module] = modules.toSeq

  /** Subscribe to events */
  def subscribe[T <: Event](handler: T => Unit): Subscription = {
    eventBus.subscribe(handler)
  }

  /** Publish an event */
  def publish(event: Event): Unit = {
    eventBus.publish(event)
    // Also forward to modules
    modules.foreach(_.onEvent(event))
  }

  /** Legacy: Add module (backward compatibility) */
  def +=(mod: Module): Unit = addModule(mod)
  def ++=(mods: Seq[Module]): Unit = mods.foreach(addModule)
  def useModule(mod: Module): Unit = addModule(mod)
  def useModules(mods: Seq[Module]): Unit = mods.foreach(addModule)

  /** Run the runtime (initialize, start, and run main loop) */
  def run(): Unit = {
    if (running) return
    
    running = true
    println("Initializing modules..")
    
    // Create all modules
    modules.foreach { module =>
      if (!module.isCreated) {
        module.create()
        publish(ModuleCreated(module))
      }
    }
    onCreate()
    publish(ModuleCreated(null)) // Global create event

    println("Starting modules..")
    // Start all modules
    modules.foreach { module =>
      if (!module.isRunning) {
        module.start()
        publish(ModuleStarted(module))
      }
    }
    onStart()
    publish(ModuleStarted(null)) // Global start event
    
    // Main loop
    lastUpdateTime = getCurrentTime()
    runLoop()
    
    // Cleanup
    stop()
  }

  /** Main update loop */
  private def runLoop(): Unit = {
    while (running && modules.nonEmpty) {
      val currentTime = getCurrentTime()
      val dt = currentTime - lastUpdateTime
      lastUpdateTime = currentTime
      
      // Update all modules
      modules.foreach(_.update(dt))
      
      // Small delay to prevent CPU spinning (platform-specific implementations may override)
      Thread.sleep(1)
    }
  }

  /** Stop the runtime */
  def stop(): Unit = {
    if (!running) return
    
    running = false
    println("Stopping modules..")
    
    // Stop all modules
    modules.foreach { module =>
      if (module.isRunning) {
        module.stop()
        publish(ModuleStopped(module))
      }
    }
    publish(ModuleStopped(null)) // Global stop event
    
    println("Cleaning up modules..")
    // Destroy all modules
    modules.foreach { module =>
      if (module.isCreated) {
        module.destroy()
        publish(ModuleDestroyed(module))
      }
    }
    onDestroy()
    publish(ModuleDestroyed(null)) // Global destroy event
  }

  /** Check if runtime is running */
  def isRunning: Boolean = running
  
  // Cross-platform time helper
  private def getCurrentTime(): Double = {
    Timestamp.now() / 1000.0
  }
}
