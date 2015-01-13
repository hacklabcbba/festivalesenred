package code
package model
package link

import code.lib.RogueMetaRecord
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{MongoListField, ObjectIdPk}
import net.liftweb.record.field.StringField

class Link private () extends MongoRecord[Link] with ObjectIdPk[Link] {

  override def meta = Link

  object description extends StringField(this, 500)
  object nameGroup extends StringField(this, 500)
  object linkDetail extends MongoListField[Link, LinkDetail](this)
}

object Link extends Link with RogueMetaRecord[Link]
