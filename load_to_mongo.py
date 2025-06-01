import pymongo
import json

client = pymongo.MongoClient("mongodb://localhost:27017/")
db = client["social"]
collection = db["tweets"]

try:
    with open("tweets.json", "r") as file:
        data = json.load(file)
        print(f"Loaded {len(data)} tweets from JSON file.")
        
        if len(data) > 0:
            collection.insert_many(data)
            print("Tweets inserted successfully into MongoDB.")
        else:
            print("No data found in tweets.json.")
except FileNotFoundError:
    print("File tweets.json not found.")
except Exception as e:
    print(f"Error: {e}")