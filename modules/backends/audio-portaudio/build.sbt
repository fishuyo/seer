
resolvers += Resolver.mavenLocal

libraryDependencies += "com.github.rjeschke" % "jpa-macos" % "0.1-SNAPSHOT"
libraryDependencies += "com.github.rjeschke" % "jpa" % "0.1-SNAPSHOT"

// lazy val getLibs:TaskKey[Unit] = TaskKey[Unit]("download unmanaged libs.")

// getLibs := {
//   val dir = baseDirectory.value / "lib"
//   Utilities.getJPA(dir)
// }

// // compile in Compile <<= (compile in Compile).dependsOn(getLibs)
// compile in Compile := ((compile in Compile).dependsOn(getLibs)).value
