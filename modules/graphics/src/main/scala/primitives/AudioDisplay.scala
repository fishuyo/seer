
package seer
package graphics

import math._

import scala.collection.mutable.Queue

// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics._
// import com.badlogic.gdx.graphics.{Mesh => GdxMesh}

/* 
* Plot stream of size data points scaled by range
*/
// class Plot2D( var size:Int, var range:Float=1f) extends Drawable {
//   var color = Vec3(1f)
//   var pose = Pose()
//   var scale = Vec3(1f)
//   val mesh = new GdxMesh(false,size,0, VertexAttribute.Position)
//   var data = Queue[Float]()
//   data.enqueue(new Array[Float](size):_*)
//   val vertices = new Array[Float](size*3)
//   var dirty = true

//   def apply(f:Float) = {
//     data.enqueue(f)
//     data.dequeue()
//     for( i<-(0 until size)){
//       vertices(3*i) = (i - size/2) / size.toFloat
//       vertices(3*i+1) = data(i) / range
//       vertices(3*i+2) = 0f
//     }
//     dirty = true
//   }

//   override def draw(){ 
//     if( dirty ){
//       mesh.setVertices( vertices )
//       dirty = false
//     }
//     Renderer().setColor(RGBA(color,1f))
//     val s = scale / 2f
//     MatrixStack.push()
//     MatrixStack.transform(pose,s)
//     Renderer().setMatrices()
//     mesh.render(Renderer().shader(), GL20.GL_LINE_STRIP)
//     MatrixStack.pop()
//   }

// }


/* 
* Display Audio Samples and boundary/playback cursors
*/
class AudioDisplay(val size:Int){
  lazy val gl = Graphics().gl

  // var color = RGBA(0,1,0,1)
  // var cursorColor = RGBA(1,1,0,1)
  var pose = Pose()
  var scale = Vec3(1.0f)
  val mesh = new Mesh()
  val cursorMesh = new Mesh()
  mesh.resize(size*2)
  cursorMesh.resize(12)
  // mesh.vertices ++= (0 until size*2).map( _ => Vec3())
  // cursorMesh.vertices ++= (0 until 12).map( _ => Vec3())
  cursorMesh.primitive = gl.GL_LINES
  var dirty = true
  var cursorDirty = true
  var samples:Array[Float] = _
  var left = 0
  var right = 0

  def setSamplesSimple(s:Array[Float], l:Int=0, r:Int=0) = {
    samples = s
    left = l
    right = if(r == 0) s.size-1 else r-1
    for(i <- 0 until size){
      val s = i / (size-1).toFloat * (right-left) + left
      val si = s.toInt
      val si2 = if( si >= right) left else si + 1
      val f = s-si
      mesh.vertices.position(3*i)
      mesh.vertices.put((i - size/2) / size.toFloat)
      mesh.vertices.put(samples(si)*(1f-f) + samples(si2)*f)
      mesh.vertices.put(0f)
    }
    mesh.primitive = gl.GL_LINE_STRIP
    mesh.size = size
    dirty = true
  }

  def setSamples(s:Array[Float], l:Int=0, r:Int=0) = {
    samples = s
    left = l
    right = if(r == 0) s.size-1 else r-1
    // for( i<-(0 until size)){
    //   val s = i / (size-1).toFloat * (right-left) + left
    //   val si = s.toInt
    //   val si2 = if( si >= right) left else si + 1
    //   val f = s-si
    //   vertices(3*i) = (i - size/2) / size.toFloat
    //   vertices(3*i+1) = samples(si)*(1f-f) + samples(si2)*f
    //   vertices(3*i+2) = 0f
    // }

    var sp = 0.0f
    var s1 = left
    var max = -1f
    var min = 1f
    for(i <- 0 until size){
      val s = (i+1) / (size).toFloat * (right-left) + left
      val s2 = s.toInt

      var offx = 0
      if( s2 - s1 < 8 ){
        val off1 = sp - s1
        val off2 = s - s2
        try{
        max = samples(s1)*(1f-off1) + samples(s1+1) * off1
        min = samples(s2)*(1f-off2) + samples(s2+1) * off2
        } catch { case e:Exception => println(e) }
        offx = 1
      }else{
        max = -1f
        min = 1f
      
        for( j <- (s1 until s2)){
          val f = samples(j)
          if( f > max ) max = f
          if( f < min ) min = f
        }
      }
      s1 = s2
      sp = s

      mesh.vertices.position(2*3*i)
      mesh.vertices.put((i - size/2) / size.toFloat)
      mesh.vertices.put(max)
      mesh.vertices.put(0f)
      mesh.vertices.put((i + offx - size/2) / size.toFloat)
      mesh.vertices.put(min)
      mesh.vertices.put(0f)
    }
    mesh.primitive = gl.GL_LINES
    mesh.size = 2*size
    dirty = true
  }

  def setCursor(i:Int,sample:Int) = {
    val x = (sample - left).toFloat / (right-left).toFloat - .5f
    cursorMesh.vertices.position(2*3*i)
    cursorMesh.vertices.put(x)
    cursorMesh.vertices.put(0.5f)
    cursorMesh.vertices.put(0.01f)
    cursorMesh.vertices.put(x)
    cursorMesh.vertices.put(-0.5f)
    cursorMesh.vertices.put(0.01f)
    cursorDirty = true
  }

  def draw() = { 
    if( dirty ){
      mesh.update()
      dirty = false
    }
    if( cursorDirty ){
      cursorMesh.update()
      cursorDirty = false
    }

      //// Renderer().textureMix = 0f
      //// Renderer().setColor(color)
    // val s = scale / 2f
    // MatrixStack.push()
    // MatrixStack.transform(pose,s)

      //// Renderer().setMaterialUniforms(material)
    // Renderer().setMatrixUniforms()
    // Renderer().shader.setUniforms()

      //// Renderer().setMatrices()
    mesh.draw()
      //// Renderer().setColor(cursorColor)
    cursorMesh.draw()
    // MatrixStack.pop()
  }

}



