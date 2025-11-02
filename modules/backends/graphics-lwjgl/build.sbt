val version = "3.3.2"
val natives = "natives-macos-arm64"
val libs = Seq(
  "org.lwjgl" % "lwjgl" % version,
  "org.lwjgl" % "lwjgl" % version classifier natives,
  "org.lwjgl" % "lwjgl-glfw" % version,
  "org.lwjgl" % "lwjgl-glfw" % version classifier natives,
  "org.lwjgl" % "lwjgl-opengl" % version,
  "org.lwjgl" % "lwjgl-opengl" % version classifier natives,
  "org.lwjgl" % "lwjgl-vulkan" % version,
  "org.lwjgl" % "lwjgl-vulkan" % version classifier natives,
)
libraryDependencies ++= libs