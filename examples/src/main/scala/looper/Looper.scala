package seer
package looper

import math._
import audio._
import graphics._
import org.lwjgl.glfw.GLFW._

import collection.mutable.ArrayBuffer

object LooperMain extends SeerApp {

  var looper:Looper = _
  var shader:ShaderProgram = _
	var timer = 0.0

  audio.onAudioIO = (io:AudioIO) => {
    looper.audioIO(io)
	}

  graphics.onInit = () => {
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
    looper = new Looper
    looper.init()

		Window().onKeyEvent = this.onKeyEvent
		Window().onMouseEvent = this.onMouseEvent
  }

	graphics.onUpdate = (dt:Double) => {
		timer += dt
    if(timer > 2.0){
			timer = 0.0
		}
	}

  graphics.onDraw = (g:Graphics) => {
    import g.gl._
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    glClear(GL_COLOR_BUFFER_BIT)

    shader.bind()
    looper.draw()
  }

	var onKeyEvent:(KeyEvent)=>Unit = (event:KeyEvent) => {
		if(event.action == GLFW_PRESS){
			println(event.key)
			event.key match {
				case "r" => looper.toggleRecord(0)
				case _ =>
			}
		}
	}

	var onMouseEvent:(MouseEvent)=>Unit = (event:MouseEvent) => {
		println(s"${event.state.x} ${event.state.y}")
		println(s"${event.state.dx} ${event.state.dy}")
		println(s"${event.state.scrollx} ${event.state.scrolly}")
	}


}






class Looper extends AudioSource {
	
	var loops = List[Loop]()
	var plots = List[AudioDisplay]()
	// var spects = List[Spectrogram]()

	var queued = ArrayBuffer[Loop]()
	
	var master = 0
	var mode = "free" // free, sync, sequenced

	def non()={}
	def sync()={}

	def setMode(s:String) = {
		s match {
			case "free" => 
			case "sync" =>
			case "sequence" =>
			case _ =>
		}
		mode = s
	}

	def newLoop() = {
		val l = new Loop(10f)
		loops = l :: loops
		val p = new AudioDisplay(500)
		val pos = Vec3( -0.75f*(plots.size%4)+1.5f,  -0.75f+0.75f*(plots.size/4), 0f)
		p.pose.pos = pos
		// p.color = RGBA(0,0,0,1)
		// p.cursorColor = RGBA(0,0,0,1)
		plots = p :: plots
		// spects = new Spectrogram() :: spects
		// spects(0).model.pose.pos = pos
	}

	def play(i:Int, times:Int=0) = {
		if( times == 0) loops(i).play
		else loops(i).play(times)
	}
	
	def stop(i:Int) = loops(i).stop

	def togglePlay(i:Int) = {
		if(!loops(i).playing){
    	loops(i).play
		}else{
      loops(i).stop()
		}
		loops(i).playing
	}

	def stack(i:Int) = {
		loops(i).stack
		loops(i).stacking
	}

	def reverse(i:Int) = loops(i).reverse
	def rewind(i:Int) = loops(i).rewind
	def clear(i:Int) = loops(i).clear

	def record(i:Int) = {
		loops(i).clear; loops(i).stop; loops(i).record
	}

	def doToggleRecord(i:Int) = {
    if(!loops(i).recording){
    	record(i)
		}else{
      loops(i).stop()
      loops(i).play()
		}
		loops(i).recording
	}
	def doToggleQueued(i:Int) = {
		if( !loops(master).playing ){
			doToggleRecord(i)
		} else if( loops(i).recording ){
			// stop recording, pad integer multiple of master
			doToggleRecord(i)
			val size = loops(master).b.curSize
			if( loops(i).b.curSize > size ){
				var newSize = 2*size
				while(loops(i).b.curSize > newSize) newSize += size 
				loops(i).allocate(newSize)
				loops(i).b.curSize = newSize
				loops(i).b.rMax = newSize
			} else {
				loops(i).allocate(size)
				loops(i).b.curSize = size
				loops(i).b.rMax = size
			}
		} else if(queued.contains(loops(i))){
    	queued -= loops(i)
		} else{
      queued += loops(i)
		}
	}

	def toggleRecord(i:Int) = {
		mode match {
			case "free" => doToggleRecord(i)
			case "sync" => doToggleQueued(i)
			case "sequence" =>
			case _ =>
		}
	}

	def setGain(i:Int,f:Float) = loops(i).gain = f
	def setDecay(i:Int,f:Float) = loops(i).decay = f
	def setPan(i:Int,f:Float) = loops(i).pan = f
	def setSpeed(i:Int,f:Float) = loops(i).b.speed = f
	def setBounds(i:Int,min:Float,max:Float) = {
		val b1 = min*loops(i).b.curSize
		val b2 = max*loops(i).b.curSize
		loops(i).reverse( min > max)
		loops(i).b.setBounds(b1.toInt,b2.toInt)
		loops(i).vocoder.setBounds(min,max)
		plots(i).setCursor(0,b1.toInt)
		plots(i).setCursor(1,b2.toInt)
	}

