package seer 
package examples 

import graphics._
import math.Random

object HelloWindowUpdate extends SeerApp {

  var timer = 0.0 

  graphics.onUpdate = (dt:Double) => {
    timer += dt
    if(timer > 1.0) timer = 0.0
  }

  graphics.onDraw = (g:Graphics) => {
    import g.gl._
    val r = Random.float
    if(timer == 0.0) glClearColor(r(), r(), r(), 1.0f);
    glClear(GL_COLOR_BUFFER_BIT)
  }

}