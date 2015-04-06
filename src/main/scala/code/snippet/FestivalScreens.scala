package code
package snippet

import code.config.Site
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common._
import net.liftweb.http.js.JsCmds.{RedirectTo, Noop}
import net.liftweb.http.{SHtml, S}
import net.liftweb.record.Field
import net.liftweb.util.{CssSel, FieldContainer}
import net.liftweb.util.Helpers._

object FestivalScreen extends BaseScreen {

  addFields(() => new FieldContainer {def allFields = Site.festival.currentValue.map(_.fields()) openOr Nil})

  def finish() {
    Site.festival.currentValue.flatMap(s => tryo(s.save(true))) match {
      case Empty => S.warning("Empty save")
      case Failure(msg, _, _) => S.error(msg)
      case Full(_) => S.notice("Festival saved")
    }
  }

}

object FestivalForm extends SnippetHelper {
  def render: CssSel = {
    for {
      inst <- Site.festival.currentValue ?~ "Opción no válida"
    } yield ({
      val generalDataFields = Site.festival.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.name, s.responsible, s.productionManagement, s.city, s.places, s.begins, s.ends, s.duration, s.call, s.areas,
          s.website, s.responsibleEmail, s.pressResponsibleEmail, s.facebookPage, s.twitter, s.skype, s.spaces, s.equipment,
          s.numberOfAttendees, s.publicKind
        ))
      val aboutFields = Site.festival.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.staff, s.presentation, s.numberEditions, s.serviceExchange, s.trainingActivity, s.communicationTools,
          s.publicInstitutionPartnerships, s.privateInstitutionPartnerships, s.civilOrganizationPartnerships,
          s.networking, s.minimalBudget, s.budget, s.collaborativeEconomyBudget, s.managementDuration, s.tags
        ))
      "data-name=general-data" #> generalDataFields.map(_.toForm) &
      "data-name=about-data" #> aboutFields.map(_.toForm) &
      "data-name=cancel [onclick]" #>  SHtml.ajaxInvoke(() => RedirectTo("/")) &
      "data-name=submit" #> SHtml.ajaxOnSubmit(() => inst.validate match {
        case Nil =>
          inst.save(true)
          RedirectTo("/", () => S.notice("Festival guardado"))
        case errors =>
          S.error(errors)
          Noop
      })
    }): CssSel
  }
}
