package com.katlex.jsmarks
package  snippet

import net.liftweb.textile.TextileParser
import xml.NodeSeq
import net.liftweb.util.Helpers._

/**
 * Support for different markups
 */
object Markup {
  def textile = "*" #> { ns: NodeSeq => TextileParser.toHtml(ns.text) }
}