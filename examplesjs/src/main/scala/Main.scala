package seer

import seer.tests.graphics.WebGLWindowCreationTest

/**
 * Main entry point for Scala.js examples.
 * Change this to run different tests.
 */
object Main {
  def main(args: Array[String]): Unit = {
    // Run WebGL window creation test by default
    WebGLWindowCreationTest.main(args)
  }
}
