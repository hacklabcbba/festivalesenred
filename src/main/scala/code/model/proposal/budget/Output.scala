package code
package model
package proposal
package budget

import net.liftweb.mongodb.record.field.MongoListField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{DecimalField, StringField}

class Output private () extends BsonRecord[Output] {

  override def meta = Output

  object nameGroupOutput extends StringField(this, 500)
  object listOutput extends MongoListField[Output, OutputDetail](this)
  object subTotal extends DecimalField(this, 0)
}

object Output extends Output with BsonMetaRecord[Output]
