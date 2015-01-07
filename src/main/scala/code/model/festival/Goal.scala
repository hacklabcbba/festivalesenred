package code
package model
package festival

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{LongRefField, LongPk}
import net.liftweb.record.field.StringField

/**
 * Created by Nataly on 07/01/2015.
 */
class Goal private() extends MongoRecord[Goal] with LongPk[Goal]{

  override def meta = Goal

  object festivalId extends LongRefField(this, Festival)
  object description extends StringField(this, 700)
}

object Goal extends Goal with MongoMetaRecord[Goal] {
  override def collectionName = "festival.goal"
}
