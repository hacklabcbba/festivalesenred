package code
package model
package festival

import java.util.Date

import code.lib.RogueMetaRecord
import code.lib.field._
import code.model.institution.Institution
import code.model.proposal.Proposal
import net.liftmodules.combobox.ComboItem
import net.liftmodules.mongoauth.model.Role
import net.liftweb.common.{Full, Box, Loggable}
import net.liftweb.http._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.{HtmlFixer, JsCmd}
import net.liftweb.http.js.JsCmds.{SetHtml, Run}
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
import org.joda.time.DateTime
import scala.xml._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http.{S}
import S._

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
  object status extends EnumNameField(this, FestivalStatus) {
    override def displayName = "Estado"
    override def shouldDisplay_? = User.currentUser.dmap(false)(u => false)
  }
  object responsible extends CustomStringField(this, 300) {
    override def displayName = "Responsable del Festival"
  }
  object productionManagement extends CustomStringField(this, 300) {
    override def displayName = "Dirección de la Producción"
  }
  object city extends OpenComboBoxField(this, City) {
    def toString(in: City) = s"${in.name.get}/${in.country.get}"
    val placeholder = "Seleccione las ciudades donde se realizara el festival"
    override def displayName = "Ciudad(es) donde se realiza"
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
          <tr><th>Lugar</th><th>&nbsp;</th></tr>
        </thead>
          <tbody data-name="places">
            <tr>
              <td data-name="name"></td>
              <td><a href="#!" data-name="edit" onclick="#"><i class="fa fa-edit"></i></a>
                &nbsp; <a href="#!" data-name="remove" onclick="#"><i class="fa fa-trash"></i></a>
              </td>
            </tr>
          </tbody>
          <tfoot>
          <tr>
            <td>
              <button data-name="add-link" href="#!" data-reveal-id="place-dialog" type="button" class="btn btn-primary btn-xs"><i class="fa fa-search-plus"></i> Agregar lugar</button>
            </td>
            <td></td>
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
              <label> <span>{place.geoLatLng.displayName}</span>
                {place.geoLatLng.toForm openOr NodeSeq.Empty}
              </label>
            </div>
            <br/>
          </div>
          <div class="row">
            <div class="form-actions large-12 columns">
              <div class="actions">
                {SHtml.hidden(addPlace)}
                <button type="submit" tabindex="1" class="btn btn-primary">
                  Agregar
                </button>
              </div>
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
  object callDate extends DatepickerField(this) {
    override def displayName = "Fecha limite convocatoria"

    def css = {
      val now = DateTime.now()
      val end = this.valueBox.dmap(DateTime.now())(new DateTime(_))
      if (now.isAfter(end))
        "alert label"
      else "success label"
    }
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
  object staff extends BsonRecordListField(this, TeamMember) with HtmlFixer {
    override def displayName = "¿Cuantas personas componen el equipo y que funciones cumplen?"
    override def helpAsHtml = Full(<span>Informa el número de personas aumentar una columna para numero que actuan regularmente junto a tu colectivo o grupo y en que funciones se desempeñan, inclusive sin son varias funciones por persona</span>)
    def title = "Datos de la persona"
    override def toForm = Full(
      css.apply(template)
    )

    def css = {
      "#staff" #> SHtml.idMemoize(body => {
        ".title" #> title &
          "tbody *" #> {
            "tr " #> value.map( p => {
              "data-name=name *" #> p.name.get &
              "data-name=email *" #> p.email.get &
              "data-name=edit [onclick]" #> SHtml.ajaxInvoke(() => dialogHtml(body, owner, p, false)) &
              "data-name=remove [onclick]" #> SHtml.ajaxInvoke(() => deleteTeamMember(body, owner, p))
            })
          } &
          "data-name=add-link [onclick]" #> SHtml.ajaxInvoke(() => dialogHtml(body, owner))
      })
    }


    def template = {
        <br />
        <div id="staff" class="panel panel-default">

          <div class="panel-heading title"></div>
          <table class="table table-hover table-condensed">
            <thead>
              <tr><th>Nombre</th><th>Email</th><th>&nbsp;</th></tr>
            </thead>
            <tbody data-name="staff">
              <tr>
                <td data-name="name"></td>
                <td data-name="email"></td>
                <td><a href="#!" data-name="edit" onclick="#"><i class="fa fa-edit"></i></a>
                  &nbsp; <a href="#!" data-name="remove" onclick="#"><i class="fa fa-trash"></i></a>
                </td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td>
                  <button data-name="add-link" href="#!" data-reveal-id="edition-dialog" type="button" class="btn btn-primary btn-xs"><i class="fa fa-search-plus"></i> Agregar edición</button>
                </td>
                <td></td>
                <td></td>
              </tr>
            </tfoot>
          </table>
        </div>
    }


    def deleteTeamMember(body: IdMemoizeTransform, festival: Festival, teamMember: TeamMember): JsCmd = {
      festival.staff.set(festival.staff.get.filter(p => p != teamMember))
      body.setHtml()
    }

    def dialogHtml(body: IdMemoizeTransform, festival: Festival, teamMember: TeamMember= TeamMember.createRecord, isNew: Boolean = true): JsCmd = {
      val modalId = nextFuncName
      val addTeamMember = () => {
        if (isNew) festival.staff.set(festival.staff.get ++ List(teamMember))
        body.setHtml() &
          Run(s"$$('#${modalId}').foundation('reveal', 'close');") &
          Run(s"$$('#${modalId}').remove();")
      }


      val html =
        <div id={modalId} class="reveal-modal" data-reveal="" aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
          <h2 id="modalTitle">Agregar Persona</h2>
          <form data-lift="form.ajax">
            <div class="row">
              <div class="large-12 columns" >
                <label> <span>{teamMember.name.displayName}</span>
                  {teamMember.name.toForm openOr NodeSeq.Empty}
                </label>
              </div>
            </div>
            <div class="row">
              <div class="large-12 columns" >
                <label> <span>{teamMember.email.displayName}</span>
                  {teamMember.email.toForm openOr NodeSeq.Empty}
                </label>
              </div>
            </div>
            <div class="row">
              <div class="large-12 columns" >
                <label> <span>{teamMember.function.displayName}</span>
                  {teamMember.function.toForm openOr NodeSeq.Empty}
                </label>
              </div>
            </div>
            <div class="row">
              <div class="large-12 columns" >
                <label> <span>{teamMember.cellphone.displayName}</span>
                  {teamMember.cellphone.toForm openOr NodeSeq.Empty}
                </label>
              </div>
            </div>
            <div class="row">
              <div class="form-actions large-12 columns">
                <div class="actions">
                  {SHtml.hidden(addTeamMember)}
                  <button type="submit" tabindex="1" class="btn btn-primary">
                    Agregar
                  </button>
                </div>
              </div>
            </div>
          </form>
          <a class="close-reveal-modal" aria-label="Cerrar">&#215;</a>
        </div>

      val (xml, js) = fixHtmlAndJs("modal", html)
      Run("$(" + xml + ").foundation('reveal', 'open');")
    }
  }
  object presentation extends TextareaField(this, 300) {
    override def displayName = "Breve histórico / presentación"
    override def helpAsHtml = Full(<span>Cuenta sobre la trayectoria del festival (máximo de 300 caracteres)</span>)
    private def elem = {
      SHtml.textarea(
        valueBox openOr "",
        this.set(_),
        "rows" -> textareaRows.toString,
        "cols" -> textareaCols.toString,
        "tabindex" -> tabIndex.toString,
        "onkeyup" -> "textCounter(this, 300);"
      ) ++ <br/> ++ <div> Caracteres restantes: <span id="presentation-chars-left">{this.maxLength - this.valueBox.dmap(0)(_.length)}</span></div>
    }

    override def toForm: Box[NodeSeq] = Full(elem)

  }
  object numberEditions extends BsonRecordListField(this, FestivalEdition) with HtmlFixer {
    override def displayName = "¿Cuantas ediciones del festival se han realizado y en que años?"
    override def helpAsHtml = Full(<span>Informa cuantas ediciones fueron realizadas y en que años mismo si no han sido sucesivos Ej: Festival del Sol - 5 ediciones 2004 -2006- 2009 - 2010 - 2013. Elegir varias fechas, sólo MES y AÑO. Si es consecutivo "Desde...".</span>)
    def title = "Datos de la edición"
    override def toForm = Full(
      css.apply(template)
    )

    def css = {
      "#editions" #> SHtml.idMemoize(body => {
        ".title" #> title &
          "tbody *" #> {
            "tr " #> value.map( p => {
              "data-name=name *" #> p.name.get &
              "data-name=date *" #> p.date.toString &
              "data-name=edit [onclick]" #> SHtml.ajaxInvoke(() => dialogHtml(body, owner, p, false)) &
              "data-name=remove [onclick]" #> SHtml.ajaxInvoke(() => deleteEdition(body, owner, p))
            })
          } &
          "data-name=add-link [onclick]" #> SHtml.ajaxInvoke(() => dialogHtml(body, owner))
      })
    }


    def template = {
        <br />
        <div id="editions" class="panel panel-default">

          <div class="panel-heading title"></div>
          <table class="table table-hover table-condensed">
            <thead>
              <tr><th>Nombre</th><th>Fecha</th><th>&nbsp;</th></tr>
            </thead>
            <tbody data-name="editions">
              <tr>
                <td data-name="name"></td>
                <td data-name="date"></td>
                <td><a href="#!" data-name="edit" onclick="#"><i class="fa fa-edit"></i></a>
                  &nbsp; <a href="#!" data-name="remove" onclick="#"><i class="fa fa-trash"></i></a>
                </td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td>
                  <button data-name="add-link" href="#!" data-reveal-id="edition-dialog" type="button" class="btn btn-primary btn-xs"><i class="fa fa-search-plus"></i> Agregar edición</button>
                </td>
                <td></td>
                <td></td>
              </tr>
            </tfoot>
          </table>
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
            <div class="row">
              <div class="form-actions large-12 columns">
                <div class="actions">
                  {SHtml.hidden(addEdition)}
                  <button type="submit" tabindex="1" class="btn btn-primary">
                    Agregar
                  </button>
                </div>
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
    if (User.hasRole("admin") || owner.email.get.endsWith("genso.com.bo"))
      Festival.findAll
    else
      Festival.where(_.owner eqs owner.id.get).fetch()
  }

  def findAllByPage(itemsPerPage: Int, page: Int): List[Festival] = {
    Festival.paginate(itemsPerPage).setPage(page).fetch()
  }

  private def filterQry(area: Box[Area], tag: Box[String], equipment: Box[EquipmentDetail],
                        service: Box[ServiceExchange], training: Box[TrainingActivity], communication: Box[CommunicationTool],
                        partnership: Box[Partnership], networking: Box[Network]) = {
    Festival
      .where(_.status neqs FestivalStatus.Draft)
      .andOpt(area)(_.areas contains _.id.get)
      .andOpt(tag)(_.tags.subfield(_.tag) contains _)
      .andOpt(equipment)(_.equipment contains _.id.get)
      .andOpt(service)(_.serviceExchange contains _.id.get)
      .andOpt(training)(_.trainingActivity contains _.id.get)
      .andOpt(communication)(_.communicationTools contains _.id.get)
      .andOpt(partnership)(_.civilOrganizationPartnerships contains _.id.get)
      .andOpt(networking)(_.networking contains _.id.get)
      .orderAsc(_.status).andDesc(_.begins)
  }

  def findAllFilteredByPage(
                     itemsPerPage: Int, page: Int, area: Box[Area], tag: Box[String], equipment: Box[EquipmentDetail],
                     service: Box[ServiceExchange], training: Box[TrainingActivity], communication: Box[CommunicationTool],
                     partnership: Box[Partnership], networking: Box[Network]): List[Festival] = {
    filterQry(area, tag, equipment, service, training, communication, partnership, networking)
      .paginate(itemsPerPage)
      .setPage(page)
      .fetch()
  }

  def countFiltered(
                     area: Box[Area], tag: Box[String], equipment: Box[EquipmentDetail],
                     service: Box[ServiceExchange], training: Box[TrainingActivity], communication: Box[CommunicationTool],
                     partnership: Box[Partnership], networking: Box[Network]): Long = {
    filterQry(area, tag, equipment, service, training, communication, partnership, networking).count()
  }

  def findAllForMapByAreasCitiesAndDates(areas: List[Area], cities: List[City], begins: Box[Date], ends: Box[Date]): List[Festival] = {
    val qry = Festival
      .andOpt(begins)(_.begins gte new DateTime(_))
      .andOpt(ends)(_.ends lte new DateTime(_))

    val areasQry = areas match {
      case Nil => qry
      case other => qry.where(_.areas in areas.map(_.id.get))
    }

    val citiesQry = cities match {
      case Nil => areasQry
      case other => areasQry.where(_.city in cities.map(_.id.get))
    }

    citiesQry.fetch()
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

object FestivalStatus extends Enumeration {
  type FestivalStatus = Value
  val Draft = Value("Borrador")
  val Approved = Value("Aprobado")
  val Archived = Value("Archivado")
}