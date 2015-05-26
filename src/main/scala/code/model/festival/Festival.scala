package code
package model
package festival

import code.lib.RogueMetaRecord
import code.lib.field._
import code.model.institution.Institution
import code.model.proposal.Proposal
import net.liftmodules.combobox.ComboItem
import net.liftmodules.mongoauth.model.Role
import net.liftweb.common.{Full, Box, Loggable}
import net.liftweb.http._
import net.liftweb.http.js.{HtmlFixer, JsCmd}
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field._
import net.liftweb.record.LifecycleCallbacks
import net.liftweb.record.field.IntField
import net.liftweb.record.field._
import code.model.field.{ListStringDataType, Field}
import code.model.link.Link
import com.foursquare.rogue.LiftRogue._
import net.liftweb.sitemap.Loc.If
import net.liftweb.util.Helpers
import Helpers._

import scala.xml.{Text, NodeSeq}

class Festival private () extends MongoRecord[Festival] with ObjectIdPk[Festival] {

  override def meta = Festival

  object name extends CustomStringField(this, 500) {
    override def displayName = "Nombre del Festival"
  }
  object owner extends ObjectIdRefField(this, User) {
    override def shouldDisplay_? = false
    override def valueBox = User.currentUser.map(_.id.get)
  }
  object responsible extends CustomStringField(this, 300) {
    override def displayName = "Responsable del Festival"
  }
  object productionManagement extends CustomStringField(this, 300) {
    override def displayName = "Dirección de la Producción"
  }
  object city extends ComboBoxField(this, City) {
    def toString(in: City) = s"${in.name.get}/${in.country.get}"
    val placeholder = "Seleccione las ciudades donde se realizara el festival"
    override def displayName = "Ciudad"
  }
  object places extends BsonRecordListField(this, Place) with HtmlFixer {
    override def displayName = ""
    def title = "Lugar donde se desarrolla el Festival"
    override def toForm = Full(
      css.apply(template)
    )

