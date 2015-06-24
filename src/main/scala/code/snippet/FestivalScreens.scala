package code
package snippet

import code.config.Site
import code.model.{FileRecord, User}
import code.model.festival.{EquipmentDetail, Festival}
import com.foursquare.rogue.LatLong
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common._
import net.liftweb.http.js.JsCmds.{Run, SetHtml, RedirectTo, Noop}
import net.liftweb.http.{SHtml, S}
import net.liftweb.record.Field
import net.liftweb.util._
import net.liftweb.util.Helpers._

import scala.xml.{NodeSeq, Text}
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
      "data-name=name" #> inst.name.toForm &
      "data-name=name-error [data-alertid]" #> inst.name.uniqueFieldId.openOr(nextFuncName) &
      "data-name=description" #> inst.presentation.toForm &
      "data-name=description-error [data-alertid]" #> inst.presentation.uniqueFieldId.openOr(nextFuncName) &
      "#presentation-chars-left *" #> inst.presentation.charsLeft &
      "data-name=responsible" #> inst.responsible.toForm &
      "data-name=responsible-error [data-alertid]" #> inst.responsible.uniqueFieldId.openOr(nextFuncName) &
      "data-name=responsible-email" #> inst.responsibleEmail.toForm &
      "data-name=responsible-email-error [data-alertid]" #> inst.responsibleEmail.uniqueFieldId.openOr(nextFuncName) &
      "data-name=responsible-press-email" #> inst.pressResponsibleEmail.toForm &
      "data-name=responsible-press-email-error [data-alertid]" #> inst.pressResponsibleEmail.uniqueFieldId.openOr(nextFuncName) &
      "data-name=production-management" #> inst.productionManagement.toForm &
      "data-name=production-management-error [data-alertid]" #> inst.productionManagement.uniqueFieldId.openOr(nextFuncName) &
      "data-name=cities" #> inst.city.toForm &
      "data-name=cities-error [data-alertid]" #> inst.city.uniqueFieldId.openOr(nextFuncName) &
      "data-name=begins" #> inst.begins.toForm &
      "data-name=begins-error [data-alertid]" #> inst.begins.uniqueFieldId.openOr(nextFuncName) &
      "data-name=ends" #> inst.ends.toForm &
      "data-name=ends-error [data-alertid]" #> inst.ends.uniqueFieldId.openOr(nextFuncName) &
      "data-name=duration" #> inst.duration.toForm &
      "data-name=duration-error [data-alertid]" #> inst.duration.uniqueFieldId.openOr(nextFuncName) &
      "data-name=duration-type" #> inst.durationType.toForm &
      "data-name=duration-type-error [data-alertid]" #> inst.durationType.uniqueFieldId.openOr(nextFuncName) &
      "data-name=areas" #> inst.areas.toForm &
      "data-name=areas-error [data-alertid]" #> inst.areas.uniqueFieldId.openOr(nextFuncName) &
      "data-name=website" #> inst.website.toForm &
      "data-name=website-error [data-alertid]" #> inst.website.uniqueFieldId.openOr(nextFuncName) &
      "data-name=facebook" #> inst.facebookPage.toForm &
      "data-name=facebook-error [data-alertid]" #> inst.facebookPage.uniqueFieldId.openOr(nextFuncName) &
      "data-name=twitter" #> inst.twitter.toForm &
      "data-name=twitter-error [data-alertid]" #> inst.twitter.uniqueFieldId.openOr(nextFuncName) &
      "data-name=skype" #> inst.skype.toForm &
      "data-name=skype-error [data-alertid]" #> inst.skype.uniqueFieldId.openOr(nextFuncName) &
      "data-name=spaces" #> inst.spaces.toForm &
      "data-name=spaces-error [data-alertid]" #> inst.spaces.uniqueFieldId.openOr(nextFuncName) &
      "data-name=equipment" #> inst.equipment.toForm &
      "data-name=equipment-error [data-alertid]" #> inst.equipment.uniqueFieldId.openOr(nextFuncName) &
      "data-name=number-of-attendees" #> inst.numberOfAttendees.toForm &
      "data-name=number-of-attendees-error [data-alertid]" #> inst.numberOfAttendees.uniqueFieldId.openOr(nextFuncName) &
      "data-name=public-kind" #> inst.publicKind.toForm &
      "data-name=public-kind-error [data-alertid]" #> inst.publicKind.uniqueFieldId.openOr(nextFuncName) &
      {
        if (inst.status.shouldDisplay_?) {
          "data-name=status" #> inst.status.toForm &
          "data-name=status-error [data-alertid]" #> inst.status.uniqueFieldId.openOr(nextFuncName) 
        } else {
          "data-name=status-div" #> NodeSeq.Empty
        }
      } &
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
