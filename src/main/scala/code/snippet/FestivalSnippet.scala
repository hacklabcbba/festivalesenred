package code
package snippet

import code.config.Site
import net.liftmodules.extras.SnippetHelper
import net.liftweb.util.Helpers._

object FestivalSnippet extends SnippetHelper {

  def edit = {
    "*" #> Site.festival.currentValue.map(festival => {
      "data-name=form" #> festival.toForm(f => ())
    })
  }

}
