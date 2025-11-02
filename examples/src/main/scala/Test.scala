
package seer
package examples 

import graphics._

object Test extends App {


  val gen = new ShaderGenerator() 
  // gen.setPlatform("gl")
  gen.vert()
    .attributes(true,true,true)
    .out("vec2 thigy")
    .main("""
      pos = vec4(1,0,0,0);
    """)

  println(gen())

  gen.frag()
    .in()
    .out("vec4 fragColor")
    .main("""
      fragColor = vec4(thigy,0,1);
    """)

  println(gen())


}