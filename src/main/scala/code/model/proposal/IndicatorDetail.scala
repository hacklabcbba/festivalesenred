package code
package model
package proposal

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{DecimalField, StringField}

class IndicatorDetail private () extends BsonRecord[IndicatorDetail] {

  override def meta = IndicatorDetail

  object description extends StringField(this, 500)
  object inputValue extends DecimalField(this, 0)
  object inputValueFde extends DecimalField(this, 0)
  object outputValue extends DecimalField(this, 0)
  object outputValueFde extends DecimalField(this, 0)
  object percentValue extends DecimalField(this, 0)
  object percentValueFde extends DecimalField(this, 0)

}

object IndicatorDetail extends IndicatorDetail with BsonMetaRecord[IndicatorDetail]