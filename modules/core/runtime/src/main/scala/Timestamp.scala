package seer

/**
 * Cross-platform timestamp helper.
 * Uses platform-specific implementations.
 * 
 * Note: For cross-platform code, we need separate implementations.
 * This uses reflection to detect the platform at runtime.
 */
object Timestamp {
  def now(): Long = {
    // Use reflection to check if we're in JS or JVM
    // In JS, scala.scalajs.js package exists
    // In JVM, it doesn't
    try {
      // Try to load scala.scalajs.js - this will fail in JVM
      val jsPackage = Class.forName("scala.scalajs.js.package$")
      val jsModule = jsPackage.getField("MODULE$").get(null)
      val dynamic = jsModule.getClass.getMethod("Dynamic").invoke(jsModule)
      val global = dynamic.getClass.getMethod("global").invoke(dynamic)
      val date = global.getClass.getMethod("Date").invoke(global)
      val nowMethod = date.getClass.getMethod("now")
      val result = nowMethod.invoke(date).asInstanceOf[Double].toLong
      result
    } catch {
      case _: ClassNotFoundException | _: NoSuchMethodException | _: NoClassDefFoundError =>
        // JVM path - use System.currentTimeMillis()
        System.currentTimeMillis()
      case _: Exception =>
        // Fallback to JVM
        System.currentTimeMillis()
    }
  }
}
