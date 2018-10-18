package searcher.service.spec

import model._
import service.Reader

class ReaderMock(command: String) extends Reader {

  private var state = false

  override def readLine(text: String, args: String*): String = if (!state) {
    state = true
    command
  } else {
    Quit.code
  }

}
