package code
package model
package proposal

import code.model.field.{Field, StringDataType}
import net.liftweb.mongodb.record.field.{BsonRecordField, MongoCaseClassListField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField

class Goal private() extends BsonRecord[Goal] {

  override def meta = Goal

  //List of descriptions about of goals
  object descriptions extends MongoCaseClassListField[Goal, StringDataType](this)
  //How is the main goal, where we need to arrive?
  object narrative extends StringField(this, 1000)
  //Other fields if need more information.
  object otherDescriptions extends BsonRecordField(this, Field) {
    override def optional_? = true
  }
}

object Goal extends Goal with BsonMetaRecord[Goal]