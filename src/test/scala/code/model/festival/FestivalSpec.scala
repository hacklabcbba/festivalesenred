package code
package model
package festival

import code.model.field.{Field, StringDataType}

/**
 * Created by Nataly on 08/01/2015.
 */
class FestivalSpec extends BaseMongoSessionWordSpec{

  "Festival" should {
    "create, validate, save, and retrieve properly" in {


      var content = StringDataType("Contenido", List("Trabajo físico", "Trabajo de Investigación y Exploración"))
      var result = StringDataType("RESULTADO", List("Renovar conceptos-ideas acerca de la Danza"))

      var newField = Field.createRecord
        .listFields(content :: result :: Nil)

      val errsField = newField.validate
      if (errsField.length > 1) {
        fail("Validation error: " + errsField.mkString(", "))
      }

      var goal1 = StringDataType("1.", List("Trabajo de centro, equilibrio y fluir de la energía a través de la respiración, visualización y elongación del cuerpo."))
      var goal2 = StringDataType("2.", List("Despertar el cuerpo a estímulos y reacciones. Atención, energía ZAT. Estado de alerta."))
      var goal3 = StringDataType("3.", List("Trabajo guiado para descubrir calidades corporales, atravesando estados que nos planteemos."))

      var goal = Goal.createRecord
        .descriptions(goal1 :: goal2 :: goal3 :: Nil)

      val errsGoal = goal.validate
      if(errsGoal.length > 1) {
        fail("Validation error : " + errsGoal.mkString(", "))
      }

      var newFestival = Festival.createRecord
        .name("TALLER CUERPO PRESENTE Composición Coreográfica")
        .description("El objetivo es repensar la mirada del cuerpo como soporte de la danza y hacer propuestas " +
        "coreográficas con un enfoque conceptual y estético, buscando que cada cuerpo encuentre sus necesidades " +
        "expresivas, y reflexione sobre la historicidad de su cuerpo y el contexto actual que lo rodea, con el que " +
        "además va a instaurar un diálogo a través de la Danza.")
        .place("Bolivia Cochabamba")
        .concept("Transportaremos al cuerpo a reconocerse, abrirse y cuestionarse, para generar particularidad en el " +
        "movimiento de cada cuerpo y el desarrollo de la comprensión de lo que necesita decir. El uso de nociones " +
        "básicas de ritmo y dinámica corporal serán explorados, así como nociones espaciales, sonoras y conceptuales, " +
        "para enriquecer nuestras composiciones coreográficas con el único fin de transformarnos en cuerpos consientes y " +
        "presentes en escena.")
        .proposal("Trabajo físico - Trabajo de Investigación y Exploración - Creación y Composición.")
        .extraFields(newField)
        .goals(goal)


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
