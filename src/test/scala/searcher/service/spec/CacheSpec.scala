package searcher.service.spec

import org.scalatest._
import service.Cache

class CacheSpec extends FlatSpec with Matchers with GivenWhenThen {

  import infrastructure.DescendingResultOrdering
  private implicit val lookup = JarLookup

  private val limit = 10

  it should "provide 100 percentage match for keywords to search if all present in file " in {
    Given("text file")
    val name = "/regular/file.txt"
    val is = getClass.getResourceAsStream(name)
    And("instance of cache")
    val cache = Cache(limit, Seq(name)).right.get
    When("calling calculate")
    val percentage = cache.calculate(Seq("Lorem", "ipsum"))
    Then("result percentage should be returned")

    assert(percentage === List((name, 100)), "expected to have 100 percent match")
  }

  it should "return 50 percentage match for keywords when only 1 of two exists in file " in {
    Given("text file")
    val name = "/regular/file.txt"
    val is = getClass.getResourceAsStream(name)
    And("instance of cache")
    val cache = Cache(limit, Seq(name)).right.get
    When("calling calculate")
    val percentage = cache.calculate(Seq("Lorem", "ENGLISH"))
    Then("result percentage should be returned")
    assert(percentage === List((name, 50)), "expected to have 50 percent match")
  }

  it should "return 0 percentage match for keywords when only none exists in file content" in {
    Given("text file")
    val name = "/regular/file.txt"
    And("instance of cache")
    val cache = Cache(limit, Seq(name)).right.get
    When("calling calculate")
    val percentage = cache.calculate(Seq("POLISH", "ENGLISH"))
    Then("result percentage should be returned")
    assert(percentage === Nil, "expected to have 0 percent match")
  }

  it should "return 0 percentage match when no keywords passed" in {
    Given("text file")
    val name = "/regular/file.txt"
    And("instance of cache")
    val cache = Cache(limit, Seq(name)).right.get
    When("calling calculate")
    val percentage = cache.calculate(Seq.empty)
    Then("result percentage should be returned")
    assert(percentage === Nil, "expected to have 0 percent match")
  }

  it should "be able to process multiple files" in {
    Given("text file")
    val name1 = "/regular/file.txt"
    val name2 = "/regular/another.txt"
    And("instance of cache")
    val cache = Cache(limit, Seq(name1, name2)).right.get
    When("calling calculate")
    val percentage = cache.calculate(Seq("Lorem", "Aliquam"))
    Then("result percentage should be returned")
    assert(
      percentage === List(name1 -> 100, name2 -> 50),
      "expected to have 100 percent match in first file and 0 in second file"
    )
  }

  it should "be able to process multiple files in nested structure" in {
    Given("text files")
    val names = Seq(
      "/extended/left/inner.txt",
      "/extended/nested/nested/inner.txt",
      "/extended/right/inner.txt",
      "/extended/another.txt",
      "/extended/root.txt"
    )
    And("instance of cache")
    val cache = Cache(4, names).right.get
    When("calling calculate")
    val percentage = cache.calculate(Seq("Lorem", "Aliquam", "Fusce", "Phasellus", "eu"))
    Then("result percentage should be returned")
    println(percentage.mkString("\n"))
    assert(
      percentage === List(
        "/extended/root.txt" -> 100,
        "/extended/nested/nested/inner.txt" -> 60,
        "/extended/another.txt" -> 60,
        "/extended/left/inner.txt" -> 40
      ),
      "expected to files in proper order with"
    )
  }


}