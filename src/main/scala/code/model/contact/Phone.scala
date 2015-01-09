package code
package model
package contact

import code.model.field.StringDataType
import net.liftweb.mongodb.record.field.MongoCaseClassListField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}

class Phone private() extends BsonRecord[Phone] {
  override def meta = Phone

  object numbers extends MongoCaseClassListField[Phone, StringDataType](this)
}

object Phone extends Phone with BsonMetaRecord[Phone]
