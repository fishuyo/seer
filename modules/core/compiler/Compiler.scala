package seer
package compiler


import java.io.File
import scala.io.Source

import com.eed3si9n.eval._


object Compiler {

  def eval[T](source:String) : T = {
    Eval[T](source)
  }

}


/**
  * Toolbox implementation of a ScriptLoader
  */
// class ToolboxScriptLoader extends ScriptLoader {
//   val toolbox = ScriptManager.toolbox //currentMirror.mkToolBox() 

//   def eval[T]() : T = {
//     val source = getCode()
//     val tree = toolbox.parse(source)
//     toolbox.eval(tree).asInstanceOf[T]
//   }
  
//   override def checkErrors() = {
//     if(toolbox.frontEnd.hasErrors){
//       val errs = toolbox.frontEnd.infos.map { case info =>
//         val line = info.pos.line
//         val msg = s"""
//           ${info.msg}
//           ${info.pos.lineContent}
//           ${info.pos.lineCaret} 
//         """
//         (line,msg)
//       }.toSeq
//       errors = errs
//     } else errors = Seq()
//   }

// }


// import javax.script.ScriptEngineManager

// class DummyClass

// object Evaluator {
//   val engine = new ScriptEngineManager().getEngineByName("scala")
//   val settings = engine.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings
//   settings.embeddedDefaults[DummyClass]
//   engine.eval("val x: Int = 5")
//   val thing = engine.eval("x + 9").asInstanceOf[Int]
// }



