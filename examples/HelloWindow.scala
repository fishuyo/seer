package seer 
package examples

import runtime.SeerApp
import graphics._
import graphics.lwjgl._

object HelloWindow extends SeerApp {

	val graphics = new LwjglGraphicsRuntimeModule()

	useModules(graphics :: List())

	graphics.onDraw = (g:Graphics) => {
		import g.gl._
		glClearColor(0.0f, 1.0f, 0.0f, 0.0f)
		glClear(GL_COLOR_BUFFER_BIT)
	}

}