import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}
import com.sksamuel.elastic4s.searches.RichSearchResponse
import com.sksamuel.elastic4s.embedded.LocalNode
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.common.settings.Settings

import com.sksamuel.elastic4s.circe._
import io.circe.generic.auto._

case class Artist(name: String)

// copies from elastic4s top page.
object ArtistIndex {

    val clusterName = "artists"
    val homePath = "./data"
    val localNode = LocalNode(clusterName, homePath)

    val client = localNode.elastic4sclient()

    import com.sksamuel.elastic4s.ElasticDsl._

  client.execute {
    createIndex("bands").mappings(
       mapping("artist") as(
          textField("name")
       )
    )
  }.await

  client.execute {
  	indexInto("bands" / "artists") doc Artist("Coldplay") refresh(RefreshPolicy.IMMEDIATE)
  }.await

  val resp = client.execute {
    search("bands" / "artists") query "coldplay"
  }.await

  println("---- Search Hit Parsed ----")
  resp.to[Artist].foreach(println)

  import io.circe.Json
  import io.circe.parser._
  println("---- Response as JSON ----")
  println(decode[Json](resp.original.toString).right.get.spaces2)

  client.close()
}
