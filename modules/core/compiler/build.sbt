
libraryDependencies ++= Seq(
  // "com.twitter" %% "util-eval" % "6.43.0",
  // "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  // "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  // "org.scala-lang" % "scala-library" % scalaVersion.value,
  // "com.github.dmytromitin" %% "eval" % "0.1",

  // "com.eed3si9n.eval" %% "eval" % "0.2.0" cross CrossVersion.full,
  "com.eed3si9n.eval" %% "eval" % "0.3.0" cross CrossVersion.full,

  "com.typesafe.akka" %% "akka-actor" % Dependencies.akka.version,
  // "com.typesafe.akka" %% "akka-remote" % Dependencies.akka.version,
  // "com.typesafe.akka" %% "akka-stream" % Dependencies.akka.version,

  // "io.altoo" %% "akka-kryo-serialization" % "1.1.5", // upgrade? port old kryo data pointclouds?
  
  // "com.twitter" %% "chill" % "0.10.0",
  // "com.twitter" %% "chill-bijection" % "0.10.0",
  // "com.twitter" %% "chill-akka" % "0.10.0",

  // "com.github.pathikrit" %% "better-files" % "3.9.2",
  // "com.github.pathikrit" %% "better-files-akka" % "3.9.2",
  // "io.methvin" % "directory-watcher" % "0.18.0",
  // "io.methvin" %% "directory-watcher-better-files" % "0.18.0",
)

