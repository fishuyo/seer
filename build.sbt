
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}


ThisBuild / organization := "seer"
ThisBuild / scalaVersion := "3.3.1" //"3.2.0"
ThisBuild / version      := "0.2.0-SNAPSHOT"
ThisBuild / updateOptions := updateOptions.value.withCachedResolution(true)


/*
 * Core Modules
 */

// Runtime modules provide an interface for building applications from modular components
lazy val runtime = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/runtime"))

// Base spatial math and data handling
lazy val math = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/math"))
  .settings(libraryDependencies ++= Dependencies.math.value)

// Base Graphics API
lazy val graphics = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/graphics"))
  .dependsOn(math)

// Base Audio API
lazy val audio = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/audio"))
  .dependsOn(math)
  .settings(libraryDependencies ++= Dependencies.audio.value)


// ?? split up into: Parameter
lazy val actor = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/actor"))

// Live-coding interface
lazy val live = project
  .in(file("modules/core/live"))
  .dependsOn(actor.jvm)


/**
 * Backend Implementations
 */
lazy val graphics_lwjgl = project
  .in(file("modules/backends/graphics-lwjgl"))
  .dependsOn(graphics.jvm, runtime.jvm)
  .settings(libraryDependencies ++= Dependencies.lwjgl.libs.value)

lazy val graphics_webgl = project.enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .in(file("modules/backends/graphics-webgl"))
  .dependsOn(graphics.js, runtime.js)
//   .settings(scalaJSUseMainModuleInitializer := true)
//   .settings(libraryDependencies ++= Dependencies.coreJs.value)
  .settings(
    Compile / npmDependencies ++= Seq(
      "@types/webgl2" -> "0.0.6"
    ),
    useYarn := true
  )

lazy val audio_portaudio = project
  .in(file("modules/backends/audio-portaudio"))
  .dependsOn(audio.jvm, runtime.jvm)
  // .settings(Settings.common: _*)
  // .settings(libraryDependencies ++= Dependencies.audio.value)

lazy val audio_jack = project
  .in(file("modules/backends/audio-jack"))
  .dependsOn(audio.jvm, runtime.jvm)
  // .settings(Settings.common: _*)
  // .settings(libraryDependencies ++= Dependencies.audio.value)



/**
 * App - Simplified application interfaces using default module configurations for introductory examples / simple projects
 */
lazy val app = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/app"))
  // .settings(Settings.common: _*)
  // .dependsOn(actor)

lazy val appJVM = app.jvm.dependsOn(graphics_lwjgl, audio_portaudio)
lazy val appJS = app.js.dependsOn(graphics_webgl)


// /**
//  * Examples
//  */
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
  // .settings(Settings.common: _*)
  // .settings(scalaJSUseMainModuleInitializer := true)
//   .settings(libraryDependencies ++= Dependencies.coreJs.value)
  .dependsOn(app.js, math.js)




/**
 * Extensions
 */

/// Multitouch wrapper for Apple trackpads
// lazy val multitouch = project
//   .in(file("modules/multitouch"))
//   .dependsOn(math.jvm)
//   .settings(Settings.common: _*)


// /// VideoPlayer (VLC)
// val video = project
//   .in(file("modules/video-vlc"))
//   .dependsOn()
//   .settings(Settings.common: _*)
//   .settings(libraryDependencies += "uk.co.caprica" % "vlcj" % "4.8.2")



lazy val server_base = project
  .in(file("modules/server/server-base"))
  