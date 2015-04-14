package code
package snippet

import code.config.Site
import code.model.User
import code.model.festival.Festival
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common._
import net.liftweb.http.js.JsCmds.{RedirectTo, Noop}
import net.liftweb.http.{SHtml, S}
import net.liftweb.record.Field
import net.liftweb.util.{CssSel, FieldContainer}
import net.liftweb.util.Helpers._

object FestivalScreen extends BaseScreen {

  addFields(() => new FieldContainer {def allFields = Site.festivalEdit.currentValue.map(_.fields()) openOr Nil})

  def finish() {
    Site.festivalEdit.currentValue.flatMap(s => tryo(s.save(true))) match {
      case Empty => S.warning("Empty save")
      case Failure(msg, _, _) => S.error(msg)
      case Full(_) => S.notice("Festival saved")
    }
  }

}

object FestivalForm extends SnippetHelper {
  def render: CssSel = {
    for {
      inst <- Site.festivalEdit.currentValue ?~ "Opción no válida"
    } yield ({
      val generalDataFields = Site.festivalEdit.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.name, s.responsible, s.productionManagement, s.city, s.places, s.begins, s.ends, s.durationType, s.call, s.areas,
          s.website, s.responsibleEmail, s.pressResponsibleEmail, s.facebookPage, s.twitter, s.skype, s.spaces, s.equipment,
          s.numberOfAttendees, s.publicKind
        ))
      val aboutFields = Site.festivalEdit.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.staff, s.presentation, s.numberEditions, s.serviceExchange, s.trainingActivity, s.communicationTools,
          s.publicInstitutionPartnerships, s.privateInstitutionPartnerships, s.civilOrganizationPartnerships,
          s.networking, s.minimalBudget, s.budget, s.collaborativeEconomyBudget, s.managementDuration, s.tags
        ))
      "data-name=general-data" #> generalDataFields.map(field => {
        "span" #> field.displayName &
        "data-name=general-data-field" #> field.toForm
      }) &
      "data-name=about-data" #> aboutFields.map(field => {
        "span" #> field.displayName &
        "data-name=about-data-field" #> field.toForm
      }) &
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

object FestivalesSnippet extends SnippetHelper {
  def render: CssSel = {
    for {
      user <- User.currentUser
    } yield ({
      "data-name=row *" #> Festival.findAllByUser(user).map(festival => {
        "data-name=name *" #> festival.name.get &
        "data-name=owner *" #> festival.owner.obj.dmap("")(_.name.get) &
        "data-name=responsible *" #> festival.responsible.get &
        "data-name=city *" #> festival.city.objs.map(_.name.get).mkString(",") &
        "data-name=edit [href]" #> Site.festivalEdit.calcHref(festival)
      }) &
      "data-name=add [href]" #> (Site.festivalEdit.calcHref(Festival.createRecord) + "new")
    }): CssSel
  }
}