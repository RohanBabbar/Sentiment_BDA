name := "SocialSentimentProject"

version := "0.1"

scalaVersion := "2.13.11"

libraryDependencies ++= Seq(
  // Spark
  "org.apache.spark" %% "spark-core" % "3.4.1",
  "org.apache.spark" %% "spark-sql" % "3.4.1",

  // MongoDB (native Scala driver)
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.0",

  // MongoDB Spark connector (if used in PySpark side or Spark job)
  "org.mongodb.spark" %% "mongo-spark-connector" % "10.2.1",

  // HTTP requests
  "com.softwaremill.sttp.client3" %% "core" % "3.9.0",

  // CSV handling
  "com.github.tototoshi" %% "scala-csv" % "1.3.10",

  // JSON parsing
  "com.typesafe.play" %% "play-json" % "2.9.4"
)