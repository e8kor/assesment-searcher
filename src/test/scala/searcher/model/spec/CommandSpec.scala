package searcher.model.spec

import org.scalatest._

class CommandSpec extends FlatSpec with Matchers with GivenWhenThen {

  import model._

  "Command" should "return ignore when empty string passed" in {
    Given("input string")
    val source = Ignore.code
    When("create command instance")
    val command = Command(source)
    Then("expected command returned")
    assert(command == model.Ignore, "should be ignore command")
  }

  it should "return quit when :quit string passed" in {
    Given("input string")
    val source = Quit.code
    When("create command instance")
    val command = Command(source)
    Then("expected command returned")
    assert(command == Quit, "should be quit command")
  }

  it should "return quit when one word string passed" in {
    Given("input string")
    val source = "hello"
    When("create command instance")
    val command = Command(source)
    Then("expected command returned")
    assert(command == Keyword(Seq("hello")), "should be keyword command")
  }

  it should "return quit when multiple words string passed" in {
    Given("input string")
    val source = "hello world"
    When("create command instance")
    val command = Command(source)
    Then("expected command returned")
    assert(command == Keyword(Seq("hello", "world")), "should be keyword command")
  }

}
