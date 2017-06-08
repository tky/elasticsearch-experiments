import java.io.File
import org.apache.commons.io.FileUtils
import org.scalatest.FunSuite

class ArticleIndexTest extends FunSuite {
  test ("works") {
    ArtistIndex
    FileUtils.deleteDirectory(new File("./data"))
  }
}
