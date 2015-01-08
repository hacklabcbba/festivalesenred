package code
package model
package field

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.mongodb.record.field.MongoCaseClassListField

/**
 * Created by Nataly on 07/01/2015.
 */
class Field private() extends BsonRecord[Field]{

  override def meta = Field

  object listFields extends MongoCaseClassListField[Field, DataType](this)
}

object Field extends Field with BsonMetaRecord[Field]