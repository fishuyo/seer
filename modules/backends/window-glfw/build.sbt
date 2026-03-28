val version = "3.3.2"
val natives = "natives-macos-arm64"
val libs = Seq(
  "org.lwjgl" % "lwjgl-glfw" % version,
  "org.lwjgl" % "lwjgl-glfw" % version classifier natives,
  "org.lwjgl" % "lwjgl" % version,
  "org.lwjgl" % "lwjgl" % version classifier natives,
)
libraryDependencies ++= libs
