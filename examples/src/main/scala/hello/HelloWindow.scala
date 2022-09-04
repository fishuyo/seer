package seer 
package examples

import graphics._

object HelloWindow extends SeerApp {

	graphics.onDraw = (g:Graphics) => {
		import g.gl._
		glClearColor(0.0f, 1.0f, 0.0f, 0.0f)
		glClear(GL_COLOR_BUFFER_BIT)
	}

}