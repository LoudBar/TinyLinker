ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisclient" % "3.42",
  "redis.clients" % "jedis" % "3.7.0",
  "com.github.pureconfig" %% "pureconfig" % "0.17.4",
  "com.softwaremill.sttp.client3" %% "core" % "3.8.15",
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
  "com.typesafe.akka" %% "akka-actor" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.8.0"
)

lazy val root = (project in file("."))
  .settings(
    name := "TinyLinker",
    assembly / mainClass := Some("Main"),
    assembly / assemblyJarName := "tinyLinker.jar"
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}