    def css = {
      "#places" #> SHtml.idMemoize(body => {
      ".title" #> title &
      "tbody *" #> {
        "tr " #> value.map( p => {
          "data-name=date *" #> p.date.toString &
          "data-name=name *" #> p.name.get &
          "data-name=edit [onclick]" #> SHtml.ajaxInvoke(() => dialogHtml(body, owner, p, false)) &
          "data-name=remove [onclick]" #> SHtml.ajaxInvoke(() => deletePlace(body, owner, p))
          })
      } &
      "data-name=add-link [onclick]" #> SHtml.ajaxInvoke(() => dialogHtml(body, owner))
      })
    }


    def template = {
      <br />
      <div id="places" class="panel panel-default">

        <div class="panel-heading title"></div>
        <table class="table table-hover table-condensed">
        <thead>
          <tr><th>Fecha</th><th>Lugar</th><th>&nbsp;</th></tr>
        </thead>
          <tbody data-name="places">
            <tr><td data-name="date"></td>
                <td data-name="name"></td>
                <td><a href="#!" data-name="edit" onclick="#"><i class="fa fa-edit"></i></a>
                  &nbsp; <a href="#!" data-name="remove" onclick="#"><i class="fa fa-trash"></i></a>
                </td>
            </tr>
          </tbody>
          <tfoot>
          <tr>
            <td>
              <button data-name="add-link" href="#!" data-reveal-id="place-dialog" type="button" class="btn btn-primary btn-xs"><i class="fa fa-search-plus"></i> Agregar lugar</button></td>
          </tr>
          </tfoot>
        </table>
      </div>
    }


    def deletePlace(body: IdMemoizeTransform, festival: Festival, place: Place): JsCmd = {
      festival.places.set(festival.places.get.filter(p => p != place))
      body.setHtml()
    }

    def dialogHtml(body: IdMemoizeTransform, festival: Festival, place: Place = Place.createRecord, isNew: Boolean = true): JsCmd = {
      val modalId = nextFuncName
      val addPlace = () => {
        if (isNew) festival.places.set(festival.places.get ++ List(place))
        body.setHtml() &
          Run(s"$$('#${modalId}').foundation('reveal', 'close');") &
          Run(s"$$('#${modalId}').remove();")
     }


      val html =
      <div id={modalId} class="reveal-modal" data-reveal="" aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
        <h2 id="modalTitle">Agregar Lugar</h2>
        <form data-lift="form.ajax">
          <div class="row">
            <div class="large-12 columns" >
              <label> <span>{place.name.displayName}</span>
                {place.name.toForm openOr NodeSeq.Empty}
              </label>
            </div>
          </div>
          <div class="row">
            <div class="large-12 columns" >
              <label> <span>{place.date.displayName}</span>
                {place.date.toForm openOr NodeSeq.Empty}
              </label>
            </div>
          </div>
          <div class="row">
            <div class="large-12 columns" >
              <label> <span>{place.city.displayName}</span>
                {place.city.toForm openOr NodeSeq.Empty}
              </label>
            </div>
          </div>
          <div class="row">
            <div class="large-12 columns" >
              <label> <span>{place.geoLatLng.displayName}</span>
                {place.geoLatLng.toForm openOr NodeSeq.Empty}
              </label>
            </div>
          </div>
          <div class="form-actions">
            <div class="actions">
              {SHtml.hidden(addPlace)}
              <button type="submit" tabindex="1" class="btn btn-primary">
                Agregar
              </button>
            </div>
          </div>
        </form>
        <a class="close-reveal-modal" aria-label="Cerrar">&#215;</a>
      </div>

      val (xml, js) = fixHtmlAndJs("modal", html)
      Run("$(" + xml + ").foundation('reveal', 'open');")
    }
  }
  object begins extends DatepickerField(this) {
    override def displayName = "Fecha inicial"
  }
  object ends extends DatepickerField(this) {
    override def displayName = "Fecha final"
  }

  object duration extends IntField(this, 1) {
    override def shouldDisplay_? = false
  }

  object durationType extends EnumNameField(this, FestivalDuration) {
    override def displayName = "Duración"
    override def toForm = {
      for {
        f1 <- duration.toForm
        f2 <- super.toForm
      } yield {
        <div class="row collapse">
          <div class="small-6 large-6 columns">
            {f1}
          </div>
          <div class="small-6 large-6 columns">
            {f2}
          </div>
        </div>
      }
    }
  }
  object call extends FileField(this) {
    override def displayName = "Convocatoria"
  }

  object logo extends FileField(this) {
    override def displayName = "Logo"
  }

  object photo1 extends FileField(this) {
    override def displayName = "Foto 1"
  }

  object photo2 extends FileField(this) {
    override def displayName = "Foto 2"
  }

  object photo3 extends FileField(this) {
    override def displayName = "Foto 3"
  }

  object areas extends OpenComboBoxField(this, Area) {
    def toString(in: Area) = s"${in.name.get}${if (in.description.get.isEmpty) "" else s"(${in.description.get})"}"
    val placeholder = ""
    override def displayName = "¿En qué área actúa?"
    override def beforeSave() {
      super.beforeSave
      this.set(this.get ++ this.tempItems.map(s => Area.createRecord.name(s.text).save(true).id.get))
    }
    override def helpAsHtml = Full(<span>Elije 1 o mas opciones</span>)
  }
  object website extends LinkField(this, 500) {
    override def displayName = "Sitio web"
  }
  object responsibleEmail extends EmailField(this, 128) {
    override def displayName = "Email del/la responsable"
  }
  object pressResponsibleEmail extends EmailField(this, 128) {
    override def displayName = "Email de contacto del/la responsable de comunicación o prensa"
  }
  object facebookPage extends FacebookField(this, 500) {
    override def displayName = "Nombre de usuario en facebook"
    override def optional_? = true
  }
  object twitter extends TwitterField(this, 500) {
    override def displayName = "Twitter"
    override def optional_? = true
  }
  object skype extends CustomStringField(this, 128) {
    override def displayName = "Skype"
    override def optional_? = true
  }
  object spaces extends OpenComboBoxField(this, Space) {
    def toString(in: Space) = s"${in.name.get}"
    val placeholder = ""
    override def displayName = "Espacios donde se realiza"
    override def beforeSave() {
      super.beforeSave
      this.set(this.get ++ this.tempItems.map(s => Space.createRecord.name(s.text).save(true).id.get))
    }
    override def helpAsHtml = Full(<span>Informe donde acostumbras a realizar las acciones de tu festival</span>)
  }
  object equipment extends OpenComboBoxField(this, EquipmentDetail) {
    def toString(in: EquipmentDetail) = s"${in.name.get}"
    val placeholder = ""
    override def displayName = "Equipamientos que posees "
    override def beforeSave() {
      super.beforeSave
      this.set(this.get ++ this.tempItems.map(s => EquipmentDetail.createRecord.name(s.text).save(true).id.get))
    }
    override def helpAsHtml = Full(<span>Informa con que equipamientos propios cuentas para realizar el festival, haz clic en las opciones y especifica en el ítem "otros"</span>)
  }
  object numberOfAttendees extends EnumNameField(this, NumberOfAttendees) {
    override def displayName = "¿Qué cantidad de público convoca y moviliza tu festival? "
    override def helpAsHtml = Full(<span>poner un número estimado</span>)
  }
  object publicKind extends OpenComboBoxField(this, PublicKind) {
    def toString(in: PublicKind) = s"${in.name.get}"
    val placeholder = ""
    override def displayName = "¿A qué tipo de público se dirige tu festival?"
  }
  object staff extends BsonRecordListField(this, Staff) {
    override def displayName = "¿Cuantas personas componen el equipo y que funciones cumplen?"
    override def helpAsHtml = Full(<span>Informa el número de personas aumentar una columna para numero que actuan regularmente junto a tu colectivo o grupo y en que funciones se desempeñan, inclusive sin son varias funciones por persona</span>)
  }
  object presentation extends TextareaField(this, 300) {
    override def displayName = "Breve histórico / presentación"
    override def helpAsHtml = Full(<span>Cuenta sobre la trayectoria del festival (máximo de 300 caracteres)</span>)
  }
  object numberEditions extends BsonRecordListField(this, FestivalEdition) with HtmlFixer {
    override def displayName = "¿Cuantas ediciones del festival se han realizado y en que años?"
    override def helpAsHtml = Full(<span>Informa cuantas ediciones fueron realizadas y en que años mismo si no han sido sucesivos Ej: Festival del Sol - 5 ediciones 2004 -2006- 2009 - 2010 - 2013. Elegir varias fechas, sólo MES y AÑO. Si es consecutivo "Desde...".</span>)
    override def toForm = Full(
      css.apply(template)
    )

    def css = {
      "#editions" #> SHtml.idMemoize(body => {
        "data-name=editions" #> <ol>{value.foldLeft(NodeSeq.Empty){ case (node, edition) => {
          node ++ <li>
            {(edition.name.get ++ " - " ++ edition.date.toString)}
            <a href="#!" data-name="edit" onclick={SHtml.ajaxInvoke(() => dialogHtml(body, owner, edition, false)).toJsCmd}><i class="fa fa-edit"></i></a>
            <a href="#!" data-name="remove" onclick={SHtml.ajaxInvoke(() => deleteEdition(body, owner, edition)).toJsCmd}><i class="fa fa-trash"></i></a>
          </li>}}}</ol> &
          "data-name=dialog-link [onclick]" #> SHtml.ajaxInvoke(() => dialogHtml(body, owner))
      })
    }

    def template = {
      <div id="editions">
        <span data-name="places"></span>
        <label><a data-name="dialog-link" href="#!" data-reveal-id="place-dialog"><i class="fa fa-search-plus"></i> Agregar Edición</a></label>
      </div>
    }

    def deleteEdition(body: IdMemoizeTransform, festival: Festival, edition: FestivalEdition): JsCmd = {
      festival.numberEditions.set(festival.numberEditions.get.filter(p => p != edition))
      body.setHtml()
    }

    def dialogHtml(body: IdMemoizeTransform, festival: Festival, edition: FestivalEdition = FestivalEdition.createRecord, isNew: Boolean = true): JsCmd = {
      val modalId = nextFuncName
      val addEdition = () => {
        if (isNew) festival.numberEditions.set(festival.numberEditions.get ++ List(edition))
        body.setHtml() &
          Run(s"$$('#${modalId}').foundation('reveal', 'close');") &
          Run(s"$$('#${modalId}').remove();")
      }

      val html =
        <div id={modalId} class="reveal-modal" data-reveal="" aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
          <h2 id="modalTitle">Agregar Edición</h2>
          <form data-lift="form.ajax">
            <div class="row">
              <div class="large-12 columns" >
                <label> <span>{edition.name.displayName}</span>
                  {edition.name.toForm openOr NodeSeq.Empty}
                </label>
              </div>
            </div>
            <div class="row">
              <div class="large-12 columns" >
                <label> <span>{edition.date.displayName}</span>
                  {edition.date.toForm openOr NodeSeq.Empty}
                </label>
              </div>
            </div>
            <div class="form-actions">
              <div class="actions">
                {SHtml.hidden(addEdition)}
                <button type="submit" tabindex="1" class="btn btn-primary">
                  Agregar
                </button>
              </div>
            </div>
          </form>
          <a class="close-reveal-modal" aria-label="Cerrar">&#215;</a>
        </div>

      val (xml, js) = fixHtmlAndJs("modal", html)
      Run("$(" + xml + ").foundation('reveal', 'open');")
    }
  }
  object serviceExchange extends OpenComboBoxField(this, ServiceExchange) {
    def toString(in: ServiceExchange) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def beforeSave() {
      super.beforeSave
      this.set(this.get ++ this.tempItems.map(s => ServiceExchange.createRecord.name(s.text).save(true).id.get))
    }
    override def displayName = "¿Realizas intercambio de servicios, productos o conocimiento con otras organizaciones? En caso que tu respuesta sea Si, por favor especifica"
    override def optional_? = true
    override def helpAsHtml = Full(<span>A través de trueques, fondos y/o otros</span>)
  }
  object trainingActivity extends OpenComboBoxField(this, TrainingActivity) {
    def toString(in: TrainingActivity) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def beforeSave() {
      super.beforeSave
      this.set(this.get ++ this.tempItems.map(s => TrainingActivity.createRecord.name(s.text).save(true).id.get))
    }
    override def displayName = "¿Desarrollas alguna acción de formación?"
    override def optional_? = true
    override def helpAsHtml = Full(<span>Debates, talleres, conferencias y/o otros</span>)
  }
  object communicationTools extends OpenComboBoxField(this, CommunicationTool) {
    def toString(in: CommunicationTool) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def beforeSave() {
      super.beforeSave
      this.set(this.get ++ this.tempItems.map(s => CommunicationTool.createRecord.name(s.text).save(true).id.get))
    }
    override def displayName = "¿Especificar que herramientas de comunicación utilizas?"
  }
  object publicInstitutionPartnerships extends OpenComboBoxField(this, Partnership) {
    def toString(in: Partnership) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def options =
      Partnership.where(_.kind eqs PartnershipKind.Public).fetch().map(s => s.id.get -> toString(s))
    override def beforeSave() {
      super.beforeSave
      this.set(
        this.get ++ this.tempItems.map(
          s => Partnership.createRecord.kind(PartnershipKind.Public).name(s.text).save(true).id.get
        )
      )
    }
    override def displayName = "¿Tienes alguna alianza con instituciones públicas?"
    override def optional_? = true
  }
  object privateInstitutionPartnerships extends OpenComboBoxField(this, Partnership) {
    def toString(in: Partnership) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def options =
      Partnership.where(_.kind eqs PartnershipKind.Private).fetch().map(s => s.id.get -> toString(s))
    override def beforeSave() {
      super.beforeSave
      this.set(
        this.get ++ this.tempItems.map(
          s => Partnership.createRecord.kind(PartnershipKind.Private).name(s.text).save(true).id.get
        )
      )
    }
    override def displayName = "¿Tienes alguna alianza con alguna entidad privada?"
    override def optional_? = true
  }
  object civilOrganizationPartnerships extends OpenComboBoxField(this, Partnership) {
    def toString(in: Partnership) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def options =
      Partnership.where(_.kind eqs PartnershipKind.CivilSociety).fetch().map(s => s.id.get -> toString(s))
    override def beforeSave() {
      super.beforeSave
      this.set(
        this.get ++ this.tempItems.map(
          s => Partnership.createRecord.kind(PartnershipKind.CivilSociety).name(s.text).save(true).id.get
        )
      )
    }
    override def displayName = "¿Tienes alguna alianza con una organización social?"
    override def optional_? = true
  }
  object networking extends OpenComboBoxField(this, Network) {
    def toString(in: Network) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def beforeValidation(): Unit = {
      println("VALUES:"+ this.tempItems)
      super.beforeSave
      this.set(
        this.get ++ this.tempItems.map(
          s => Network.createRecord.name(s.text).save(true).id.get
        )
      )
    }
    override def displayName = "¿Participa de alguna red?"
    override def optional_? = true
  }
  object minimalBudget extends AmountField(this) {
    override def displayName = "A partir de que presupuesto mínimo realizas el festival"
    override def helpAsHtml = Full(<span>Especifica un monto mínimo en moneda</span>)
  }
  object budget extends AmountField(this) {
    override def displayName = "Cual es el costo monetario que utilizas para realizar el festival"
    override def helpAsHtml = Full(<span>Especifica el monto real en moneda que utilizas para la producción de tu festival</span>)
  }
  object collaborativeEconomyBudget extends AmountField(this) {
    override def displayName = "Cual el monto en economía colaborativa"
    override def helpAsHtml = Full(<span>Haz un estimativo de los movimientos que se realizan a partir de intercambio, donaciones voluntariados etc.</span>)
  }
  object managementDuration extends EnumNameField(this, ManagementDuration) {
    override def displayName = "Cual es el tiempo de duración de gestión del Festival?"
    override def helpAsHtml = Full(<span>Especifica en los tiempos: Desde inicio de gestión, planificación, duración del Festival y el Cierre</span>)
  }
  object tags extends TagField(this) {
    override def displayName = "Palabras Claves, Etiquetas, HashTags"
  }

}

