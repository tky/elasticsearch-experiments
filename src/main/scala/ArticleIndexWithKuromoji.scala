import java.nio.file.Paths

import com.sksamuel.elastic4s.analyzers.CustomAnalyzerDefinition
import com.sksamuel.elastic4s.{ ElasticsearchClientUri, TcpClient }
import com.sksamuel.elastic4s.searches.RichSearchResponse
import com.sksamuel.elastic4s.embedded.LocalNode

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.common.settings.Settings

import com.sksamuel.elastic4s.circe._
import io.circe.generic.auto._

object ArtistIndexWithKuromoji {

  val clusterName = "artists"
  val homePath = "./home"

  val settings = Map(
    "cluster.name" -> clusterName,
    "path.home" -> homePath,
    "path.repo" -> Paths.get(homePath).resolve("repo").toString,
    "path.data" -> Paths.get(homePath).resolve("data").toString
  )

  val localNode = LocalNode(settings)

  val client = localNode.elastic4sclient()

  import com.sksamuel.elastic4s.ElasticDsl._

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
    indexInto("bands" / "artists") doc Artist("山田太郎", "東京で活躍していたアーティストです") refresh (RefreshPolicy.IMMEDIATE)
  }.await

  val resp = client.execute {
    search("bands" / "artists") query termQuery("description", "東京")
  }.await

  println("---- Search Hit Parsed ----")
  resp.to[Artist].foreach(println)

  import io.circe.Json
  import io.circe.parser._
  println("---- Response as JSON ----")
  println(decode[Json](resp.original.toString).right.get.spaces2)

  client.close()
}
