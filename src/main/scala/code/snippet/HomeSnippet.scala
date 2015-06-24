package code
package snippet

import code.model.festival.{City, Area}
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common.Full
import net.liftweb.util.PassThru
import omniauth.{AuthInfo, Omniauth}
import net.liftweb.util.Helpers._

object HomeSnippet extends SnippetHelper {

  def render = {
    "*" #> PassThru
  }

  def searchForm = {
    "data-name=area" #> Area.findAll.map(area => {
      "data-name=code *" #> area.code.get &
      "input [id]" #> area.name.get &
      "label [for]" #> area.name.get &
      "label *" #> area.name.get &
      "data-name=description *" #> area.description.get
    }) &
    "data-name=city" #> City.findAll.map(city => {
      "label *" #> city.name.get &
      "input [id]" #> city.name.get &
      "label [for]" #> city.name.get
    })
  }

}
