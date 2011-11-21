package com.katlex.jsmarks
package snippet

import net.liftweb.util.Helpers._
import net.liftweb.sitemap._
import net.liftweb.http._
import net.liftweb.textile.TextileParser
import utils.BoxUtils._
import net.liftweb.common.{Empty, Full, Loggable}

/**
 * Class implementing knowledge base part of the site.
 * It takes textile articles and performs lift setting up,
 * to let the user see the pretty site's knowledge base and make it easier for content manager
 * to edit the articles.
 */
object KnowledgeBase extends Loggable {

  val menuNameLink = "Knowledge base"
  val installUri = "info"
  val templatesBase = "_info"
  val startPage = "about"

  /**
   * This creates a menu item for knowledge base with sub menus
   */
  lazy val menu = {
    import Loc._
    Menu.i(menuNameLink) / installUri / ** submenus (
      Menu.i("What is it") / installUri / "about"
    )
  }

  /**
   * Calculates base template (the template calling to KnowledgeBase snippet)
   */
  protected def baseTemplate = Templates(templatesBase :: "index" :: Nil)

  /**
   * Returns path to current article in a Box
   * Can return Failure if snippet is used on inappropriate page
   */
  protected def currentArticlePath = for {
      req <- S.request
      path = req.path.partPath.drop(1)
      _ <- (req.path.partPath.head == installUri) ?~
        ("Illegal usage of Knowlege base snippet. Should be used only on /%s/** pages" format installUri)
    } yield path

  /**
   * Returns current template from .textile article file
   */
  protected def currentTemplate = currentArticlePath.flatMap { path =>
    Templates(templatesBase :: path)
  }

  /**
   * Returns current article in HTML format
   */
  protected def currentArticle = currentTemplate.map(Markup.textile)

  protected lazy val defaultPath = installUri :: startPage :: Nil

  /**
   * Main snippet function
   */
  def render =
    "#article *" #> currentArticle &
    "#submenu *" #> <lift:Menu.builder level="1" li:class="all" li_item:class="current"/>
  
  // Setup dispatch to redirect to default article
  LiftRules.dispatch.append {
    case Req(`installUri` :: (Nil | "index" :: Nil), _, _) =>
      () => Full(RedirectResponse(defaultPath.mkString("/", "/", "")))
  }

  // Setup a view dispatch which will accept only existing textile articles
  LiftRules.viewDispatch.append {
    case `installUri` :: xs if !Templates(templatesBase :: xs).isEmpty =>
      Left(baseTemplate _)
  }

  // Let .textile suffix to be legal for template engine
  LiftRules.templateSuffixes = (LiftRules.templateSuffixes.toSet + "textile").toList
}