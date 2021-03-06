package code
package snippet

import code.config.Site
import code.model.{FileRecord, User}
import code.model.festival.{EquipmentDetail, Festival}
import com.foursquare.rogue.LatLong
import net.liftmodules.FoBoBs.lib.BootstrapSH
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
      "data-name=logo-preview [src]" #> inst.logo.previewUrl &
      "data-name=logo-url [href]" #> inst.logo.fileUrl &
      "data-name=logo" #> inst.logo.toForm &
      "data-name=remove-logo [data-file-id]" #> inst.logo.get.fileId.get &
      "data-name=logo-container-field-id [class+]" #> inst.logo.containerFieldId &
      "data-name=logo-container-input-id [class+]" #> inst.logo.containerInputId &
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
      "data-name=service-exchange" #> inst.serviceExchange.toForm &
      "data-name=service-exchange-error [data-alertid]" #> inst.serviceExchange.uniqueFieldId.openOr(nextFuncName) &
      "data-name=training" #> inst.trainingActivity.toForm &
      "data-name=training-error [data-alertid]" #> inst.trainingActivity.uniqueFieldId.openOr(nextFuncName) &
      "data-name=tools" #> inst.communicationTools.toForm &
      "data-name=tools-error [data-alertid]" #> inst.communicationTools.uniqueFieldId.openOr(nextFuncName) &
      "data-name=public-partnership" #> inst.publicInstitutionPartnerships.toForm &
      "data-name=public-partnership-error [data-alertid]" #> inst.publicInstitutionPartnerships.uniqueFieldId.openOr(nextFuncName) &
      "data-name=private-partnership" #> inst.privateInstitutionPartnerships.toForm &
      "data-name=private-partnership-error [data-alertid]" #> inst.privateInstitutionPartnerships.uniqueFieldId.openOr(nextFuncName) &
      "data-name=civil-partnership" #> inst.civilOrganizationPartnerships.toForm &
      "data-name=civil-partnership-error [data-alertid]" #> inst.civilOrganizationPartnerships.uniqueFieldId.openOr(nextFuncName) &
      "data-name=networking" #> inst.networking.toForm &
      "data-name=networking-error [data-alertid]" #> inst.networking.uniqueFieldId.openOr(nextFuncName) &
      "data-name=photo1-preview [src]" #> inst.photo1.previewUrl &
      "data-name=photo1-url [href]" #> inst.photo1.fileUrl &
      "data-name=photo1" #> inst.photo1.toForm &
      "data-name=remove-photo1 [data-file-id]" #> inst.photo1.get.fileId.get &
      "data-name=photo1-container-field-id [class+]" #> inst.photo1.containerFieldId &
      "data-name=photo1-container-input-id [class+]" #> inst.photo1.containerInputId &
      "data-name=photo2-preview [src]" #> inst.photo2.previewUrl &
      "data-name=photo2-url [href]" #> inst.photo2.fileUrl &
      "data-name=photo2" #> inst.photo2.toForm &
      "data-name=remove-photo2 [data-file-id]" #> inst.photo2.get.fileId.get &
      "data-name=photo2-container-field-id [class+]" #> inst.photo2.containerFieldId &
      "data-name=photo2-container-input-id [class+]" #> inst.photo2.containerInputId &
      "data-name=photo3-preview [src]" #> inst.photo3.previewUrl &
      "data-name=photo3-url [href]" #> inst.photo3.fileUrl &
      "data-name=photo3" #> inst.photo3.toForm &
      "data-name=remove-photo3 [data-file-id]" #> inst.photo3.get.fileId.get &
      "data-name=photo3-container-field-id [class+]" #> inst.photo3.containerFieldId &
      "data-name=photo3-container-input-id [class+]" #> inst.photo3.containerInputId &
      "data-name=photo4-preview [src]" #> inst.photo4.previewUrl &
      "data-name=photo4-url [href]" #> inst.photo4.fileUrl &
      "data-name=photo4" #> inst.photo4.toForm &
      "data-name=remove-photo4 [data-file-id]" #> inst.photo4.get.fileId.get &
      "data-name=photo4-container-field-id [class+]" #> inst.photo4.containerFieldId &
      "data-name=photo4-container-input-id [class+]" #> inst.photo4.containerInputId &
      "data-name=call-preview [src]" #> inst.call.previewUrl &
      "data-name=call-url [href]" #> inst.call.fileUrl &
      "data-name=call" #> inst.call.toForm &
      "data-name=remove-call [data-file-id]" #> inst.call.get.fileId.get &
      "data-name=call-container-field-id [class+]" #> inst.call.containerFieldId &
      "data-name=call-container-input-id [class+]" #> inst.call.containerInputId &
      "data-name=call-date" #> inst.callDate.toForm &
      "data-name=call-date-error [data-alertid]" #> inst.callDate.uniqueFieldId.openOr(nextFuncName) &
      "data-name=places" #> SHtml.idMemoize(body => {
        "data-name=place" #> inst.places.get.zipWithIndex.map{ case (place, index) => {
          "data-name=number *" #> (index + 1) &
          "data-name=name *" #> place.name.get &
          "data-name=edit [onclick]" #> SHtml.ajaxInvoke(() => inst.places.dialogHtml(body, inst, place, false)) &
          "data-name=delete [onclick]" #> SHtml.ajaxInvoke(() => inst.places.deletePlace(body, inst, place))
        }} &
        "data-name=add [onclick]" #> SHtml.ajaxInvoke(() => inst.places.dialogHtml(body, inst))
      }) &
      "data-name=persons" #> SHtml.idMemoize(body => {
        "data-name=person" #> inst.staff.get.zipWithIndex.map{ case (person, index) => {
          "data-name=number *" #> (index + 1) &
          "data-name=name *" #> person.name.get &
          "data-name=email *" #> person.email.get &
          "data-name=role *" #> person.role.get &
          "data-name=cellphone *" #> person.cellphone.get &
          "data-name=edit [onclick]" #> SHtml.ajaxInvoke(() => inst.staff.dialogHtml(body, inst, person, false)) &
          "data-name=delete [onclick]" #> SHtml.ajaxInvoke(() => inst.staff.deleteTeamMember(body, inst, person))
        }} &
        "data-name=add [onclick]" #> SHtml.ajaxInvoke(() => inst.staff.dialogHtml(body, inst))
      }) &
      "data-name=editions" #> SHtml.idMemoize(body => {
        "data-name=edition" #> inst.numberEditions.get.zipWithIndex.map{ case (edition, index) => {
          "data-name=number *" #> (index + 1) &
          "data-name=name *" #> edition.name.get &
          "data-name=date *" #> edition.date.literalDate &
          "data-name=edit [onclick]" #> SHtml.ajaxInvoke(() => inst.numberEditions.dialogHtml(body, inst, edition, false)) &
          "data-name=delete [onclick]" #> SHtml.ajaxInvoke(() => inst.numberEditions.deleteEdition(body, inst, edition))
        }} &
        "data-name=add [onclick]" #> SHtml.ajaxInvoke(() => inst.numberEditions.dialogHtml(body, inst))
      }) &
      "data-name=minimal-budget" #> inst.minimalBudget.get.amount.toForm &
      "data-name=minimal-budget-error [data-alertid]" #> inst.minimalBudget.get.amount.uniqueFieldId.openOr(nextFuncName) &
      "data-name=budget" #> inst.budget.get.amount.toForm &
      "data-name=budget-error [data-alertid]" #> inst.budget.get.amount.uniqueFieldId.openOr(nextFuncName) &
      "data-name=collaborative-budget" #> inst.collaborativeEconomyBudget.get.amount.toForm &
      "data-name=collaborative-budget-error [data-alertid]" #> inst.collaborativeEconomyBudget.get.amount.uniqueFieldId.openOr(nextFuncName) &
      "data-name=minimal-budget-currency" #> inst.minimalBudget.get.currency.toForm &
      "data-name=minimal-budget-currency-error [data-alertid]" #> inst.minimalBudget.get.currency.uniqueFieldId.openOr(nextFuncName) &
      "data-name=budget-currency" #> inst.budget.get.currency.toForm &
      "data-name=budget-currency-error [data-alertid]" #> inst.budget.get.currency.uniqueFieldId.openOr(nextFuncName) &
      "data-name=collaborative-budget-currency" #> inst.collaborativeEconomyBudget.get.currency.toForm &
      "data-name=collaborative-budget-currency-error [data-alertid]" #> inst.collaborativeEconomyBudget.get.currency.uniqueFieldId.openOr(nextFuncName) &
      "data-name=management-duration" #> inst.managementDuration.toForm &
      "data-name=management-duration-error [data-alertid]" #> inst.managementDuration.uniqueFieldId.openOr(nextFuncName) &
      "data-name=tags" #> inst.tags.toForm &
      "data-name=tags [data-alertid]" #> inst.tags.uniqueFieldId.openOr(nextFuncName) &
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
