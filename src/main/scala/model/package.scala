package object model {


  import infrastructure._

  sealed trait Command {

    def code: String

  }

  object Command {


    def apply(code: String): Command = {
      if (code == Quit.code) {
        Quit
      } else if (code == Ignore.code) {
        Ignore
      } else {
        val keywords = code.split(delimiter)
          .toSeq
          .map(_.replaceAll(punctuation, ""))
          .filterNot(_.isEmpty)
        Keyword(keywords)
      }
    }

  }

  case object Ignore extends Command {
    override val code: String = ""
  }

  case class Keyword(keywords: Seq[String]) extends Command {
    override val code: String = "keyword"
  }

  case object Quit extends Command {
    override val code: String = ":quit"
  }

}
