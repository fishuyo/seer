
package seer
package graphics

trait Renderer {
  // var scene
  // var camera
  // var viewport
  // var shader

  def update(dt:Double) = {}
  def draw() = {}
  def resize() = {}

}