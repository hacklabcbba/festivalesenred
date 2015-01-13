package code
package model
package link

import code.model.development.Development
import net.liftweb.mongodb.record.field.{MongoListField, MongoCaseClassListField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField
import code.model.field._

class LinkDetail private() extends BsonRecord[LinkDetail] {

  override def meta = LinkDetail

  object product extends StringField(this, 500)
  object function extends StringField(this, 500)
  object observation extends StringField(this, 800)
  object responsible extends MongoListField[LinkDetail, Development](this)
  object link extends MongoCaseClassListField[LinkDetail, StringDataType](this)

}

object LinkDetail extends LinkDetail with BsonMetaRecord[LinkDetail]
