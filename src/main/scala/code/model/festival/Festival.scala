package code
package model
package festival

import code.lib.RogueMetaRecord
import net.liftweb.common.Loggable
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field.{StringField, IntField}
import code.model.field.{ListStringDataType, Field}

class Festival private () extends MongoRecord[Festival] with ObjectIdPk[Festival] {

  override def meta = Festival

  object name extends StringField(this, 500)
  object description extends StringField(this, 700)
  object placesAndDates extends MongoListField[Festival, Place](this)
  object concept extends StringField(this, 1000)
  object proposal extends StringField(this, 1000)
  object startDate extends DateField(this)
  object endDate extends DateField(this)
  object numberEditions extends IntField(this)
  object organizersInstitutions extends MongoCaseClassField[Festival, ListStringDataType](this)
  object alliances extends MongoCaseClassField[Festival, ListStringDataType](this)
  object otherDescriptions extends BsonRecordField(this, Field) {
    override def optional_? = true
  }
}

object Festival extends Festival with RogueMetaRecord[Festival] with Loggable {
  override def collectionName = "festival.festivals"
}
