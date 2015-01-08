package code
package model
package contact

import code.model.contact.TypeContact._

/**
 * Created by Nataly on 08/01/2015.
 */
class ContactSpec extends BaseMongoSessionWordSpec {
  "Contact" should {
    "create, validate, save, and retrieve properly" in {

      val phone = Phone.createRecord
        .number(60383513)

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
