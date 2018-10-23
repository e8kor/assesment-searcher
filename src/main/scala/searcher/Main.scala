package searcher

import service._
import infrastructure.files._
object Main extends App {

  import infrastructure.DescendingResultOrdering
  implicit val lookup = infrastructure.resource.FileLookup

  if (args.length == 0 || args(0).trim() == "") {
    println("Search home path not specified. Please check README usage chapter")
    sys.exit(-1)
  } else {
    val path = listFiles(args(0))
    val limit = 10
    val reader = Reader()
    val writer = Writer("%s : %d%%", "no matches found")
    val applicationEither = Application(path, limit, reader, writer)
    val result = applicationEither.right.flatMap(_.run())

    result match {
      case Left(error) =>
        sys.error(error)
      case Right(app) =>
        println(app)
        sys.exit(0)
    }
  }


}