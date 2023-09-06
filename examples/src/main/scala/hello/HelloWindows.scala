package seer
package examples

import graphics._
import math.Random 

/*
 * This example shows how to create multiple windows and assign different onDraw functions to each.
 */
object HelloWindows extends SeerApp {

  var timers = collection.mutable.ArrayBuffer[Double]()

  // We create 15 windows in onInit each with its own draw function
  graphics.onInit = () => {

    val numWindows = 15
    val (sw,sh) = Screen.size()

    val rdim = Random.int(40,300)
    val rx = Random.int(-sw/2, sw/2)
    val ry = Random.int(-sh/2, sh/2)
    val rcol = Random.float

    (0 until numWindows).foreach{ case i =>
      
      // initial random values for clear color
      var (a,b,c) = (rcol(), rcol(), rcol())

      // Create onDraw function foreach window that sets random clear color
      val onDraw = (g:Graphics) => {
        import g.gl._

        // if window's timer reset, new random values for clear color
        if(timers(i) == 0.0){ 
          a = rcol()
          b = rcol()
          c = rcol()
        }
        glClearColor(a, b, c, 0.0f)
        glClear(GL_COLOR_BUFFER_BIT)
      }

      // Create window using onDraw above and random dimensions and position on screen
      Window.create(onDraw, rdim(), rdim(), rx(), ry())	

      // append double to buffer for use as animation timer
      timers += 0.0
    }

  }

  // onUpdate we reset timers at different threshold times otherwise increment them by dt
  graphics.onUpdate = (dt:Double) => {
    timers = timers.zipWithIndex.map{ case (t,i) => if (t > (i+1) * 0.1) 0.0 else t + dt }
  }


}
