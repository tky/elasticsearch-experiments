import java.io.File
import org.apache.commons.io.FileUtils
import org.scalatest.FunSuite

class ArticleIndexSpec extends FunSuite {
  test("works") {
    ArtistIndex
    FileUtils.deleteDirectory(new File("./home/data"))
    ArtistIndexWithKuromoji
    FileUtils.deleteDirectory(new File("./home/data"))
  }
}