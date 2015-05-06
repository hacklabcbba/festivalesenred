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
    }: CssSel
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
      "data-name=add [href]" #> "/festival-form/new"
    }): CssSel
  }
}

object FestivalView extends SnippetHelper {
  def render: CssSel = {
    for {
      item <- Site.festival.currentValue ?~ "Opción no válida"
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

      "data-name=title *+" #> item.name &
      "data-name=dates *+" #> ("De: "+ item.begins.toString +" Al: "+item.ends.toString )&
      "data-name=website *" #> item.website &
      "data-name=website [href]" #> item.website &
      "data-name=facebook [href]" #> item.facebookPage &
      "data-name=facebook *" #> item.facebookPage &
      "data-name=twitter [href]" #> item.twitter &
      "data-name=twitter *" #> item.twitter &
      "data-name=responsible *" #> item.responsible &
      "data-name=responsibleEmail" #> item.responsibleEmail &
      "data-name=responsiblePress *" #> "" &
      "data-name=responsiblePressEmail *" #> item.pressResponsibleEmail &
      "data-name=productionManagement *" #> item.productionManagement &
      "data-name=productionManagementEmail *" #> "" &
      "data-name=tags *" #> item.tags.get.map( t =>
        "data-name=tag *" #> t.tag.get
      ) &
      "data-name=photo1-link [href]" #> (item.photo1.get match {
        case p: FileRecord => "/service/images/"+ p.fileId.get
        case _ => "#"
      }) &
      "data-name=photo1" #> (item.photo1.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      }) &
        "data-name=photo2-link [href]" #> (item.photo2.get match {
          case p: FileRecord => "/service/images/"+ p.fileId.get
          case _ => "#"
        }) &
      "data-name=photo2" #> (item.photo2.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      }) &
        "data-name=photo3-link [href]" #> (item.photo3.get match {
          case p: FileRecord => "/service/images/"+ p.fileId.get
          case _ => "#"
        }) &
      "data-name=photo3" #> (item.photo3.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      }) &
      "data-name=presentation *" #> item.presentation &
      "data-name=equipments " #> item.equipment.objs.map({d =>
        "data-name=equipment *" #> <h5>{d.name}</h5>
      }) &
      "data-name=serviceExchanges" #> item.serviceExchange.objs.map({s =>
        "data-name=serviceExchange *" #> <h5>{s.name}</h5>
      }) &
      "data-name=trainingActivities" #> item.trainingActivity.objs.map({t =>
        "data-name=trainingActivity *" #> <h5>{t.name}</h5>
      }) &
      "data-name=communicationTools" #> item.communicationTools.objs.map({c =>
        "data-name=communicationTool *" #> <h5>{c.name}</h5>
      }) &
      "data-name=networkings" #> item.networking.objs.map({n =>
        "data-name=networking *" #> <h5>{n.name}</h5>
      }) &
      "data-name=minimalBudget" #> (item.minimalBudget.get.amount.get +" "+ item.minimalBudget.get.currency.obj.get.toString) &
      "data-name=budget" #> (item.budget.get.amount.get + " "+ item.budget.get.currency.obj.get.toString) &
      "data-name=collaborativeEconomyBudget" #> (item.collaborativeEconomyBudget.get.amount.get + " "+ item.collaborativeEconomyBudget.get.currency.obj.get.toString)
    }: CssSel
  }
}