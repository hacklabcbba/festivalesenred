package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.common.Loggable
import net.liftweb.mongodb.record.{BsonRecord, MongoRecord}
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field.StringField
import code.model.field.Field

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
  object extraFields extends BsonRecordField(this, Field){
    override def optional_? = true
  }
  object goals extends BsonRecordField(this, Goal){
    override def optional_? = true
  }

}

object Festival extends Festival with RogueMetaRecord[Festival] with Loggable{
  override def collectionName = "festival.festivals"
}
