import java.io.File
import java.nio.file.Paths
import org.apache.commons.io.FileUtils
import com.sksamuel.elastic4s.searches.RichSearchResponse
import com.sksamuel.elastic4s.HitReader
import com.sksamuel.elastic4s.embedded.LocalNode
import com.sksamuel.elastic4s.{ ElasticsearchClientUri, TcpClient }

trait ExperimentsSupport {

  val clusterName = "artists"
  val homePath = "./home"

  val settings = Map(
    "cluster.name" -> clusterName,
    "path.home" -> homePath,
    "path.repo" -> Paths.get(homePath).resolve("repo").toString,
    "path.data" -> Paths.get(homePath).resolve("data").toString
  )

  def connect(f: TcpClient => Unit) = {
    val localNode = LocalNode(settings)
    val client = localNode.elastic4sclient()
    try {
      f(client)
    } finally {
      client.close
      FileUtils.deleteDirectory(new File("./home/data"))
    }
  }

  def format(resp: RichSearchResponse)(implicit hitReader: HitReader[Artist]): Unit = {

    println("---- Search Hit Parsed ----")
    resp.to[Artist].foreach(println)

    import io.circe.Json
    import io.circe.parser._
    println("---- Response as JSON ----")
    println(decode[Json](resp.original.toString).right.get.spaces2)
  }
}
