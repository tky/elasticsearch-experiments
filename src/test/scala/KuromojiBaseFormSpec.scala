import java.io.File
import org.apache.commons.io.FileUtils
import org.scalatest.FunSuite

class KuromojiBaseformSpec extends FunSuite {
  test ("works") {
    KuromojiBaseform
    FileUtils.deleteDirectory(new File("./home/data"))
  }
}