	def duplicate(i:Int,times:Int) = loops(i).duplicate(times)

	def switchTo(i:Int) = {
		play(master,1);
		loops(master).onDone = ()=>{loops(i).play; loops(master).onDone=non; master=i}
	}

	def setMaster(i:Int) = {
		mode match {
			case "sync" => 
				loops(master).onSync = non
				master = i
				loops(master).onSync = this.rewindAndQueued //this.recordQueued
			case _ => ()
		}

	}

	def rewindAll(){ loops.foreach( _.rewind ) }
	def recordQueued(){ queued.foreach( _.record ); queued.clear }
	def rewindAndQueued(){ rewindAll();recordQueued();  }

	def init(){
		for( i<-(0 until 1)) newLoop
	}

  override def audioIO( io:AudioIO ){
	// override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){

		loops.foreach( _.audioIO(io) )
	}

	def draw(){
		for( i<-(0 until plots.size)){
			if( loops(i).vocoderActive){
				if(loops(i).vocoder.update){
					// spects(i).setData(loops(i).vocoder.spectrumData)
					loops(i).vocoder.update = false
				}
				// spects(i).draw()
			} else{
				val p = plots(i)
				if( loops(i).recording || loops(i).stacking ) p.setSamplesSimple(loops(i).b.samples, 0, loops(i).b.curSize)
				else if( loops(i).dirty ){
					p.setSamples(loops(i).b.samples, 0, loops(i).b.curSize) //loops(i).b.rMin,loops(i).b.rMax)
					loops(i).dirty = false
				}
				p.setCursor(2,loops(i).b.rPos.toInt)
			}

			if( loops(i).vocoderActive){
				val s = loops(i).vocoder.nextWindow / loops(i).vocoder.numWindows.toFloat
				plots(i).setCursor(2,(loops(i).b.curSize * s).toInt)
			}
			// if(loops(i).recOut){ MatrixStack.push; MatrixStack.translate(0f,-0.1f,0f)}
			plots(i).draw()
			// if(loops(i).recOut){ MatrixStack.pop }

		}

	}


	def save(name:String){
		// var project = "project-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-')
    // if( name != "") project = name
    // var path = "LoopData/" + project
    // var file = Gdx.files.external(path).file()
    // file.mkdirs()

  	// var map = Map[String,Any]()
    // for( i <- (0 until loops.size)){
    // 	val b = loops(i).b
    // 	val l = loops(i)
    // 	map = map + (("loop"+i) -> List(b.curSize,b.rPos,b.wPos,b.rMin,b.rMax,b.speed,l.gain,l.pan,l.decay))
    // 	val f = Gdx.files.external(path+"/"+i+".wav").file()
    // 	loops(i).save(f.getAbsolutePath())
    // }

    // file = Gdx.files.external(path+"/loops.json").file()
  	// val p = new java.io.PrintWriter(file)
	  // p.write( scala.util.parsing.json.JSONObject(map).toString( (o) =>{
	  // 	o match {
	  // 		case o:List[Any] => s"""[${o.mkString(",")}]"""
	  // 		case s:String => s"""${'"'}${s}${'"'}"""
	  // 		case a:Any => a.toString()  
	  // 	}
	  // }))
	  // p.close

	}

	def load(name:String){
  //   val path = "LoopData/" + name
  //   val file = Gdx.files.external(path+"/loops.json").file()

  //   val sfile = scala.io.Source.fromFile(file)
  // 	val json_string = sfile.getLines.mkString
  // 	sfile.close

  // 	val parsed = scala.util.parsing.json.JSON.parseFull(json_string)
  // 	if( parsed.isEmpty ){
  // 		println(s"failed to parse: $path")
  // 		return
  // 	}

  // 	val map = parsed.get.asInstanceOf[Map[String,Any]]

  //   for( i <- (0 until loops.size)){
  //   	loops(i).load("/Users/fishuyo/"+path+"/"+i+".wav")

	//   	val l = map("loop"+i).asInstanceOf[List[Double]]
	//   	// println(l)
	//   	val loop = loops(i)
	//   	val b = loop.b

	//   	b.curSize = l(0).toInt
	//   	b.maxSize = b.curSize
	//   	b.rPos = l(1).toFloat
	//   	b.wPos = l(2).toInt
	//   	b.rMin = l(3).toInt
	//   	b.rMax = l(4).toInt
	//   	b.speed = l(5).toFloat
	//   	loop.gain = l(6).toFloat
	//   	loop.pan = l(7).toFloat
	//   	loop.decay = l(8).toFloat

  //   }
	}
}


