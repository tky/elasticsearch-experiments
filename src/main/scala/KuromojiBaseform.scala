import com.sksamuel.elastic4s.analyzers.CustomAnalyzerDefinition
import com.sksamuel.elastic4s.{ ElasticsearchClientUri, TcpClient }
import com.sksamuel.elastic4s.searches.RichSearchResponse
import com.sksamuel.elastic4s.embedded.LocalNode

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.common.settings.Settings

import com.sksamuel.elastic4s.circe._
import io.circe.generic.auto._

import filter.KuromojiBaseformFilter

object KuromojiBaseform extends ExperimentsSupport {
  import com.sksamuel.elastic4s.ElasticDsl._

  println("--------- with kuromoji_baseform ------------------")
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

    client.execute {
      indexInto("bands" / "artists") doc Artist("山田太郎", "歌い放題") refresh (RefreshPolicy.IMMEDIATE)
    }.await

    val resp = client.execute {
      search("bands" / "artists") query termQuery("description", "歌う")
    }.await

    format(resp)
  }

  println("--------- without kuromoji_baseform ------------------")
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
            KuromojiTokenizer
          )
        )
    }.await

    client.execute {
      indexInto("bands" / "artists") doc Artist("山田太郎", "歌い放題") refresh (RefreshPolicy.IMMEDIATE)
    }.await

    val resp = client.execute {
      search("bands" / "artists") query termQuery("description", "歌う")
    }.await

    format(resp)
  }
}

