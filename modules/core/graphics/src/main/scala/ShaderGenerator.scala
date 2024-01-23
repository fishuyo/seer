
package seer
package graphics

import scala.collection.mutable.ListBuffer


/**
 * 
 *  
 * 
 * 
 * 
 * 
 * 
*/


class ShaderGenerator {

  var _prefix = "#version 330 core\n"
  var _fragPrefix = ""
  var _vert = true
  var _code = ""
  val _varying = ListBuffer[String]()

  def setPlatform(platform:String) = {
    platform match { 
      case "gles" => _prefix = "#version 300 es\n"; _fragPrefix = "precision highp float;\n"
      case _ => _prefix = "#version 330 core\n"
    }
  }

  def apply() = _code.stripMargin
  
  def reset() = {_code = ""; this}

  def vert() = { 
    _vert = true
    _varying.clear()
    reset().append(_prefix)
  }
  def frag() = { 
    _vert = false
    reset().append(_prefix).append(_fragPrefix)
  }
  

  def append(s:String) = {
    _code += s
    this
  }

  def attributes(color:Boolean=false, uv:Boolean=false, normal:Boolean=false) = {
    val s = s"""
    |layout(location = 0) in vec3 position;
    |${if(color) "layout(location = 1) in vec4 color;" else ""}
    |${if(uv) "layout(location = 2) in vec2 uv;" else ""}
    |${if(normal) "layout(location = 3) in vec3 normal;" else ""}
    |""".stripLeading
    append(s)
  }

  def out(decl:String) = {
    if(_vert) _varying += decl
    append(s"out $decl;\n")
  }

  def in(decl:String="") = {
    if(!_vert && decl.isEmpty)
      _varying.foreach{ case v => append(s"in $v;\n") }
    else
      append(s"in $decl;\n")
    this
  }

  def main(body:String) = {
    val s = s"""
    |void main(){
$body
    |}"""
    append(s)
  }

}


