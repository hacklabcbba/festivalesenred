package code
package model
package contact

import code.lib.RogueMetaRecord
import code.model.field.DataType
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.{MongoCaseClassListField, BsonRecordField, LongPk}
import net.liftweb.record.field.{EmailField, StringField, EnumNameField}

class Contact extends MongoRecord[Contact] with LongPk[Contact] {
  override def meta = Contact

  object contactType extends EnumNameField(this, ContactType)
  object name extends StringField(this, 500)
  object country extends StringField(this, 500)
  object email extends EmailField(this, 200)
  object phone extends BsonRecordField(this, Phone)
  object city extends StringField(this, 500)
  object organization extends StringField(this, 500)
  object extraField extends MongoCaseClassListField[Contact, DataType](this)

}

object Contact extends Contact with RogueMetaRecord[Contact] {
  override def collectionName = "contact.contacts"
}
