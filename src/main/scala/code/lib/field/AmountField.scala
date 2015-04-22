package code
package lib
package field

import code.model.festival.Amount
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.field.BsonRecordField

class AmountField[OwnerType <: BsonRecord[OwnerType]](rec: OwnerType) extends BsonRecordField(rec, Amount) {
  override def toForm = {
    val inst = this.valueBox openOr Amount.createRecord
    for {
      f1 <- inst.amount.toForm
      f2 <- inst.currency.toForm
    } yield {
      <div class="row collapse">
        <div class="small-12 large-8 columns">
          {f1}
        </div>
        <div class="small-12 large-4 columns">
          {f2}
        </div>
      </div>
    }
  }
}
