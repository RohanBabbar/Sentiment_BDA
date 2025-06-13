from pyspark.sql import SparkSession
from textblob import TextBlob
from pyspark.sql.functions import udf
from pyspark.sql.types import StringType
from pymongo import MongoClient
import pandas as pd

# Connect to MongoDB and load tweets
client = MongoClient("mongodb://localhost:27017/")
db = client["social"]
collection = db["tweets"]

# Load all tweets from MongoDB into a DataFrame
data = list(collection.find({}, {"_id": 0, "text": 1}))  # Get only tweet text
df_raw = pd.DataFrame(data)

# Initialize Spark
spark = SparkSession.builder.appName("SentimentAnalysis").getOrCreate()

# Convert Pandas DataFrame to Spark DataFrame
df = spark.createDataFrame(df_raw)

# Define UDF for sentiment
def get_sentiment(text):
    if not text:
        return "Neutral"
    polarity = TextBlob(text).sentiment.polarity
    if polarity > 0:
        return "Positive"
    elif polarity < 0:
        return "Negative"
    else:
        return "Neutral"

sentiment_udf = udf(get_sentiment, StringType())

# Add sentiment column
df_with_sentiment = df.withColumn("sentiment", sentiment_udf(df["text"]))

# Convert to Pandas and save to CSV
df_with_sentiment.toPandas().to_csv("final_sentiment_results.csv", index=False)

print("✅ Sentiment analysis complete. Results saved to final_sentiment_results.csv")