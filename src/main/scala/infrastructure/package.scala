package object infrastructure {

  val delimiter = "\\W+"
  val punctuation = """[\p{Punct}&&[^.]]"""

  implicit object DescendingResultOrdering extends Ordering[(String, Int)] {
    override def compare(x: (String, Int), y: (String, Int)): Int = {
      y._2.compare(x._2)
    }
  }

}
