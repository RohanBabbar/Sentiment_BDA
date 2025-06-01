import sttp.client3._
import sttp.model.Uri
import java.io.FileWriter
import com.github.tototoshi.csv._
import scala.util.parsing.json.JSON

object TweetFetcher {

  val BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAANOR2AEAAAAAO2D3AfK2euo7nfeY0FfDmaFnC5k%3D0Zp5U1MgXWaPZXL1h67d1OKoiMry56Y7LZydZZQu0Y70WNEQ4a"  // Replace this

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
        val parsed = JSON.parseFull(jsonString)
        parsed match {
          case Some(jsonMap: Map[String, Any]) =>
            val tweets = jsonMap("data").asInstanceOf[List[Map[String, String]]]
            saveToCSV(tweets)
          case _ =>
            println("Could not parse JSON.")
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
      val id = tweet.getOrElse("id", "")
      val createdAt = tweet.getOrElse("created_at", "")
      val lang = tweet.getOrElse("lang", "")
      val text = tweet.getOrElse("text", "").replaceAll("\n", " ").replaceAll("\"", "'")
      writer.writeRow(List(id, createdAt, lang, text))
    }

    writer.close()
    println("✅ Tweets saved to fetched_tweets.csv")
  }
}