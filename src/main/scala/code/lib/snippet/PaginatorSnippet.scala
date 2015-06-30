package code
package lib
package snippet

import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.{PaginatorSnippet => LiftPaginatorSnippet, S}
import net.liftweb.util.Helpers._
import net.liftweb.util.{CssSel, PassThru}

import scala.language.postfixOps
import scala.xml.{NodeSeq, Text}

trait PaginatorSnippet[T] extends LiftPaginatorSnippet[T]{

  lazy val baseURL = S.uri

  override def curPage: Int = {
    val p = S.param(offsetParam) map(asInt(_) openOr 1)  openOr 1
    ((p <= 0 || p > numPages) && p != 1) match {
      case true =>
        S.redirectTo(appendParams(baseURL, List(offsetParam -> "1")))
        1
      case false =>
        p
    }
  }

  def squerylOffset: Int = (curPage - 1) * itemsPerPage

  override def firstXml: NodeSeq = Text(S ? "1")

  override def nextXml: NodeSeq = Text("»")

  override def prevXml: NodeSeq = Text("«")

  override def lastXml: NodeSeq = Text(S ? numPages.toString)

  override def itemsPerPage: Int = 10

  override def offsetParam: String = "page"

  override def currentXml: NodeSeq = Text(curPage.toString)

  override def zoomedPages: List[Int] = {
    ((curPage - 1) to (curPage + 1)).toList filter(n => n > 0)
  }

  def prevPages: List[Int] = curPage == numPages match {
    case true =>
      curPage - 3 to curPage - 1 filter (_ > 1) toList
    case _ =>
      curPage - 2 to curPage - 1 filter (_ > 1) toList
  }

  def nextPages: List[Int] = curPage match {
    case 1 =>
      curPage + 1 to curPage + 3 filter (_ > 1) filter (_ < numPages) toList
    case _ =>
      curPage + 1 to curPage + 2 filter (_ > 1) filter (_ < numPages) toList
  }

  def pagesXml(pages: Seq[Int]): NodeSeq ={
      pages map {n =>
        pageXml(n, Text(n toString))
      } match {
        case one :: Nil => one
        case first :: rest => rest.foldLeft(first) {
          case (a,b) => a ++ b
        }
        case Nil => Nil
      }
  }

  override def first: Long = 1

  override def pageXml(newFirst: Long, ns: NodeSeq): NodeSeq = {
    newFirst == curPage match {
      case false =>
        <a href={pageUrl(newFirst)}>{ns}</a>
      case true =>
        <a href="#">{ns}</a>
    }
  }

  def prevLink: NodeSeq = {
    curPage match {
      case 1 => <li class="active"><a href="#">{prevXml}</a></li>
      case _ => <li>{pageXml(curPage-1 max 1, prevXml)}</li>
    }
  }

  def firstLink: NodeSeq = {
    curPage match {
      case 1 => NodeSeq.Empty
      case _ => <li>{pageXml(1, firstXml)}</li>
    }
  }

  def nextLink: NodeSeq = {
    (curPage >= numPages)  match {
      case true => <li class="active"><a href="#">{nextXml}</a></li>
      case false => <li>{pageXml(curPage + 1 min (numPages), nextXml)}</li>
    }
  }

  def lastLink: NodeSeq = {
    (curPage >= numPages)  match {
      case true => NodeSeq.Empty
      case false => <li>{pageXml(numPages, lastXml)}</li>
    }
  }

  def listURLDelete(totalDeleted: Long, totalPage: Long, page: Long): Box[String] = {
    (totalDeleted == totalPage) match {
      case true =>
        page match {
          case 0 =>
            Full(appendParams(baseURL, Nil))
          case _ =>
            Full(appendParams(baseURL, List(offsetParam -> page.toString)))
        }
      case false =>
        Empty
    }
  }

  def cleanLeft: CssSel = {
    curPage <= 4 match {
      case true => ".zoompagesleft" #> NodeSeq.Empty
      case false => ".zoompagesleft" #> PassThru
    }
  }

  def cleanRight: CssSel = {
    (curPage >= numPages-3)  match {
      case true => ".zoompagesright" #> NodeSeq.Empty
      case false => ".zoompagesright" #> PassThru
    }
  }

  //override def pageUrl(page: Long): String = baseURL + "" page

  def paginate: CssSel = {
    "data-name=pagination *" #> {
      "data-name=first" #> firstLink &
      "data-name=prev" #> prevLink &
      cleanLeft &
      "data-name=allpages" #> {(n:NodeSeq) => pagesXml(0 until numPages, n)} &
      "data-name=prevpages" #> pagesXml(prevPages).map(e => {
        "data-name=page" #> e
      }) &
      ".active *" #> pageXml(curPage, currentXml) &
      ".nextpages" #> pagesXml(nextPages).map(e => {
        "data-name=page" #> e
      }) &
      cleanRight &
      "data-name=next" #> nextLink &
      "data-name=last" #> lastLink &
      "data-name=records" #> currentXml
    }
  }

}