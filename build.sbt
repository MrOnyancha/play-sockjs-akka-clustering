name := "play_sockjs_akka"

version := "1.0"

lazy val `play_sockjs_akka` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val akkaVersion = "2.4.8"

libraryDependencies ++=
  Seq(
    jdbc , cache , ws   , specs2 % Test,
    "com.github.fdimuccio" %% "play2-sockjs" % "0.5.0",
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "com.hootsuite" %% "akka-persistence-redis" % "0.4.0"
  )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers ++=
  Seq(
    "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    Resolver.sonatypeRepo("releases"),
    Resolver.jcenterRepo
  )