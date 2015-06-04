package code
package snippet

import code.config.Site
import code.model.{FileRecord, User}
import code.model.festival.{EquipmentDetail, Festival}
import com.foursquare.rogue.LatLong
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common._
import net.liftweb.http.js.JsCmds.{RedirectTo, Noop}
import net.liftweb.http.{SHtml, S}
import net.liftweb.record.Field
import net.liftweb.util._
import net.liftweb.util.Helpers._

import scala.xml.Text
import net.liftweb.util.Mailer._


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
    } yield {
      val generalDataFields = Site.festivalEdit.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.logo, s.name, s.responsible, s.productionManagement, s.city, s.places, s.begins, s.ends, s.durationType, s.call, s.areas,
          s.website, s.responsibleEmail, s.pressResponsibleEmail, s.facebookPage, s.twitter, s.skype, s.spaces, s.equipment,
          s.numberOfAttendees, s.publicKind, s.photo1, s.photo2, s.photo3
        ))
      val aboutFields = Site.festivalEdit.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.staff, s.presentation, s.numberEditions, s.serviceExchange, s.trainingActivity, s.communicationTools,
          s.publicInstitutionPartnerships, s.privateInstitutionPartnerships, s.civilOrganizationPartnerships,
          s.networking, s.minimalBudget, s.budget, s.collaborativeEconomyBudget, s.managementDuration, s.tags
        ))
      "data-name=general-data" #> generalDataFields.map(field => {
        ".control-label [for]" #> field.uniqueFieldId.openOr(nextFuncName) &
        "data-name=label" #> field.displayName &
        "data-name=general-data-field" #> field.toForm &
        "data-alertid=ajaxerr [data-alertid]" #> field.uniqueFieldId.openOr(nextFuncName)
      }) &
      "data-name=about-data" #> aboutFields.map(field => {
        ".control-label [for]" #> field.uniqueFieldId.openOr(nextFuncName) &
        "data-name=label" #> field.displayName &
        "data-name=about-data-field" #> field.toForm &
        "data-alertid=ajaxerr [data-alertid]" #> field.uniqueFieldId.openOr(nextFuncName)
      }) &
      "data-name=cancel [onclick]" #>  SHtml.ajaxInvoke(() => RedirectTo("/")) &
      "data-name=submit" #> SHtml.ajaxOnSubmit(() => inst.validate match {
        case Nil =>
          val isNew = Festival.find(inst.id.get).isEmpty
          inst.save(true)
          if (isNew) {
            Mailer.sendMail(
              From(Props.get("mail.smtp.user", "")),
              Subject("Se creo un nuevo festival"),
              To(Props.get("mail.smtp.user", "")),
              PlainMailBodyType(s"Se creo un nuevo festival http://festivalesenred.telartes.org.bo${Site.festivalEdit.calcHref(inst)}")
            )
          }

          RedirectTo("/", () => S.notice("Festival guardado"))
        case errors =>
          errors.foreach(err => S.error(err.field.uniqueFieldId openOr nextFuncName, err.msg))
          Noop
      })
    }: CssSel
  }
}
