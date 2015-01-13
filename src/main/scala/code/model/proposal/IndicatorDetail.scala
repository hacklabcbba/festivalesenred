package code
package model
package proposal

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{DoubleField, StringField}

class IndicatorDetail private () extends BsonRecord[IndicatorDetail] {

  override def meta = IndicatorDetail

  object description extends StringField(this, 500)
  object inputValue extends DoubleField(this)
  object inputValueFde extends DoubleField(this)
  object outputValue extends DoubleField(this)
  object outputValueFde extends DoubleField(this)
  object percentValue extends DoubleField(this)
  object percentValueFde extends DoubleField(this)

}

object IndicatorDetail extends IndicatorDetail with BsonMetaRecord[IndicatorDetail]