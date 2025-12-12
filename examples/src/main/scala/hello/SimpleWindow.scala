// package examples

// // more bespopke api, non-obsfucated -- so instead of seerapp hiding the magic suace..
// // showing the sauce itself


// //

// object SimpleWindow {

//   // define domain functions

//   def draw(g:Graphics) = {
//     // import g.gl._
//     // glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
//     // glClear(GL_COLOR_BUFFER_BIT)

//     // or
//     import g._
//     triangle(Vec3(0.0f, 0.0f, 0.0f), Vec3(1.0f, 0.0f, 0.0f), Vec3(0.0f, 1.0f, 0.0f))
//     // how to use a dsl that builds up graphics objects as a graph? a triangel a triangle, but not until we ask to draw do we know it's mesh structe / primitive -- if we couuld work at the more bespoke level without sacrificing performance, ie
//     // we use higher order functions to build up lists of triangles and then draw sucha an object would do so efficiently using appropriate graphics construct approach / vbo / instansing // ..

//     // a dsl like unit audio gens but graphics -- visual synthesiz / geometrix synthesis --
//   }



//   def main(args: Array[String]): Unit = {
  
//     // initialize modules
//     // create window
//     // bind events io to some active
//     // not a basic boring, but a rich instrument in each example, what can i do with a window given basic mappings from window events mouse keyboard to window properties / ismple graphics ie bgcolor....
//     // how to 
//   }
// }


// // how about the idea that properties everything is a parameter so binding window dimension a Var(x,y,w,h) -- and a property is updated on change?
// // or we bind some data flow / stream to a property and it is updated at stream rate -- but minimize garbage...