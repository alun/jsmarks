package com.katlex.jsmarks
package utils

import net.liftweb.http.SHtml
import net.liftweb.http.js.jquery.JqJE.JqId
import net.liftweb.http.js.{JsMember, JsExp}

object ScriptUtils {
  import net.liftweb.http.js.JE._
  import net.liftweb.http.js.JsCmds._

  def jqAddClass(id:String, cssClass:String) = (JqId(id) ~> JsFunc("addClass", Str(cssClass))).cmd
  def jqRemoveClass(id:String, cssClass:String) = (JqId(id) ~> JsFunc("removeClass", Str(cssClass))).cmd
  def jqToggleClass(id:String, cssClass:String) = (JqId(id) ~> JsFunc("toggleClass", Str(cssClass))).cmd

  /**
   * Returns a JsCmd which
   * will call your Scala handler when page is unloaded.
   * Must have jQuery.js loaded
   */
  def onPageUnloadCmd(handler: => Unit) = {
    val callExp = ajaxHandler(handler)
    val setupHandler = Call("jQuery", JsRaw("window")) ~> JsFunc("unload", AnonFunc(callExp.cmd))
    JsTry(setupHandler.cmd, false)
  }

  /**
   * Returns a <script/> Elem containing JavaScript which
   * will call your Scala handler when page is unloaded.
   * Must have jQuery.js loaded
   */
  def onPageUnloadScript(handler: => Unit) = {
    Script(onPageUnloadCmd(handler))
  }

  def ajaxHandler(handler: => Unit, paramCalculator: Option[JsExp] = None) = {
    val (_, jsExp) = SHtml.ajaxCall(paramCalculator.getOrElse(JsTrue), { p => handler; Noop })
    jsExp
  }
}