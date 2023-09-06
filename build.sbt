
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}


/**
 * Examples
 */
lazy val examples = project //crossProject(JVMPlatform, JSPlatform)
  // .crossType(CrossType.Pure)
  .in(file("examples"))
  // .dependsOn(math)
  .dependsOn(app.jvm, math.jvm, live) // multitouch, video)
  .settings(Settings.app: _*)

// lazy val examplesJVM = examples.jvm.dependsOn(graphics_lwjgl, audio_portaudio, audio_jack, live, multitouch)
// lazy val examplesJS = examples.js.dependsOn(graphics_webgl)

lazy val examplesjs = project.enablePlugins(ScalaJSPlugin)
  .in(file("examplesjs"))
  .settings(Settings.common: _*)
  // .settings(scalaJSUseMainModuleInitializer := true)
//   .settings(libraryDependencies ++= Dependencies.coreJs.value)
  .dependsOn(app.js, math.js)


/*
 * Core Modules
 */
lazy val actor = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/actor"))
  .settings(Settings.common: _*)

lazy val runtime = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/runtime"))
  .settings(Settings.common: _*)

lazy val graphics = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/graphics"))
  .dependsOn(math)
  .settings(Settings.common: _*)

lazy val audio = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/audio"))
  .dependsOn(math)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.audio.value)

lazy val math = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/math"))
  .settings(Settings.common: _*)



/**
 * Backends 
 */
lazy val graphics_lwjgl = project
  .in(file("modules/graphics-lwjgl"))
  .dependsOn(graphics.jvm, runtime.jvm)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.lwjgl.libs.value)

lazy val graphics_webgl = project.enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .in(file("modules/graphics-webgl"))
  .dependsOn(graphics.js, runtime.js)
  .settings(Settings.common: _*)
//   .settings(scalaJSUseMainModuleInitializer := true)
//   .settings(libraryDependencies ++= Dependencies.coreJs.value)
  .settings(
    Compile / npmDependencies ++= Seq(
      "@types/webgl2" -> "0.0.6"
    ),
    useYarn := true
  )

lazy val audio_portaudio = project
  .in(file("modules/audio-portaudio"))
  .dependsOn(audio.jvm, runtime.jvm)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.audio.value)

lazy val audio_jack = project
  .in(file("modules/audio-jack"))
  .dependsOn(audio.jvm, runtime.jvm)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.audio.value)

/**
 * SeerApp - Default module configurations for simple projects
 */
lazy val app = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/app"))
  .settings(Settings.common: _*)
  // .dependsOn(actor)

lazy val appJVM = app.jvm.dependsOn(graphics_lwjgl, audio_portaudio)
lazy val appJS = app.js.dependsOn(graphics_webgl)


/**
 * Desktop addons
 */

/// Livecoding / scala script compiler
lazy val live = project
  .in(file("modules/live"))
  .dependsOn(actor.jvm)
  .settings(Settings.common: _*)


/// Multitouch wrapper for Apple trackpads
lazy val multitouch = project
  .in(file("modules/multitouch"))
  .dependsOn(math.jvm)
  .settings(Settings.common: _*)


/// VideoPlayer (VLC)
val video = project
  .in(file("modules/video-vlc"))
  .dependsOn()
  .settings(Settings.common: _*)
  .settings(libraryDependencies += "uk.co.caprica" % "vlcj" % "4.8.2")