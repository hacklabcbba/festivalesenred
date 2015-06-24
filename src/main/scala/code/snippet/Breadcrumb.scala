package code
package snippet

import scala.xml.Text
import net.liftweb.http.S
import net.liftweb.sitemap.Loc
import net.liftweb.util.Helpers.strToCssBindPromoter
import scala.xml.NodeSeq

class Breadcrumb {

  /*   The goal is to create a twitter bootstrap conforming breadcrumb snippet, or at least something similar:
  *
  *   http://twitter.github.io/bootstrap/components.html#breadcrumbs
  *
  *   Example:
  *
  *   <ul class="breadcrumb">
  *     <li><a href="#">Home</a> <span class="divider">/</span></li>
  *     <li><a href="#">Library</a> <span class="divider">/</span></li>
  *     <li class="active">Data</li>
  *   </ul>
  *
  *   Andreas Joseph Krogh: Google Groups: 29. May 2013
  *   Title: Breadcrumb with dividers
  *
  *   The intersperse-method below was written by Naftoli with the help of Gabriel Cardoso for making it tail-recursive
  *
  *   Bootstrap example:
  *
  *   USAGE:
  *
  *   <ul class="breadcrumb">
  *      <div class="lift:breadcrumb_snippet.bootstrap_breadcrumb"></div>
  *    </ul>
  *
  *  RESULT:
  *
  *    <ul class="breadcrumb">
  *      <li>
  *        <a href="/agent/basedata/">Base Data</a>
  *        <span class="divider">/</span>
  *      </li>
  *      <li class="active">Hotel</li>
  *    </ul>
  *
  *  The result in the browser looks like this:
  *
  *    Base Data / Hotel
  *
  *  where "Base Data" is a link and "Hotel" is not a link.
  *
  *  Have also created a generic_breadcrumb function which could be used in an environment where bootstrap is not used.
  *
  *  Generic example:
  *
  *  USAGE:
  *
  *    <div class="lift:breadcrumb_snippet.generic_breadcrumb"></div>
  *
  *  RESULT:
  *
  *    <a href="/agent/basedata/">Base Data</a> / Hotel
  *
  */

  @scala.annotation.tailrec
  private def intersperse[T](list: List[T], co: T, acc: List[T] = Nil): List[T] = list match {
    case Nil => Nil
    case one :: Nil => (one :: acc).reverse
    case one :: two :: rest => intersperse(two :: rest, co, co :: one :: acc)
  }

  def bootstrapBreadcrumb = "*" #> S.location.map(loc =>
    intersperse(
      loc.breadCrumbs.map { loc =>

        val href = loc.createDefaultLink.getOrElse(NodeSeq.Empty)
        val text = loc.linkText.openOr(NodeSeq.Empty)

        if (loc == S.location.openOr(NodeSeq.Empty))
          <li class="active">{ text }</li>
        else
          <li><a href={ href }>{ text }</a><span class="divider">/</span></li>
      },
      Text(""))).openOr(NodeSeq.Empty)

  def genericBreadcrumb = "*" #> S.location.map(loc =>
    intersperse(
      loc.breadCrumbs.map { loc =>

        val href = loc.createDefaultLink.getOrElse(NodeSeq.Empty)
        val text = loc.linkText.openOr(NodeSeq.Empty)

        if (loc == S.location.openOr(NodeSeq.Empty))
        { text }
        else

          <a href={ href }>{ text }</a>
      },
      Text(" / "))).openOr(NodeSeq.Empty)
}