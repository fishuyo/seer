libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % Dependencies.akka.version,
  "com.typesafe.akka" %% "akka-remote" % Dependencies.akka.version,
  // "com.typesafe.akka" %% "akka-stream" % Dependencies.akka.version,

  "io.altoo" %% "akka-kryo-serialization" % "1.1.5", // upgrade? port old kryo data pointclouds?
  
  "com.twitter" %% "chill" % "0.10.0",
  "com.twitter" %% "chill-bijection" % "0.10.0",
  "com.twitter" %% "chill-akka" % "0.10.0",

  "com.github.pathikrit" %% "better-files" % "3.9.1",
  "com.github.pathikrit" %% "better-files-akka" % "3.9.1",
  "io.methvin" % "directory-watcher" % "0.15.0",
  "io.methvin" %% "directory-watcher-better-files" % "0.15.0",

  "com.lihaoyi" %% "os-lib" % "0.7.2",
)