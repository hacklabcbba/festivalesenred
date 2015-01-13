package code
package model
package proposal
package budget

import code.model.development.Development
import net.liftweb.mongodb.record.field.MongoListField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{DecimalField, StringField, IntField}

class OutputDetail private () extends BsonRecord[OutputDetail] {

  override def meta = OutputDetail

  object quantity extends IntField(this)
  object unit extends IntField(this)
  object hourQuantity extends IntField(this)
  object description extends StringField(this, 500)
  object output extends DecimalField(this, 0)
  object negotiation extends StringField(this, 1000)
  object supplier extends StringField(this, 500)
  object responsible extends MongoListField[OutputDetail, Development](this)
}

object OutputDetail extends OutputDetail with BsonMetaRecord[OutputDetail]