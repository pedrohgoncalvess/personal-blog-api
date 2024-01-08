ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "cthaeh-blog-api"
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

val AkkaVersion = "2.8.0"
val AkkaHttpVersion = "10.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "org.ektorp" % "org.ektorp" % "1.5.0",
  "org.codehaus.jackson" % "jackson-core-lgpl" % "1.9.13",
  "org.json4s" %% "json4s-native" % "4.0.7",
  "com.typesafe" % "config" % "1.4.2",
  "com.password4j" % "password4j" % "1.7.3",
  "ch.qos.logback" % "logback-classic" % "1.4.13" % Test,
  "ch.megard" %% "akka-http-cors" % "1.2.0"
)