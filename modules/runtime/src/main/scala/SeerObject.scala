
package seer
package runtime

import collection.mutable.ArrayBuffer

object SeerObject {
    val objects = ArrayBuffer[SeerObject]()


}


trait SeerObject {
  // var initialized = false
  // var active = true

  // def init_() = {
  //   SeerObject.objects += this
  //   init()
  //   initialized = true
  // }

  // def draw_() = if(active) draw()
  // def update_() = if(active) update()
  

  // def init() = {}
  // def draw() = {}
  // def update() = {}
}
