package code
package snippet

import code.config.Site
import net.liftweb.util.Helpers._

object FestivalSnippet {

  def edit = {
    "*" #> Site.festival.currentValue.map(festival => {
      "data-name=form" #> festival.toForm(f => ())
    })
  }

}
