package code
package model
package proposal
package budget

import net.liftweb.mongodb.record.field.MongoListField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.DecimalField

class Viability private () extends BsonRecord[Viability] {

  override def meta = Viability

  object input extends MongoListField[Viability, Input](this)
  object output extends MongoListField[Viability, Output](this)
  object totalInput extends DecimalField(this, 0)
  object totalOutput extends DecimalField(this, 0)
  object residue extends DecimalField(this, 0)
}

object Viability extends Viability with BsonMetaRecord[Viability]