object Festival extends Festival with RogueMetaRecord[Festival] with Loggable {
  override def collectionName = "festival.festivals"
  override def fieldOrder = List(
    logo, name, responsible, productionManagement, city, places, begins, ends, duration, call, areas, website,
    responsibleEmail, pressResponsibleEmail, facebookPage, twitter, skype, spaces, equipment, numberOfAttendees,
    publicKind, photo1, photo2, photo3, staff, presentation, numberEditions, serviceExchange, trainingActivity, communicationTools,
    publicInstitutionPartnerships, privateInstitutionPartnerships, civilOrganizationPartnerships, networking,
    minimalBudget, budget, collaborativeEconomyBudget, managementDuration, tags
  )
  def findOrNew(in: String): Box[Festival] = in match {
    case "new" =>
      Full(Festival.createRecord)
    case _ =>
      find(in)
  }

  def findAllByUser(owner: User): List[Festival] = {
    if (User.hasRole("admin"))
      Festival.findAll
    else
      Festival.where(_.owner eqs owner.id.get).fetch()
  }

}


object FestivalDuration extends Enumeration {
  type ContactType = Value
  val Hours = Value("Horas")
  val Days = Value("Dias")
  val Weeks = Value("Semanas")
  val Months = Value("Meses")
  val Years = Value("Años")
}

object NumberOfAttendees extends Enumeration {
  type NumberOfAttendees = Value
  val LessThanFifty = Value("1 - 50")
  val FiftyToOneHundred = Value("50 - 100")
  val OneHundredToFiveHundred = Value("100 - 500")
  val FiveHundredToOneThousand = Value("500 - 1000")
  val MoreThanOneThousand = Value("Más de 1000")
}

object ManagementDuration extends Enumeration {
  type PublicKind = Value
  val OneDay = Value("1 día")
  val OneWeek = Value("1 semana")
  val OneMonth = Value("1 mes")
  val SeveralMonths = Value("Varios meses")
  val OneYear = Value("Un año")
  val MoreThanOneYear = Value("Más de 1 año")
}