package code
package model
package proposal

import code.model.field.Field
import net.liftweb.mongodb.record.field.{BsonRecordField, MongoListField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}

class Schedule private () extends BsonRecord[Schedule] {

  override def meta = Schedule

  object activities extends MongoListField[Schedule, Activity](this)
  object otherDescriptions extends BsonRecordField(this, Field) {
    override def optional_? = true
  }
}

object Schedule extends Schedule with BsonMetaRecord[Schedule]
