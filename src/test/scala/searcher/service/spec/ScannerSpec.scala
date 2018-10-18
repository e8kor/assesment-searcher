package searcher.service.spec

import org.scalatest._


class ScannerSpec extends FlatSpec with Matchers with GivenWhenThen {

  import service._
  import model._

  "Scanner" should "behave as expected in regular scenario" in {
    Given("mock reader instance")
    val reader = new ReaderMock("hello   world")

    And("scanner instance")
    val scanner = Scanner(reader)

    And("assertion flag")
    var flag = false

    When("scanner loop method called")
    scanner.loop { keywords =>
      println(keywords)
      flag = keywords === Seq("hello", "world")
    }

    Then("no exceptions thrown, loop lambda called")
    assert(flag, "expected keywords passed and loop called")
  }

  it should "ignore loop method when ignore code passed" in {
    Given("mock reader instance")
    val reader = new ReaderMock(Ignore.code)

    And("scanner instance")
    val scanner = Scanner(reader)

    And("assertion flag")
    var flag = true

    When("scanner loop method called")
    scanner.loop { _ =>
      flag = false
    }

    Then("no exceptions thrown, loop lambda not called")
    assert(flag, "expected keywords passed and loop called")
  }


}
