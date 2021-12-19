
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}


/**
 * Examples
 */
lazy val examples = project //.enablePlugins(JavaAgent)
  .in(file("examples"))
  .dependsOn(graphics_lwjgl, math.jvm)
  .settings(Settings.app: _*)
  // .settings(javaAgents += "org.lwjglx" % "lwjglx-debug" % "1.0.0" % "runtime" from "https://build.lwjgl.org/addons/lwjglx-debug/lwjglx-debug-1.0.0.jar")

lazy val examplesjs = project.enablePlugins(ScalaJSPlugin)
  .in(file("examplesjs"))
  .settings(Settings.common: _*)
  // .settings(scalaJSUseMainModuleInitializer := true)
//   .settings(libraryDependencies ++= Dependencies.coreJs.value)
  .dependsOn(graphics_webgl, math.js)


/*
 * Core Modules
 */
lazy val runtime = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/runtime"))
  .settings(Settings.common: _*)

lazy val graphics = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/graphics"))
  .settings(Settings.common: _*)

lazy val math = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/math"))
  .settings(Settings.common: _*)


/**
 * Graphics / Audio Backends 
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


