package service

trait Writer {

  def write(stats: Seq[Statistic]): Unit

}

object Writer {

  def apply(
    format: String,
    default: String
  ): Writer = {
    new StdWriter(format, default)
  }

}

class StdWriter(
  val format: String,
  val default: String
) extends Writer {

  override def write(args: Seq[Statistic]): Unit = {
    val output = if (args.isEmpty){
      default
    } else {
      args.map {
        case (key, value) => format.format(key, value)
      }.mkString("\n")
    }

    println(output)
  }

}
