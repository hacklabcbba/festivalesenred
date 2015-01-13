package code
package model
package festival

import code.lib.RogueMetaRecord
import code.model.institution.Institution
import code.model.proposal.Proposal
import net.liftweb.common.Loggable
import net.liftweb.mongodb.record.MongoRecord
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field.{StringField, IntField}
import code.model.field.{ListStringDataType, Field}
import code.model.link.Link

class Festival private () extends MongoRecord[Festival] with ObjectIdPk[Festival] {

  override def meta = Festival

  object name extends StringField(this, 500)
  object description extends StringField(this, 700)
  object places extends MongoListField[Festival, Place](this)
  object concept extends StringField(this, 1000)
  object proposal extends BsonRecordField(this, Proposal)
  object startDate extends DateField(this)
  object endDate extends DateField(this)
  object numberEditions extends IntField(this)
  object institutions extends MongoListField[Festival, Institution](this)
  object alliances extends MongoListField[Festival, Institution](this)
  object links extends MongoListField[Festival, Link](this)
  object otherDescriptions extends BsonRecordField(this, Field) {
    override def optional_? = true
  }
}

object Festival extends Festival with RogueMetaRecord[Festival] with Loggable {
  override def collectionName = "festival.festivals"
}
