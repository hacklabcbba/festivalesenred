package code
package model
package contact

import code.model.contact.ContactType._
import code.model.festival.Place
import code.model.field.StringDataType
import code.model.institution.Institution

class ContactSpec extends BaseMongoSessionWordSpec {
  "Contact" should {
    "create, validate, save, and retrieve properly" in {

      val number1 = StringDataType("home", List("011 30670900", "(21) 2531-1232"))
      val number2 = StringDataType("cel", List("(21) 2541-1234"))

      val phone = Phone.createRecord
        .numbers(number1 :: number2 :: Nil)

      val place = Place.createRecord
        .city("Cochabamba")
        .country("Bolivia")

      //List of organizations
      val institution1 = Institution.createRecord
        .name("La Usina Cultura")

      val newContact = Contact.createRecord
        .email("nataly@genso.com.bo")
        .name("Nataly Nanda")
        .organization(Institution)
        .phone(phone)
        .contactType(Organizer)
        .place(place)

      val errs = newContact.validate
      if (errs.length > 1) {
        fail("Validation error: "+errs.mkString(", "))
      }

      newContact.validate.length should equal (0)
      newContact.save(false)

      // retrieve from db and compare
      val contactFromDb = Contact.find(newContact.id.get)
      contactFromDb.isDefined should equal (true)
      contactFromDb.map(u => u.id.get should equal (newContact.id.get))
    }
  }
}
