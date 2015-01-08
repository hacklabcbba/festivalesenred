package code.model.festival

import code.BaseMongoSessionWordSpec

/**
 * Created by Nataly on 08/01/2015.
 */
class FestivalSpec extends BaseMongoSessionWordSpec{

  "Festival" should {
    "create, validate, save, and retrieve properly" in {

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
