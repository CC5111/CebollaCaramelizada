name := """play-scala-intro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  evolutions,
  "com.h2database" % "h2" % "1.4.191",
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


//fork in run := false