package code
package model
package festival


import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{MongoCaseClassListField, LongPk}

/**
 * Created by Nataly on 07/01/2015.
 */
class Field private() extends MongoRecord[Field] with LongPk[Field]{

  override def meta = Field

  object listFields extends MongoCaseClassListField[Field, DataType](this)

}

object Field extends Field with MongoMetaRecord[Field]{
  override def collectionName = "festival.field"
}
