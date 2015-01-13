package code.model.proposal.budget

import net.liftweb.mongodb.record.field.MongoListField
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField

class Indicator private () extends BsonRecord[Indicator] {

  override def meta = Indicator

  object nameGroup extends StringField(this, 500)
  object detailIndicator extends MongoListField[Indicator, IndicatorDetail](this)
}

object Indicator extends Indicator with BsonMetaRecord[Indicator]
