import com.sksamuel.elastic4s.analyzers.CustomizedTokenizer
import org.elasticsearch.common.xcontent.XContentBuilder
import com.sksamuel.elastic4s.analyzers.Tokenizer

case object KuromojiTokenizer extends Tokenizer("kuromoji_tokenizer")

case class KuromojiTokenizer(override val name: String) extends CustomizedTokenizer(name) {
 override def build(source: XContentBuilder): Unit = {
   source.field("type", "kuromoji_tokenizer")
 }
}
