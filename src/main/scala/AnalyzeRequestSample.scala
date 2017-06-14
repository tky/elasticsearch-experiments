import com.sksamuel.elastic4s.analyzers.CustomAnalyzerDefinition
import com.sksamuel.elastic4s.{ ElasticsearchClientUri, TcpClient }
import com.sksamuel.elastic4s.searches.RichSearchResponse
import com.sksamuel.elastic4s.embedded.LocalNode

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.action.admin.indices.analyze.{ AnalyzeRequest, AnalyzeResponse }
import org.elasticsearch.common.settings.Settings

import com.sksamuel.elastic4s.circe._
import io.circe.generic.auto._

import filter.KuromojiBaseformFilter

object AnalyzeRequestSample extends ExperimentsSupport {
  import com.sksamuel.elastic4s.ElasticDsl._

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
            KuromojiBaseformFilter
          )
        )
    }.await

    val response: AnalyzeResponse = client.java.admin().indices().analyze(new AnalyzeRequest("bands").analyzer("my_analyzer").text("飲み放題")).actionGet()
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
