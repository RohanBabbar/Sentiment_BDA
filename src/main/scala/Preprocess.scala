import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object Preprocess {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Tweet Preprocessing")
      .master("local[*]")
      .config("spark.mongodb.connection.uri", "mongodb://127.0.0.1/")
      .config("spark.mongodb.read.database", "social")
      .config("spark.mongodb.read.collection", "tweets")
      .getOrCreate()

    val df = spark.read
      .format("mongodb")
      .load()

    val cleaned = df
      .filter(col("text").isNotNull)
      .withColumn("cleaned_text", regexp_replace(col("text"), "[^a-zA-Z\\s]", ""))
      .withColumn("cleaned_text", lower(col("cleaned_text")))

    cleaned.write.parquet("cleaned_tweets.parquet")

    spark.stop()
  }
}