package seer.graphics

/**
 * Window trait - extracted from GraphicsAPI for better visibility.
 * This is the unified Window interface used across all platforms.
 */
trait Window {
  def id: String
  def width: Int
  def height: Int
  def bufferWidth: Int
  def bufferHeight: Int
  
  def makeCurrent(): Unit
  def swapBuffers(): Unit
  def shouldClose: Boolean
  def setShouldClose(value: Boolean): Unit
  
  def getSize: (Int, Int)
  def getBufferSize: (Int, Int)
  def setSize(width: Int, height: Int): Unit
  
  def isFullscreen: Boolean
  def setFullscreen(fullscreen: Boolean): Unit
  
  def title: String
  def setTitle(title: String): Unit
}
