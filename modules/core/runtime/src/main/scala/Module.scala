package seer

/**
 * Module encapsulates a computation environment and allows for modular construction of app functionality.
 * For instance each graphics backend and each audio backend is implemented within a Module.
 * Modules could also be used to encapsulate asynchronous computation or other application service like a web server or communications API.
 * An app is constructed through a collection of Modules that are bound to application callback functions.
 */

trait Module {
  
  /** Unique identifier for this module */
  def id: String
  
  /** Human-readable name for this module */
  def name: String
  
  /** Lifecycle: Initialize module resources */
  def create(): Unit = {}
  
  /** Lifecycle: Cleanup module resources */
  def destroy(): Unit = {}
  
  /** Lifecycle: Start module (begin main loop, start threads, etc.) */
  def start(): Unit = {}
  
  /** Lifecycle: Stop module (pause main loop, stop threads, etc.) */
  def stop(): Unit = {}
  
  /** Lifecycle: Update module state (called each frame/update cycle) */
  def update(dt: Double): Unit = {}
  
  /** Handle events from the event bus */
  def onEvent(event: Event): Unit = {}
  
  /** Check if module has been created */
  def isCreated: Boolean = false
  
  /** Check if module is currently running */
  def isRunning: Boolean = false
}



