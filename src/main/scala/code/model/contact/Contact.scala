package code
package model
package contact

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field.{BsonRecordField, LongPk}
import net.liftweb.record.field.{EmailField, StringField, EnumNameField}

/**
 * Created by Nataly on 08/01/2015.
 */
class Contact extends MongoRecord[Contact] with LongPk[Contact]{
  override def meta = Contact

  object typeContact extends EnumNameField(this, TypeContact)
  object name extends StringField(this, 500)
  object country extends StringField(this, 500)
  object email extends EmailField(this, 200)
  object phone extends BsonRecordField(this, Phone)
  object city extends StringField(this, 500)
  object organization extends StringField(this, 500)
//  object extraField extends MongoCaseClassListField(Contact, DataType) // la clase esta en otra rama

}

object Contact extends Contact with RogueMetaRecord[Contact]{
  override def collectionName = "contact.contacts"
}
