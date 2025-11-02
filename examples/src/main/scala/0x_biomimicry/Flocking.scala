
package seer.examples.bio

import seer._
import math._
import graphics._
import actor._


object Flocking extends SeerApp {

  var shader:ShaderProgram = _
  var mesh:Mesh = _

  val n = 100

  val boids = Array.fill(n)(new Boid)


  graphics.onCreate = () => {
    val gl = Graphics().gl
    import gl._ 

    val vertText = """
      #version 330 core
      layout(location = 0) in vec3 position;
      void main(){ 
        gl_Position = vec4(position, 1.0); 
      }"""

    val fragText = """
      #version 330 core

      out vec4 fragColor;
      void main() {
        fragColor = vec4(1.0,1.0,1.0,1.0);
      }"""

    shader = new ShaderProgram().create(vertText, fragText)
    
    mesh = new Mesh()
    mesh.primitive = GL_LINES
    mesh.resize(n*5*2)
  }

  graphics.onUpdate = (dt:Double) => {

    boids.foreach(_.step(boids))

    val coords = boids.flatMap{ case b => 
      val ps = b.pos :: b.tail
      ps.sliding(2,1).toList.filter { case List(a,b) => (a - b).mag() < 1.0f }.flatten.map{ case v => List(v.x,v.y,v.x) }.flatten
    }
    mesh.vertices.put(coords)
    mesh.update()

  }

  graphics.onDraw = (g:Graphics) => {
    import g.gl._

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    glClear(GL_COLOR_BUFFER_BIT)

    shader.bind()

    mesh.draw()
  }

}


class Boid {
  val speed = Var("flock/speed", 0.01f)
  val separateDist = Var("flock/sDist", 0.05f)
  val alignDist = Var("flock/aDist", 0.1f)
  val cohesionDist = Var("flock/cDist", 0.15f)
  val tailLength = Var("flock/tailLength", 5.0f)

  val pos = Random.vec3()
  val lpos = Vec3(pos)
  var tail = List[Vec3](pos)
  val vel = Random.vec3()*speed()

  def step(boids:Seq[Boid]) = {
    val newDir = Vec3()
    val avgVel = Vec3()

    val center = Vec3()
    var count = 0

    boids.collect { case b if b != this =>
      val d = pos - b.pos
      val dist = d.mag()
      
      // separate
      if(dist < separateDist()){
        val dir = d.normalize() / dist
        vel.lerpTo(dir * speed(), 0.01f)
      }

      // align
      if(dist < alignDist()){
        vel.lerpTo(b.vel, 0.01f)
      }

      // cohesion
      if(dist < cohesionDist()){
        center += b.pos
        count += 1
      }
    
    }

    if(count > 0){
      center /= count
      val dir = (center - pos).normalize()
      vel.lerpTo(dir * speed(), 0.01f)
    }
    pos.wrap(Vec3(-1f), Vec3(1f))
    pos.z = 0f
    // vel.set(vel.normalize * speed() + Random.vec3()*0.01f)
    lpos.set(pos)
    tail = Vec3(pos) :: tail
    tail = tail.take(tailLength().toInt)
    pos += vel 
  }
}


//   var showGui = false
//   val panel = UI.panel(0,0,1,1).layoutX()
//   panel += UI.slider().range(1f,10f).bind(b.tailLength)
//   panel += UI.slider().range(0.0f,0.1f).bind(b.speed)
//   panel += UI.slider().range(0.01f,1f).bind(b.separateDist)
//   panel += UI.slider().range(0.01f,1f).bind(b.alignDist) 
//   panel += UI.slider().range(0.01f,1f).bind(b.cohesionDist)
//   panel.positionChildren()


//   Mouse.listen { 
//     case m if showGui =>
//       m.event match {
//         case "move" => rayEvent(Point, m.x, m.y)
//         case "down" => rayEvent(Pick, m.x, m.y)
//         case "drag" => rayEvent(Drag, m.x, m.y)
//         case "up" => rayEvent(Unpick, m.x, m.y)
//       }
//     case _ => ()
//   }

//   Keyboard.listen {
//     case '!' => Parameter.save("1", "flock.*")
//     case '1' => Parameter.load("1", "flock.*")
//     case '@' => Parameter.save("2", "flock.*")
//     case '2' => Parameter.load("2", "flock.*")
//     case '#' => Parameter.save("3", "flock.*")
//     case '3' => Parameter.load("3", "flock.*")
//     case '$' => Parameter.save("4", "flock.*")
//     case '4' => Parameter.load("4", "flock.*")
//     case ']' => showGui = !showGui
//   }

//   def rayEvent(evt:PickEventType, x:Float, y:Float) = {
//     val ray = Camera.ray(x * Window.width, (1f - y) * Window.height)
//     val e = PickEvent(evt, ray)
//     panel.event(e)
//   }

