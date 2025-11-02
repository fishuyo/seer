
package seer
package graphics


trait WindowBase {
  def id():String
  
  def width():Int = getSize()._1
  def height():Int = getSize()._2

  def create(w:Int, h:Int):Unit
  def destroy():Unit

  def getSize():(Int,Int)
  def getBufferSize():(Int,Int)

}