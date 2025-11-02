import scala.xml.XML
import scala.meta._
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.net.URL
import java.nio.channels.Channels

object OpenGLCodeGen {
  case class GLFunction(name: String, returnType: String, params: List[(String, String)], api: String, version: String)

  def downloadOpenGLSpec(url: String, outputPath: String): Unit = {
    val website = new URL(url)
    val rbc = Channels.newChannel(website.openStream())
    val fos = Files.newOutputStream(Paths.get(outputPath))
    fos.getChannel.transferFrom(rbc, 0, Long.MaxValue)
    fos.close()
    rbc.close()
  }

  def parseOpenGLSpec(filePath: String, apiFilter: String): List[GLFunction] = {
    val xml = XML.loadFile(filePath)
    (xml \ "commands" \ "command").flatMap { cmd =>
      val proto = (cmd \ "proto").text.trim.split(" ")
      val returnType = proto.init.mkString(" ")
      val name = proto.last
      val params = (cmd \ "param").map { p =>
        val parts = p.text.trim.split(" ")
        (parts.last, parts.init.mkString(" "))
      }.toList
      
      // Find which API version this function belongs to
      val feature = (xml \ "feature").find(f => (f \ "require" \ "command").exists(_.text == name))
      val api = feature.map(f => (f \ "@api").text).getOrElse("unknown")
      val version = feature.map(f => (f \ "@number").text).getOrElse("unknown")
      
      if (api == apiFilter) Some(GLFunction(name, returnType, params, api, version)) else None
    }.toList
  }

  def generateScalaBindings(functions: List[GLFunction], apiName: String): String = {
    val methods = functions.map { f =>
      val paramList = f.params.map { case (name, tpe) => param"$name: $tpe" }
      q"@inline def ${Term.Name(f.name)}(..$paramList): ${Type.Name(f.returnType)} = GL.${Term.Name(f.name)}(..${f.params.map(p => Term.Name(p._1))})"
    }

    val obj = q"object ${Term.Name(s"${apiName}Bindings")} { ..$methods }"
    obj.syntax
  }

  def writeToFile(content: String, outputPath: String): Unit = {
    Files.write(Paths.get(outputPath), content.getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  def main(args: Array[String]): Unit = {
    val specs = List(
      ("https://www.khronos.org/registry/OpenGL/api/GL/gl.xml", "gl.xml", "gl", "OpenGL"),
      ("https://www.khronos.org/registry/OpenGL/api/GL/gl2.xml", "gl2.xml", "gl", "OpenGL2"),
      ("https://www.khronos.org/registry/OpenGL/api/GL/gl3.xml", "gl3.xml", "gl", "OpenGL3"),
      ("https://www.khronos.org/registry/OpenGL/api/GL/gl4.xml", "gl4.xml", "gl", "OpenGL4"),
      ("https://www.khronos.org/registry/OpenGL/api/GLES/gl.xml", "gles.xml", "gles2", "OpenGLES2"),
      ("https://www.khronos.org/registry/OpenGL/api/GLES3/gl.xml", "gles3.xml", "gles3", "OpenGLES3"),
      ("https://www.khronos.org/registry/webgl/specs/latest/1.0/webgl.xml", "webgl1.xml", "webgl", "WebGL1"),
      ("https://www.khronos.org/registry/webgl/specs/latest/2.0/webgl.xml", "webgl2.xml", "webgl", "WebGL2")
    )
    
    specs.foreach { case (url, filePath, apiFilter, apiName) =>
      println(s"Downloading $apiName specs...")
      downloadOpenGLSpec(url, filePath)
      println(s"Parsing $apiName specs...")
      val functions = parseOpenGLSpec(filePath, apiFilter)
      println(s"Generating $apiName Scala bindings...")
      val scalaCode = generateScalaBindings(functions, apiName)
      val outputPath = s"src/main/scala/${apiName}Bindings.scala"
      writeToFile(scalaCode, outputPath)
      println(s"Generated $apiName bindings at $outputPath")
    }
  }
}
