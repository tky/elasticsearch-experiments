package filter

import com.sksamuel.elastic4s.analyzers.TokenFilter

case object KuromojiBaseformFilter extends TokenFilter {
  val name = "kuromoji_baseform"
}
