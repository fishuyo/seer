package seer 
package examplesjs

import graphics._
import math.Random

import scalajs.js.annotation._


@JSExportTopLevel("HelloWorld")
object HelloWorld extends SeerApp {

	var timer = 0.0

	graphics.onUpdate = (dt:Double) => {
		timer += dt
		if(timer > 0.5) timer = 0.0
	}

	graphics.onDraw = (g:Graphics) => {
		import g.gl._

    val r = Random.float 
    if(timer == 0.0) glClearColor(r(), r(), r(), 1.0f)
    glClear(GL_COLOR_BUFFER_BIT)
	}

}