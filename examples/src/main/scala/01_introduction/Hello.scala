
// package examples.intro

// import seer._
// import seer.graphics._


// object RuntimeConfig {
//   val runtime = new Runtime()

//   val graphics = new LwjglGraphicsModule()
//   val audio = new PortAudioModule()

//   runtime.useModules(audio :: graphics :: List())

//   runtime
// }



// object Hello {
//   def main(args:Array[String]) = {
    
//     Runtime.graphics.onDraw = AppDefinition.draw
//     RuntimeConfig.runtime.run()
//   }
// }


// object AppDefinition {

//   val cube = Mesh.cube()

//   def draw(g:Graphics) = {

//     g.modelMatrix.rotate(0f, 0.1f, 0f)
//     cube.draw()

//   }

// }

