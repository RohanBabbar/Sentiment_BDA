import sttp.client3._
import sttp.model.Uri
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import com.github.tototoshi.csv._
import play.api.libs.json._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object TweetFetcher {

  val BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAANOR2AEAAAAAO2D3AfK2euo7nfeY0FfDmaFnC5k%3D0Zp5U1MgXWaPZXL1h67d1OKoiMry56Y7LZydZZQu0Y70WNEQ4a" 

  def main(args: Array[String]): Unit = {
    val query = "AI OR machine learning OR data science"
    val maxResults = 10

    val url = uri"https://api.twitter.com/2/tweets/search/recent?query=${query}&max_results=$maxResults&tweet.fields=created_at,lang"

    val backend = HttpURLConnectionBackend()
    val request = basicRequest
      .get(url)
      .header("Authorization", s"Bearer $BEARER_TOKEN")

    val response = request.send(backend)

    response.body match {
      case Right(jsonString) =>
        val parsedJson = Json.parse(jsonString)
        val tweets = (parsedJson \ "data").asOpt[List[JsValue]]

        tweets match {
          case Some(tweetList) =>
            val formattedTweets = tweetList.map { tweet =>
              Map(
                "id" -> (tweet \ "id").asOpt[String].getOrElse(""),
                "created_at" -> (tweet \ "created_at").asOpt[String].getOrElse(""),
                "lang" -> (tweet \ "lang").asOpt[String].getOrElse(""),
                "text" -> (tweet \ "text").asOpt[String].getOrElse("")
              )
            }
            saveToMongo(formattedTweets)

          case None =>
            println("No tweets found or JSON format incorrect.")
        }

      case Left(error) =>
        println(s"Request failed: $error")
    }
  } // <-- this was missing

  def saveToMongo(tweets: List[Map[String, String]]): Unit = {
    val mongoClient: MongoClient = MongoClient() // Default localhost:27017
    val database: MongoDatabase = mongoClient.getDatabase("twitterDB")
    val collection: MongoCollection[Document] = database.getCollection("tweets")

    val documents = tweets.map { tweet =>
      Document(
        "id" -> tweet("id"),
        "created_at" -> tweet("created_at"),
        "lang" -> tweet("lang"),
        "text" -> tweet("text").replaceAll("\n", " ").replaceAll("\"", "'")
      )
    }

    
      val insertFuture = collection.insertMany(documents).toFuture()
      Await.result(insertFuture, 10.seconds)
      println("✅ Tweet batch inserted to MongoDB")
  }
}