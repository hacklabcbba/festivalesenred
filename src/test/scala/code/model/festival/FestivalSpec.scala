package code
package model
package festival

import java.util.Calendar
import code.model.development.Development
import code.model.development.RoleType._
import net.liftweb.util.Helpers._
import code.model.field.{ListStringDataType, Field, StringDataType}
import code.model.proposal._
import net.liftweb.common.Box
import code.model.proposal.ScopeType._

class FestivalSpec extends BaseMongoSessionWordSpec {

  "Festival" should {
    "create, validate, save, and retrieve properly" in {

      // otherDescriptions
      val content = StringDataType("Contenido", List("Trabajo físico", "Trabajo de Investigación y Exploración"))
      val result = StringDataType("RESULTADO", List("Renovar conceptos-ideas acerca de la Danza"))

      val newField = Field.createRecord
        .fieldList(content :: result :: Nil)

      val errsField = newField.validate
      if (errsField.length > 1) {
        fail("Validation error: " + errsField.mkString(", "))
      }

      val dateFormat : java.text.DateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")
      val date : Box[java.util.Date] = tryo {
        val calendar = Calendar.getInstance()
        calendar.setTime(dateFormat.parse("20-05-2001"))
        calendar.getTime
      }

      //placesAndDates
      val place1 = Place.createRecord
        .city("Cochabamba")
        .country("Bolivia")
        .date(date)

      val place2 = Place.createRecord
        .city("Oruro")
        .country("Bolivia")
        .date(date)

      // for create a proposal

      // Participant description
      val participant1 = Participant.createRecord
        .scope(National)
        .accessibility("Abierta a grupos o creadores profesionales bolivianos residentes o no en Bolivia. " +
          "Desde 2015 se gestionará su apertura a participación internacional.")
        .audience("Abierto para Todo público")
        .convocatory("Convocatoria abierta en coordinación con El Gangocho, Red nacional de danza contemporánea")

      // Goal description
      val goal1 = StringDataType("1.", List("Trabajo de centro, equilibrio y fluir de la energía a través de la respiración, visualización y elongación del cuerpo."))
      val goal2 = StringDataType("2.", List("Despertar el cuerpo a estímulos y reacciones. Atención, energía ZAT. Estado de alerta."))
      val goal3 = StringDataType("3.", List("Trabajo guiado para descubrir calidades corporales, atravesando estados que nos planteemos."))

      val goal = Goal.createRecord
        .descriptions(goal1 :: goal2 :: goal3 :: Nil)
        .narrative("Danza en diálogo abierto con otras artes (nuevas tecnologías, teatro, poesía, artes plásticas, fotografía, danza teatro,etc.). " +
        "Encuentro Boliviano de coreógrafos en espacios reflexivos y formativos.")

      val errsGoal = goal.validate
      if(errsGoal.length > 1) {
        fail("Validation error : " + errsGoal.mkString(", "))
      }

      // For create Schedule description

      // Development
      val newDevelopment = Development.createRecord
        .name("Jhon Charles")
        .role(Responsible)

      val errsDevelopment = newDevelopment.validate
      if (errsDevelopment.length > 1) {
        fail("Validation error : " + errsDevelopment.mkString(", "))
      }

      //Activity
      val activity1 = Activity.createRecord
        .date(date)
        .description("Espectáculos")
        .name("Activity1")
        .responsibles(newDevelopment :: Nil)

      val newSchedule = Schedule.createRecord
        .activities(activity1 :: Nil)

      // for create bugdets
      val newBudget = Budget.createRecord
        .description("Autogestión anual: Cada año busca recursos en cooperación internacional, en Ministerio de Culturas" +
        " y Municipio: Hivos. Goethe Institut, Alianza Francesa y Ministerio apoyaron sus primeras versiones.")


      val proposal1 = Proposal.createRecord
        .participants(participant1)
        .goals(goal)
        .schedules(newSchedule)
        .budgets(newBudget)


      val errsProposal = proposal1.validate
      if (errsProposal.length > 1) {
        fail("Validation error: "+errsProposal.mkString(", "))
      }

      proposal1.validate.length should equal (0)

      //List of organizations
      val listInstitutions = ListStringDataType(List("La Usina Cultura", "VIDANZA"))

      //List alliances
      val alliancesDT = ListStringDataType(List("VIDANZA", "TELARTES", "EL GANGOCHO", "GOBIERNO MUNICIPAL"))

      val newFestival = Festival.createRecord
        .name("TALLER CUERPO PRESENTE Composición Coreográfica")
        .description("El objetivo es repensar la mirada del cuerpo como soporte de la danza y hacer propuestas " +
        "coreográficas con un enfoque conceptual y estético, buscando que cada cuerpo encuentre sus necesidades " +
        "expresivas, y reflexione sobre la historicidad de su cuerpo y el contexto actual que lo rodea, con el que " +
        "además va a instaurar un diálogo a través de la Danza.")
        .placesAndDates(place1 :: place2 :: Nil)
        .concept("Transportaremos al cuerpo a reconocerse, abrirse y cuestionarse, para generar particularidad en el " +
        "movimiento de cada cuerpo y el desarrollo de la comprensión de lo que necesita decir. El uso de nociones " +
        "básicas de ritmo y dinámica corporal serán explorados, así como nociones espaciales, sonoras y conceptuales, " +
        "para enriquecer nuestras composiciones coreográficas con el único fin de transformarnos en cuerpos consientes y " +
        "presentes en escena.")
        .proposal(proposal1)
        .otherDescriptions(newField)
        .startDate(date)
        .endDate(date)
        .numberEditions(3)
        .organizersInstitutions(listInstitutions)
        .alliances(alliancesDT)


      val errs = newFestival.validate
      if (errs.length > 1) {
        fail("Validation error: "+errs.mkString(", "))
      }

      newFestival.validate.length should equal (0)

      // save to db
      newFestival.save(false)

      // retrieve from db and compare
      val festivalFromDb = Festival.find(newFestival.id.get)
      festivalFromDb.isDefined should equal (true)
      festivalFromDb.map(u => u.id.get should equal (newFestival.id.get))
    }
  }
}
