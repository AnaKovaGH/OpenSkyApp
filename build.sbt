//GENERAL SETTINGS------------------------------------------------------------------------------------------------------
ThisBuild / name := "sbtTemplate"
ThisBuild / scalaVersion := "2.13.0"


//PROJECTS SETTINGS-----------------------------------------------------------------------------------------------------
lazy val root = (project in file("."))
  .aggregate(content, mainApp)
  .enablePlugins(ScalastylePlugin)
  .enablePlugins(DependencyGraphPlugin)
  .settings(
    publishTo := Some(Resolver.file("releases",  new File( "releases/" ))),
    skip in publish := true
  )

lazy val content = Project(id="content", base = file("content"))
  .settings(
    publishTo := Some(Resolver.file("releases",  new File( "releases/" )))
  )

lazy val mainApp = Project(id="mainApp", base = file("mainApp"))
  .dependsOn(content)
  .enablePlugins(DockerPlugin)
  //the reason for enabling JavaAppPackaging
  //[error] [1] You have no mappings defined! This will result in an empty package
  //[error] Try enabling an archetype, e.g. `enablePlugins(JavaAppPackaging)`
  .enablePlugins(JavaAppPackaging)
  .settings(
    publishTo := Some(Resolver.file("releases",  new File( "releases/" )))
  )
mainClass in (Compile, packageBin) := Some("Main")
