package code
package model
package proposal
package budget

import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField

class Budget private () extends BsonRecord[Budget] {

  override def meta = Budget

  //Description how can obtain the budget for the festival
  object description extends StringField(this, 1000)
  object indicator extends BsonRecordField(this, Indicator)
  object viability extends BsonRecordField(this, Viability)
}

object Budget extends Budget with BsonMetaRecord[Budget]