name := "play_sockjs_akka"

version := "1.0"

lazy val `play_sockjs_akka` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

val akkaVersion = "2.5.17"

libraryDependencies ++=
  Seq(
    jdbc, cache, ws, specs2 % Test,
      guice,
    "com.github.fdimuccio" %% "play2-sockjs" % "0.6.0",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
    "com.hootsuite" %% "akka-persistence-redis" % "0.8.0" % "runtime",
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
  )
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.22"


unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers ++=
  Seq(
    "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    Resolver.sonatypeRepo("releases"),
    Resolver.jcenterRepo
  )