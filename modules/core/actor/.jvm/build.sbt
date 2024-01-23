libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % Dependencies.akka.version,
  "com.typesafe.akka" %% "akka-remote" % Dependencies.akka.version,
  // "com.typesafe.akka" %% "akka-stream" % Dependencies.akka.version,

  // "io.altoo" %% "akka-kryo-serialization" % "2.5.0",
  "io.altoo" %% "akka-kryo-serialization" % "2.5.0" excludeAll (ExclusionRule("com.typesafe.akka", "akka-actor_2.13"), ExclusionRule("org.agrona", "agrona")),
  
  // "com.twitter" %% "chill" % "0.10.0",
  // "com.twitter" %% "chill-bijection" % "0.10.0",
  // "com.twitter" %% "chill-akka" % "0.10.0",

  "com.github.pathikrit" %% "better-files" % "3.9.2",
  "com.github.pathikrit" %% "better-files-akka" % "3.9.2",
  "io.methvin" % "directory-watcher" % "0.18.0",
  "io.methvin" %% "directory-watcher-better-files" % "0.18.0",

  "com.lihaoyi" %% "os-lib" % "0.9.1",
  "com.lihaoyi" %% "os-lib-watch" % "0.9.1"
)