package service

import infrastructure.resource.ResourceLookup

trait Application {

  def run(): Either[String, String]

}

object Application {

  def apply[A](
    path: Seq[A],
    limit: Int,
    reader: Reader,
    writer: Writer
  )(
    implicit lookup: ResourceLookup[A],
    ordering: Ordering[Statistic]
  ): Either[String, Application] = {
    val reader: Reader = Reader()
    val scanner: Scanner = Scanner(reader)
    apply(path, limit, scanner, writer)
  }

  def apply[A](
    path: Seq[A],
    limit: Int,
    scanner: Scanner,
    writer: Writer
  )(
    implicit lookup: ResourceLookup[A],
    ordering: Ordering[Statistic]
  ): Either[String, Application] = {
    Cache(limit, path)
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
