import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import xerial.sbt.Sonatype.sonatypeCentralHost

ThisBuild / organization := "io.github.fishuyo"
ThisBuild / scalaVersion := "3.3.5"
ThisBuild / version := "0.2.0-SNAPSHOT"
ThisBuild / updateOptions := updateOptions.value.withCachedResolution(true)

ThisBuild / sonatypeCredentialHost := sonatypeCentralHost
// ThisBuild / sbtPluginPublishLegacyMavenStyle := false
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := sonatypePublishTo.value
// {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value)
//     Some("snapshots" at nexus + "content/repositories/snapshots")
//   else
//     Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }

// ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / licenses := Seq(
  "BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause")
)
ThisBuild / homepage := Some(url("https://github.com/fishuyo/seer"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/fishuyo/seer"),
    "scm:git:git@github.com:fishuyo/seer.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "fishuyo",
    name = "Timothy Wood",
    email = "fishuyo@gmail.com",
    url = url("http://embodiedworlds.com")
  )
)
// ThisBuild / sonatypeCredentialHost := "oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://central.sonatype.com/repository/maven-snapshots/" //"https://oss.sonatype.org/service/local"

/*
 * Core Modules
 */

// Runtime modules provide an interface for building up applications from modular components at runtime
lazy val runtime = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/runtime"))
  .settings(Settings.common: _*)

// Base spatial math and types
lazy val math = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/math"))
  .settings(Settings.common: _*)
  .settings(scalacOptions += "-explain")
  .settings(libraryDependencies ++= Dependencies.math.value)

// Base Graphics API
lazy val graphics = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/graphics"))
  .settings(Settings.common: _*)
  .dependsOn(math)

// Base Audio API
lazy val audio = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/audio"))
  .settings(Settings.common: _*)
  .dependsOn(math)
  .settings(libraryDependencies ++= Dependencies.audio.value)

// Compiler interface enabling Scripting / Live-coding / Runtime compilation
lazy val compiler = project
  .in(file("modules/core/compiler"))
  .settings(Settings.common: _*)
  .dependsOn(actor.jvm)

lazy val osc = project
  .in(file("modules/core/osc"))
  .settings(Settings.common: _*)
// .settings(libraryDependencies += "de.sciss" %% "scalaosc" % "1.3.1")
// .dependsOn(actor.jvm)

// Base Pekko actor utilities
lazy val actor = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/actor"))
  .settings(Settings.common: _*)

/** Backend Audio/Graphics Implementations
  */
lazy val graphics_lwjgl = project
  .in(file("modules/backends/graphics-lwjgl"))
  .dependsOn(graphics.jvm, runtime.jvm)
  .settings(Settings.common: _*)
// .settings(libraryDependencies ++= Dependencies.lwjgl.libs.value)

lazy val graphics_webgl = project
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .in(file("modules/backends/graphics-webgl"))
  .settings(Settings.common: _*)
  .dependsOn(graphics.js, runtime.js)
//   .settings(scalaJSUseMainModuleInitializer := true)
//   .settings(libraryDependencies ++= Dependencies.coreJs.value)
  .settings(
    Compile / npmDependencies ++= Seq(
      "@types/webgl2" -> "0.0.6"
    )
    // useYarn := true
  )

lazy val audio_portaudio = project
  .in(file("modules/backends/audio-portaudio"))
  .dependsOn(audio.jvm, runtime.jvm)
  .settings(Settings.common: _*)
// .settings(libraryDependencies ++= Dependencies.audio.value)

lazy val audio_jack = project
  .in(file("modules/backends/audio-jack"))
  .dependsOn(audio.jvm, runtime.jvm)
  .settings(Settings.common: _*)
// .settings(libraryDependencies ++= Dependencies.audio.value)

/** App - Simplified application interfaces using default module configurations
  * for introductory examples / simple projects
  */
lazy val app = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/app"))
  .settings(Settings.common: _*)
// .dependsOn(actor)

lazy val appJVM = app.jvm.dependsOn(actor.jvm, graphics_lwjgl, audio_portaudio)
lazy val appJS = app.js.dependsOn(graphics_webgl)

// /**
//  * Examples
//  */
lazy val examples = project // crossProject(JVMPlatform, JSPlatform)
  // .crossType(CrossType.Pure)
  .in(file("examples"))
  // .dependsOn(math)
  .dependsOn(app.jvm, math.jvm, compiler, osc, actor.jvm) // multitouch, video)
  .settings(Settings.app: _*)

// lazy val examplesJVM = examples.jvm.dependsOn(graphics_lwjgl, audio_portaudio, audio_jack, compiler, multitouch)
// lazy val examplesJS = examples.js.dependsOn(graphics_webgl)

lazy val examplesjs = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("examplesjs"))
  .settings(Settings.common: _*)
  // .settings(scalaJSUseMainModuleInitializer := true)
//   .settings(libraryDependencies ++= Dependencies.coreJs.value)
  .dependsOn(app.js, math.js)

/** Extensions
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

// val ndi = project
//   .in(file("modules/extensions/video-ndi"))
//   .dependsOn(app.jvm)
//   .settings(Settings.app: _*)
