import com.sksamuel.elastic4s.analyzers.CustomAnalyzerDefinition
import org.elasticsearch.action.admin.indices.analyze.{ AnalyzeRequest, AnalyzeResponse }
import filter.KuromojiReadingform

object KuromojiReadingformSample extends ExperimentsSupport {
  import com.sksamuel.elastic4s.ElasticDsl._

  def apply(userRomaji: Boolean) {
    connect { client =>
      client.execute {
        createIndex("bands").mappings(
          mapping("artist") as (
            textField("name"),
            textField("description") analyzer "my_analyzer"
          )
        )
          .analysis(
            CustomAnalyzerDefinition(
              "my_analyzer",
              KuromojiTokenizer,
              KuromojiReadingform("kuromoji_kana_filter", userRomaji)
            )
          )
      }.await

      val response: AnalyzeResponse = client.java.admin().indices().analyze(new AnalyzeRequest("bands").analyzer("my_analyzer").text("働きたくない")).actionGet()
      response.forEach { token =>
        {
          println(s"""
        |token: ${token.getTerm()}
        |start_offset: ${token.getStartOffset()}
        |end_offset: ${token.getEndOffset()}
        |type: ${token.getType}
        |position: ${token.getPosition}
        """.stripMargin)
        }
      }
    }
  }
}
