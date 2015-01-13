package code
package model
package proposal
package budget

import net.liftweb.mongodb.record.field.MongoListField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{DecimalField, StringField}

class Input private () extends BsonRecord[Input] {

  override def meta = Input

  object nameGroupInput extends StringField(this, 500)
  object listInput extends MongoListField[Input, InputDetail](this)
  object subTotal extends DecimalField(this, 0)
}

object Input extends Input with BsonMetaRecord[Input]