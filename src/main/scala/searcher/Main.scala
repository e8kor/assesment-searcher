package searcher

import service._

object Main extends App {

  import infrastructure.DescendingResultOrdering

  private val path = args(0)
  private val limit = 10
  private val reader = Reader()
  private val writer = Writer("%s : %d%%", "no matches found")
  private val applicationEither = Application(path, limit, reader, writer)
  private val result = applicationEither.right.flatMap(_.run())

  result match {
    case Left(error) =>
      sys.error(error)
    case Right(app) =>
      println(app)
      sys.exit(0)
  }


}