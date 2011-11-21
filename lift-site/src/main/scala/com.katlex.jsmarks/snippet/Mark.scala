package com.katlex.jsmarks
package snippet

import net.liftweb.util.Helpers._
import net.liftweb.json.JsonAST.JObject
import net.liftweb.sitemap.Menu
import org.bson.types.ObjectId
import net.liftweb.http.{StatefulSnippet, SHtml}
import net.liftweb.common.{Full, Empty, Box, LazyLoggable}

import lib.Compressor
import xml.{Text, NodeSeq}

class Mark extends StatefulSnippet with LazyLoggable {
  import model._
  import net.liftweb.json.JsonDSL._
  import Mark._

  def dispatch = {
    case _ => render
  }

  val I = identity[NodeSeq] _

  var paramsBox:Box[Map[JsMark.Param, String]] = Empty

  def render = "*" #> loc.currentValue.flatMap { id =>
    JsMark.find("_id", id).map { mark =>

      val compiledBox = paramsBox.map { params =>
        "javascript:" + Compressor.compress("(function(%s){%s})(%s)" format (
          params.map(_._1.name).mkString(","),
          mark.content,
          params.map(e => "\"%s\"" format e._2).mkString(",")
        ))
      }

      "#markLink" #> compiledBox.dmap("* *" #> Text(mark.name.get)) { compiledJsLine =>
        "*" #> <a href={compiledJsLine}>{mark.name}</a>
      } &
      ".markId *" #> ("[" + mark.id  + "]") &
      ".dragHint" #> compiledBox.map(_ => I) &
      ".code *" #> mark.content &
      /*".markLink *" #> "Compile" &
      ".markLink [click]" #> mark.content &*/
      ".params" #> {
        "tr" #> mark.params.get.map { param =>
          val userValue = paramsBox.flatMap(_.get(param))

          ".name *" #> param.name &
          ".value *" #> SHtml.text(userValue.getOrElse(param.description), { value =>
            paramsBox = paramsBox.map(_ + (param -> value)).or(Full(Map(param -> value)))
          })
        }
      } &
      ".compiledBlock" #> compiledBox.map { compiledJS =>
        ".compiled *" #> compiledJS
      }
    }
  }

}

object Mark {
  import net.liftweb.sitemap.Loc._

  val menu = Menu.param[ObjectId]("Mark", "Mark", s => tryo {
        new ObjectId(s)
    }, _.toString) / "mark" >> Hidden

  lazy val loc = menu.toLoc

}