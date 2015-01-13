package code
package model
package proposal

import java.util.Calendar

import code.model.development.Development
import code.model.development.RoleType._
import code.model.field.StringDataType
import code.model.proposal.ScopeType._
import net.liftweb.common.Box
import net.liftweb.util.Helpers._

class ProposalSpec extends BaseMongoSessionWordSpec {

  "Proposal" should {
    "create, validate, save, and retrieve properly" in {

      val dateFormat: java.text.DateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")
      val date: Box[java.util.Date] = tryo {
        val calendar = Calendar.getInstance()
        calendar.setTime(dateFormat.parse("20-05-2001"))
        calendar.getTime
      }

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
      if (errsGoal.length > 1) {
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

      // create Indicator

      val indicatorDetail1 = IndicatorDetail.createRecord
        .inputValue(0.00)
        .inputValueFde(0.00)
        .outputValue(0.00)
        .outputValueFde(0.00)
        .percentValue(0.00)
        .percentValueFde(0.00)
        .description("Privado")

      val indicatorDetail2 = IndicatorDetail.createRecord
        .inputValue(0.00)
        .inputValueFde(0.00)
        .outputValue(0.00)
        .outputValueFde(0.00)
        .percentValue(0.00)
        .percentValueFde(0.00)
        .description("Publico")

      val newIndicator = Indicator.createRecord
        .nameGroup("Por sector")
        .detailIndicator(indicatorDetail1 :: indicatorDetail2 :: Nil)

      // for create bugdets
      val newBudget = Budget.createRecord
        .description("Autogestión anual: Cada año busca recursos en cooperación internacional, en Ministerio de Culturas" +
        " y Municipio: Hivos. Goethe Institut, Alianza Francesa y Ministerio apoyaron sus primeras versiones.")
        .indicator(newIndicator)

      val proposal1 = Proposal.createRecord
        .participants(participant1)
        .goals(goal)
        .schedules(newSchedule)
        .budgets(newBudget)

      val errsProposal = proposal1.validate
      if (errsProposal.length > 1) {
        fail("Validation error: " + errsProposal.mkString(", "))
      }

      proposal1.validate.length should equal(0)
    }
  }
}
