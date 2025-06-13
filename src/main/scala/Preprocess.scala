import scala.io.Source
import org.mongodb.scala._
import org.mongodb.scala.bson.collection.mutable.Document
import com.github.tototoshi.csv._
import scala.util.matching.Regex

object PreprocessTweets {

  def cleanText(text: String): String = {
    val urlPattern: Regex = "(https?|ftp)://[^\\s]+".r
    val mentionPattern: Regex = "@\\w+".r
    val hashtagPattern: Regex = "#\\w+".r
    val specialCharsPattern: Regex = "[^a-zA-Z\\s]".r

    val noURLs = urlPattern.replaceAllIn(text, "")
    val noMentions = mentionPattern.replaceAllIn(noURLs, "")
    val noHashtags = hashtagPattern.replaceAllIn(noMentions, "")
    val noSpecialChars = specialCharsPattern.replaceAllIn(noHashtags, "")
    val lowerCased = noSpecialChars.toLowerCase
    val cleaned = lowerCased.replaceAll("\\s+", " ").trim
    cleaned
  }

  def main(args: Array[String]): Unit = {
    val inputPath = "fetched_tweets.csv"
    val reader = CSVReader.open(inputPath)

    val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017")
    val database: MongoDatabase = mongoClient.getDatabase("social")
    val collection: MongoCollection[Document] = database.getCollection("tweets")

    val allRows = reader.allWithHeaders()

    println("🧹 Preprocessing and inserting tweets into MongoDB...")

    for (row <- allRows.take(100)) {
      val id = row.getOrElse("id", "")
      val rawText = row.getOrElse("text", "")
      val cleanedText = cleanText(rawText)

      val doc = Document("id" -> id, "text" -> cleanedText)
      collection.insertOne(doc).results()
    }

    reader.close()
    mongoClient.close()
    println("✅ Preprocessing complete: tweets inserted into MongoDB collection `social.tweets`.")
  }

  // Helper extension for blocking insert
  implicit class DocumentObservable[C](val observable: Observable[C]) {
    import scala.concurrent.Await
    import scala.concurrent.duration._
    def results(): Seq[C] = Await.result(observable.toFuture(), 10.seconds)
  }
}