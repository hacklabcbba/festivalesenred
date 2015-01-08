package code
package model
package field

import code.lib.RogueMetaRecord
import code.model.festival.Festival
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{LongPk, LongRefField, MongoCaseClassListField}

/**
 * Created by Nataly on 07/01/2015.
 */
class Field private() extends BsonRecord[Field]{

  override def meta = Field

  object listFields extends MongoCaseClassListField[Field, DataType](this)
}

object Field extends Field with BsonMetaRecord[Field]