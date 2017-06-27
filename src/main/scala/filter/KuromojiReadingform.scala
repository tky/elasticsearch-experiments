package filter

import org.elasticsearch.common.xcontent.XContentBuilder
import com.sksamuel.elastic4s.analyzers.TokenFilter

import com.sksamuel.elastic4s.analyzers.TokenFilterDefinition

case class KuromojiReadingform(name: String, userRomaji: Boolean) extends TokenFilterDefinition {
  val filterType = "kuromoji_readingform"

  override def build(source: XContentBuilder): Unit = {
    source.field("use_romaji", userRomaji)
  }
}

object KuromojiReadingform {
  def apply(name: String, userRomaji: Boolean) = new KuromojiReadingform(name, userRomaji)
}
