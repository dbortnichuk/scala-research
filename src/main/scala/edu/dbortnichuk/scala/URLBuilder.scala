package edu.dbortnichuk.scala

import scala.util.matching.Regex.Match

object URLBuilder {

  val placeHolderRegex = """(&)?\{(\w+)\}""".r("sep","key")
  val repl1 = URLBuilder.replacer("%1s")_
  val repl2 = URLBuilder.replacer("%3$s%2$s=%1s")_
  val replacers = Seq(repl1, repl2)

  def replacer(fmt: String)(replacements: Map[String, String])(m: Match): String = {
    val s = Option(m.group("sep")).getOrElse("")
    val n = m.group("key")
    replacements.get(n).map(fmt.format(_, n, s)).getOrElse("")
  }

  def apply(template: String)(replacements: Map[String, String]): String = {
    template.split('?').zip(replacers.map(f => f(replacements))).map {
      case (urlPart, replacer) =>
      placeHolderRegex.replaceAllIn(urlPart, replacer)
    } mkString "?"
  }
}

object URLBuilder1 {
  def apply(schema: String)(path: Seq[String])(params: Map[String, String]): String = {
    s"$schema://${path.mkString("/")}?${params.map(entry => entry._1 + "=" + entry._2).mkString("&")}"
  }


}

object BuilderApp extends App {
  // some example params
  val params = Map(
    "apiversion" -> "2",
    "project" -> "PRJCT",
    "reporter" -> "Walter",
    "assignee" -> "Jesse",
    "resolution" -> "Unresolved",
    "priority" -> "Mayor"
  )
  // Template for Jira issues filter
  val urlTemplate = "https://jira.atlassian.com/rest/api/{apiversion}/issue/{project}?{reporter}&{assignee}&{resolution}&{priority}"

  // lets try the builder
  println(URLBuilder(urlTemplate)(params))
  println()
  val builder = URLBuilder(urlTemplate)(_)
  println(builder(params))
  val atlassianPath: Seq[String] => String => Seq[String] = path("jira.atlassian.com")

  def path(host: String)(path: Seq[String])(version: String): Seq[String] = path.+:(host).:+(version)

  //val atlassianRestApiPath = atlassianPath(Seq("rest", "api"))("V2")


  println(URLBuilder1("https")(Seq("jira.atlassian.com", "rest", "api", "v2"))(Map("reporter" -> "Walter", "assignee" -> "Jesse")))

//  val builder1 = URLBuilder1("https")(Seq("jira.atlassian.com", "rest", "api"))(_)
//  builder1(Map("reporter" -> "Walter", "assignee" -> "Jesse"))

  println(
    URLBuilder1
    (schema = "http")
    (path = atlassianPath(Seq("rest", "api"))("V2"))
    (params = Map("reporter" -> "Walter", "assignee" -> "Jesse")))

}