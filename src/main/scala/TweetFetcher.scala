import sttp.client3._
import sttp.model.Uri
import java.io.FileWriter
import com.github.tototoshi.csv._
import play.api.libs.json._

object TweetFetcher {

  val BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAANOR2AEAAAAAO2D3AfK2euo7nfeY0FfDmaFnC5k%3D0Zp5U1MgXWaPZXL1h67d1OKoiMry56Y7LZydZZQu0Y70WNEQ4a"  // Replace with your actual token

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
            saveToCSV(formattedTweets)
          case None =>
            println("No tweets found or JSON format incorrect.")
        }

      case Left(error) =>
        println(s"Request failed: $error")
    }
  }

  def saveToCSV(tweets: List[Map[String, String]]): Unit = {
    val file = new FileWriter("fetched_tweets.csv")
    val writer = CSVWriter.open(file)
    writer.writeRow(List("id", "created_at", "lang", "text"))

    tweets.foreach { tweet =>
      val id = tweet("id")
      val createdAt = tweet("created_at")
      val lang = tweet("lang")
      val text = tweet("text").replaceAll("\n", " ").replaceAll("\"", "'")
      writer.writeRow(List(id, createdAt, lang, text))
    }

    writer.close()
    println("✅ Tweets saved to fetched_tweets.csv")
  }
}