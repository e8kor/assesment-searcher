package service

import scala.io.StdIn

trait Reader {

  def readLine(text: String, args: String*): String

}

object Reader {

  def apply(): Reader = StdReader

}

object StdReader extends Reader {

  override def readLine(text: String, args: String*): String = {
    StdIn.readLine(text, args: _*)
  }

}
