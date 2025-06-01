import scala.io.Source
import java.io.FileWriter
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
    val outputPath = "cleaned_tweets.csv"

    val reader = CSVReader.open(inputPath)
    val writer = CSVWriter.open(new FileWriter(outputPath))
    
    val allRows = reader.allWithHeaders()
    writer.writeRow(List("id", "clean_text"))

    for (row <- allRows.take(100)) {  // limit to 100 tweets as per your quota
      val id = row.getOrElse("id", "")
      val rawText = row.getOrElse("text", "")
      val cleaned = cleanText(rawText)
      writer.writeRow(List(id, cleaned))
    }

    reader.close()
    writer.close()
    println("✅ Preprocessing complete: cleaned_tweets.csv generated.")
  }
}