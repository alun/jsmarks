package com.katlex.jsmarks
package snippet

import net.liftweb.util.Helpers._
import net.liftweb.json.JsonAST.JObject

class Marks {
  import model._
  import net.liftweb.json.JsonDSL._

  def render = "tr" #> JsMark.findAll(JObject(Nil), "name" -> 1).map { mark =>
    ".title" #> {
      "a *" #> mark.name &
      "a [href]" #> ("/mark/" + mark.id)
    }
  }

}