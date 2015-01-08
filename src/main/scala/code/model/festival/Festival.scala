package code
package model
package festival

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field.StringField

/**
 * Created by Nataly on 07/01/2015.
 */
class Festival private () extends MongoRecord[Festival] with LongPk[Festival]{

  override def meta = Festival

  object name extends StringField(this, 500)
  object description extends StringField(this, 700)
  object place extends StringField(this, 700)
  object concept extends StringField(this, 1000)
  object proposal extends StringField(this, 1000)

}

object Festival extends Festival with MongoMetaRecord[Festival] {
  override def collectionName = "festival.festivals"
}
