package code
package snippet

import code.config.Site
import code.model.{FileRecord, User}
import code.model.festival.{EquipmentDetail, Festival}
import com.foursquare.rogue.LatLong
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common._
import net.liftweb.http.js.JsCmds.{RedirectTo, Noop}
import net.liftweb.http.{PaginatorSnippet, SHtml, S}
import net.liftweb.record.Field
import net.liftweb.util._
import net.liftweb.util.Helpers._

import scala.xml.Text
import net.liftweb.util.Mailer._

object FestivalesSnippet extends SnippetHelper with PaginatorSnippet[Festival] {

  override def itemsPerPage = 5

  override def count = Festival.count

  def mongoPage = if (curPage < 1) 1 else curPage

  override def page = Festival.findAllByPage(itemsPerPage, mongoPage)


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

  def list = {
    "data-name=list" #> page.map(festival => {
      "data-name=festival-link [href]" #> Site.festival.calcHref(festival) &
      "data-name=name" #> festival.name.get &
      "data-name=cities *" #> festival.city.objs.map(_.name.get).mkString(", ") &
      "data-name=dates *" #> (festival.begins + " al " + festival.ends)
    })
  }
}

object FestivalView extends SnippetHelper {

  def title: CssSel = {
    for {
      item <- Site.festival.currentValue ?~ "Opción no válida"
    } yield ("title -*" #> item.name): CssSel
  }

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

      "data-name=title *+" #> item.name.get &
        "data-name=dates *+" #> (item.begins +" al " + item.ends)&
        "data-name=place" #> item.productionManagement.get &
        "data-name=cities" #> item.city.objs.map(_.name.get).mkString(",") &
        "data-name=duration" #> (item.duration.get + " " + item.durationType.get) &
        "data-name=website *" #> item.website.get &
        "data-name=website [href]" #> item.website.get &
        "data-name=facebook [href]" #> item.facebookPage.get &
        "data-name=facebook *" #> item.facebookPage.get &
        "data-name=twitter [href]" #> item.twitter.get &
        "data-name=skype *" #> item.twitter.get &
        "data-name=skype [href]" #> item.skype.get &
        "data-name=twitter *" #> item.skype.get &
        "data-name=responsible *" #> item.responsible.get &
        "data-name=responsibleEmail" #> item.responsibleEmail.get &
        "data-name=areas *" #> item.areas.objs.map(_.name.get) &
        "data-name=tags *" #> item.tags.get.map(_.tag.get) &
        "data-name=description *" #> item.presentation.get &
        "data-name=equipment *" #> item.equipment.objs.map(_.name.get) &
        "data-name=item-exchange *" #> item.serviceExchange.objs.map(_.name.get) &
        "data-name=item-training *" #> item.trainingActivity.objs.map(_.name.get) &
        "data-name=item-tools *" #> item.communicationTools.objs.map(_.name.get) &
        "data-name=item-public" #> item.publicInstitutionPartnerships.objs.map(_.name.get).mkString(", ") &
        "data-name=item-private" #> item.privateInstitutionPartnerships.objs.map(_.name.get).mkString(", ") &
        "data-name=item-civil" #> item.civilOrganizationPartnerships.objs.map(_.name.get).mkString(", ") &
        "data-name=item-tools" #> item.communicationTools.objs.map(_.name.get).mkString(", ") &
        "data-name=item-networking" #> item.networking.objs.map(_.name.get).mkString(", ") &
        "data-name=minimal-budget *" #> item.minimalBudget.toString &
        "data-name=budget *" #> item.budget.toString &
        "data-name=colaborative-budget *" #> item.collaborativeEconomyBudget.toString &
        "data-name=logo-link [href]" #> (item.logo.get match {
          case p: FileRecord => "/service/images/"+ p.fileId.get
          case _ => "#"
        }) &
        "data-name=logo" #> (item.logo.get match {
          case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
          case _ => <br />
        }) &
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
        })
    }: CssSel
  }
}