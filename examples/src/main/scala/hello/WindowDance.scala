// package seer
// package examples

// import graphics._
// import math.Random 

// /*
//  * This example shows how to use the composition API to create windows and make them dance.
//  */
// object WindowDance {

//   val runtime = new SeerRuntime()
//   val graphics = new GraphicsModule()
//   runtime += graphics  

//   val n = 10 // number of windows

//   for(i <- 0 until n){
//     graphics.createWindow()
//   }

//   val x = Parameter("x", 0)  // osc namespace uri /x
//   val y = Parameter("y", 0)
//   val w = Parameter("w", 100)
//   val h = Parameter("h", 100)
//   val show = Parameter("show", false)
//   val group = Parameter.group("window")(x,y,w,h,show) // osc namespace uri /window/x  
//   val params = Parameter.array(n, "$path/$index/$name")(group) // osc namespace uri /window/0/x


//   params.onChange {
//     case ParameterList(s"/window/", index, wx, wy, ww, wh, wshow) =>
//       graphics.windows(index).setPosition(wx,wy)
//       graphics.windows(index).setSize(ww,wh)
//       graphics.windows(index).setVisible(wshow)
//   }


//   Timeline.fromText("""
//   @0 /window/* 0 0 100 100
//   @0 /window/*/show false

//   # after 1 second and 30 frames
//   +00:00:01:30 

//   # after 1 second and 30 frames
//   +1:30

//   # after 30 frames
//   +:30

//   # after 1.1 second
//   +1.1

//   # after 1 minute 10 seconds
//   +:1:10:

//   @

//   """)


//   def main(args: Array[String]): Unit = {
//     runtime.run()
//   }
// }
