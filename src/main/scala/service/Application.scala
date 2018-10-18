package service

import scala.io.Codec

trait Application {

  def run(): Either[String, String]

}

object Application {

  def apply(
    path: String,
    limit: Int,
    reader: Reader,
    writer: Writer
  )(implicit codec: Codec): Either[String, Application] = {
    val reader: Reader = Reader()
    val scanner: Scanner = Scanner(reader)
    apply(path, limit, scanner, writer)
  }

  def apply(
    path: String,
    limit: Int,
    scanner: Scanner,
    writer: Writer
  )(implicit codec: Codec): Either[String, Application] = {
    Cache(limit, path, false)
      .right
      .map(apply(_, scanner, writer))
  }

  def apply(
    cache: Cache,
    scanner: Scanner,
    writer: Writer
  ): Application = {
    new SearcherApplication(cache, scanner, writer)
  }

}

class SearcherApplication(
  val cache: Cache,
  val scanner: Scanner,
  val writer: Writer
) extends Application {

  override def run(): Either[String, String] = {
    scanner.loop { keywords =>
      val pretty = cache.calculate(keywords)
      writer.write(pretty)
    }
  }

}
