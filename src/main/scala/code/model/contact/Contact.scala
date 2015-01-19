package code
package model
package contact

import code.lib.RogueMetaRecord
import code.model.festival.Place
import code.model.field.DataType
import code.model.institution.Institution
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.{ObjectIdRefField, ObjectIdPk, MongoCaseClassListField, BsonRecordField}
import net.liftweb.record.field.{EmailField, StringField, EnumNameField}

class Contact extends MongoRecord[Contact] with ObjectIdPk[Contact] {
  override def meta = Contact

  object contactType extends EnumNameField(this, ContactType)
  object name extends StringField(this, 500)
  object email extends EmailField(this, 200)
  object phone extends BsonRecordField(this, Phone)
  object place extends BsonRecordField(this, Place)
  object organization extends ObjectIdRefField[Contact, Institution](this, Institution)
  object extraField extends MongoCaseClassListField[Contact, DataType](this)

}

object Contact extends Contact with RogueMetaRecord[Contact] {
  override def collectionName = "contact.contacts"
}
