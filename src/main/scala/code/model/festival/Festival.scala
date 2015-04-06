package code
package model
package festival

import code.lib.RogueMetaRecord
import code.lib.field.{CustomStringField, OpenComboBoxField, DatepickerField, ComboBoxField}
import code.model.institution.Institution
import code.model.proposal.Proposal
import net.liftmodules.combobox.ComboItem
import net.liftweb.common.{Full, Box, Loggable}
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field._
import net.liftweb.record.LifecycleCallbacks
import net.liftweb.record.field._
import code.model.field.{ListStringDataType, Field}
import code.model.link.Link
import com.foursquare.rogue.LiftRogue._

import scala.xml.{Text, NodeSeq}

class Festival private () extends MongoRecord[Festival] with ObjectIdPk[Festival] {

  override def meta = Festival

  object name extends CustomStringField(this, 500) {
    override def displayName = "Nombre del Festival"
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
  object places extends BsonRecordListField(this, Place) {
    override def displayName = "Lugares donde se desarrolla el Festival"
    override def toForm = Full(
      value.foldLeft(NodeSeq.Empty){ case (node, place) => {
        node ++ Text(place.name.get) ++ Text(place.date.get.toString)
      }} ++ <label><a href="#"><i class="fa fa-search-plus"></i> Agregar Lugar</a></label>
    )
  }
  object begins extends DatepickerField(this) {
    override def displayName = "Fecha inicial"
  }
  object ends extends DatepickerField(this) {
    override def displayName = "Fecha final"
  }
  object duration extends EnumNameField(this, FestivalDuration) {
    override def displayName = "Duración"
  }
  object call extends BinaryField(this) {
    override def displayName = "Convocatoria"
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
  object website extends CustomStringField(this, 500) {
    override def displayName = "Sitio web"
  }
  object responsibleEmail extends EmailField(this, 128) {
    override def displayName = "Email del/la responsable"
  }
  object pressResponsibleEmail extends EmailField(this, 128) {
    override def displayName = "Email de contacto del/la responsable de comunicación o prensa"
  }
  object facebookPage extends CustomStringField(this, 500) {
    override def displayName = "Página en facebook"
    override def optional_? = true
  }
  object twitter extends CustomStringField(this, 500) {
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
  object numberEditions extends BsonRecordListField(this, FestivalEdition) {
    override def displayName = "¿Cuantas ediciones del festival se han realizado y en que años?"
    override def helpAsHtml = Full(<span>Informa cuantas ediciones fueron realizadas y en que años mismo si no han sido sucesivos Ej: Festival del Sol - 5 ediciones 2004 -2006- 2009 - 2010 - 2013. Elegir varias fechas, sólo MES y AÑO. Si es consecutivo "Desde...".</span>)
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
    override def displayName = "¿Tienes alguna alianza con alguna entidad privada?"
    override def optional_? = true
  }
  object networking extends OpenComboBoxField(this, Network) {
    def toString(in: Network) = s"${in.name.get}"
    val placeholder = "Seleccione uno o más valores"
    override def beforeSave() {
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
  object minimalBudget extends DecimalField(this, 0) {
    override def displayName = "A partir de que presupuesto mínimo realizas el festival"
    override def helpAsHtml = Full(<span>Especifica un monto mínimo en moneda</span>)
  }
  object budget extends DecimalField(this, 0) {
    override def displayName = "Cual es el costo monetario que utilizas para realizar el festival"
    override def optional_? = true
    override def helpAsHtml = Full(<span>Especifica el monto real en moneda que utilizas para la producción de tu festival</span>)
  }
  object collaborativeEconomyBudget extends DecimalField(this, 0) {
    override def displayName = "Cual el monto en economía colaborativa"
    override def optional_? = true
    override def helpAsHtml = Full(<span>Haz un estimativo de los movimientos que se realizan a partir de intercambio, donaciones voluntariados etc.</span>)
  }
  object managementDuration extends EnumNameField(this, ManagementDuration) {
    override def displayName = "Cual es el tiempo de duración de gestión del Festival?"
    override def helpAsHtml = Full(<span>Especifica en los tiempos: Desde inicio de gestión, planificación, duración del Festival y el Cierre</span>)
  }
  object tags extends CustomStringField(this, 1000) {
    override def displayName = "Palabras Claves, Etiquetas, HashTags"
  }

}

object Festival extends Festival with RogueMetaRecord[Festival] with Loggable {
  override def collectionName = "festival.festivals"
  override def fieldOrder = List(
    name, responsible, productionManagement, city, places, begins, ends, duration, call, areas, website,
    responsibleEmail, pressResponsibleEmail, facebookPage, twitter, skype, spaces, equipment, numberOfAttendees,
    publicKind, staff, presentation, numberEditions, serviceExchange, trainingActivity, communicationTools,
    publicInstitutionPartnerships, privateInstitutionPartnerships, civilOrganizationPartnerships, networking,
    minimalBudget, budget, collaborativeEconomyBudget, managementDuration, tags
  )
  def findOrNew(in: String): Box[Festival] = in match {
    case "new" =>
      Full(Festival.createRecord)
    case _ =>
      find(in)
  }
}


object FestivalDuration extends Enumeration {
  type ContactType = Value
  val Hours = Value("Horas")
  val Days = Value("Dias")
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
  val OneWeek = Value("1 semana")
  val OneMonth = Value("1 mes")
  val SeveralMonths = Value("Varios meses")
  val OneYear = Value("Un año")
  val MoreThanOneYear = Value("Más de 1 año")
}