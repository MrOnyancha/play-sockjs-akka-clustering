logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.20")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "3.0.0")