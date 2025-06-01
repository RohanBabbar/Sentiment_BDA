name := "SocialSentimentProject"

version := "0.1"

scalaVersion := "2.13.11"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.4.1",
  "org.apache.spark" %% "spark-sql" % "3.4.1",
  "org.mongodb.spark" %% "mongo-spark-connector" % "10.2.1",
  "com.softwaremill.sttp.client3" %% "core" % "3.9.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.10",
  "com.typesafe.play" %% "play-json" % "2.9.4"
)