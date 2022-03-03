package seer 
package examples

import runtime._
import graphics._
import graphics.lwjgl._
import actor._
import script._

object LiveLoader extends SeerApp {

	val graphics = new LwjglGraphicsRuntimeModule()

	useModules(graphics :: List())

	graphics.onDraw = (g:Graphics) => {
		import g.gl._
		glClearColor(0.0f, 1.0f, 0.0f, 0.0f)
		glClear(GL_COLOR_BUFFER_BIT)
	}


  override def parseArgs(args:Array[String]){
    if(args.length == 0){
      println("""Please provide path of script or script directory to load.""")
      java.lang.System.exit(0)
    }

    if(args.length >= 3){
      val address = args(1)
      val port = args(2).toInt
      val a = akka.actor.Address("akka","seer", address, port)
      System() = ActorSystemManager(a)
    }

    val path = args.head
    println(s"Loading ${path}")
    var actor = ScriptManager.load(path)
  }

}