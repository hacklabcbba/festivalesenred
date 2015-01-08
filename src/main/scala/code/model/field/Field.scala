package code
package model
package field

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.mongodb.record.field.MongoCaseClassListField

class Field private() extends BsonRecord[Field] {

  override def meta = Field

  object fieldList extends MongoCaseClassListField[Field, DataType](this)
}

object Field extends Field with BsonMetaRecord[Field]