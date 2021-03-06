lazy val getLibs:TaskKey[Unit] = TaskKey[Unit]("download unmanaged libs.")

getLibs := {
  val dir = baseDirectory.value / "lib"
  Utilities.getJPA(dir)
}

// compile in Compile <<= (compile in Compile).dependsOn(getLibs)
compile in Compile := ((compile in Compile).dependsOn(getLibs)).value
