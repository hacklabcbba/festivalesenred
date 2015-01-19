package code
package model
package proposal
package schedule

import code.model.field.Field
import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{DecimalField, StringField}

class Spending private() extends BsonRecord[Spending] {

  override def meta = Spending

  object name extends StringField(this, 500)
  object listField extends BsonRecordField(this, Field) {
    override def optional_? = true
  }
  object total extends DecimalField(this, 0)
}

object Spending extends Spending with BsonMetaRecord[Spending]
