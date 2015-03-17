package code
package snippet

import code.config.Site
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common._
import net.liftweb.http.S
import net.liftweb.util.FieldContainer
import net.liftweb.util.Helpers._

object FestivalSnippet extends BaseScreen {

  addFields(() => new FieldContainer {def allFields = Site.festival.currentValue.map(_.fields()) openOr Nil})

  def finish() {
    Site.festival.currentValue.flatMap(s => tryo(s.save(true))) match {
      case Empty => S.warning("Empty save")
      case Failure(msg, _, _) => S.error(msg)
      case Full(_) => S.notice("Festival saved")
    }
  }

}
