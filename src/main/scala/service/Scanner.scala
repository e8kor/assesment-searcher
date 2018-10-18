package service

import model._

import scala.annotation.tailrec

sealed trait Scanner {

  def loop(f: Seq[String] => Unit): Either[String, String]

}

object Scanner {

  def apply(reader: Reader): Scanner = new InputScanner(reader)

}

class InputScanner(reader: Reader) extends Scanner {

  private val query = "search> "
  private val goodbye = "Bye Bye!!!"

  @tailrec
  override final def loop(f: Seq[String] => Unit): Either[String, String] = {
    val raw = reader.readLine(query).trim()
    Command(raw) match {

      case Quit =>
        Right(goodbye)

      case Ignore =>
        loop(f)

      case command: Keyword =>
        f(command.keywords)
        loop(f)

    }
  }
}
