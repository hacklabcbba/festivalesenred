package code
package model
package festival

import code.lib.RogueMetaRecord
import code.lib.field.ComboBoxField
import code.model.institution.Institution
import code.model.proposal.Proposal
import net.liftmodules.combobox.ComboItem
import net.liftweb.common.{Full, Box, Loggable}
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import code.model.field.{ListStringDataType, Field}
import code.model.link.Link

class Festival private () extends MongoRecord[Festival] with ObjectIdPk[Festival] {

  override def meta = Festival

  object name extends StringField(this, 500) {
    override def displayName = "Nombre del Festival"
  }
  object responsible extends StringField(this, 300) {
    override def displayName = "Responsable del Festival"
  }
  object productionManagement extends StringField(this, 300) {
    override def displayName = "Dirección de la Producción"
  }
  object city extends ComboBoxField(this, City) {
    def toString(in: City) = s"${in.name.get}/${in.country.get}"
    val placeholder = "Seleccione las ciudades donde se realizara el festival"
    override def displayName = "Ciudad"
  }
  object places extends BsonRecordListField(this, Place) {
    override def displayName = "Lugares donde se desarrolla el Festival"
  }//ToDo map
  object begins extends DateField(this) {
    override def displayName = "Fecha inicial"
  }//ToDo dates
  object ends extends DateField(this) {
    override def displayName = "Fecha final"
  }
  object duration extends EnumNameField(this, FestivalDuration) {
    override def displayName = "Duración"
  }
  object call extends BinaryField(this) {
    override def displayName = "Convocatoria"
  }
  object areas extends ComboBoxField(this, Area) {
    def toString(in: Area) = s"${in.name.get}(${in.description.get})"
    val placeholder = ""
    override def displayName = "¿En qué área actúa?"
  }
  object website extends StringField(this, 500) {
    override def displayName = "Sitio web"
  }
  object responsibleEmail extends EmailField(this, 128) {
    override def displayName = "Email del/la responsable"
  }
  object pressResponsibleEmail extends EmailField(this, 128) {
    override def displayName = "Email de contacto del/la responsable de comunicación o prensa"
  }
  object facebookPage extends StringField(this, 500) {
    override def displayName = "Página en facebook"
    override def optional_? = true
  }
  object twitter extends StringField(this, 500) {
    override def displayName = "Twitter"
    override def optional_? = true
  }
  object skype extends StringField(this, 128) {
    override def displayName = "Skype"
    override def optional_? = true
  }
  object spaces extends ObjectIdRefListField(this, Space) {
    override def displayName = "Espacios donde se realiza"
  }
  object equipment extends ObjectIdRefListField(this, EquipmentDetail) {
    override def displayName = "Equipamientos que posees "
  }
  object numberOfAttendees extends EnumNameField(this, NumberOfAttendees) {
    override def displayName = "¿Qué cantidad de público convoca y moviliza tu festival? "
  }
  object publicKind extends EnumNameField(this, PublicKind) {
    override def displayName = "¿A qué tipo de público se dirige tu festival?"
  }
  object staff extends BsonRecordListField(this, Staff) {
    override def displayName = "¿Cuantas personas componen el equipo y que funciones cumplen?"
  }
  object presentation extends TextareaField(this, 500) {
    override def displayName = "Breve histórico / presentación"
  }
  object numberEditions extends BsonRecordListField(this, FestivalEdition) {
    override def displayName = "¿Cuantas ediciones del festival se han realizado y en que años?"
  }
  object serviceExchange extends ObjectIdRefListField(this, ServiceExchange) {
    override def displayName = "¿Realizas intercambio de servicios, productos o conocimiento con otras organizaciones? En caso que tu respuesta sea Si, por favor especifica"
    override def optional_? = true
  }
  object trainingActivity extends ObjectIdRefListField(this, TrainingActivity) {
    override def displayName = "¿Desarrollas alguna acción de formación?"
    override def optional_? = true
  }
  object communicationTools extends ObjectIdRefListField(this, CommunicationTool) {
    override def displayName = "¿Especificar que herramientas de comunicación utilizas?"
  }
  object publicInstitutionPartnerships extends BsonRecordListField(this, Partnership) {
    override def displayName = "¿Tienes alguna alianza con instituciones públicas?"
    override def optional_? = true
  }
  object privateInstitutionPartnerships extends BsonRecordListField(this, Partnership) {
    override def displayName = "¿Tienes alguna alianza con alguna entidad privada?"
    override def optional_? = true
  }
  object civilOrganizationPartnerships extends BsonRecordListField(this, Partnership) {
    override def displayName = "¿Tienes alguna alianza con alguna entidad privada?"
    override def optional_? = true
  }
  object networking extends ObjectIdRefListField(this, Network) {
    override def displayName = "¿Participa de alguna red?"
    override def optional_? = true
  }
  object minimalBudget extends DecimalField(this, 2) {
    override def displayName = "A partir de que presupuesto mínimo realizas el festival"
  }
  object budget extends DecimalField(this, 2) {
    override def displayName = "Cual es el costo monetario que utilizas para realizar el festival"
    override def optional_? = true
  }
  object collaborativeEconomyBudget extends DecimalField(this, 2) {
    override def displayName = "Cual el monto en economía colaborativa"
    override def optional_? = true
  }
  object managementDuration extends EnumNameField(this, ManagementDuration) {
    override def displayName = "Cual es el tiempo de duración de gestión del Festival?"
  }
  object tags extends StringField(this, 1000) {
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
  val Hours, Days, Months, Years  = Value
}

object NumberOfAttendees extends Enumeration {
  type NumberOfAttendees = Value
  val LessThanFifty, FiftyToOneHundred, OneHundredToFiveHundred, FiveHundredToOneThousand, MoreThanOneThousand = Value
}

object PublicKind extends Enumeration {
  type PublicKind = Value
  val Child, Youth, Adult, Elderly, All = Value
}

object ManagementDuration extends Enumeration {
  type PublicKind = Value
  val OneWeek, OneMonth, SeveralMonths, OneYear, MoreThanOneYear = Value
}