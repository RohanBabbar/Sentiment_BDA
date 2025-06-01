from pyspark.sql import SparkSession
from textblob import TextBlob
from pyspark.sql.functions import udf
from pyspark.sql.types import StringType

# Start Spark
spark = SparkSession.builder \
    .appName("SentimentAnalysis") \
    .getOrCreate()

# Load cleaned data
df = spark.read.parquet("cleaned_tweets.parquet")

# Define UDF
def get_sentiment(text):
    polarity = TextBlob(text).sentiment.polarity
    if polarity > 0:
        return "Positive"
    elif polarity < 0:
        return "Negative"
    else:
        return "Neutral"

sentiment_udf = udf(get_sentiment, StringType())

# Add sentiment column
df_with_sentiment = df.withColumn("sentiment", sentiment_udf(df["cleaned_text"]))
# Save to CSV
df_with_sentiment.toPandas().to_csv("final_sentiment_results.csv", index=False)

print("✅ Sentiment analysis complete. Results saved to final_sentiment_results.csv")