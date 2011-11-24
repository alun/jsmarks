package com.katlex.jsmarks
package utils
package js

import net.liftweb.http.js.jquery.JqJE._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmd

object Jq {
  private [js] trait ProxyCmd extends JsCmd {
    val realCmd: JsCmd
    def toJsCmd = realCmd.toJsCmd
  }

  case class JqSetText(id:String, text:String) extends ProxyCmd {
    val realCmd = JqId(id) ~> JsFunc("text", text) cmd
  }

  case class JqSetAttrs(id:String, attrs:(String, String)*) extends ProxyCmd {
    val realCmd = attrs.map {
      case (key, value) => JqId(id) ~> JsFunc("attr", key, value) cmd
    } .reduceLeft(_ & _)
  }

  case class FlushFade(id: String) extends ProxyCmd {
    val callback = AnonFunc(JqId(id) ~> JsFunc("fadeTo", 200, 1) cmd)
    val realCmd = JqId(id) ~> JsFunc("fadeTo", 0, 0, callback) cmd
  }

}