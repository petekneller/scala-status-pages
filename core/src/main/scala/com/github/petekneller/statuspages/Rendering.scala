package com.github.petekneller.statuspages

import argonaut.Json
import argonaut.Json._

import scala.xml.{NodeSeq, PrettyPrinter, Unparsed}
import scalaz.\/
import scalaz.\/._
import scalaz.syntax.std.boolean._

object Rendering {

  private def healthy(statii: Seq[Status]): Boolean = statii.forall(_.healthy)

  def toText(status: Seq[Status]): String \/ String = {
    def text0(st: Status): Seq[String] = st match {
      case SingleStatus(name, _, value, healthy) => Seq(s"$name = $value")
      case CompositeStatus(name, statii) => statii.flatMap(text0(_)).map(s => s"$name.$s")
    }

    val text = status.flatMap(text0).mkString("\n")
    healthy(status).fold(right(text), left(text))
  }

  def toHtml(status: Seq[Status]): String \/ String = {
    def html0(status: Status): Seq[(String, NodeSeq)] = status match {
      case SingleStatus(name, description, value, healthy) =>
        Seq(name -> <td>{description}</td><td class={healthy.fold("", "unhealthy")}>{value}</td>)
      case CompositeStatus(name, statii) =>
        statii.flatMap(html0).map{ case (existingName, row) => s"$name.$existingName" -> row }
    }

    val html =
      <html>
        <style type="text/css">
          {new Unparsed(" .unhealthy { background-color: orange; } tr>td:nth-of-type(3) { width: 20%; } tr:nth-of-type(odd) { background-color: lightgray; } ")}
        </style>
        <body>
          <table>
            {status.flatMap(html0).map{ case (name, row) => <tr><td>{name}</td>{row}</tr>}}
          </table>
        </body>
      </html>

    val text = new PrettyPrinter(200, 2).format(html)
    healthy(status).fold(right(text), left(text))
  }

  def toJson(status: Seq[Status]): String \/ String = {
    def json0(st: Status): Json = {
      st match {
        case SingleStatus(name, desc, value, healthy) => Json(
          "name" -> jString(name),
          "description" -> jString(desc),
          "value" -> jString(value),
          "healthy" -> jBool(healthy)
        )
        case CompositeStatus(name, statii) => Json(
          "name" -> jString(name),
          "subStatus" -> jArray(statii.map(json0).toList)
        )
      }
    }

    val text = fromTryCatchNonFatal(jArray(status.map(json0).toList).spaces2).fold(_.toString, identity)
    healthy(status).fold(right(text), left(text))
  }

}
