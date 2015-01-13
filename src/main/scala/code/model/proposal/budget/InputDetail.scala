package code
package model
package proposal
package budget

import code.model.development.Development
import net.liftweb.mongodb.record.field.MongoListField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{DecimalField, StringField, IntField}

class InputDetail private () extends BsonRecord[InputDetail] {

  override def meta = InputDetail

  object quantity extends IntField(this)
  object unit extends IntField(this)
  object hourQuantity extends IntField(this)
  object description extends StringField(this, 500)
  object input extends DecimalField(this, 0)
  object negotiation extends StringField(this, 1000)
  object supplier extends StringField(this, 500)
  object responsible extends MongoListField[InputDetail, Development](this)
}

object InputDetail extends InputDetail with BsonMetaRecord[InputDetail]