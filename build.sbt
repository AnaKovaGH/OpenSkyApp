//GENERAL SETTINGS------------------------------------------------------------------------------------------------------
ThisBuild / name := "OpenSkyApp"
ThisBuild / scalaVersion := "2.13.0"


//PROJECTS SETTINGS-----------------------------------------------------------------------------------------------------
lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin)
  //the reason for enabling JavaAppPackaging
  //[error] [1] You have no mappings defined! This will result in an empty package
  //[error] Try enabling an archetype, e.g. `enablePlugins(JavaAppPackaging)`
  .enablePlugins(JavaAppPackaging)
  .settings(
    publishTo := Some(Resolver.file("releases",  new File( "releases/" ))),
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.25",
  "com.typesafe.akka" %% "akka-http"   % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.25",
  "com.typesafe" % "config" % "1.3.4",
  
  "org.apache.kafka" % "kafka_2.12" % "2.3.0",
  "io.tmos" % "arm4s_2.12" % "1.1.0",
  "org.apache.httpcomponents" % "httpclient" % "4.5.9",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime, //SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
  
  "io.circe" %% "circe-core" % "0.12.1",
  "io.circe" %% "circe-generic" % "0.12.1",
  "io.circe" %% "circe-parser"% "0.12.1"

)