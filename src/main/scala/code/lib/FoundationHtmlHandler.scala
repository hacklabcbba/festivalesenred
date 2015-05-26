package code.lib

import net.liftmodules.extras.HtmlHandler

import scala.xml._


trait FoundationHtmlHandler extends HtmlHandler {
  def noticeHtml(msg: NodeSeq): NodeSeq =
    <div data-alert="" class="alert-box success radius">
      {msg}
      <a href="#" class="close">&times;</a>
    </div>

  def warningHtml(msg: NodeSeq): NodeSeq =
    <div data-alert="" class="alert-box warning round">
      {msg}
      <a href="#" class="close">&times;</a>
    </div>

  def errorHtml(msg: NodeSeq): NodeSeq =
    <div data-alert="" class="alert-box alert round">
      {msg}
      <a href="#" class="close">&times;</a>
    </div>

}

object FoundationHtmlHandler extends FoundationHtmlHandler
