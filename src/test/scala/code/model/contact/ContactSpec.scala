package code
package model
package contact

import code.model.contact.TypeContact._
import code.model.field.StringDataType

class ContactSpec extends BaseMongoSessionWordSpec {
  "Contact" should {
    "create, validate, save, and retrieve properly" in {

      var number1 = StringDataType("home", List("011 30670900", "(21) 2531-1232"))
      var number2 = StringDataType("cel", List("(21) 2541-1234"))

      val phone = Phone.createRecord
        .numbers(number1 :: number2 :: Nil)

      val newContact = Contact.createRecord
        .city("Cochabamba")
        .country("Bolivia")
        .email("nataly@genso.com.bo")
        .name("Nataly Nanda")
        .organization("Genso")
        .phone(phone)
        .typeContact(Organizador)

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
