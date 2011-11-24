package com.katlex.jsmarks
package snippet

import net.liftweb.util.Helpers._
import net.liftweb.json.JsonAST.JObject
import net.liftweb.sitemap.Menu
import org.bson.types.ObjectId
import net.liftweb.common.{Full, Empty, Box, LazyLoggable}

import lib.Compressor
import xml.{Text, NodeSeq}
import net.liftweb.textile.TextileParser
import net.liftweb.http.js.JE
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds
import net.liftweb.http.{Templates, StatefulSnippet, SHtml}
import net.liftweb.http.js.jquery.JqJE

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

      import SHtml._
      import JsCmds._
      import JqJsCmds._

      def compiledCodeBox = paramsBox.map { params =>
        "javascript:" + Compressor.compress("(function(%s){%s})(%s)" format (
          params.map(_._1.name).mkString(","),
          mark.content,
          params.map(e => "\"%s\"" format e._2).mkString(",")
        ))
      }
      
      def doCompile = {
        import utils.js.Jq._

        Show("compiledBlock") &
        FlushFade("compiledBlock") &
        compiledCodeBox.dmap(Noop) { code =>
          JqSetText("compiledCode", code) &
          JqSetText("markLink", mark.name.is) &
          JqSetAttrs("markLink", "href" -> code)
        }
      }
      def withCompile(ns:NodeSeq) = {
        ns :+ hidden (doCompile _)
      }

      def form(transform:NodeSeq => NodeSeq)(ns:NodeSeq) = ajaxForm(transform(ns))

      "#markName" #> mark.name &
      ".desc *" #> TextileParser.toHtml(mark.description.is) &
      /*".dragHint" #> compiledBox.map(_ => I) &*/
      ".code *" #> mark.content &
      /*".markLink *" #> "Compile" &
      ".markLink [click]" #> mark.content &*/
      "#paramsForm" #> form {
          ".params" #> {
              "tr" #> mark.params.get.map { param =>
                ".name *" #> param.name &
                ".value *" #> text(param.description, { value =>
                  paramsBox = paramsBox.map(_ + (param -> value)).or(Full(Map(param -> value)))
                })
              }
            } &
          "#send" #> withCompile _
        } _
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