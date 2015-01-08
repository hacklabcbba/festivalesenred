package code
package model
package festival

import code.model.field.StringDataType
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.mongodb.record.field.MongoCaseClassListField

/**
 * Created by Nataly on 07/01/2015.
 */
class Goal private() extends BsonRecord[Goal]{

  override def meta = Goal

  object descriptions extends MongoCaseClassListField[Goal, StringDataType](this)
}

object Goal extends Goal with BsonMetaRecord[Goal]