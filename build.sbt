name := """play-scala-intro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
