import org.scalatest.FunSuite

class KuromojiReadingformSpec extends FunSuite {
  test("works with romaji") {
    KuromojiReadingformSample(true)
  }

  test("works with kana") {
    KuromojiReadingformSample(false)
  }
}
