
package seer.runtime 

/**
 * Runtime Module encapsulates a computation environment and allows for modular construction of app functionality.
 * For instance each graphics backend and each audio backend is implemented within a Runtime Module.
 * Runtime Modules could also be used to encapsulate asynchronous computation or other application service like a web server or communications API.
 * An app is constructed through a collection of RuntimeModules that are bound to application callback functions.
 */

trait RuntimeModule {

  def init() = {}
  def cleanup() = {}

  def startBlocking() = {}
  def start() = {}
  def stop() = {}

  def update() = {}
}

// trait BlockingRuntimeModule extends RuntimeModule {

// }


