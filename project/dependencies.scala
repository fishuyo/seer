import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  object akka {
    val version = "2.6.17"
  }

  object lwjgl {
    val version = "3.3.0"
    val natives = "natives-macos-arm64"
    val libs = Def.setting(Seq(
      "org.lwjgl" % "lwjgl" % version,
      "org.lwjgl" % "lwjgl" % version classifier natives,
      "org.lwjgl" % "lwjgl-glfw" % version,
      "org.lwjgl" % "lwjgl-glfw" % version classifier natives,
      "org.lwjgl" % "lwjgl-opengl" % version,
      "org.lwjgl" % "lwjgl-opengl" % version classifier natives
    ))
  }

  val chillV = "0.9.5" 

  val spatial = Def.setting(Seq(
    "org.typelevel" %%% "spire" % "0.17.0-RC1",
    "javax.vecmath" % "vecmath" % "1.5.2"
  ))

  val audio = Def.setting(Seq(
    "net.sourceforge.jtransforms" % "jtransforms" % "2.4.0",
    "de.sciss" %%% "audiofile" % "2.3.3"
  ))

  val runtime = Def.setting(Seq(
    "com.typesafe.akka" %% "akka-actor" % akka.version,
    "com.typesafe.akka" %% "akka-remote" % akka.version,
    // "com.typesafe.akka" %% "akka-stream" % akka.version,

    "io.altoo" %% "akka-kryo-serialization" % "1.1.5",
    "com.twitter" %% "chill" % "0.10.0",
    "com.twitter" %% "chill-bijection" % "0.10.0",
    "com.twitter" %% "chill-akka" % "0.10.0",

    "com.github.pathikrit" %% "better-files" % "3.9.1",
    "com.github.pathikrit" %% "better-files-akka" % "3.9.1",
    "io.methvin" % "directory-watcher" % "0.10.1",
    "io.methvin" %% "directory-watcher-better-files" % "0.10.1",

    "de.sciss" %% "scalaosc" % "1.2.1",
    "com.lihaoyi" %% "os-lib" % "0.7.2"
  ))


  val core = Def.setting(Seq(
    "com.chuusai" %%% "shapeless" % "2.3.3",
    // "org.typelevel" %%% "spire" % "0.17.0-RC1",
    // "net.sourceforge.jtransforms" % "jtransforms" % "2.4.0",
    "com.lihaoyi" %%% "scalarx" % "0.4.3",
    // "javax.vecmath" % "vecmath" % "1.5.2"
  ))

  val coreJvm = Def.setting(core.value ++ Seq(
    "com.typesafe.akka" %% "akka-actor" % akka.version,
    "com.typesafe.akka" %% "akka-remote" % akka.version,
    // "com.typesafe.akka" %% "akka-stream" % akka.version,
    "io.altoo" %% "akka-kryo-serialization" % "1.1.5",
    "com.twitter" %% "chill" % chillV,
    "com.twitter" %% "chill-bijection" % chillV,
    "com.twitter" %% "chill-akka" % chillV,
    "de.sciss" %% "scalaosc" % "1.2.1",
    // "com.beachape.filemanagement" %% "schwatcher" % "0.3.5",
    "com.github.pathikrit" %% "better-files" % "3.9.1",
    "com.github.pathikrit" %% "better-files-akka" % "3.9.1",
    "io.methvin" % "directory-watcher" % "0.10.1",
    "io.methvin" %% "directory-watcher-better-files" % "0.10.1"
  ))

  val coreJs = Def.setting(core.value ++ Seq(
    "org.akka-js" %%% "akkajsactor" % "2.2.6.9",
    // "org.akka-js" %%% "akkajsactorstream" % "2.2.6.9",
    "org.scala-js" %%% "scalajs-dom" % "1.1.0"
  ))

  val rx = Def.setting(Seq(
    "com.lihaoyi" %%% "scalarx" % "0.4.3"
  ))



}
